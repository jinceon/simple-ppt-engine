package io.gitee.jinceon.core;

import org.apache.poi.xslf.usermodel.XSLFSlide;

public interface SlideProcessor extends TemplateProcessor{

    void process(XSLFSlide slide, Object context);
}
