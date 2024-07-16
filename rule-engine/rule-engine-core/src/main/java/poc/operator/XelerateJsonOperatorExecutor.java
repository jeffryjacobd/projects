package poc.operator;

import org.springframework.expression.Operation;

import java.util.List;

public interface XelerateJsonOperatorExecutor {
    Operation getOperation();

    List<List<Class<?>>> getAllowedOperandTypes();

    Object operate(Operation operation, List<Object> operands);
}
