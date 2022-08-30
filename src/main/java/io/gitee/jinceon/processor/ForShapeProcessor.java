package io.gitee.jinceon.processor;

import com.aspose.slides.IShape;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.ShapeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * #for = items
 * when `items` is null or empty array (list), slide will be removed
 */
@Order(1000)
@Slf4j
public class ForShapeProcessor implements ShapeProcessor {
    private static final String DIRECTIVE = "#for";
    @Override
    public boolean supports(String directive) {
        return directive.equals(DIRECTIVE);
    }

    @Override
    public Object parseDirective(String expression, DataSource dataSource) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Object o = parser.parseExpression(expression).getValue(dataSource.getEvaluationContext());
        log.debug("expression:{}, value:{}", expression, o);
        return o;
    }

    @Override
    public void process(IShape shape, Object context) {
        if(!Boolean.TRUE.equals(context)){
            shape.setHidden(true);
        }
    }
}
