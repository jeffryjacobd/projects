package poc.operator.executor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonPrimitive;
import org.springframework.expression.Operation;
import poc.operator.XelerateJsonOperatorExecutor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoService(XelerateJsonOperatorExecutor.class)
public class NumberSubtractBinaryOperatorExecutor extends AbstractBinaryOperatorExecutor {
    @Override
    public Operation getOperation() {
        return Operation.SUBTRACT;
    }

    @Override
    public List<List<Class<?>>> getAllowedOperandTypes() {
        return Collections.unmodifiableList(
                Arrays.asList(
                        Collections.singletonList(JsonPrimitive.class),
                        Collections.unmodifiableList(Arrays.asList(Number.class, String.class, JsonPrimitive.class))));
    }

    @Override
    public BigDecimal operate(final BigDecimal leftArgument,
                              final BigDecimal rightArgument) {
        return leftArgument.subtract(rightArgument);
    }


}
