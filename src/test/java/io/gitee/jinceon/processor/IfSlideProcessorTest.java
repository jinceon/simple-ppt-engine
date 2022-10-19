package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.processor.data.User;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class IfSlideProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/if-slide.pptx");
        DataSource dataSource = new DataSource();
        List users = new ArrayList<>();
        users.add(new User("jinceon"));
        dataSource.setVariable("users", users);
        dataSource.setVariable("users1", null);
        dataSource.setVariable("users2", new ArrayList<>());
        engine.setDataSource(dataSource);
        engine.process();
        String outputfile = "src/test/resources/test-if-slide.pptx";
        engine.save(outputfile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputfile));
        List<XSLFSlide> slides = outputPpt.getSlides();
        Assertions.assertEquals(1, slides.size());
        List<XSLFShape> shapes = slides.get(0).getShapes();
        String text = ((XSLFAutoShape)(shapes.get(1))).getText();
        Assertions.assertEquals("你好，jinceon", text);
    }

    @Test
    void supports() {
        IfSlideProcessor processor = new IfSlideProcessor();
        Assertions.assertEquals(true, processor.supports("#if"));
    }

    @Test
    void parseDirective() {
        IfSlideProcessor processor = new IfSlideProcessor();
        DataSource dataSource = new DataSource();
        List users = new ArrayList<>();
        users.add(new User("jinceon"));
        dataSource.setVariable("users", users);
        dataSource.setVariable("users1", null);
        dataSource.setVariable("users2", new ArrayList<>());
        Assertions.assertEquals(false, processor.parseDirective("#if=( #users1 != null and #users1.size() > 0)", dataSource));
        Assertions.assertEquals(false, processor.parseDirective("#if=( #users2 != null and #users2.size() > 0)", dataSource));
        Assertions.assertEquals(true, processor.parseDirective("#if=( #users != null and #users.size() > 0)", dataSource));
    }

}