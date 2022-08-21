package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.Processor;

import java.util.ArrayList;
import java.util.List;

@Order(70)
public class ChartProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return shape instanceof IChart;
    }

    @Override
    public void process(IShape shape, DataSource dataSource) {
        IChart iChart= (IChart) shape;
        System.out.println("shape--begin");
        IChartData chartData = iChart.getChartData();
        System.out.println("1 "+chartData.getRange());
        IChartCategoryCollection categories = chartData.getCategories();
        IChartSeriesCollection series = chartData.getSeries();
        for(int i=0;i<categories.size();i++){
            System.out.println("category "+i+" "+categories.get_Item(i).getValue());
        }
        for(int i=0;i<series.size();i++){
            System.out.println("series "+i+" "+series.get_Item(i).getName());
        }
        //series.add("", 1);
        //categories.add("类别");
        System.out.println("shape--end");
        IChartDataWorkbook chartDataWorkbook = chartData.getChartDataWorkbook();
        int workSheetIndex = 0, row = 0, col = 0;
        IChartDataCell cell = chartDataWorkbook.getCell(workSheetIndex, row, col);
    }
}
