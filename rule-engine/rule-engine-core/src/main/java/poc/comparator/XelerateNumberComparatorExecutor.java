package poc.comparator;

import com.google.auto.service.AutoService;
import com.google.gson.JsonPrimitive;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(XelerateJsonComparatorExecutor.class)
public class XelerateNumberComparatorExecutor implements XelerateJsonComparatorExecutor {

    @Override
    public Integer getExecutionOrder() {
        return 1;
    }

    @Override
    public Map.Entry<List<Class<?>>, List<Class<?>>> getAllowedOperandTypes() {
        return new AbstractMap.SimpleImmutableEntry<>(
                Collections.singletonList(JsonPrimitiveWrapperForEqualityCheck.class),
                Collections.unmodifiableList(Arrays.asList(Number.class, String.class, JsonPrimitiveWrapperForEqualityCheck.class)));
    }

    @Override
    public int compare(final List<Object> arguments) {
        if (((arguments.size() != 2) && (!(
                arguments.get(0) instanceof JsonPrimitiveWrapperForEqualityCheck)))) {
            throw new IllegalArgumentException();
        }
        final List<BigDecimal> bigDecimalArguments = arguments.stream().map(this::convertOperandToPrimitive)
                .map(this::convertStringToNumber)
                .map(JsonPrimitive::getAsBigDecimal).collect(Collectors.toList());
        return bigDecimalArguments.get(0).compareTo(bigDecimalArguments.get(1));
    }

    private JsonPrimitive convertStringToNumber(final JsonPrimitive inputPrimitive) {
        if (inputPrimitive.isString()) {
            return new JsonPrimitive(new BigDecimal(inputPrimitive.getAsString()));
        }
        return inputPrimitive;
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
