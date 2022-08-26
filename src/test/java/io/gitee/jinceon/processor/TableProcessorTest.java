package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Pair;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.Table;
import io.gitee.jinceon.processor.data.Score;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TableProcessorTest {

    @Test
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/table.pptx");
        DataSource dataSource = new DataSource();
        Pair[] header = new Pair[]{
                new Pair("姓名", "name"),
                new Pair("语文", "chinese"),
                new Pair("数学", "math"),
                new Pair("英语", "english"),
                new Pair("物理", "physics"),
                new Pair("化学", "chemistry"),
        };
        List<Score> scores = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            scores.add(Score.builder().name("Student " + i)
                    .math(random.nextInt(100))
                    .english(random.nextInt(100))
                    .chinese(random.nextInt(100))
                    .chemistry(random.nextInt(100))
                    .physics(random.nextInt(100))
                    .build());
        }
        String[] headers = new String[]{"name", "chinese", "math", "english", "physics", "chemistry"};
        Table tableA = new Table();
        tableA.setData(headers, scores, Table.Direction.HORIZONTAL);
        tableA.merge(Table.Position.TOP, new Object[1][1]);
        dataSource.setVariable("tableA", tableA);

        Table tableB = new Table();
        tableB.setData(headers, scores, Table.Direction.VERTICAL);
        tableA.merge(Table.Position.LEFT, new Object[1][1]);
        dataSource.setVariable("tableB", tableB);

        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-table.pptx");
    }
}