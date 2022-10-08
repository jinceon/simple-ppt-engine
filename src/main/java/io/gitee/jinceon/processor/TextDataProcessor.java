package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.DataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.List;

@Order(90)
@Slf4j
public class TextDataProcessor implements DataProcessor {
    @Override
    public boolean supports(XSLFShape shape) {
        if(!(shape instanceof XSLFAutoShape)){
            return false;
        }

        String text = ((XSLFAutoShape)shape).getText();
        return StringUtils.hasText(text) && text.contains("#{");
    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
        XSLFAutoShape textFrame = (XSLFAutoShape) shape;
        List<XSLFTextParagraph> paragraphs = textFrame.getTextParagraphs();
        for (int i = 0; i < paragraphs.size(); i++) {
            XSLFTextParagraph paragraph = paragraphs.get(i);
            List<XSLFTextRun> portions = paragraph.getTextRuns();
            for (int j = 0; j < portions.size(); j++) {
                XSLFTextRun portion = portions.get(j);
                String spel = portion.getRawText();
                log.debug("spel: {}", spel);
                SpelExpressionParser parser = new SpelExpressionParser();
                String text = String.valueOf(parser.parseExpression(spel, new TemplateParserContext()).getValue(dataSource.getEvaluationContext()));
                log.debug("spel: {}, text: {}", spel, text);
                portion.setText(text);
            }
        }
    }
}
