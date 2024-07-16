package poc.constructor;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import poc.constructor.executor.BooleanJsonPrimitiveConstructorExecutor;
import poc.constructor.executor.CopyConstructorConstructorExecutor;
import poc.constructor.executor.JsonArrayConstructorExecutor;
import poc.constructor.executor.JsonObjectConstructorExecutor;
import poc.constructor.executor.NumberJsonPrimitiveConstructorExecutor;
import poc.constructor.executor.StringJsonPrimitiveConstructorExecutor;

public final class JsonConstructorResolver implements ConstructorResolver {

	public static final Set<String> JSON_OBJECT_TYPE_NAMES;

	static {
		final Set<String> jsonObjectTypeNames = new HashSet<>();
		jsonObjectTypeNames.add(Object.class.getName().toUpperCase());
		jsonObjectTypeNames.add(Object.class.getSimpleName().toUpperCase());
		jsonObjectTypeNames.add(Map.class.getName().toUpperCase());
		jsonObjectTypeNames.add(Map.class.getSimpleName().toUpperCase());
		jsonObjectTypeNames.add(JsonObject.class.getName().toUpperCase());
		jsonObjectTypeNames.add(JsonObject.class.getSimpleName().toUpperCase());
		JSON_OBJECT_TYPE_NAMES = Collections.unmodifiableSet(jsonObjectTypeNames);
	}

	public static final Set<String> JSON_ARRAY_TYPE_NAMES;

	static {
		final Set<String> jsonArrayTypeNames = new HashSet<>();
		jsonArrayTypeNames.add(Array.class.getSimpleName().toUpperCase());
		jsonArrayTypeNames.add(List.class.getName().toUpperCase());
		jsonArrayTypeNames.add(List.class.getSimpleName().toUpperCase());
		jsonArrayTypeNames.add(JsonArray.class.getName().toUpperCase());
		jsonArrayTypeNames.add(JsonArray.class.getSimpleName().toUpperCase());
		JSON_ARRAY_TYPE_NAMES = Collections.unmodifiableSet(jsonArrayTypeNames);
	}

	@Override
	public ConstructorExecutor resolve(final EvaluationContext context, final String typeName,
			final List<TypeDescriptor> argumentTypes) {
		if (argumentTypes.size() > 1) {
			if (argumentTypes.stream().allMatch(
					typeDescriptor -> typeDescriptor.isAssignableTo(TypeDescriptor.valueOf(JsonElement.class)))) {
				return new JsonArrayConstructorExecutor(true);
			}
		} else if (argumentTypes.size() == 1) {
			final TypeDescriptor possibleJsonPrimitiveTypeField = argumentTypes.get(0);
			if (possibleJsonPrimitiveTypeField.isAssignableTo(TypeDescriptor.valueOf(Boolean.class))) {
				return new BooleanJsonPrimitiveConstructorExecutor();
			} else if (possibleJsonPrimitiveTypeField.isAssignableTo(TypeDescriptor.valueOf(String.class))) {
				return new StringJsonPrimitiveConstructorExecutor();
			} else if (possibleJsonPrimitiveTypeField.isAssignableTo(TypeDescriptor.valueOf(Number.class))) {
				return new NumberJsonPrimitiveConstructorExecutor();
			} else if (possibleJsonPrimitiveTypeField.isAssignableTo(TypeDescriptor.valueOf(JsonElement.class))) {
				return new CopyConstructorConstructorExecutor();
			}
		} else {
			final String typeNameInUpperCase = typeName.trim().toUpperCase();
			if (JSON_OBJECT_TYPE_NAMES.contains(typeNameInUpperCase)) {
				return new JsonObjectConstructorExecutor();
			} else if (JSON_ARRAY_TYPE_NAMES.contains(typeNameInUpperCase)) {
				return new JsonArrayConstructorExecutor();
			}
		}
		return null;
	}

}
