package poc.constructor.executor;

import java.util.Objects;

import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import com.google.gson.JsonObject;

public class JsonObjectConstructorExecutor implements ConstructorExecutor {

	@Override
	public TypedValue execute(final EvaluationContext context, final Object... arguments) throws AccessException {
		if (Objects.isNull(arguments) || arguments.length == 0) {
			return new TypedValue(new JsonObject());
		}
		throw new AccessException("No argument is expected for Json object creation");
	}

}
