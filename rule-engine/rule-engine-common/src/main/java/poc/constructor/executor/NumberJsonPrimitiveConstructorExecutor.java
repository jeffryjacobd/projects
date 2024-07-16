package poc.constructor.executor;

import com.google.gson.JsonPrimitive;

public final class NumberJsonPrimitiveConstructorExecutor extends AbstractJsonPrimitiveConstructorExecutor<Number> {

	public NumberJsonPrimitiveConstructorExecutor() {
		super(Number.class);
	}

	@Override
	public JsonPrimitive getValue(final Number value) {
		return new JsonPrimitive(value);
	}

}
