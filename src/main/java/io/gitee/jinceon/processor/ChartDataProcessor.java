package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.*;
import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.utils.MatrixUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

@Order(70)
@Slf4j
public class ChartDataProcessor implements DataProcessor {
    @Override
    public boolean supports(XSLFShape shape) {
        String text = "";//shape.getAlternativeText();
        return shape instanceof Object // XSLFChart
                && StringUtils.hasText(text)
                && text.contains("#");

    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
//        IChart iChart = (IChart) shape;
        // wps has AlternativeTextTitle and AlternativeText
        // PowerPoint only has AlternativeText
        String spel = "";//shape.getAlternativeText();
        SpelExpressionParser parser = new SpelExpressionParser();
        Chart chart = (Chart) parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        log.debug("spel: {}, chart: {}", spel, chart);
        if(chart == null){
            return;
        }
        /*IChartData chartData = iChart.getChartData();
        log.debug("before rendering, iChart series:{}, categories: {}",chartData.getSeries().size(), chartData.getCategories().size());
        IChartDataWorkbook chartDataWorkbook = chartData.getChartDataWorkbook();
        log.debug("spel : {}, template range: {}", spel, chartData.getRange());
        int workSheetIndex = 0;
        int seriesRow = 0;
        int categoriesColumn = 0;
        String[] categories = chart.getCategories();
        Chart.Pair[] series = chart.getSeries();
        boolean debug = log.isDebugEnabled();
        Object[][] matrix = new Object[0][];
        if(debug){
            matrix = new Object[categories.length+1][series.length+1];
        }
        for (int row = 0; row < categories.length; row++) {
            chartDataWorkbook.getCell(workSheetIndex, row+1, categoriesColumn).setValue(categories[row]);
            if(debug){
                matrix[row+1][categoriesColumn] = categories[row];
            }
        }
        for (int col = 0; col < series.length; col++) {
            chartDataWorkbook.getCell(workSheetIndex, seriesRow, col+1).setValue(series[col].getLabel());
            if(debug){
                matrix[seriesRow][col+1] = series[col].getLabel();
            }
        }
        Object[][] data = chart.getData();
        for (int row = 0; row < categories.length; row++) {
            for (int col = 0; col < series.length; col++) {
                chartDataWorkbook.getCell(workSheetIndex, row+1, col+1).setValue(data[row][col]);
                matrix[row+1][col+1]=data[row][col];
            }
        }
        String newRange = String.format("Sheet1!$A$1:$%s$%d", number2Char(series.length+1) , categories.length +1);
        log.debug("spel: {}, new range: {}",spel, newRange);
        chartData.setRange(newRange);
        if(debug) {
            log.debug(MatrixUtil.visual(matrix));
        }
         */
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
