package poc.constructor.executor;

import com.google.gson.JsonPrimitive;

public final class StringJsonPrimitiveConstructorExecutor extends AbstractJsonPrimitiveConstructorExecutor<String> {

    public StringJsonPrimitiveConstructorExecutor() {
        super(String.class);
    }

    @Override
    public JsonPrimitive getValue(final String value) {
        return new JsonPrimitive(value);
    }

}