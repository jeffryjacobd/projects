package poc.constructor.executor;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import com.google.gson.JsonPrimitive;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;

public abstract class AbstractJsonPrimitiveConstructorExecutor<T extends Serializable> implements ConstructorExecutor {

    private final Class<T> genericClass;

    protected AbstractJsonPrimitiveConstructorExecutor(final Class<T> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public TypedValue execute(final EvaluationContext context, final Object... arguments) throws AccessException {
        return Optional.ofNullable(arguments).filter(argumentArray -> argumentArray.length == 1)
                .map(argumentArray -> argumentArray[0]).map(genericClass::cast).map(this::getValue)
                .map(JsonPrimitiveWrapperForEqualityCheck::new).map(TypedValue::new)
                .orElseThrow(() -> new AccessException("Invalid number of arguments for Primitive construction"));
    }

    abstract JsonPrimitive getValue(final T value);

}
