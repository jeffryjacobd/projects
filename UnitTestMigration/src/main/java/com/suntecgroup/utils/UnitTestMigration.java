package com.suntecgroup.utils;

import com.suntecgroup.xelerate.sunrise.common.context.ThreadLocalWithContext;
import com.suntecgroup.xelerate.sunrise.common.property.ConfigurationServiceManager;
import com.suntecgroup.xelerate.sunrise.common.service.ManageEntityService;
import com.suntecgroup.xelerate.sunrise.common.test.PlatformTest;
import com.suntecgroup.xelerate.sunrise.common.utilities.CommonEntityUtils;
import com.suntecgroup.xelerate.sunrise.common.utilities.CommonUtils;
import com.suntecgroup.xelerate.sunrise.common.utilities.SequenceGeneratorService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.FieldAccessFilter;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UnitTestMigration extends AbstractProcessor<CtClass<?>> {

    private static final AtomicReference<Set<CtTypeReference<?>>> CLASS_LEVEL_ANNOTATIONS = new AtomicReference<>();

    private static final AtomicReference<Set<CtTypeReference<?>>> FIELD_LEVEL_ANNOTATIONS = new AtomicReference<>();

    private static final AtomicReference<Set<CtTypeReference<?>>> METHOD_LEVEL_ANNOTATIONS = new AtomicReference<>();

    private static final AtomicReference<Map<CtTypeReference<?>, String>> PLATFORM_TEST_CLASS_FIELDS = new AtomicReference<>();

    @Override
    public final void init() {
        super.init();
        CLASS_LEVEL_ANNOTATIONS.getAndUpdate(annotationTypeSet ->
                Optional.ofNullable(annotationTypeSet).orElseGet(() -> {
                    final Set<CtTypeReference<?>> fieldLevelAnnotations = new HashSet<>();
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(SpringBootTest.class));
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(EnableAutoConfiguration.class));
                    return fieldLevelAnnotations;
                }));
        FIELD_LEVEL_ANNOTATIONS.getAndUpdate(annotationTypeSet ->
                Optional.ofNullable(annotationTypeSet).orElseGet(() -> {
                    final Set<CtTypeReference<?>> fieldLevelAnnotations = new HashSet<>();
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(Autowired.class));
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(Mock.class));
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(Qualifier.class));
                    return fieldLevelAnnotations;
                }));
        METHOD_LEVEL_ANNOTATIONS.getAndUpdate(annotationTypeSet ->
                Optional.ofNullable(annotationTypeSet).orElseGet(() -> {
                    final Set<CtTypeReference<?>> fieldLevelAnnotations = new HashSet<>();
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(Autowired.class));
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(Mock.class));
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(InjectMocks.class));
                    fieldLevelAnnotations.add(getFactory().Annotation().createReference(Qualifier.class));
                    return fieldLevelAnnotations;
                }));
        PLATFORM_TEST_CLASS_FIELDS.getAndUpdate(superClassFieldsMap ->
                Optional.ofNullable(superClassFieldsMap).orElseGet(() -> {
                    final Map<CtTypeReference<?>, String> superClassFields = new HashMap<>();
                    superClassFields.put(getFactory().Annotation().createReference(ThreadLocalWithContext.class), "threadLocalWithContext");
                    superClassFields.put(getFactory().Annotation().createReference(CommonEntityUtils.class), "commonEntityUtils");
                    superClassFields.put(getFactory().Annotation().createReference(SequenceGeneratorService.class), "sequenceGenerator");
                    superClassFields.put(getFactory().Annotation().createReference(ManageEntityService.class), "service");
                    superClassFields.put(getFactory().Annotation().createReference(CommonUtils.class), "commonUtils");
                    superClassFields.put(getFactory().Annotation().createReference(ConfigurationServiceManager.class), "configurationServiceManager");
                    return superClassFields;
                }));
    }

    @Override
    public void process(CtClass<?> testClass) {
        if (isSpringBootUnitTestClass(testClass)) {
            changeToMockitoPlatformSuperClass(testClass);
            removeClassLevelSpringAnnotations(testClass);
            removeInnerClassForSpringTestConfiguration(testClass);
            processFieldAndReferSuperClassFields(testClass);
            processMethodsAndRemoveMockitoOpen(testClass);
        }
    }

    private void processMethodsAndRemoveMockitoOpen(CtClass<?> testClass) {
        List<CtMethod<?>> openMockOnlyMethods = testClass.getMethods().stream()
                .peek(this::removeUnnecessaryAnnotationsInMethod)
                .filter(this::removeMockitoOpenAndCheckEmpty)
                .collect(Collectors.toList());
        openMockOnlyMethods.forEach(testClass::removeMethod);
    }

    private boolean removeMockitoOpenAndCheckEmpty(CtMethod<?> ctMethod) {
        List<CtStatement> openMockStatements = StreamSupport.stream(ctMethod.getBody().spliterator(), false)
                .filter(this::filterForOpenMocksStatement)
                .collect(Collectors.toList());
        openMockStatements.forEach(ctMethod.getBody()::removeStatement);
        return ctMethod.getBody().getStatements().isEmpty() && !openMockStatements.isEmpty();
    }

    private boolean filterForOpenMocksStatement(CtStatement ctStatement) {
        return Optional.of(ctStatement)
                .filter(CtInvocation.class::isInstance)
                .map(CtInvocation.class::cast)
                .filter(ctInvocation ->
                        Optional.ofNullable(ctInvocation.getExecutable())
                                .map(CtExecutableReference::getDeclaringType)
                                .map(CtTypeReference::getSimpleName)
                                .filter(getFactory().createCtTypeReference(MockitoAnnotations.class).getSimpleName()::equals)
                                .isPresent()
                                && Optional.ofNullable(ctInvocation.getArguments())
                                .filter(argumentList -> argumentList.size() == 1)
                                .map(argumentList -> argumentList.get(0))
                                .filter(CtThisAccess.class::isInstance).isPresent())
                .isPresent();
    }

    private void removeUnnecessaryAnnotationsInMethod(CtMethod<?> ctMethod) {
        List<CtAnnotation<?>> annotationsCopy = new ArrayList<>(ctMethod.getAnnotations());
        annotationsCopy.stream()
                .filter(ctAnnotation -> METHOD_LEVEL_ANNOTATIONS.get().stream()
                        .map(CtTypeReference::getSimpleName).collect(Collectors.toList())
                        .contains(ctAnnotation.getAnnotationType().getSimpleName()))
                .forEach(ctMethod::removeAnnotation);
    }

    private void processFieldAndReferSuperClassFields(CtClass<?> testClass) {
        List<CtField<?>> fieldsToRemove = testClass.getFields().stream()
                .filter(this::filterForFieldsWIthMockitoAndSpringAnnotations)
                .flatMap(ctField -> {
                    removeUnnecessaryAnnotationsInField(ctField);
                    addDefaultConstructorCallToGsonField(ctField);
                    addInjectMockToAPIField(testClass, ctField);
                    return renameFieldNamesAndFieldAccess(testClass, ctField);
                })
                .collect(Collectors.toList());
        fieldsToRemove.forEach(testClass::removeField);
    }

    private void addInjectMockToAPIField(CtClass<?> testClass, CtField<?> ctField) {
        final boolean isTestBSApi =
                testClass.getTypeErasure().getSimpleName().startsWith(ctField.getType().getSimpleName());
        final boolean isTestBEApi = ctField.getType().getSimpleName().contains("EntityAPI");
        final boolean isUpdateMockAbsent = ctField.getAnnotations()
                .stream()
                .noneMatch(ctAnnotation -> ctAnnotation.getAnnotationType()
                        .equals(getFactory().createCtTypeReference(InjectMocks.class)));
        if ((isTestBSApi || isTestBEApi) && isUpdateMockAbsent) {
            ctField.addAnnotation(getFactory()
                    .createAnnotation(getFactory()
                            .createCtTypeReference(InjectMocks.class)));
        }
    }

    private Stream<? extends CtField<?>> renameFieldNamesAndFieldAccess(CtClass<?> testClass, CtField<?> ctField) {
        return Optional.ofNullable(PLATFORM_TEST_CLASS_FIELDS.get().get(ctField.getType()))
                .map(newFieldName -> {
                    testClass.filterChildren(new FieldAccessFilter(ctField.getReference())).list(CtFieldAccess.class)
                            .stream()
                            .filter(ctFieldAccess -> ctFieldAccess.getVariable().equals(ctField.getReference()))
                            .forEach(ctFieldAccess ->
                                    ctFieldAccess.getVariable().setSimpleName(newFieldName));
                    ctField.setSimpleName(newFieldName);
                    ctField.updateAllParentsBelow();
                    return ctField;
                }).map(Stream::of).orElseGet(Stream::empty);
    }

    private void addDefaultConstructorCallToGsonField(CtField<?> ctField) {
        if (ctField.getType().getQualifiedName().endsWith(".Gson")) {
            CtTypeReference gsonTypeReference = getFactory().createTypeReference();
            gsonTypeReference.setSimpleName("Gson");
            ctField.setDefaultExpression(getFactory().Code().createConstructorCall(gsonTypeReference));
        }
    }

    private void removeUnnecessaryAnnotationsInField(CtField<?> ctField) {
        List<CtAnnotation<?>> annotationsCopy = new ArrayList<>(ctField.getAnnotations());
        annotationsCopy.stream()
                .filter(ctAnnotation -> !(ctAnnotation.getAnnotationType().getSimpleName()
                        .equals(getFactory().Annotation().createReference(Mock.class).getSimpleName())
                        && !PLATFORM_TEST_CLASS_FIELDS.get().keySet().stream()
                        .map(CtTypeReference::getSimpleName).collect(Collectors.toList())
                        .contains(ctField.getType().getSimpleName())))
                .forEach(ctField::removeAnnotation);
    }

    private boolean filterForFieldsWIthMockitoAndSpringAnnotations(CtField<?> ctFieldRef) {
        return ctFieldRef.getAnnotations().stream()
                .map(CtAnnotation::getType)
                .anyMatch(FIELD_LEVEL_ANNOTATIONS.get()::contains) ||
                ctFieldRef.getAnnotations().stream().map(CtAnnotation::getAnnotationType)
                        .map(CtTypeReference::getSimpleName)
                        .anyMatch(FIELD_LEVEL_ANNOTATIONS.get()
                                .stream()
                                .map(CtTypeReference::getSimpleName)
                                .collect(Collectors.toList())::contains);
    }

    private void removeInnerClassForSpringTestConfiguration(CtClass<?> testClass) {
        Optional.ofNullable(testClass.<CtClass<?>>getNestedType("TestApplication"))
                .ifPresent(testClass::removeNestedType);
    }

    private void removeClassLevelSpringAnnotations(CtClass<?> testClass) {
        testClass.removeAnnotation(getClassLevelAnnotation(testClass, SpringBootTest.class));
        testClass.removeAnnotation(getClassLevelAnnotation(testClass, EnableAutoConfiguration.class));
    }

    private CtAnnotation<?> getClassLevelAnnotation(CtClass<?> testClass, Class<? extends Annotation> annotationClass) {
        return testClass.getAnnotation(getFactory().Annotation().createReference(annotationClass));
    }

    private void changeToMockitoPlatformSuperClass(CtClass<?> testClass) {
        testClass.setSuperclass(getFactory().Class().get(PlatformTest.class).getReference());
    }

    private boolean isSpringBootUnitTestClass(CtClass<?> testClass) {
        return testClass.getAnnotations().stream().map(CtAnnotation::getAnnotationType)
                .anyMatch(CLASS_LEVEL_ANNOTATIONS.get()::contains) ||
                testClass.getAnnotations().stream().map(CtAnnotation::getAnnotationType)
                        .map(CtTypeReference::getSimpleName)
                        .anyMatch(CLASS_LEVEL_ANNOTATIONS.get()
                                .stream()
                                .map(CtTypeReference::getSimpleName)
                                .collect(Collectors.toList())::contains);
    }
}
