package io.gitee.jinceon.processor;

import com.aspose.slides.IShape;
import io.gitee.jinceon.core.Context;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.Processor;

@Order(90)
public class TextProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return false;
    }

    @Override
    public void process(IShape shape, Context context) {

    }
}
