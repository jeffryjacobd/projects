package poc.comparator;

import java.util.List;
import java.util.Map;

public interface XelerateJsonComparatorExecutor {

    Map.Entry<List<Class<?>>, List<Class<?>>> getAllowedOperandTypes();

    int compare(final List<Object> arguments) throws IllegalArgumentException;

    default Integer getExecutionOrder() {
        return 0;
    }
}
