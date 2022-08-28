package io.gitee.jinceon.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELTest {
    @Test
    void expression() {
        SpelExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("name", "jinceon");
        Object name1 = parser.parseExpression("#name").getValue(context);
        Object name2 = parser.parseExpression(" #name ").getValue(context);
        Assertions.assertEquals(name1, name2, "space with be ignored");
        Object name3 = parser.parseExpression("#{#name}", new TemplateParserContext()).getValue(context);
        Object name4 = parser.parseExpression(" #{#name} ", new TemplateParserContext()).getValue(context);
        Assertions.assertNotEquals(name3, name4, "space in template will be reserved");

        // must use TemplateParserContext when mixed text with variables
        Assertions.assertThrows(Exception.class, ()->parser.parseExpression("a #name ").getValue(context));
    }

}
