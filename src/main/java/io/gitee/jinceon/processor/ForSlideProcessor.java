package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.SlideProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * #for = items
 * when `items` is null or empty array (list), slide will be removed
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
        Object o = parser.parseExpression(expression).getValue(dataSource.getEvaluationContext());
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
        if(context == null){
            log.debug("#for=null delete slide");
            slide.remove();
            return;
        }
        int size = 0;
        if(context instanceof List list){
            size = list.size();
        }else if(context instanceof Object[] array){
            size = array.length;
        }
        if(size == 0){
            log.debug("#for=( a empty object) delete slide");
            slide.remove();
            return;
        }
        log.debug("#for=[ {} item{} ], insert {} clone slide{}", size, size>1?"s":"", size-1, size>2?"s":"");
        int number = slide.getSlideNumber();
        for (int page = 1; page < size; page++) {
            // 自身占了一张幻灯片，只需复制 size-1 张
            ISlide cloned = slide.getPresentation().getSlides().insertClone(number+page-1, slide);
            replaceIndex(cloned, page+"");
        }
        replaceIndex(slide, "0");//自身的下标也要换，并且要最后，不然复制的就不是#_index_，就没法替换下标了
    }

    private static void replaceIndex(ISlide slide, String pageIndex){
        IShapeCollection shapes = slide.getShapes();
        for (IShape shape : shapes.toArray()) {
            if (shape instanceof IAutoShape text) {
                ITextFrame textFrame = text.getTextFrame();
                IParagraphCollection paragraphs = textFrame.getParagraphs();
                for (int i = 0; i < paragraphs.getCount(); i++) {
                    IParagraph paragraph = paragraphs.get_Item(i);
                    IPortionCollection portions = paragraph.getPortions();
                    for (int j = 0; j < portions.getCount(); j++) {
                        IPortion portion = portions.get_Item(j);
                        portion.setText(StringUtils.replace(portion.getText(), INDEX_PLACEHOLDER, pageIndex));
                    }
                }
            } else {
                shape.setAlternativeText(StringUtils.replace(shape.getAlternativeText(), INDEX_PLACEHOLDER, pageIndex));
            }
        }
    }
}
