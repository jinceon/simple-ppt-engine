package io.gitee.jinceon.core.template.slide;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.template.slide.SlideProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.ShapeHelper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
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
     */
    @Override
    public void process(XSLFSlide slide, Object context) {
        SlideShow ppt = slide.getSlideShow();
        if(!(ppt instanceof XMLSlideShow)){
            throw new UnsupportedOperationException("only supports pptx");
        }
        XMLSlideShow pptx = (XMLSlideShow) ppt;

        if(context == null){
            log.debug("#for=null delete slide");
            pptx.removeSlide(slide.getSlideNumber()-1);
            return;
        }
        int size = 0;
        if(context instanceof List){
            size = ((List) context).size();
        }else if(context instanceof Object[] ){
            size = ((Object[]) context).length;
        }
        if(size == 0){
            log.debug("#for=( a empty object) delete slide");
            pptx.removeSlide(slide.getSlideNumber()-1);
            return;
        }
        log.debug("#for=[ {} item{} ], insert {} clone slide{}", size, size>1?"s":"", size-1, size>2?"s":"");
        int number = slide.getSlideNumber();
        for (int page = 1; page < size; page++) {
            // 自身占了一张幻灯片，只需复制 size-1 张
            Slide cloned = copy(number+page-1, slide);

            replaceIndex(cloned, page+"");
        }
        replaceIndex(slide, "0");//自身的下标也要换，并且要最后，不然复制的就不是#_index_，就没法替换下标了
    }

    private static Slide copy(int index, XSLFSlide slide){
        XSLFSlide newSlide = slide.getSlideShow().createSlide();
        newSlide.importContent(slide);
        newSlide.getSlideShow().setSlideOrder(newSlide, index);
        return newSlide;
    }

    private static void replaceIndex(Slide slide, String pageIndex){
        List<Shape> shapes = slide.getShapes();
        for (Shape shape : shapes) {
            if (shape instanceof AutoShape) {
                AutoShape text = (AutoShape) shape;
                text.setText(StringUtils.replace(text.getText(), INDEX_PLACEHOLDER, pageIndex));
            } else if (shape instanceof XSLFShape){
                XSLFShape shape1 = (XSLFShape) shape;
                ShapeHelper.setAlternativeText(shape1,
                        StringUtils.replace(ShapeHelper.getAlternativeText(shape1), INDEX_PLACEHOLDER, pageIndex));
            }
        }
    }
}
