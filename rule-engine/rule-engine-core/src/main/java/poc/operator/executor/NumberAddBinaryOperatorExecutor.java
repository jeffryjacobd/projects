package poc.operator.executor;

import com.google.auto.service.AutoService;
import org.springframework.expression.Operation;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;
import poc.operator.XelerateJsonOperatorExecutor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoService(XelerateJsonOperatorExecutor.class)
public class NumberAddBinaryOperatorExecutor extends AbstractBinaryOperatorExecutor {
    @Override
    public Operation getOperation() {
        return Operation.ADD;
    }

    @Override
    public List<List<Class<?>>> getAllowedOperandTypes() {
        return Collections.unmodifiableList(
                Arrays.asList(
                        Collections.singletonList(JsonPrimitiveWrapperForEqualityCheck.class),
                        Collections.unmodifiableList(Arrays.asList(Number.class, String.class, JsonPrimitiveWrapperForEqualityCheck.class))));
    }

    @Override
    public BigDecimal operate(final BigDecimal leftArgument,
                              final BigDecimal rightArgument) {
        return leftArgument.add(rightArgument);
    }


}
