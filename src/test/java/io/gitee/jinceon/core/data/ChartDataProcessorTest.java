package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.core.Chart.Pair;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.processor.data.AgeCount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ChartDataProcessorTest {

    @Test
    void process(){
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        DataSource dataSource = new DataSource();
        Chart chartA = pieChart();
        Chart chartB = barChart();
        Chart chartC = lineChart();
        dataSource.setVariable("chartA", chartA);
        dataSource.setVariable("chartB", chartB);
        dataSource.setVariable("chartC", chartC);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-chart.pptx");
    }


    Chart pieChart() {
        List<AgeCount> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            counts.add(new AgeCount("age "+(i*10)+"-"+(i+1)*10, random.nextInt(100)));
        }
        Pair[] series = new Pair[]{
                new Pair("数量", "count")
        };
        Chart chart = new Chart(series);
        chart.setDataWithCategories(counts, "ageRange");
       return chart;
    }

    Chart barChart() {
        List<Score> scores = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            scores.add(Score.builder().name("Student " + i)
                    .math(random.nextInt(100))
                    .english(random.nextInt(100))
                    .chinese(random.nextInt(100))
                    .build());
        }

        Pair[] series = new Pair[]{
                new Pair("数学", "math"),
                new Pair("语文", "chinese"),
                new Pair("英语", "english")
        };
        Chart chart = new Chart(series);
        chart.setDataWithCategories(scores, "name");
        return chart;
    }

    Chart lineChart() {
        List<Sales> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=1;i<13;i++){
            counts.add(new Sales(i+"月 ", random.nextInt(100), random.nextInt(100), random.nextInt(100)));
        }

        String[] categories = counts.stream().map(Sales::getMonth).toArray(String[]::new);
        Pair[] series = new Pair[]{
                new Pair("食物", "food"),
                new Pair("服饰", "cloth"),
                new Pair("饮料", "drink")
        };
        Chart chart = new Chart(categories, series);
        chart.setData(counts);
        return chart;
    }

    @Data
    @Builder
    public static
    class Score {
        private String name;
        private int math;
        private int chinese;
        private int english;
        private int chemistry;
        private int physics;
    }

    @Data
    @AllArgsConstructor
    public static
    class Sales {
        private String month;
        private long food;
        private long cloth;
        private long drink;
    }
}

