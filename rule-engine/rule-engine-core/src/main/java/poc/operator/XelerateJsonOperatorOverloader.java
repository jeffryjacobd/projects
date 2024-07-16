package poc.operator;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class XelerateJsonOperatorOverloader implements OperatorOverloader {
    private static final Map<Operation, List<XelerateJsonOperatorExecutor>> ALLOWED_OPERATORS;

    static {
        ALLOWED_OPERATORS = StreamSupport.stream(ServiceLoader.load(XelerateJsonOperatorExecutor.class).spliterator(), false)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(XelerateJsonOperatorExecutor::getOperation,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        Collections::unmodifiableList)),
                        Collections::unmodifiableMap));
    }

    @Override
    public boolean overridesOperation(final Operation operation,
                                      final Object leftOperand,
                                      final Object rightOperand) throws EvaluationException {
        return getXelerateJsonOperatorExecutor(operation, leftOperand, rightOperand).isPresent();
    }

    private Optional<XelerateJsonOperatorExecutor> getXelerateJsonOperatorExecutor(final Operation operation,
                                                                                   final Object leftOperand,
                                                                                   final Object rightOperand) {
        return Optional.ofNullable(ALLOWED_OPERATORS.get(operation)).map(List::stream)
                .orElseGet(Stream::empty)
                .filter(xelerateJsonOperatorExecutor -> {
                    final List<Object> operandList = collectOperandsAsList(leftOperand, rightOperand);
                    if (operandList.size() != xelerateJsonOperatorExecutor.getAllowedOperandTypes().size()) {
                        return false;
                    }
                    final Iterator<List<Class<?>>> allowedOperandTypesIt = xelerateJsonOperatorExecutor.getAllowedOperandTypes().iterator();
                    for (final Object currentObject : operandList) {
                        if (allowedOperandTypesIt.next().stream().noneMatch(classType -> classType.isInstance(currentObject))) {
                            return false;
                        }
                    }
                    return true;
                }).findAny();
    }

    private List<Object> collectOperandsAsList(final Object leftOperand,
                                               final Object rightOperand) {
        return Stream.of(leftOperand, rightOperand)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Object operate(final Operation operation,
                          final Object leftOperand,
                          final Object rightOperand) throws EvaluationException {
        return getXelerateJsonOperatorExecutor(operation, leftOperand, rightOperand)
                .map(xelerateJsonOperatorExecutor ->
                        xelerateJsonOperatorExecutor.operate(operation, collectOperandsAsList(leftOperand, rightOperand)))
                .orElseThrow(IllegalArgumentException::new);
    }
}
