package org.apache.poi.xslf.usermodel;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.model.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import java.util.List;

@Slf4j
public class TextHelper {
    /**
     * 直接将textShape的文本清除掉，并更新为text
     * 样式采用第一个paragraph下的第一个textrun的。
     * @param textShape
     * @param text
     */
    public static void setText(XSLFTextShape textShape, String text){
        List<XSLFTextParagraph> paragraphs = textShape.getTextParagraphs();
        if (paragraphs.isEmpty()) {
            textShape.setText(text);
        }else{
            int size = paragraphs.size();
            for(int i=size;i>1;i--){
                textShape.removeTextParagraph(paragraphs.get(i-1));
            }
            XSLFTextParagraph paragraph = paragraphs.get(0);
            List<XSLFTextRun> textRuns = paragraph.getTextRuns();
            int textRunSize = textRuns.size();
            if(textRunSize > 1) {
                for (int i = textRunSize; i > 1; i--) {
                    paragraph.removeTextRun(textRuns.get(i - 1));
                }
                textRuns.get(0).setText(text);
            }else {
                textShape.setText(text);
            }
        }
    }

    public static void renderText(XSLFTextShape textShape, DataSource dataSource){
        String shapeSpel = ShapeHelper.getAlternativeText(textShape);
        Object rootObject = null;
        SpelExpressionParser parser = new SpelExpressionParser();
        if(shapeSpel.contains("#")){
            // #user，把 #user 作为 rootObject
            rootObject = parser.parseExpression(shapeSpel).getValue(dataSource.getEvaluationContext());
        }
        List<XSLFTextParagraph> paragraphs = textShape.getTextParagraphs();
        for (XSLFTextParagraph paragraph : paragraphs) {
            List<XSLFTextRun> portions = paragraph.getTextRuns();
            XSLFTextRun complete = null;
            for (XSLFTextRun portion : portions) {
                String spel = portion.getRawText();
                log.debug("spel: {}", spel);
                if(spel.contains("#{") && spel.contains("}")){
                    complete = portion;
                }else if(spel.contains("#{")){
                    complete = portion;
                    continue;
                }else if(complete != null && spel.contains("}")){
                    complete.setText(complete.getRawText() + portion.getRawText());
                    portion.setText("");
                }else if(complete != null){
                    complete.setText(complete.getRawText() + portion.getRawText());
                    portion.setText("");
                    continue;
                }else{
                    continue;
                }

                Object text = parser.parseExpression(complete.getRawText(), new TemplateParserContext()).getValue(dataSource.getEvaluationContext(), rootObject);
                log.debug("spel: {}, text: {}", complete.getRawText(), text);
                if(text instanceof String){
                    complete.setText((String) text);
                }else if(text instanceof Text){
                    Text txt = (Text)text;
                    complete.setText(txt.getText());
                    if(txt.getCustomizeFunction() != null){
                        txt.getCustomizeFunction().accept(complete);
                    }
                }
                complete = null;
            }
        }
    }
}
