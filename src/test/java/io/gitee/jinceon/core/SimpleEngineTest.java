package io.gitee.jinceon.core;

import com.aspose.slides.IShape;
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
        int oldSize = engine.getProcessors().size();
        Processor first = new MyPreProcessor();
        Processor last = new MyPostProcessor();
        engine.addProcessor(first);
        engine.addProcessor(last);
        List<Processor> newProcessors = engine.getProcessors();
        Assertions.assertEquals(oldSize+2, newProcessors.size());
        Assertions.assertEquals(first, newProcessors.get(0));
        Assertions.assertEquals(last, newProcessors.get(newProcessors.size()-1));
    }

    @Test
    void process() {
    }
}

@Order(1)
class MyPreProcessor implements Processor{

    @Override
    public boolean supports(IShape shape) {
        return false;
    }

    @Override
    public void process(IShape shape, Context context) {

    }
}

@Order(Integer.MAX_VALUE)
class MyPostProcessor implements Processor{

    @Override
    public boolean supports(IShape shape) {
        return false;
    }

    @Override
    public void process(IShape shape, Context context) {

    }
}