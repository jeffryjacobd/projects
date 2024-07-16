package poc.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeLocator;
import org.springframework.stereotype.Component;

import poc.accessor.JsonPrimitiveWrapperForEqualityCheck;

import static poc.constructor.JsonConstructorResolver.JSON_ARRAY_TYPE_NAMES;
import static poc.constructor.JsonConstructorResolver.JSON_OBJECT_TYPE_NAMES;

@Component
public final class JsonTypeLocator implements TypeLocator {

    private static final Set<String> JSON_PRIMITIVE_TYPE;

    static {
        final Set<String> mutableJsonPrimitiveType = new HashSet<>();
        mutableJsonPrimitiveType.add(Integer.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(Long.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(Boolean.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(BigDecimal.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(BigInteger.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(Double.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(Float.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(String.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(int.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(float.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(long.class.getName().toUpperCase());
        mutableJsonPrimitiveType.add(Integer.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(Long.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(Boolean.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(BigDecimal.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(BigInteger.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(Double.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(Float.class.getSimpleName().toUpperCase());
        mutableJsonPrimitiveType.add(String.class.getSimpleName().toUpperCase());
        JSON_PRIMITIVE_TYPE = Collections.unmodifiableSet(mutableJsonPrimitiveType);
    }

    private static final Set<String> JSON_DATE_TYPE;

    static {
        final Set<String> mutableDateType = new HashSet<>();
        mutableDateType.add(Date.class.getSimpleName().toUpperCase());
        mutableDateType.add(Date.class.getName().toUpperCase());
        mutableDateType.add(LocalDate.class.getSimpleName().toUpperCase());
        mutableDateType.add(LocalDate.class.getName().toUpperCase());
        mutableDateType.add(LocalDateTime.class.getSimpleName().toUpperCase());
        mutableDateType.add(LocalDateTime.class.getName().toUpperCase());
        JSON_DATE_TYPE = Collections.unmodifiableSet(mutableDateType);
    }

    public static final Map<Predicate<String>, Supplier<Class<?>>> TYPE_DECIDING_MAP;

    static {
        final Map<Predicate<String>, Supplier<Class<?>>> typeDecidingMapMutable = new LinkedHashMap<>();
        typeDecidingMapMutable.put(JSON_PRIMITIVE_TYPE::contains, () -> JsonPrimitiveWrapperForEqualityCheck.class);
        typeDecidingMapMutable.put(JSON_ARRAY_TYPE_NAMES::contains, () -> JsonArray.class);
        typeDecidingMapMutable.put(JSON_OBJECT_TYPE_NAMES::contains, () -> JsonObject.class);
        typeDecidingMapMutable.put(JSON_DATE_TYPE::contains, () -> LocalDateTime.class);
        TYPE_DECIDING_MAP = Collections.unmodifiableMap(typeDecidingMapMutable);
    }

    @Override
    public Class<?> findType(final String typeName) throws EvaluationException {
        System.out.println("JsonTypeLocator.findType() input: " + typeName);
        final String upperCasedTypeName = typeName.trim().toUpperCase();
        return TYPE_DECIDING_MAP.entrySet().stream()
                .filter(predicateSupplierEntry -> predicateSupplierEntry.getKey()
                        .test(upperCasedTypeName))
                .map(Map.Entry::getValue)
                .findFirst().map(Supplier::get)
                .orElseThrow(() -> new EvaluationException(typeName + " is not supported yet"));
    }

}
