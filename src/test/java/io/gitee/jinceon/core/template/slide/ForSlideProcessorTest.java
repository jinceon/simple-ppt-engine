package io.gitee.jinceon.core.template.slide;

import io.gitee.jinceon.core.model.Chart;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.model.Table;
import io.gitee.jinceon.processor.data.AgeCount;
import io.gitee.jinceon.processor.data.Trend;
import org.junit.jupiter.api.Test;

import java.util.*;

class ForSlideProcessorTest {
    @Test
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/for-slide.pptx");
        DataSource dataSource = new DataSource();
        dataSource.setVariable("users1", null);
        dataSource.setVariable("users2", new ArrayList<>());
        List pages = new ArrayList();
        for(int i=0;i<4;i++){
            Chart chart = createChart();
            Table table = createTable();
            Map page = new HashMap<>();
            page.put("page", i+1);
            page.put("chart", chart);
            page.put("table", table);
            pages.add(page);
        }
        // 支持list和array
        dataSource.setVariable("pages", pages.toArray());
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-for-slide.pptx");
    }

    private Chart createChart(){
        List<AgeCount> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            counts.add(new AgeCount("age "+(i*10)+"-"+(i+1)*10, random.nextInt(100)));
        }

        String[] categories = counts.stream().map(AgeCount::getAgeRange).toArray(String[]::new);
        Chart.Pair[] series = new Chart.Pair[]{
//                new Chart.Pair("年龄区间", "ageRange"),
                new Chart.Pair("数量", "count")
        };
        Chart chart = new Chart(categories, series);
        chart.setData(counts);
        return chart;
    }

    private Table createTable(){
        String[] headers = new String[]{
                "goods",
                "theYearBeforeLast",
                "lastYear",
                "thisYear"
        };
        Random random = new Random();
        List<Trend> trends = new ArrayList<>();
        trends.add(new Trend("cloth",random.nextInt(100),random.nextInt(200),random.nextInt(300)));
        trends.add(new Trend("drink",random.nextInt(100),random.nextInt(200),random.nextInt(300)));
        trends.add(new Trend("food ",random.nextInt(100),random.nextInt(200),random.nextInt(300)));

        Table table = new Table();
        table.setData(headers, trends, Table.Direction.HORIZONTAL);
        table.merge(Table.Position.TOP, new Object[][]{headers});
        return table;
    }
}