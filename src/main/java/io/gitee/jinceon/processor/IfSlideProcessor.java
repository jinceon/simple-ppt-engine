package io.gitee.jinceon.processor;

import com.aspose.slides.ISlide;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.SlideProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * #if =  expression
 * when expression evaluate result equal false, slide will be removed.
 */
@Order(1000)
@Slf4j
public class IfSlideProcessor implements SlideProcessor {
    private static final String DIRECTIVE = "#if";
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
    public void process(ISlide slide, Object context) {
        if(!Boolean.TRUE.equals(context)){
            slide.remove();
        }
    }
}
