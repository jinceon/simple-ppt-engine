package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChartProcessorTest {

    @Test
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        DataSource dataSource = new DataSource();
        dataSource.setVariable("title", "jinceon");
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/gitignore/chart-rendered.pptx");
    }
}