package poc.comparator;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeComparator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class XelerateJsonTypeComparator implements TypeComparator {

    private static final List<XelerateJsonComparatorExecutor> TYPE_COMPARATORS;

    static {
        TYPE_COMPARATORS = StreamSupport.stream(ServiceLoader.load(XelerateJsonComparatorExecutor.class)
                                .spliterator(),
                        false)
                .sorted(Comparator.comparing(XelerateJsonComparatorExecutor::getExecutionOrder))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public boolean canCompare(Object firstObject, Object secondObject) {
        return TYPE_COMPARATORS.stream()
                .anyMatch(typeExecutor -> typeExecutor.getAllowedOperandTypes().getKey().stream()
                        .anyMatch(allowedOperandClass -> allowedOperandClass.isInstance(firstObject)) &&
                        typeExecutor.getAllowedOperandTypes().getValue().stream()
                                .anyMatch(allowedOperandClass -> allowedOperandClass.isInstance(secondObject)));
    }

    @Override
    public int compare(Object firstObject, Object secondObject) throws EvaluationException {
        return TYPE_COMPARATORS.stream().filter(typeExecutor -> typeExecutor.getAllowedOperandTypes().getKey().stream()
                        .anyMatch(allowedOperandClass -> allowedOperandClass.isInstance(firstObject)) &&
                        typeExecutor.getAllowedOperandTypes().getValue().stream()
                                .anyMatch(allowedOperandClass -> allowedOperandClass.isInstance(secondObject)))
                .map(typeExecutor -> {
                    try {
                        return typeExecutor.compare(Arrays.asList(firstObject, secondObject));
                    } catch (final IllegalArgumentException argumentException) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new EvaluationException("No Comparator found"));
    }
}
