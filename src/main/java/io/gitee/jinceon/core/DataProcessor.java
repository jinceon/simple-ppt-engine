package io.gitee.jinceon.core;

import com.aspose.slides.IShape;

public interface DataProcessor extends Processor{
    boolean supports(IShape shape);
    void process(IShape shape, DataSource dataSource);
}
