package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Processor;
import org.apache.poi.xslf.usermodel.XSLFShape;

public interface DataProcessor extends Processor {
    boolean supports(XSLFShape shape);
    void process(XSLFShape shape, DataSource dataSource);
}
