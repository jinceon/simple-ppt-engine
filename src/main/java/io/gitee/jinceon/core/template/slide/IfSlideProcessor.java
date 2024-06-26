package io.gitee.jinceon.core.template.slide;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
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
    public void process(XSLFSlide slide, Object context) {
        if(context == null || Boolean.FALSE.equals(context)){
            log.debug("#if=false remove slide `{}`", slide.getSlideNumber());
            SlideShow ppt = slide.getSlideShow();
            if(ppt instanceof XMLSlideShow){
                XMLSlideShow pptx = (XMLSlideShow) ppt;
                pptx.removeSlide(slide.getSlideNumber()-1);
            }else{
                throw new UnsupportedOperationException("only supports pptx");
            }
        }
    }
}
