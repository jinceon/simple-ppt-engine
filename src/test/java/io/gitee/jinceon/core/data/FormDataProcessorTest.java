package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class FormDataProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/form.pptx");
        DataSource dataSource = new DataSource();

        User user = new User();
        user.setName("张山");
        user.setUsedName("张三");
        user.setSex("男");
        user.setEthnicity("汉族");
        user.setWeight("65kg");
        user.setHeight("179cm");
        user.setIdcard("440223200001011234");
        user.setPhone("15012341234");
        user.setAddress("广东省广州市天河区天河公园");
        dataSource.setVariable("user", user);

        engine.setDataSource(dataSource);
        engine.process();
        String outputfile = "src/test/resources/test-form.pptx";
        engine.save(outputfile);
    }

    @Data
    static class User {
        private String name;
        private String usedName;
        private String sex;
        private String ethnicity;
        private String height;
        private String weight;
        private String idcard;
        private String phone;
        private String address;
    }

}

