package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import org.apache.poi.xslf.usermodel.*;
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
        byte[] png = Files.readAllBytes(Paths.get("src/test/resources/image.png"));
        byte[] jpg = Files.readAllBytes(Paths.get("src/test/resources/image.jpg"));
        dataSource.setVariable("img11", png);
        dataSource.setVariable("img2", jpg);
        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-image.pptx";
        engine.save(outputFile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputFile));
        XSLFPictureData pic1 = ((XSLFPictureShape)(outputPpt.getSlides().get(0).getShapes().get(0))).getPictureData();
        Assertions.assertArrayEquals(png, pic1.getData());
        XSLFPictureData pic2 = ((XSLFPictureShape)(outputPpt.getSlides().get(1).getShapes().get(0))).getPictureData();
        Assertions.assertArrayEquals(jpg, pic2.getData());
    }
}