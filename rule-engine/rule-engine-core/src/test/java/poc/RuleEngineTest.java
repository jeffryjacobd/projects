package poc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.testng.Assert;
import org.testng.annotations.Test;
import poc.context.XelerateRuleContext;

import java.time.LocalDateTime;

public class RuleEngineTest {
    @Test
    public void springExpressionStringSubstitution() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression exp = expressionParser.parseExpression("'select ' + a + ', ' + b + ' from ' + c + ' where a= ' +in.a");
        final JsonObject obj = new Gson().fromJson("{\"a\":\"one\",\"b\":\"two\",\"c\":\"three\",\"in\":{\"a\":\"1\"}}", JsonObject.class);
        Assert.assertEquals(exp.getValue(new XelerateRuleContext(), obj, String.class),
                "select one, two from three where a= 1");
    }

    @Test
    public void springExpressionStringSubstitutionWithSelectionGreater() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression exp = expressionParser.parseExpression("'select ' + a + ', ' + b + ' from ' + c + ' where a= ' + in.^[a > 1].b");
        final JsonObject obj = new Gson().fromJson("{\"a\":\"one\",\"b\":\"two\",\"c\":\"three\",\"in\":[{\"a\":1,\"b\":\"one\"},{\"a\":2,\"b\":\"two\"}]}", JsonObject.class);
        Assert.assertEquals(exp.getValue(new XelerateRuleContext(), obj, String.class),
                "select one, two from three where a= two");
    }

    @Test
    public void springExpressionStringSubstitutionWithSelectionEquals() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression exp = expressionParser.parseExpression("'select ' + a + ', ' + b + ' from ' + c + ' where a= ' + in.^[a == 1].b");
        final JsonObject obj = new Gson().fromJson("{\"a\":\"one\",\"b\":\"two\",\"c\":\"three\",\"in\":[{\"a\":1,\"b\":\"one\"},{\"a\":2,\"b\":\"two\"}]}", JsonObject.class);
        Assert.assertEquals(exp.getValue(new XelerateRuleContext(), obj, String.class),
                "select one, two from three where a= one");
    }

    @Test
    public void springExpressionTypeReferencedNowMethodCall() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression exp = expressionParser.parseExpression("T(Date).now()");
        Assert.assertEquals(exp.getValue(new XelerateRuleContext(), LocalDateTime.class).toLocalDate(),
                LocalDateTime.now().toLocalDate());
    }

    @Test
    public void springExpressionNowMethodCall() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression exp = expressionParser.parseExpression("now()");
        Assert.assertEquals(exp.getValue(new XelerateRuleContext(), LocalDateTime.class).toLocalDate(),
                LocalDateTime.now().toLocalDate());
    }

    @Test
    public void springExpressionAbbreviateMethodCall() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression exp = expressionParser.parseExpression("abbreviate('abcdefgggg',4)");
        Assert.assertEquals(exp.getValue(new XelerateRuleContext(), JsonPrimitive.class).getAsString(),
                "a...");
    }
}
