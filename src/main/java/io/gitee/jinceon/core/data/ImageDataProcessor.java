package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.ShapeHelper;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
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
        byte[] picture = (byte[]) parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        if(picture == null){
            return;
        }
        log.debug("spel: {}, picture: {}", spel, picture.length);
        try {
            iPicture.getPictureData().setData(picture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
