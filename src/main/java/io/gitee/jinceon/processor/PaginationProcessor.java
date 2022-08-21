package io.gitee.jinceon.processor;

import com.aspose.slides.IShape;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.Processor;

@Order(1000)
public class PaginationProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return false;
    }

    @Override
    public void process(IShape shape, DataSource dataSource) {

    }
}
