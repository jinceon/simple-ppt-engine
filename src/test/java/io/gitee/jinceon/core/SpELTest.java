package io.gitee.jinceon.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELTest {

    SpelExpressionParser parser = new SpelExpressionParser();
    private static final EvaluationContext context = new StandardEvaluationContext();
    private final TemplateParserContext templateParserContext = new TemplateParserContext();

    @BeforeAll
    static void beforeAll(){
        context.setVariable("name", "jinceon");
    }
    @Test
    void expression1() {
        Object name1 = parser.parseExpression("#name").getValue(context);
        Object name2 = parser.parseExpression(" #name ").getValue(context);
        // name1 = "jinceon", name2 = "jinceon"
        Assertions.assertEquals("jinceon", name1);
        Assertions.assertEquals(name1, name2, "space with be ignored");
    }

    @Test
    void expression2(){
        Object name3 = parser.parseExpression("#{#name}", templateParserContext).getValue(context);
        Object name4 = parser.parseExpression(" #{#name} ", templateParserContext).getValue(context);
        // name3 = "jinceon", name4 = " jinceon"
        Assertions.assertEquals("jinceon", name3);
        Assertions.assertEquals(" jinceon ", name4, "space in template will be reserved");
    }

    @Test
    void expression3(){
        // must use TemplateParserContext when mixed text with variables
        Assertions.assertThrows(Exception.class,
                ()->parser.parseExpression("a #name ").getValue(context),
                "throws exception when mixed text with variables");
    }
    
    @Test
    void expression4(){
        // #variable without #{} will be ignored in TemplateParserContext
        Object name5 = parser.parseExpression("#name", templateParserContext).getValue(context);
        Assertions.assertEquals("#name", name5);
    }
    
    @Test
    void expression5(){
        Object value = parser.parseExpression("#if = #{#name=='jinceon'}", templateParserContext).getValue(context);
        Assertions.assertEquals("#if = true", value);
    }
    @Test
    void expression6(){
        Object value = parser.parseExpression("#if = #{#name!='jinceon'}", templateParserContext).getValue(context);
        Assertions.assertEquals("#if = false", value);
    }
}
