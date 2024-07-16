package poc.operator.executor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonPrimitive;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;
import poc.operator.XelerateJsonOperatorExecutor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@AutoService(XelerateJsonOperatorExecutor.class)
public class NumberSubUnaryOperatorExecutor implements XelerateJsonOperatorExecutor {
    @Override
    public Operation getOperation() {
        return Operation.SUBTRACT;
    }

    @Override
    public List<List<Class<?>>> getAllowedOperandTypes() {
        return Collections.singletonList(Collections.singletonList(JsonPrimitiveWrapperForEqualityCheck.class));
    }

    @Override
    public Object operate(final Operation operation, final List<Object> operands) {
        if (((operands.size() != 1) && (!(operands.get(0) instanceof JsonPrimitiveWrapperForEqualityCheck)))) {
            throw new IllegalArgumentException();
        }
        final JsonPrimitive primitiveOperand = ((JsonPrimitiveWrapperForEqualityCheck) operands.get(0)).getElement();
        if (primitiveOperand.isNumber()) {
            return operateOnNumber(primitiveOperand);
        } else if (primitiveOperand.isBoolean()) {
            return new JsonPrimitive(!primitiveOperand.getAsBoolean());
        } else {
            final String stringPrimitive = primitiveOperand.getAsString();
            try {
                final BigDecimal decimal = new BigDecimal(stringPrimitive);
                return new JsonPrimitive(decimal.negate().toPlainString());
            } catch (final NumberFormatException numberFormatException) {
                throw new EvaluationException("Unary subtract can work only on Number, boolean types and on Strings which are numbers", numberFormatException);
            }
        }
    }

    private JsonPrimitive operateOnNumber(final JsonPrimitive primitiveOperand) {
        if (isIntegral(primitiveOperand)) {
            final BigInteger integerOperand = primitiveOperand.getAsBigInteger();
            return new JsonPrimitive(integerOperand.negate());
        } else {
            final BigDecimal decimalOperand = primitiveOperand.getAsBigDecimal();
            return new JsonPrimitive(decimalOperand.negate());
        }
    }

    private boolean isIntegral(final JsonPrimitive primitive) {
        final Number number = primitive.getAsNumber();
        return number instanceof BigInteger || number instanceof Long || number instanceof Integer
                || number instanceof Short || number instanceof Byte;

    }
}
