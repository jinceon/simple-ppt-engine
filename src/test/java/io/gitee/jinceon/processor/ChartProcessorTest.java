package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.ChartData;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ChartProcessorTest {

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
        ChartData.Pair[] series = new ChartData.Pair[]{
                new ChartData.Pair("年龄区间", "ageRange"),
                new ChartData.Pair("数量", "count")
        };
        ChartData chart = new ChartData(categories, series);
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

        List<Score> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            counts.add(new Score("Student "+i, random.nextInt(100), random.nextInt(100), random.nextInt(100)));
        }

        String[] categories = counts.stream().map(Score::getName).collect(Collectors.toList()).toArray(new String[0]);
        ChartData.Pair[] series = new ChartData.Pair[]{
                new ChartData.Pair("姓名", "name"),
                new ChartData.Pair("数学", "math"),
                new ChartData.Pair("语文", "chinese"),
                new ChartData.Pair("英语", "english")
        };
        ChartData chart = new ChartData(categories, series);
        chart.setData(counts);
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
        ChartData.Pair[] series = new ChartData.Pair[]{
                new ChartData.Pair("食物", "food"),
                new ChartData.Pair("服饰", "cloth"),
                new ChartData.Pair("饮料", "drink")
        };
        ChartData chart = new ChartData(categories, series);
        chart.setData(counts);
        dataSource.setVariable("chartC", chart);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-chartC.pptx");
    }
}

@Data
@AllArgsConstructor
class AgeCount {
    private String ageRange;
    private int count;
}

@Data
@AllArgsConstructor
class Score {
    private String name;
    private int math;
    private int chinese;
    private int english;
}

@Data
@AllArgsConstructor
class Sales{
    private String month;
    private long food;
    private long cloth;
    private long drink;
}