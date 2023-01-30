package io.gitee.jinceon.core.template;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Processor;

public interface TemplateProcessor extends Processor {
    boolean supports(String directive);

    Object parseDirective(String expression, DataSource dataSource);

}
