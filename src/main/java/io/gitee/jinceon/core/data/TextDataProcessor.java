package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.TextHelper;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.springframework.util.StringUtils;

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
        TextHelper.renderText(textFrame, dataSource);
    }
}
