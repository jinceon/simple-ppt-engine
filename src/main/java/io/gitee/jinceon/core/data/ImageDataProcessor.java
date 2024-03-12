package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.ShapeHelper;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Order(100)
@Slf4j
public class ImageDataProcessor implements DataProcessor {
    @Override
    public boolean supports(XSLFShape shape) {
        if(!(shape instanceof XSLFPictureShape)){
            return false;
        }
        String text = ShapeHelper.getAlternativeText(shape);
        return StringUtils.hasText(text) && text.contains("#");
    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
        XSLFPictureShape iPicture = (XSLFPictureShape) shape;
        String spel = ShapeHelper.getAlternativeText(shape);
        SpelExpressionParser parser = new SpelExpressionParser();
        Object picture = parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        if(picture == null){
            return;
        }
        if(picture instanceof byte[]){
            byte[] pic = (byte[])picture;
            log.debug("spel: {}, picture: {}", spel, pic.length);
            try {
                iPicture.getPictureData().setData(pic);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if(picture instanceof Image){
            Image pic = (Image) picture;
            try {
                iPicture.getPictureData().setData(pic.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(pic.getCustomizeFunction() != null){
                pic.getCustomizeFunction().accept(iPicture);
            }
        }
    }
}
