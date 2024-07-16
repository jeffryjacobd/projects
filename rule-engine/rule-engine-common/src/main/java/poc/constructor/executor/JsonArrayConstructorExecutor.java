package poc.constructor.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import com.google.gson.JsonElement;

public class JsonArrayConstructorExecutor implements ConstructorExecutor {

	private final boolean arrayCopy;

	public JsonArrayConstructorExecutor() {
		this(false);
	}

	public JsonArrayConstructorExecutor(final Boolean isArrayCopy) {
		this.arrayCopy = isArrayCopy;
	}

	@Override
	public TypedValue execute(final EvaluationContext context, Object... arguments) throws AccessException {
		if (Objects.isNull(arguments) || arguments.length == 0) {
			return new TypedValue(new ArrayList<>(),
					TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(JsonElement.class)));
		} else if (arrayCopy) {
			final List<JsonElement> constructedElementList = Arrays.stream(arguments)
					.filter(JsonElement.class::isInstance).map(JsonElement.class::cast).collect(Collectors.toList());
			if (arguments.length == constructedElementList.size()) {
				return new TypedValue(constructedElementList,
						TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(JsonElement.class)));
			} else {
				throw new AccessException(
						"For Json array creation with variable args all are supposed to be JsonElement");
			}
		} else {
			throw new AccessException("No argument is expected for Json array creation");
		}
	}

}