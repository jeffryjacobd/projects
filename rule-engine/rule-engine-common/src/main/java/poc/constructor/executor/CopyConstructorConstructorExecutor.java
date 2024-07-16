package poc.constructor.executor;

import java.util.Objects;

import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

public final class CopyConstructorConstructorExecutor implements ConstructorExecutor {

	@Override
	public TypedValue execute(final EvaluationContext context, final Object... arguments) throws AccessException {
		if (Objects.nonNull(arguments) && arguments.length == 1) {
			return new TypedValue(arguments[0]);
		}
		throw new AccessException("For copy constructor exactly one argument is expected");
	}

}
