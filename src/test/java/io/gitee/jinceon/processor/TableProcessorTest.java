package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Pair;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.TableData;
import io.gitee.jinceon.processor.data.Score;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.sql.Array;
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
        TableData tableA = new TableData(header, TableData.Direction.HORIZONTAL);
        tableA.getOffset().setTop(1);
        tableA.setData(scores);
        TableData tableB = new TableData(header, TableData.Direction.VERTICAL);
        tableB.getOffset().setLeft(1);
        tableB.setData(scores);
        dataSource.setVariable("tableA", tableA);
        dataSource.setVariable("tableB", tableB);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-table.pptx");
    }
}