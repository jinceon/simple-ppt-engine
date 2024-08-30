package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.model.Chart;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.utils.MatrixUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Order(70)
@Slf4j
public class ChartDataProcessor implements DataProcessor {
    @Override
    public boolean supports(XSLFShape shape) {
        if(!(shape instanceof XSLFGraphicFrame)){
            return false;
        }
        if(shape instanceof XSLFTable){
            return false;
        }
        String text = ShapeHelper.getAlternativeText(shape);
        return StringUtils.hasText(text) && text.contains("#");

    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
        XSLFChart iChart = ((XSLFGraphicFrame)shape).getChart();
        // wps has AlternativeTextTitle and AlternativeText
        // PowerPoint only has AlternativeText
        String spel = ShapeHelper.getAlternativeText(shape);
        SpelExpressionParser parser = new SpelExpressionParser();
        Chart chart = (Chart) parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        log.debug("spel: {}, chart: {}", spel, chart);
        if(chart == null){
            return;
        }
        XSSFWorkbook chartDataWorkbook = null;
        try {
            iChart.setWorkbook(null);// for-slide指令复制出来的chart会引用同一个workbook
            chartDataWorkbook = iChart.getWorkbook();
        } catch (IOException e) {
            log.error("I/O exception", e);
        } catch (InvalidFormatException e) {
            log.error("invalid format", e);
        }
        int workSheetIndex = 0;
        int seriesRow = 0;
        int categoriesColumn = 0;
        String[] categories = chart.getCategories();
        Chart.Pair[] series = chart.getSeries();
        XSSFSheet sheet = chartDataWorkbook.getSheetAt(workSheetIndex);
        log.debug("before rendering, spel : {}, template range: {}", spel, sheet.getDimension().formatAsString());
        CellRangeAddress newRange = new CellRangeAddress(0, categories.length, 0, series.length);
        sheet.setDimensionOverride(newRange);
        log.debug("spel: {}, new range: {}",spel, newRange.formatAsString());
        makesureRange(sheet, newRange);
        boolean debug = log.isDebugEnabled();
        Object[][] matrix = new Object[0][];
        if(debug){
            matrix = new Object[categories.length+1][series.length+1];
        }
        for (int row = 0; row < categories.length; row++) {
            sheet.getRow(row+1).getCell(categoriesColumn).setCellValue(categories[row]);
            if(debug){
                matrix[row+1][categoriesColumn] = categories[row];
            }
        }
        for (int col = 0; col < series.length; col++) {
            sheet.getRow(seriesRow).getCell(col+1).setCellValue(series[col].getLabel());
            if(debug){
                matrix[seriesRow][col+1] = series[col].getLabel();
            }
        }
        Double[][] data = chart.getData();
        for (int row = 0; row < categories.length; row++) {
            for (int col = 0; col < series.length; col++) {
                sheet.getRow(row+1).getCell(col+1).setCellValue(data[row][col]);
                if(debug) {
                    matrix[row + 1][col + 1] = data[row][col];
                }
            }
        }
        if(debug) {
            log.debug(MatrixUtil.visual(matrix));
        }
        // 自动缩容。组合图表缩且只缩最后一个
        autoScale(chart, iChart);

        int lastColIndex = 0;
        for(XDDFChartData chartData: iChart.getChartSeries()) {
            XDDFDataSource<String> cat2 = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                    new CellRangeAddress(1, categories.length, 0, 0));
            log.debug("category range: {}", cat2.getDataRangeReference());
            int minSeries = Math.min(chartData.getSeriesCount(), series.length);
            for(int i=0;i<minSeries;i++){
                XDDFChartData.Series iSeries = chartData.getSeries(i);
                XDDFNumericalDataSource<Double> val2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(1, categories.length, lastColIndex + 1, lastColIndex + 1));
                log.debug("series {} range: {}", series[lastColIndex].getLabel(), val2.getDataRangeReference());
                iSeries.setTitle(series[lastColIndex].getLabel(), new CellReference(sheet.getRow(seriesRow).getCell(lastColIndex + 1)));
                iSeries.replaceData(cat2, val2);
                lastColIndex++;
            }
            iChart.plot(chartData);
            try {
                log.debug("after plot range: {}", iChart.getWorkbook().getSheetAt(workSheetIndex).getDimension().formatAsString());
            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }
            if (chart.getCustomizeFunction() != null) {
                chart.getCustomizeFunction().accept(iChart);
            }
        }
    }

    /**
     * 自动缩容
     * @param chart
     * @param iChart
     */
    private static void autoScale(Chart chart, XSLFChart iChart) {
        int seriesCountOfData = chart.getSeries().length;
        int seriesCountOfUI = 0;
        List<XDDFChartData> chartDatas = iChart.getChartSeries();
        for(XDDFChartData chartData: chartDatas) {
            seriesCountOfUI += chartData.getSeriesCount();
        }
        for (int i = 0; i < seriesCountOfUI - seriesCountOfData; i++) {
            for(int j=chartDatas.size();j>0;j--) {
                int count = chartDatas.get(j-1).getSeriesCount();
                if(count > 0){
                    chartDatas.get(j-1).removeSeries(count-1);
                    break;//跳出内，继续外
                }
            }
        }
    }

    private void makesureRange(XSSFSheet sheet, CellRangeAddress range) {
        int firstRow = range.getFirstRow();
        int firstColumn = range.getFirstColumn();
        int lastRow = range.getLastRow();
        int lastColumn = range.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                XSSFRow row = sheet.getRow(rowIn);
                if (row == null) {
                    row = sheet.createRow(rowIn);
                }
                XSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    row.createCell(colIn);
                }
            }
        }
    }
}
