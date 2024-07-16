package poc.type;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;
import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;

public class JsonTypeConverter implements TypeConverter {
    @Override
    public boolean canConvert(final TypeDescriptor sourceType,
                              final TypeDescriptor targetType) {
        System.out.println("JsonTypeConverter.canConvert called with " + sourceType + " " + targetType);
        return sourceType.isAssignableTo(TypeDescriptor.valueOf(JsonPrimitiveWrapperForEqualityCheck.class)) &&
                targetType.isAssignableTo(TypeDescriptor.valueOf(String.class));
    }

    @Override
    public Object convertValue(final Object value,
                               final TypeDescriptor sourceType,
                               final TypeDescriptor targetType) {
        System.out.println("JsonTypeConverter.convertValue called with " +
                sourceType + " " + targetType + " " + value);
        if (sourceType.isAssignableTo(TypeDescriptor.valueOf(JsonPrimitiveWrapperForEqualityCheck.class)) &&
                targetType.isAssignableTo(TypeDescriptor.valueOf(String.class)) && value instanceof JsonPrimitiveWrapperForEqualityCheck) {
            return ((JsonPrimitiveWrapperForEqualityCheck) value).getElement().getAsString();
        }
        return value;
    }
}
