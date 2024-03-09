package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.Text;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

class TextDataProcessorHookTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/text-hook.pptx");
        DataSource dataSource = new DataSource();
        int time = LocalTime.now().getSecond();
        dataSource.setVariable("time", new Text(time+"", xslfTextRun -> {
            if(time > 30) {
                xslfTextRun.setFontColor(Color.RED);
            }else{
                xslfTextRun.setFontColor(Color.GREEN);
            }
        }));

        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-text-hook.pptx";
        engine.save(outputFile);

        XMLSlideShow outputPpt = new XMLSlideShow(Files.newInputStream(Paths.get(outputFile)));
        List<XSLFShape> shapes = outputPpt.getSlides().get(0).getShapes();
        String text1 = ((XSLFAutoShape)(shapes.get(0))).getText();
        Assertions.assertEquals("动态颜色, " + time, text1.trim());
        XSLFAutoShape textFrame = (XSLFAutoShape) shapes.get(0);
        XSLFTextRun text = textFrame.getTextParagraphs().get(0).getTextRuns().get(2);
        Assertions.assertEquals(time+"", text.getRawText());
        PaintStyle.SolidPaint paint = (PaintStyle.SolidPaint) text.getFontColor();
        if(time > 30) {
            Assertions.assertEquals(Color.RED, paint.getSolidColor().getColor());
        }else{
            Assertions.assertEquals(Color.GREEN, paint.getSolidColor().getColor());
        }
    }
}