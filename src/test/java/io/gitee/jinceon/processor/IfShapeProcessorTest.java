package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

class IfShapeProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/if-shape.pptx");
        DataSource dataSource = new DataSource();
        dataSource.setVariable("name", "延春");
        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-if-shape.pptx";
        engine.save(outputFile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputFile));
        List<XSLFShape> shapes = outputPpt.getSlides().get(0).getShapes();
        Assertions.assertEquals(2, shapes.size());
        String text1 = ((XSLFAutoShape)(shapes.get(1))).getText();
        Assertions.assertEquals("name !=null     保留", text1);
        String text2 = ((XSLFAutoShape)(shapes.get(0))).getText();
        Assertions.assertEquals("删除上面这个文本框，控制指令写在【可选文本】", text2);
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