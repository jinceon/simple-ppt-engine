package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.Processor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Order(90)
@Slf4j
public class TextProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return shape instanceof IAutoShape;
    }

    @Override
    public void process(IShape shape, DataSource dataSource) {
        ITextFrame textFrame;
        if(shape instanceof IAutoShape autoShape){
            textFrame = autoShape.getTextFrame();
        }else {
            textFrame = (ITextFrame) shape;
        }
        IParagraphCollection paragraphs = textFrame.getParagraphs();
        for (int i = 0; i < paragraphs.getCount(); i++) {
            IParagraph paragraph = paragraphs.get_Item(i);
            IPortionCollection portions = paragraph.getPortions();
            for (int j = 0; j < portions.getCount(); j++) {
                IPortion portion = portions.get_Item(j);
                String spel = portion.getText();
                log.debug("spel: {}", spel);
                SpelExpressionParser parser = new SpelExpressionParser();
                String text = String.valueOf(parser.parseExpression(spel, new TemplateParserContext()).getValue(dataSource.getEvaluationContext()));
                log.debug("spel: {}, text: {}", spel, text);
                portion.setText(text);
            }
        }
    }
}
