package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.model.Text;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Stack;

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
        for (XSLFTextParagraph paragraph : paragraphs) {
            List<XSLFTextRun> portions = paragraph.getTextRuns();
            XSLFTextRun mergedPortion = null;
            for (XSLFTextRun portion : portions) {
                String spel = portion.getRawText();
                log.debug("spel: {}", spel);
                if(mergedPortion!=null){
                    spel = mergedPortion.getRawText()+spel;
                    mergedPortion.setText(spel);
                    portion.setText("");
                    log.debug("merged spel: {}", spel);
                    if(incomplete(spel)){
                        continue;
                    }
                }else if(incomplete(spel)){
                    mergedPortion = portion;
                    continue;
                }
                SpelExpressionParser parser = new SpelExpressionParser();
                Object text = parser.parseExpression(spel, new TemplateParserContext()).getValue(dataSource.getEvaluationContext());
                log.debug("final spel: {}, text: {}", spel, text);
                XSLFTextRun finalPortion = mergedPortion == null ? portion:mergedPortion;
                mergedPortion = null;
                if(text instanceof String){
                    finalPortion.setText((String) text);
                }else if(text instanceof Text){
                    Text txt = (Text)text;
                    finalPortion.setText(txt.getText());
                    if(txt.getCustomizeFunction() != null){
                        txt.getCustomizeFunction().accept(finalPortion);
                    }
                }
            }
        }
    }

    private static boolean incomplete(String str) {
        Stack<Character> stack = new Stack<>();
        boolean isStarted = false;

        for (char c : str.toCharArray()) {
            if (c == '#') {
                isStarted = true;
            } else if (isStarted && c == '{') {
                stack.push(c);
            } else if (c == '}') {
                // 如果栈为空或者栈顶不是`{`，则返回false
                if (stack.isEmpty() || stack.pop() != '{') {
                    return true;
                }
            }
        }

        // 如果栈为空，说明所有的`{`都有对应的`}`，返回true；否则返回false
        return !stack.isEmpty();
    }
}
