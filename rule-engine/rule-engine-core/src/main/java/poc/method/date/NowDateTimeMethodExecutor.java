package poc.method.date;

import com.google.auto.service.AutoService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import poc.method.XelerateJavaMethodExecutor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@AutoService(XelerateJavaMethodExecutor.class)
public class NowDateTimeMethodExecutor implements XelerateJavaMethodExecutor {
    @Override
    public String getMethodName() {
        return "now";
    }

    @Override
    public boolean allowTypeReferencedInvocations() {
        return true;
    }

    @Override
    public TypedValue execute(final EvaluationContext context,
                              final Object target,
                              final Object... arguments) throws AccessException {
        if (Objects.isNull(target) || Arrays.stream(arguments).findAny().isPresent()) {
            throw new IllegalArgumentException();
        }
        return new TypedValue(LocalDateTime.now(),
                TypeDescriptor.valueOf(LocalDateTime.class));
    }
}
