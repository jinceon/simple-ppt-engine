package io.gitee.jinceon.core;

import org.apache.poi.xslf.usermodel.XSLFShape;

public interface DataProcessor extends Processor{
    boolean supports(XSLFShape shape);
    void process(XSLFShape shape, DataSource dataSource);
}
