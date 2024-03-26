package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.model.FormItem;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class FormDataProcessorHookTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/form-hook.pptx");
        DataSource dataSource = new DataSource();

        Map<String, Object> user = new HashMap<>();
        user.put("name", "张山");
        user.put("usedName",new FormItem("张三", cell -> {
            cell.setFillColor(Color.RED);
        }));
        dataSource.setVariable("user", user);


        User user1 = new User();
        user1.setName("李四");
        user1.setUsedName(new FormItem("李斯", cell -> {
            cell.setFillColor(Color.RED);
        }));
        dataSource.setVariable("user1", user1);

        engine.setDataSource(dataSource);
        engine.process();
        String outputfile = "src/test/resources/test-form-hook.pptx";
        engine.save(outputfile);
    }

    @Data
    static class User {
        private String name;
        private FormItem usedName;
    }

}

