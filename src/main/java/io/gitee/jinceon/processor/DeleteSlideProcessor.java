package io.gitee.jinceon.processor;

import com.aspose.slides.ISlide;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.SlideProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * #delete = #{ expression }
 * when expression evaluate result equals '#hide = true', the slide will be deleted
 */
@Order(1000)
@Slf4j
public class DeleteSlideProcessor implements SlideProcessor {
    private static final String PREFIX = "#delete=";
    @Override
    public boolean supports(String spel) {
        return spel.startsWith(PREFIX);
    }

    @Override
    public Object parseDirective(String spel, DataSource dataSource) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Object o = parser.parseExpression(spel.substring(PREFIX.length())).getValue(dataSource.getEvaluationContext());
        log.debug("spel:{}, value:{}", spel, o);
        return o;
    }

    @Override
    public void process(ISlide slide, Object context) {
        if(Boolean.TRUE.equals(context)){
            slide.remove();
        }
    }
}
