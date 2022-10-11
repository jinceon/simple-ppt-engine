package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IfShapeProcessorTest {

    @Test
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/if-shape.pptx");
        DataSource dataSource = new DataSource();
        dataSource.setVariable("name", "延春");
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-if-shape.pptx");
    }
    
    @Test
    void supports() {
        IfShapeProcessor processor = new IfShapeProcessor();
        Assertions.assertEquals(true, processor.supports("#if"));
    }

    @Test
    void parseDirective() {
        IfShapeProcessor processor = new IfShapeProcessor();
        DataSource dataSource = new DataSource();;
        dataSource.setVariable("name", "延春");
        Assertions.assertEquals(true, processor.parseDirective("#if = #name != null", dataSource));
        Assertions.assertEquals(false, processor.parseDirective("#if = #name1 != null", dataSource));
    }
}