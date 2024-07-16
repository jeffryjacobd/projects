package poc.operator.executor;

import com.google.gson.JsonPrimitive;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;
import poc.operator.XelerateJsonOperatorExecutor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class AbstractBinaryOperatorExecutor implements XelerateJsonOperatorExecutor {

    private enum NumberClassesEnum {
        BIG_DECIMAL(BigDecimal.class),
        BIG_INTEGER(BigInteger.class),
        DOUBLE(Double.class),
        LONG(Long.class),
        FLOAT(Float.class),
        INTEGER(Integer.class),
        SHORT(Short.class),
        BYTE(Byte.class);

        final Class<? extends Number> numberClass;

        NumberClassesEnum(Class<? extends Number> numberClass) {
            this.numberClass = numberClass;
        }
    }

    private static final Map<Class<? extends Number>, NumberClassesEnum> NUMBER_TYPE_ENUM_MAP;

    static {
        NUMBER_TYPE_ENUM_MAP = Arrays.stream(NumberClassesEnum.values()).map(numberClassesEnum ->
                        new AbstractMap.SimpleImmutableEntry<>(numberClassesEnum.numberClass, numberClassesEnum))
                .collect(Collectors.collectingAndThen(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                        Collections::unmodifiableMap));
    }

    @Override
    public Object operate(final Operation operation, final List<Object> operands) {
        if (((operands.size() != 2) && (!(operands.get(0) instanceof JsonPrimitiveWrapperForEqualityCheck)))) {
            throw new IllegalArgumentException();
        }
        try {
            final AtomicReference<NumberClassesEnum> resultNumberType = new AtomicReference<>();
            final BigDecimal bigDecimalResponse = tryNumberOperation(operands, resultNumberType);
            return convertBigDecimalToPrimitiveBasedOnReturn(bigDecimalResponse, resultNumberType.get());
        } catch (final NumberFormatException exception) {
            if (operation != Operation.ADD) {
                throw new EvaluationException("Unexpected input in the operator", exception);
            }
            return operateAsStringConcat(operands);
        }
    }

    private JsonPrimitive convertBigDecimalToPrimitiveBasedOnReturn(final BigDecimal bigDecimalResponse,
                                                                    final NumberClassesEnum responseType) {
        final Number returnResponse;
        switch (responseType) {
            case SHORT:
            case BYTE:
            case INTEGER: {
                returnResponse = bigDecimalResponse.intValue();
                break;
            }
            case LONG: {
                returnResponse = bigDecimalResponse.longValue();
                break;
            }
            case FLOAT: {
                returnResponse = bigDecimalResponse.floatValue();
                break;
            }
            case DOUBLE: {
                returnResponse = bigDecimalResponse.doubleValue();
                break;
            }
            case BIG_INTEGER: {
                returnResponse = bigDecimalResponse.toBigInteger();
                break;
            }
            case BIG_DECIMAL: {
                returnResponse = bigDecimalResponse;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return new JsonPrimitive(returnResponse);
    }

    private JsonPrimitive operateAsStringConcat(List<Object> operands) {
        return operands.stream()
                .map(this::convertOperandToPrimitive)
                .map(JsonPrimitive::getAsString)
                .collect(
                        Collectors.collectingAndThen(Collectors.joining(),
                                JsonPrimitive::new));
    }

    private BigDecimal tryNumberOperation(final List<Object> operands,
                                          final AtomicReference<NumberClassesEnum> resultNumberType) {
        return operands.stream().map(this::convertOperandToPrimitive)
                .map(this::convertStringToNumber)
                .map(JsonPrimitive::getAsNumber)
                .map(number ->
                        new AbstractMap.SimpleImmutableEntry<>(number,
                                NUMBER_TYPE_ENUM_MAP.get(number.getClass())))
                .sorted(Map.Entry.comparingByValue())
                .map(numberTypeEntry -> {
                    resultNumberType.compareAndSet(null, numberTypeEntry.getValue());
                    return numberTypeEntry.getKey();
                }).map(this::convertNumberToBigDecimal)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        collectedBigDecimalList -> {
                            if (collectedBigDecimalList.size() != 2) {
                                throw new EvaluationException("Unexpected input in the operator");
                            }
                            return operate(collectedBigDecimalList.get(0), collectedBigDecimalList.get(1));
                        }));
    }

    private BigDecimal convertNumberToBigDecimal(final Number number) {
        if (number instanceof Integer
                || number instanceof Long
                || number instanceof Short
                || number instanceof Byte) {
            return BigDecimal.valueOf(number.longValue());
        } else if (number instanceof Float || number instanceof Double) {
            return BigDecimal.valueOf(number.doubleValue());
        } else if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        } else if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else {
            return new BigDecimal(number.toString());
        }
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


    public abstract BigDecimal operate(final BigDecimal leftArgument,
                                       final BigDecimal rightArgument);
}

