package io.gitee.jinceon.core;

import com.aspose.slides.IShape;
import io.gitee.jinceon.core.Context;

public interface Processor{
    boolean supports(IShape shape);
    void process(IShape shape, Context context);
}
