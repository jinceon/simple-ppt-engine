package io.gitee.jinceon.core;

import com.aspose.slides.ISlide;

public interface SlideProcessor extends TemplateProcessor{

    void process(ISlide slide, Object context);
}
