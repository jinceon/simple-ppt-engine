package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.model.Image;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

class ImageDataProcessorHookTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/image-hook.pptx");
        DataSource dataSource = new DataSource();
        byte[] png = Files.readAllBytes(Paths.get("src/test/resources/image.png"));
        dataSource.setVariable("img1", new Image(png, xslfPictureShape -> {
            // 图片旋转90°
            xslfPictureShape.setRotation(90.0);
        }));


        byte[] jpg = Files.readAllBytes(Paths.get("src/test/resources/image.jpg"));
        BufferedImage jpg1 = ImageIO.read(new ByteArrayInputStream(png));
        int actualWidth = jpg1.getWidth();
        int actualHeight = jpg1.getHeight();
        dataSource.setVariable("img2", new Image(jpg, xslfPictureShape -> {
            // 忽略占位符尺寸，按图片原始尺寸渲染
            Rectangle2D rectangle2D = xslfPictureShape.getAnchor();
            rectangle2D.setRect(rectangle2D.getX(), rectangle2D.getY(), actualWidth, actualHeight);
            xslfPictureShape.setAnchor(rectangle2D);
        }));
        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-image-hook.pptx";
        engine.save(outputFile);

        XMLSlideShow outputPpt = new XMLSlideShow(Files.newInputStream(Paths.get(outputFile)));
        XSLFPictureShape pic1 = (XSLFPictureShape)(outputPpt.getSlides().get(0).getShapes().get(0));
        Assertions.assertArrayEquals(png, pic1.getPictureData().getData());
        Assertions.assertEquals(90.0, pic1.getRotation());

        XSLFPictureShape pic2 = (XSLFPictureShape)(outputPpt.getSlides().get(1).getShapes().get(0));
        Assertions.assertArrayEquals(jpg, pic2.getPictureData().getData());
        Assertions.assertEquals(actualWidth, pic2.getAnchor().getWidth());
        Assertions.assertEquals(actualHeight, pic2.getAnchor().getHeight());

    }
}