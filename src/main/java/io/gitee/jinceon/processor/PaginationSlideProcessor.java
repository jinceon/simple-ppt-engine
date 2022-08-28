package io.gitee.jinceon.processor;

import com.aspose.slides.ISlide;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.SlideProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * #pagination
 * #items
 * empty=hide|delete|anything else
 */
@Order(1900)
@Slf4j
public class PaginationSlideProcessor implements SlideProcessor {
    private static final String PREFIX = "#pagination=";

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
        System.out.println("dfdafads");
    }
}
