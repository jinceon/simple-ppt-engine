package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.ChartData;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.Processor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Order(70)
public class ChartProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return shape instanceof IChart;
    }

    @Override
    public void process(IShape shape, DataSource dataSource) {
        IChart iChart = (IChart) shape;
        System.out.println("shape--begin");
        // wps has AlternativeTextTitle and AlternativeText
        // PowerPoint only has AlternativeText
        String spel = shape.getAlternativeText();
        SpelExpressionParser parser = new SpelExpressionParser();
        ChartData chart = (ChartData) parser.parseExpression(spel, new TemplateParserContext()).getValue(dataSource.getEvaluationContext());
        if(chart == null){
            return;
        }
        System.out.println("spel: " + spel + ", chart: " + chart);
        IChartData chartData = iChart.getChartData();
        IChartDataWorkbook chartDataWorkbook = chartData.getChartDataWorkbook();
        System.out.println("template range:" + chartData.getRange());
        int workSheetIndex = 0;
        int seriesRow = 0;
        int CategoriesColumn = 0;
        String[] categories = chart.getCategories();
        for (int row = 1; row < categories.length; row++) {
            chartDataWorkbook.getCell(workSheetIndex, row, CategoriesColumn).setValue(categories[row]);
        }
        ChartData.Pair[] series = chart.getSeries();
        for (int col = 1; col < series.length; col++) {
            chartDataWorkbook.getCell(workSheetIndex, seriesRow, col).setValue(series[col].getLabel());
        }
        Object[][] data = chart.getData();
        for (int row = 1; row < categories.length; row++) {
            for (int col = 1; col < series.length; col++) {
                chartDataWorkbook.getCell(workSheetIndex, row, col).setValue(data[row][col]);
            }
        }
        String newRange = String.format("Sheet1!$A$1:$%s$%d", number2Char(series.length), categories.length);
        System.out.println("new range: " + newRange);
        chartData.setRange(newRange);
        IChartCellCollection cells = chartDataWorkbook.getCellCollection(chartData.getRange(), true);
        cells.forEach(iChartDataCell -> System.out.printf("cell: row=%d col=%d value=%s %n", iChartDataCell.getRow(), iChartDataCell.getColumn(), iChartDataCell.getValue()));
    }

    /**
     * excel的列用字母表示，A-Z分别表示第1-26列，AA-AZ分别表示第27-52列
     * 将列的数字表示法转成字母表示法
     *
     * @param n 1,27
     * @return s A,AA
     */
    private String number2Char(int n) {
        String str = "";
        while (n > 0) {
            int rem = n % 26;
            if (rem == 0) {
                str += "Z";
                n = (n / 26) - 1;
            } else {
                str += (char) ((rem - 1) + 'A');
                n = n / 26;
            }
        }
        return str;
    }
}
