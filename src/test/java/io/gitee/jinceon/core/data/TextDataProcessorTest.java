package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import lombok.Data;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TextDataProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/text.pptx");
        DataSource dataSource = new DataSource();
        dataSource.setVariable("name", "jinceon");

        User user = new User();
        user.setNickname("张三");
        user.setPhone("15012341234");
        dataSource.setVariable("user", user);

        List<String> categories = new ArrayList<>();
        for(int i=0;i<10;i++){
            if(i<6) {
                categories.add("栏目" + (i + 1));
            }else{
                categories.add("");
            }
        }
        dataSource.setVariable("category", categories);

        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-text.pptx";
        engine.save(outputFile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputFile));
        List<XSLFShape> shapes = outputPpt.getSlides().get(0).getShapes();
        Assertions.assertEquals(2, shapes.size());
        String text1 = ((XSLFAutoShape)(shapes.get(0))).getText();
        Assertions.assertEquals("Hello, jinceon ", text1);
        String text2 = ((XSLFAutoShape)(shapes.get(1))).getText();
//        Assertions.assertEquals("it can keep the style of variable jinceon", text2);
    }

    @Data
    static class User {
        private String nickname;
        private String phone;
    }
}