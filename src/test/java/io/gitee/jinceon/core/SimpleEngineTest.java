package io.gitee.jinceon.core;

import io.gitee.jinceon.core.data.DataProcessor;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SimpleEngineTest {

    @Test
    void addContext() {
    }

    @Test
    void addProcessor(){
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        int oldSize = engine.getDataProcessors().size();
        DataProcessor first = new MyPreDataProcessor();
        DataProcessor last = new MyPostDataProcessor();
        engine.addProcessor(first);
        engine.addProcessor(last);
        List<DataProcessor> newProcessors = engine.getDataProcessors();
        Assertions.assertEquals(oldSize+2, newProcessors.size());
        Assertions.assertEquals(first, newProcessors.get(0));
        Assertions.assertEquals(last, newProcessors.get(newProcessors.size()-1));
    }

    @Test
    void process() {
    }
}

@Order(1)
class MyPreDataProcessor implements DataProcessor {

    @Override
    public boolean supports(XSLFShape shape) {
        return false;
    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {

    }
}

@Order(Integer.MAX_VALUE)
class MyPostDataProcessor implements DataProcessor {

    @Override
    public boolean supports(XSLFShape shape) {
        return false;
    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {

    }
}