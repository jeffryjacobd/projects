package poc.comparator;

import com.google.auto.service.AutoService;
import com.google.gson.JsonPrimitive;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;

import java.util.*;
import java.util.stream.Collectors;

@AutoService(XelerateJsonComparatorExecutor.class)
public class XelerateStringComparatorExecutor implements XelerateJsonComparatorExecutor {

    @Override
    public Integer getExecutionOrder() {
        return 2;
    }

    @Override
    public Map.Entry<List<Class<?>>, List<Class<?>>> getAllowedOperandTypes() {
        return new AbstractMap.SimpleImmutableEntry<>(
                Collections.singletonList(JsonPrimitiveWrapperForEqualityCheck.class),
                Collections.unmodifiableList(Arrays.asList(String.class, JsonPrimitiveWrapperForEqualityCheck.class)));
    }

    @Override
    public int compare(final List<Object> arguments) {
        if (((arguments.size() != 2) && (!(arguments.get(0) instanceof JsonPrimitiveWrapperForEqualityCheck)))) {
            throw new IllegalArgumentException();
        }
        final List<String> stringArguments = arguments.stream().map(this::convertOperandToPrimitive)
                .map(JsonPrimitive::getAsString).collect(Collectors.toList());
        return stringArguments.get(0).compareTo(stringArguments.get(1));
    }

    private JsonPrimitive convertOperandToPrimitive(final Object operand) {
        if (operand instanceof JsonPrimitiveWrapperForEqualityCheck) {
            return ((JsonPrimitiveWrapperForEqualityCheck) operand).getElement();
        } else if (operand instanceof Number) {
            return new JsonPrimitive((Number) operand);
        } else if (operand instanceof String) {
            return new JsonPrimitive((String) operand);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
