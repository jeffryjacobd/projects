package poc.method.generation;

import com.google.gson.JsonPrimitive;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;
import poc.method.XelerateJavaMethodExecutor;

public abstract class AbstractGeneratedMethodCodeExecutor implements XelerateJavaMethodExecutor {

    protected Object convertToTargetIfPrimitive(final Object input) {
        if (input instanceof JsonPrimitiveWrapperForEqualityCheck) {
            final JsonPrimitive inputPrimitive = ((JsonPrimitiveWrapperForEqualityCheck) input).getElement();
            return unwrapJsonPrimitive(inputPrimitive);
        } else if (input instanceof JsonPrimitive) {
            return unwrapJsonPrimitive((JsonPrimitive) input);
        }
        return input;
    }

    protected JsonPrimitive convertToJsonPrimitiveIfSimpleType(final Object input) {
        if (input instanceof String) {
            return new JsonPrimitive((String) input);
        } else if (input instanceof Number) {
            return new JsonPrimitive((Number) input);
        } else if (input instanceof Boolean) {
            return new JsonPrimitive((Boolean) input);
        }
        throw new IllegalStateException("Mapping not yet compatible for " + input.getClass().getSimpleName());
    }

    private Object unwrapJsonPrimitive(final JsonPrimitive inputPrimitive) {
        if (inputPrimitive.isString()) {
            return inputPrimitive.getAsString();
        } else if (inputPrimitive.isNumber()) {
            return inputPrimitive.getAsNumber();
        } else {
            return inputPrimitive.getAsBoolean();
        }
    }
}
