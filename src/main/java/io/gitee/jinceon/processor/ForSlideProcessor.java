package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.SlideProcessor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * #pagination
 * #items
 * empty=hide|delete|anything else
 */
@Order(1900)
@Slf4j
public class ForSlideProcessor implements SlideProcessor {
    private static final String DIRECTIVE = "#for";
    private static final String INDEX_PLACEHOLDER = "#_index_";

    @Override
    public boolean supports(String directive) {
        return directive.equals(DIRECTIVE);
    }

    @Override
    public Object parseDirective(String expression, DataSource dataSource) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Object o = parser.parseExpression(expression).getValue(dataSource.getEvaluationContext(), Pagination.class);
        log.debug("expression:{}, value:{}", expression, o);
        return o;
    }

    /**
     * 将 slide 里所有shape的 #users[#_index_] 替换成 #users[0] #user[1]等下标
     * @param slide
     * @param context Pagination{collection=#user, empty=0|1|2}
     */
    @Override
    public void process(ISlide slide, Object context) {
        Pagination pagination = (Pagination) context;
        int size = pagination.getCollection().size();
        IShapeCollection shapes = slide.getShapes();
        for (int page = 0; page < size; page++) {
            String index = "" + page;
            for (IShape shape : shapes.toArray()) {
                if (shape instanceof IAutoShape text) {
                    ITextFrame textFrame = text.getTextFrame();
                    IParagraphCollection paragraphs = textFrame.getParagraphs();
                    for (int i = 0; i < paragraphs.getCount(); i++) {
                        IParagraph paragraph = paragraphs.get_Item(i);
                        IPortionCollection portions = paragraph.getPortions();
                        for (int j = 0; j < portions.getCount(); j++) {
                            IPortion portion = portions.get_Item(j);
                            portion.setText(StringUtils.replace(portion.getText(), INDEX_PLACEHOLDER, index));
                        }
                    }
                } else {
                    shape.setAlternativeText(StringUtils.replace(shape.getAlternativeText(), INDEX_PLACEHOLDER, index));
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class Pagination {
        private Collection collection;
        private EmptyStrategy empty;

        private static class EmptyStrategy {
        }
    }
}
