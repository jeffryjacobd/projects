package poc.accessor;

import com.google.gson.JsonPrimitive;
import poc.comparator.XelerateJsonComparatorExecutor;
import poc.comparator.XelerateJsonTypeComparator;

import java.util.Objects;

public class JsonPrimitiveWrapperForEqualityCheck {

    private final JsonPrimitive element;

    public JsonPrimitive getElement() {
        return element;
    }

    public JsonPrimitiveWrapperForEqualityCheck(final JsonPrimitive element) {
        this.element = element;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) return true;
        final XelerateJsonTypeComparator comparator = new XelerateJsonTypeComparator();
        if (comparator.canCompare(this, otherObject)) {
            return comparator.compare(this, otherObject) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }
}
