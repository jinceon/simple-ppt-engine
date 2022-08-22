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
    void process() {
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart.pptx");
        DataSource dataSource = new DataSource();

        List<AgeCount> counts = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<10;i++){
            counts.add(new AgeCount("age "+(i*10)+"-"+(i+1)*10, random.nextInt(100)));
        }

        String[] studentNames = counts.stream().map(AgeCount::getAgeRange).collect(Collectors.toList()).toArray(new String[0]);
        ChartData.Pair[] series = new ChartData.Pair[]{
                new ChartData.Pair("年龄区间", "ageRange"),
                new ChartData.Pair("数量", "count")
        };
        ChartData chartA = new ChartData(studentNames, series);
        chartA.setData(counts);
        dataSource.setVariable("chartA", chartA);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/gitignore/chart-rendered.pptx");
    }
}

@Data
@AllArgsConstructor
class AgeCount {
    private String ageRange;
    private int count;
}
