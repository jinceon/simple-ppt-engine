package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.core.Chart.Pair;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ChartDataProcessorHookTest {

    @Test
    void process(){
        SimpleEngine engine = new SimpleEngine("src/test/resources/chart-hook.pptx");
        DataSource dataSource = new DataSource();
        Chart chartLess = chartLess();
        Chart chartMore = chartMore();
        dataSource.setVariable("chartLess", chartLess);
        dataSource.setVariable("chartMore", chartMore);
        engine.setDataSource(dataSource);
        engine.process();
        engine.save("src/test/resources/test-chart-hook.pptx");
    }


    Chart chartLess() {
        List<Score> months = new ArrayList<>();
        Random random = new Random();
        for(int i=1;i<4;i++){
            months.add(new Score(i+"月", random.nextDouble()*5));
        }
        Pair[] series = new Pair[]{
                new Pair("月份", "score")
        };
        Chart chart = new Chart(series);
        chart.setDataWithCategories(months, "month");

        chart.setCustomizeFunction(xslfChart -> {
            xslfChart.getChartSeries().get(0).getSeries(0).getDataPoint(2)
                    .setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(0, 0,100_000)));
            // XDDFColor.from 颜色是百分比，从0到100_000的数字
        });
        return chart;
    }

    Chart chartMore() {
        List<Score> months = new ArrayList<>();
        Random random = new Random();
        for(int i=1;i<10;i++){
            months.add(new Score(i+"月", random.nextDouble()*5));
        }
        Pair[] series = new Pair[]{
                new Pair("月份", "score")
        };
        Chart chart = new Chart(series);
        chart.setDataWithCategories(months, "month");
        chart.setCustomizeFunction(xslfChart -> {
            xslfChart.getChartSeries().get(0).getSeries(0).getDataPoint(8)
                    .setFillProperties(new XDDFSolidFillProperties(XDDFColor.from(100_000, 0,0)));
        });
        return chart;
    }
    @Data
    @AllArgsConstructor
    public static class Score {
        private String month;
        private Double score;
    }

}

