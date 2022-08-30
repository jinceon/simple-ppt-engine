package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.Table;
import io.gitee.jinceon.processor.data.Trend;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ForShapeProcessorTest {

    @Test
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/for-shape.pptx");
        DataSource dataSource = new DataSource();
        String[] headers = new String[]{
                "goods",
                "theYearBeforeLast",
                "lastYear",
                "thisYear"
        };
        List<Trend> trends = new ArrayList<>();
        trends.add(new Trend("cloth",100,200,300));
        trends.add(new Trend("drink",400,500,600));
        trends.add(new Trend("food",700,800,900));

        Table tableA = new Table();
        tableA.setData(headers, trends, Table.Direction.VERTICAL);
        int thisYear = LocalDate.now().getYear();
        Object[][] dynamicHeader = new Object[][]{
                {null},{thisYear-2},{thisYear-1},{thisYear}
        };
        tableA.merge(Table.Position.LEFT, dynamicHeader);
        dataSource.setVariable("tableA", tableA);

        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-for-shape.pptx");
    }
}