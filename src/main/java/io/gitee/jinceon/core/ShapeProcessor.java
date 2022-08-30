package io.gitee.jinceon.core;

import com.aspose.slides.IShape;

public interface ShapeProcessor extends TemplateProcessor{
    void process(IShape shape, Object context);

}
