package io.gitee.jinceon.core;

import org.apache.poi.xslf.usermodel.XSLFShape;

public interface ShapeProcessor extends TemplateProcessor{
    void process(XSLFShape shape, Object context);

}
