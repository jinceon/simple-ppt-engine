package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.*;
import io.gitee.jinceon.core.Chart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Order(70)
@Slf4j
public class ChartProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return shape instanceof IChart;
    }

    @Override
    public void process(IShape shape, DataSource dataSource) {
        IChart iChart = (IChart) shape;
        // wps has AlternativeTextTitle and AlternativeText
        // PowerPoint only has AlternativeText
        String spel = shape.getAlternativeText();
        SpelExpressionParser parser = new SpelExpressionParser();
        Chart chart = (Chart) parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        log.debug("spel: {}, chart: {}", spel, chart);
        if(chart == null){
            return;
        }
        IChartData chartData = iChart.getChartData();
        IChartDataWorkbook chartDataWorkbook = chartData.getChartDataWorkbook();
        log.debug("spel : {}, template range: {}", spel, chartData.getRange());
        int workSheetIndex = 0;
        int seriesRow = 0;
        int CategoriesColumn = 0;
        String[] categories = chart.getCategories();
        for (int row = 0; row < categories.length; row++) {
            chartDataWorkbook.getCell(workSheetIndex, row+1, CategoriesColumn).setValue(categories[row]);
        }
        Chart.Pair[] series = chart.getSeries();
        for (int col = 0; col < series.length; col++) {
            chartDataWorkbook.getCell(workSheetIndex, seriesRow, col+1).setValue(series[col].getLabel());
        }
        Object[][] data = chart.getData();
        for (int row = 0; row < categories.length; row++) {
            for (int col = 0; col < series.length; col++) {
                chartDataWorkbook.getCell(workSheetIndex, row+1, col+1).setValue(data[row][col]);
            }
        }
        String newRange = String.format("Sheet1!$A$1:$%s$%d", number2Char(series.length) +1, categories.length +1);
        log.debug("spel: {}, new range: {}",spel, newRange);
        chartData.setRange(newRange);
        IChartCellCollection cells = chartDataWorkbook.getCellCollection(chartData.getRange(), true);
        cells.forEach(iChartDataCell -> log.debug("cell: row={} col={} value={}", iChartDataCell.getRow(), iChartDataCell.getColumn(), iChartDataCell.getValue()));
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
