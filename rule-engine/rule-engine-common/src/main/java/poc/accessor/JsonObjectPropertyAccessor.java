package poc.accessor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

@Component
public class JsonObjectPropertyAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{JsonObject.class, Map.class};
    }

    @Override
    public boolean canRead(final EvaluationContext context, final Object target, final String name) {
        if ((target instanceof JsonObject)) {
            return true;
        } else if (target instanceof Map) {
            final Map<?, ?> mapObject = (Map<?, ?>) target;
            if (mapObject.isEmpty()) {
                return true;
            }
            return mapObject.entrySet().stream()
                    .anyMatch(entry -> entry.getKey() instanceof String &&
                            entry.getValue() instanceof JsonElement);
        }
        return false;
    }

    @Override
    public TypedValue read(final EvaluationContext context, final Object target, final String name) {
        return Optional.ofNullable(target).map(JsonObject.class::cast)
                .map(targetJsonObject -> targetJsonObject.get(name)).map(this::boxJsonArrayOrPrimitive)
                .orElseGet(() -> new TypedValue(JsonNull.INSTANCE));
    }

    private TypedValue boxJsonArrayOrPrimitive(final JsonElement extractedElement) {
        if (extractedElement.isJsonArray()) {
            return new TypedValue(StreamSupport.stream(extractedElement.getAsJsonArray().spliterator(), false)
                    .collect(Collectors.toList()));
        } else if (extractedElement.isJsonPrimitive()) {
            return new TypedValue(new JsonPrimitiveWrapperForEqualityCheck(extractedElement.getAsJsonPrimitive()));
        }
        return new TypedValue(extractedElement);
    }

    @Override
    public void write(final EvaluationContext context, final Object target, final String name, final Object newValue)
            throws AccessException {
        if (newValue instanceof JsonElement) {
            Optional.of(target).map(JsonObject.class::cast)
                    .ifPresent(targetJsonObject -> targetJsonObject.add(name, (JsonElement) newValue));
        } else if (Objects.isNull(newValue)) {
            Optional.of(target).map(JsonObject.class::cast)
                    .ifPresent(targetJsonObject -> targetJsonObject.add(name, JsonNull.INSTANCE));
        } else if (isListOfJsonElements(newValue)) {
            @SuppressWarnings("unchecked") final List<JsonElement> elementList = (List<JsonElement>) newValue;
            Optional.of(target).map(JsonObject.class::cast).ifPresent(targetJsonObject -> targetJsonObject.add(name,
                    elementList.stream().collect(JsonArray::new, JsonArray::add, JsonArray::addAll)));
        } else {
            throw new AccessException(
                    newValue.getClass().getName() + " is not expected in property write, Use a type of Json Element");
        }
    }

    private boolean isListOfJsonElements(final Object newValue) {
        return TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(JsonElement.class))
                .isAssignableTo(Objects.requireNonNull(TypeDescriptor.forObject(newValue)));
    }

    @Override
    public boolean canWrite(final EvaluationContext context, final Object target, final String name) {
        return true;
    }
}
