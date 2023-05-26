package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ImageDataProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/image.pptx");
        DataSource dataSource = new DataSource();
        byte[] pic = Files.readAllBytes(Paths.get("src/test/resources/test-image.png"));
        dataSource.setVariable("img1", pic);
        dataSource.setVariable("img2", pic);
        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-image.pptx";
        engine.save(outputFile);
//
//        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputFile));
//        List<XSLFShape> shapes = outputPpt.getSlides().get(0).getShapes();
//        Assertions.assertEquals(2, shapes.size());
//        String text1 = ((XSLFAutoShape)(shapes.get(0))).getText();
//        Assertions.assertEquals("Hello, jinceon ", text1);
//        String text2 = ((XSLFAutoShape)(shapes.get(1))).getText();
//        Assertions.assertEquals("it can keep the style of variable jinceon", text2);
    }
}