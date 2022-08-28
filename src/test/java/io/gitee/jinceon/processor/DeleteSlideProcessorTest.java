package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.processor.data.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeleteSlideProcessorTest {

    @Test
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/delete.pptx");
        DataSource dataSource = new DataSource();
        List users = new ArrayList<>();
        users.add(new User("jinceon"));
        dataSource.setVariable("users", users);
        dataSource.setVariable("users1", null);
        dataSource.setVariable("users2", new ArrayList<>());
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-delete.pptx");
    }
}