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
public class NowDateTimeTypeReferencedMethodExecutor extends NowDateTimeMethodExecutor {
    @Override
    public TypeDescriptor getTargetObjectType() {
        return TypeDescriptor.valueOf(LocalDateTime.class);
    }

}
