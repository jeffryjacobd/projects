package poc.operator.executor;

import com.google.auto.service.AutoService;
import org.springframework.expression.Operation;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;
import poc.operator.XelerateJsonOperatorExecutor;

import java.util.Collections;
import java.util.List;

@AutoService(XelerateJsonOperatorExecutor.class)
public class NumberAddUnaryOperatorExecutor implements XelerateJsonOperatorExecutor {
    @Override
    public Operation getOperation() {
        return Operation.ADD;
    }

    @Override
    public List<List<Class<?>>> getAllowedOperandTypes() {
        return Collections.singletonList(Collections.singletonList(JsonPrimitiveWrapperForEqualityCheck.class));
    }

    @Override
    public Object operate(final Operation operation, List<Object> operands) {
        if (((operands.size() != 1) && (!(operands.get(0) instanceof JsonPrimitiveWrapperForEqualityCheck)))) {
            throw new IllegalArgumentException();
        }
        return operands.get(0); // NO OP for unary plus operator, but operator support is needed
    }
}
