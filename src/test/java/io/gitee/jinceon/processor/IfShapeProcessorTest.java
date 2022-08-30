package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.processor.data.AgeCount;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
}