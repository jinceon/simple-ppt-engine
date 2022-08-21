package io.gitee.jinceon.core;

import com.aspose.slides.IShape;

public interface Processor{
    boolean supports(IShape shape);
    void process(IShape shape, DataSource dataSource);
}
