package io.gitee.jinceon.core.template.slide;

import io.gitee.jinceon.core.template.TemplateProcessor;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public interface SlideProcessor extends TemplateProcessor {

    void process(XSLFSlide slide, Object context);
}
