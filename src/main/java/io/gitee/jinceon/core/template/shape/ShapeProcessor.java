package io.gitee.jinceon.core.template.shape;

import io.gitee.jinceon.core.template.TemplateProcessor;
import org.apache.poi.xslf.usermodel.XSLFShape;

public interface ShapeProcessor extends TemplateProcessor {
    void process(XSLFShape shape, Object context);

}
