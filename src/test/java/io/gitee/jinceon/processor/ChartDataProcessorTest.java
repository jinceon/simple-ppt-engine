package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.core.Chart.Pair;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.processor.data.AgeCount;
import io.gitee.jinceon.processor.data.Sales;
import io.gitee.jinceon.processor.data.Score;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class ChartDataProcessorTest {

    @Test
    void pieChart() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        DataSource dataSource = new DataSource();

        List<AgeCount> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            counts.add(new AgeCount("age "+(i*10)+"-"+(i+1)*10, random.nextInt(100)));
        }

        String[] categories = counts.stream().map(AgeCount::getAgeRange).collect(Collectors.toList()).toArray(new String[0]);
        Pair[] series = new Pair[]{
                new Pair("年龄区间", "ageRange"),
                new Pair("数量", "count")
        };
        Chart chart = new Chart(categories, series);
        chart.setData(counts);
        dataSource.setVariable("chartA", chart);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-chartA.pptx");
    }

    @Test
    void barChart() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        DataSource dataSource = new DataSource();

        List<Score> scores = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            scores.add(Score.builder().name("Student " + i)
                    .math(random.nextInt(100))
                    .english(random.nextInt(100))
                    .chinese(random.nextInt(100))
                    .build());
        }

        String[] categories = scores.stream().map(Score::getName).collect(Collectors.toList()).toArray(new String[0]);
        Pair[] series = new Pair[]{
                new Pair("姓名", "name"),
                new Pair("数学", "math"),
                new Pair("语文", "chinese"),
                new Pair("英语", "english")
        };
        Chart chart = new Chart(categories, series);
        chart.setData(scores);
        dataSource.setVariable("chartB", chart);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-chartB.pptx");
    }

    @Test
    void lineChart() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        DataSource dataSource = new DataSource();

        List<Sales> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=1;i<13;i++){
            counts.add(new Sales(i+"月 ", random.nextInt(100), random.nextInt(100), random.nextInt(100)));
        }

        String[] categories = counts.stream().map(Sales::getMonth).collect(Collectors.toList()).toArray(new String[0]);
        Pair[] series = new Pair[]{
                new Pair("食物", "food"),
                new Pair("服饰", "cloth"),
                new Pair("饮料", "drink")
        };
        Chart chart = new Chart(categories, series);
        chart.setData(counts);
        dataSource.setVariable("chartC", chart);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-chartC.pptx");
    }
}

