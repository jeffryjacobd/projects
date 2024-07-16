package poc.context;

import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import org.springframework.expression.*;
import poc.accessor.JsonObjectPropertyAccessor;
import poc.comparator.XelerateJsonTypeComparator;
import poc.constructor.JsonConstructorResolver;
import poc.method.XelerateJavaMethodResolver;
import poc.operator.XelerateJsonOperatorOverloader;
import poc.type.JsonTypeConverter;
import poc.type.JsonTypeLocator;

public class XelerateRuleContext implements EvaluationContext {

    private final JsonObject rootObject;

    private static final List<ConstructorResolver> CONSTRUCTOR_RESOLVERS;
    private static final List<PropertyAccessor> PROPERTY_ACCESSORS;
    private static final List<MethodResolver> METHOD_RESOLVERS;
    private static final JsonTypeConverter JSON_TYPE_CONVERTER;
    private static final XelerateJsonTypeComparator TYPE_COMPARATOR;
    private static final JsonTypeLocator TYPE_LOCATOR;
    private static final XelerateJsonOperatorOverloader OPERATOR_OVERLOAD;

    public XelerateRuleContext() {
        this.rootObject = new JsonObject();
    }

    public XelerateRuleContext(final JsonObject rootObject) {
        this.rootObject = rootObject;
    }

    static {
        PROPERTY_ACCESSORS = Collections.singletonList(new JsonObjectPropertyAccessor());
        CONSTRUCTOR_RESOLVERS = Collections.singletonList(new JsonConstructorResolver());
        METHOD_RESOLVERS = Collections.singletonList(new XelerateJavaMethodResolver());
        JSON_TYPE_CONVERTER = new JsonTypeConverter();
        TYPE_COMPARATOR = new XelerateJsonTypeComparator();
        TYPE_LOCATOR = new JsonTypeLocator();
        OPERATOR_OVERLOAD = new XelerateJsonOperatorOverloader();
    }

    @Override
    public TypedValue getRootObject() {
        return new TypedValue(rootObject);
    }

    @Override
    public List<PropertyAccessor> getPropertyAccessors() {
        return PROPERTY_ACCESSORS;
    }

    @Override
    public List<ConstructorResolver> getConstructorResolvers() {
        return CONSTRUCTOR_RESOLVERS;
    }

    @Override
    public List<MethodResolver> getMethodResolvers() {
        return METHOD_RESOLVERS;
    }

    @Override
    public BeanResolver getBeanResolver() {
        return null;
    }

    @Override
    public TypeLocator getTypeLocator() {
        return TYPE_LOCATOR;
    }

    @Override
    public TypeConverter getTypeConverter() {
        return JSON_TYPE_CONVERTER;
    }

    @Override
    public TypeComparator getTypeComparator() {
        return TYPE_COMPARATOR;
    }

    @Override
    public OperatorOverloader getOperatorOverloader() {
        return OPERATOR_OVERLOAD;
    }

    @Override
    public void setVariable(String name, Object value) {

    }

    @Override
    public Object lookupVariable(String name) {
        return rootObject.get(name);
    }
}
