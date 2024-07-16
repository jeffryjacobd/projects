package poc.method;

import com.google.gson.JsonObject;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class XelerateJavaMethodResolver implements MethodResolver {

    private static final Map<String, List<XelerateJavaMethodExecutor>> JAVA_METHOD_EXECUTOR_MAP;

    static {
        JAVA_METHOD_EXECUTOR_MAP = StreamSupport.stream(ServiceLoader.load(XelerateJavaMethodExecutor.class).spliterator(), false)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(XelerateJavaMethodExecutor::getMethodName,
                                        Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)),
                                Collections::unmodifiableMap));
    }

    @Override
    public MethodExecutor resolve(final EvaluationContext context,
                                  final Object targetObject,
                                  final String name,
                                  final List<TypeDescriptor> argumentTypes) {
        return Optional.ofNullable(JAVA_METHOD_EXECUTOR_MAP.get(name))
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(javaMethodExecutor -> argumentTypes.size() >= javaMethodExecutor.getMinimumNumberOfAttributes()
                        && argumentTypes.size() <= javaMethodExecutor.getMaximumNumberOfAttributes()
                        && compareArgumentsAndAnchorObject(argumentTypes, javaMethodExecutor, targetObject))
                .findFirst()
                .orElse(null);
    }

    private boolean compareArgumentsAndAnchorObject(final List<TypeDescriptor> argumentTypes,
                                                    final XelerateJavaMethodExecutor methodExecutor,
                                                    final Object targetObject) {
        final Iterator<TypeDescriptor> methodInputArgumentsIterator = argumentTypes.iterator();
        if (!isFixedAttributesValid(methodExecutor, methodInputArgumentsIterator)) {
            return false;
        }
        if (!isVariableAttributesValid(methodExecutor, methodInputArgumentsIterator)) {
            return false;
        }
        if (Objects.isNull(targetObject)) {
            return methodExecutor.getTargetObjectType().equals(TypeDescriptor.valueOf(null));
        } else {
            return methodExecutor.getTargetObjectType()
                    .isAssignableTo(Objects.requireNonNull(TypeDescriptor.forObject(targetObject))) ||
                    Optional.of(targetObject).filter(Class.class::isInstance).map(Class.class::cast)
                            .filter(targetClass -> methodExecutor.allowTypeReferencedInvocations()
                                    && methodExecutor.getTargetObjectType()
                                    .getType().isAssignableFrom(targetClass))
                            .isPresent() ||
                    (methodExecutor.getTargetObjectType().equals(TypeDescriptor.valueOf(null)) && targetObject instanceof JsonObject);
        }
    }

    private boolean isVariableAttributesValid(final XelerateJavaMethodExecutor methodExecutor,
                                              final Iterator<TypeDescriptor> methodInputArgumentsIterator) {
        List<TypeDescriptor> inputVariableArgumentTypes = StreamSupport.stream(Spliterators.spliteratorUnknownSize(methodInputArgumentsIterator,
                        Spliterator.SIZED),
                false).collect(Collectors.toList());
        if (inputVariableArgumentTypes.isEmpty()) {
            return true;
        }
        return methodExecutor.getVariableParameters().stream()
                .filter(configVariableArgumentTypeVariation ->
                        configVariableArgumentTypeVariation.size() == inputVariableArgumentTypes.size())
                .anyMatch(configVariableArgumentTypeVariation ->
                        isConfigListAssignableToInput(configVariableArgumentTypeVariation, inputVariableArgumentTypes));
    }

    private boolean isConfigListAssignableToInput(final List<List<TypeDescriptor>> configVariableArgumentTypeVariation,
                                                  final List<TypeDescriptor> inputVariableArgumentTypes) {
        return configVariableArgumentTypeVariation.stream()
                .allMatch(configVariableArgumentTypeVariationSubTypes ->
                        inputVariableArgumentTypes.stream()
                                .allMatch(inputVariableArgument -> configVariableArgumentTypeVariationSubTypes.stream()
                                        .anyMatch(
                                                inputVariableArgument::isAssignableTo)));
    }

    private boolean isFixedAttributesValid(XelerateJavaMethodExecutor methodExecutor, Iterator<TypeDescriptor> methodInputArgumentsIterator) {
        for (List<TypeDescriptor> allowedFixedTypeDescriptorList : methodExecutor.getFixedParameters()) {
            if (!methodInputArgumentsIterator.hasNext()) {
                return false;
            }
            final TypeDescriptor inputTypeDescriptor = methodInputArgumentsIterator.next();
            if (allowedFixedTypeDescriptorList.stream().noneMatch(inputTypeDescriptor::isAssignableTo)) {
                return false;
            }
        }
        return true;
    }
}
