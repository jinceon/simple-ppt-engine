package io.gitee.jinceon.core;

public interface TemplateProcessor extends Processor{
    boolean supports(String directive);

    Object parseDirective(String expression, DataSource dataSource);

}
