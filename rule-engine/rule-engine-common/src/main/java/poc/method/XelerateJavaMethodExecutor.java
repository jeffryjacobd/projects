package poc.method;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;
import poc.type.JsonTypeLocator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface XelerateJavaMethodExecutor extends MethodExecutor {

    String getMethodName();

    default int getMinimumNumberOfAttributes() {
        return getFixedParameterTypes().size();
    }

    default int getMaximumNumberOfAttributes() {
        return getFixedParameterTypes().size() +
                getVariableParameterTypes().stream().mapToInt(List::size).max().orElse(0);
    }

    default List<Class<?>> getFixedParameterTypes() {
        return Collections.emptyList();
    }

    default List<List<Class<?>>> getVariableParameterTypes() {
        return Collections.emptyList();
    }

    default List<List<List<TypeDescriptor>>> getVariableParameters() {
        return getVariableParameterTypes().stream().map(innerParameterList -> innerParameterList.stream()
                        .map(XelerateJavaMethodExecutor::getTypeDescriptorsAndAppendJsonTypeDescriptor)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    default List<List<TypeDescriptor>> getFixedParameters() {
        return getFixedParameterTypes().stream()
                .map(XelerateJavaMethodExecutor::getTypeDescriptorsAndAppendJsonTypeDescriptor)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    static List<TypeDescriptor> getTypeDescriptorsAndAppendJsonTypeDescriptor(final Class<?> originalTypeParameterClass) {
        final Class<?> alternativeClass = JsonTypeLocator.TYPE_DECIDING_MAP.entrySet().stream()
                .filter(typeDecidingMapEntry -> typeDecidingMapEntry.getKey()
                        .test(originalTypeParameterClass.getName().toUpperCase()))
                .findFirst().orElseThrow(() -> new EvaluationException("Not an allowed type: " + originalTypeParameterClass.getSimpleName()))
                .getValue().get();
        return Collections.unmodifiableList(Arrays.asList(TypeDescriptor.valueOf(originalTypeParameterClass),
                TypeDescriptor.valueOf(alternativeClass)));
    }

    default TypeDescriptor getTargetObjectType() {
        return TypeDescriptor.valueOf(null);
    }

    default boolean allowTypeReferencedInvocations() {
        return false;
    }

}
