package poc.constructor.executor;

import com.google.gson.JsonPrimitive;

public final class BooleanJsonPrimitiveConstructorExecutor extends AbstractJsonPrimitiveConstructorExecutor<Boolean> {

    public BooleanJsonPrimitiveConstructorExecutor() {
        super(Boolean.class);
    }

    @Override
    public JsonPrimitive getValue(final Boolean value) {
        return new JsonPrimitive(value);
    }

}