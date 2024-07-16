package poc.method.generation;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import poc.method.XelerateJavaMethodExecutor;
import poc.type.JsonTypeLocator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class XelerateMethodCodeGeneratorProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(XelerateMethodCodeGenerator.class.getName());
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        final AtomicInteger classCount = new AtomicInteger(0);
        roundEnv.getElementsAnnotatedWith(XelerateMethodCodeGenerator.class).stream()
                .flatMap(this::filterAndGenerateProcessableMethods)
                .forEach(classMethodEntry -> {
                    final ExecutableElement filteredMethod = classMethodEntry.getValue();
                    final boolean parametersValid = areInputParametersValid(filteredMethod);
                    final boolean validReturn = isReturnTypeValid(filteredMethod);
                    final boolean isAnyExceptionInThrows = (!filteredMethod.getThrownTypes().isEmpty());
                    if ((!parametersValid) || isAnyExceptionInThrows || (!validReturn)) {
                        return;
                    }
                    final MethodSpec getMethodNameSpec = getMethodNameSpec(filteredMethod);
                    final TypeName listOfClassTypeName = ParameterizedTypeName.get(ClassName.get(List.class),
                            ParameterizedTypeName.get(ClassName.get(Class.class),
                                    WildcardTypeName.subtypeOf(Object.class)));
                    final CodeBlock.Builder executeMethodCodeBlockBuilder = CodeBlock.builder();
                    final CodeBlock.Builder executeMethodReturnCodeBlockBuilder = CodeBlock.builder();
                    executeMethodReturnCodeBlockBuilder.add("return new $T( convertToJsonPrimitiveIfSimpleType($T.$L(", ClassName.get(TypedValue.class), ClassName.get(classMethodEntry.getKey().getKey()),
                            filteredMethod.getSimpleName());
                    final CodeBlock.Builder parametersCodeBlockBuilder = CodeBlock.builder();
                    parametersCodeBlockBuilder.addStatement("final $T parameterClasses = new $T<>()", listOfClassTypeName,
                            ClassName.get(ArrayList.class));
                    iterateMethodParametersAndAddInCodeBlocks(filteredMethod,
                            executeMethodReturnCodeBlockBuilder,
                            parametersCodeBlockBuilder,
                            executeMethodCodeBlockBuilder);
                    executeMethodReturnCodeBlockBuilder.add(")))");
                    parametersCodeBlockBuilder.addStatement("return $T.unmodifiableList(parameterClasses)", ClassName.get(Collections.class));
                    final MethodSpec getFixedParameterTypesMethodSpec = getGetFixedParameterTypesMethodSpec(listOfClassTypeName, parametersCodeBlockBuilder);
                    final MethodSpec executeMethodSpec = getExecuteMethodSpec(executeMethodCodeBlockBuilder, executeMethodReturnCodeBlockBuilder);
                    final TypeSpec classSpec = getGeneratedClassSpec(classMethodEntry.getKey(),
                            filteredMethod,
                            classCount,
                            getMethodNameSpec,
                            getFixedParameterTypesMethodSpec,
                            executeMethodSpec);
                    try {
                        JavaFile.builder(classMethodEntry.getKey().getValue() + ".generated", classSpec)
                                .build().writeTo(processingEnv.getFiler());
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return true;
    }

    private TypeSpec getGeneratedClassSpec(Map.Entry<TypeElement, String> targetClassPackageEntry, ExecutableElement filteredMethod, AtomicInteger classCount, MethodSpec getMethodNameSpec, MethodSpec getFixedParameterTypesMethodSpec, MethodSpec executeMethodSpec) {
        return TypeSpec.classBuilder(targetClassPackageEntry.getKey().getSimpleName().toString() +
                        StringUtils.capitalize(filteredMethod.getSimpleName().toString()) +
                        classCount.getAndIncrement())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(getMethodNameSpec)
                .addMethod(getFixedParameterTypesMethodSpec)
                .addMethod(executeMethodSpec)
                .superclass(ClassName.get(AbstractGeneratedMethodCodeExecutor.class))
                .addAnnotation(AnnotationSpec.builder(AutoService.class)
                        .addMember("value", "$T.class", ClassName.get(XelerateJavaMethodExecutor.class)).build())
                .build();
    }

    private MethodSpec getExecuteMethodSpec(CodeBlock.Builder executeMethodCodeBlockBuilder, CodeBlock.Builder executeMethodReturnCodeBlockBuilder) {
        return MethodSpec.methodBuilder("execute")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(TypedValue.class))
                .addParameter(ParameterSpec.builder(ClassName.get(EvaluationContext.class), "context",
                        Modifier.FINAL).build())
                .addParameter(ParameterSpec.builder(ClassName.get(Object.class), "target",
                        Modifier.FINAL).build())
                .varargs(true)
                .addParameter(Object[].class, "arguments")
                .addCode(executeMethodCodeBlockBuilder
                        .addStatement(executeMethodReturnCodeBlockBuilder.build()).build())
                .build();
    }

    private MethodSpec getGetFixedParameterTypesMethodSpec(TypeName listOfClassTypeName, CodeBlock.Builder parametersCodeBlockBuilder) {
        return MethodSpec.methodBuilder("getFixedParameterTypes")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(listOfClassTypeName)
                .addCode(parametersCodeBlockBuilder.build())
                .build();
    }

    private void iterateMethodParametersAndAddInCodeBlocks(ExecutableElement filteredMethod, CodeBlock.Builder executeMethodReturnCodeBlockBuilder, CodeBlock.Builder parametersCodeBlockBuilder, CodeBlock.Builder executeMethodCodeBlockBuilder) {
        final AtomicInteger parameterCount = new AtomicInteger(0);
        filteredMethod.getParameters().stream().peek(parameterType -> {
            if (parameterCount.get() != 0) {
                executeMethodReturnCodeBlockBuilder.add(",");
            }
        }).forEach(variableElement -> {
            parametersCodeBlockBuilder.addStatement("parameterClasses.add($T.class)", ClassName.get(variableElement.asType()));
            executeMethodCodeBlockBuilder.addStatement("final $T parameter"
                            .concat(String.valueOf(parameterCount.get()))
                            .concat(" = (($T) convertToTargetIfPrimitive(arguments[")
                            .concat(String.valueOf(parameterCount.get()))
                            .concat("]))"),
                    ClassName.get(variableElement.asType()),
                    ClassName.get(variableElement.asType()));
            executeMethodReturnCodeBlockBuilder.add("parameter".concat(String.valueOf(parameterCount.getAndIncrement()))
                    .concat(" "));
        });
    }

    private MethodSpec getMethodNameSpec(ExecutableElement filteredMethod) {
        return MethodSpec.methodBuilder("getMethodName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(String.class))
                .addCode(CodeBlock.builder().addStatement("return $S", filteredMethod.getSimpleName().toString())
                        .build())
                .build();
    }

    private boolean isReturnTypeValid(ExecutableElement filteredMethod) {
        return JsonTypeLocator.TYPE_DECIDING_MAP.keySet().stream()
                .anyMatch(predicate -> predicate.test(filteredMethod.getReturnType().toString().toUpperCase()));
    }

    private boolean areInputParametersValid(ExecutableElement filteredMethod) {
        return filteredMethod.getParameters().stream()
                .map(variableElement -> variableElement.asType().toString())
                .map(String::toUpperCase)
                .allMatch(parameterClass ->
                        JsonTypeLocator.TYPE_DECIDING_MAP.keySet()
                                .stream().anyMatch(predicate -> predicate.test(parameterClass)));
    }

    private Stream<Map.Entry<Map.Entry<TypeElement, String>, ExecutableElement>> filterAndGenerateProcessableMethods(final Element methodCodeGenClass) {
        final XelerateMethodCodeGenerator methodCodeGenerator =
                methodCodeGenClass.getAnnotation(XelerateMethodCodeGenerator.class);
        final String packageName = Optional.ofNullable(methodCodeGenClass.getEnclosingElement())
                .filter(PackageElement.class::isInstance)
                .map(PackageElement.class::cast).map(PackageElement::getQualifiedName)
                .map(Name::toString).orElse(StringUtils.EMPTY);
        final TypeElement targetClassElement = getTargetTypeElement(methodCodeGenClass);
        final Set<String> includeMethods = Arrays.stream(methodCodeGenerator.includesMethod()).collect(Collectors.toSet());
        final Set<String> excludeMethods = Arrays.stream(methodCodeGenerator.excludesMethod()).collect(Collectors.toSet());
        final Stream<ExecutableElement> allMethodsStream = targetClassElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .map(ExecutableElement.class::cast);
        final Stream<ExecutableElement> includedMethodStream;
        if (includeMethods.isEmpty()) {
            includedMethodStream = allMethodsStream;
        } else {
            includedMethodStream = allMethodsStream
                    .filter(allMethod -> includeMethods.contains(allMethod.getSimpleName().toString()));
        }
        return includedMethodStream.filter(includeMethod -> !excludeMethods.contains(includeMethod.getSimpleName().toString()))
                .filter(filteredMethod -> {
                    final Set<Modifier> modifiersPresent = filteredMethod.getModifiers();
                    return modifiersPresent.contains(Modifier.PUBLIC) &&
                            modifiersPresent.contains(Modifier.STATIC);
                })
                .map(filteredMethod -> new AbstractMap.SimpleImmutableEntry<>(new AbstractMap.SimpleImmutableEntry<>(targetClassElement, packageName), filteredMethod));
    }

    private TypeElement getTargetTypeElement(final Element currentMethodClass) {
        return currentMethodClass.getAnnotationMirrors().stream()
                .filter(annotationMirror -> annotationMirror.getAnnotationType()
                        .asElement().getSimpleName().contentEquals(XelerateMethodCodeGenerator.class.getSimpleName()))
                .flatMap(filteredAnnotationMirror -> filteredAnnotationMirror.getElementValues().entrySet()
                        .stream().filter(annotationKV -> annotationKV.getKey().getSimpleName().contentEquals("targetClass"))
                        .map(Map.Entry::getValue))
                .map(AnnotationValue::getValue).filter(TypeMirror.class::isInstance)
                .map(TypeMirror.class::cast)
                .filter(typeMirror -> typeMirror.getKind().equals(TypeKind.DECLARED))
                .map(DeclaredType.class::cast).map(DeclaredType::asElement)
                .map(TypeElement.class::cast).findFirst().orElseThrow(IllegalStateException::new);
    }
}
