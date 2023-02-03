package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.Chart;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.utils.MatrixUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.IOException;
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
        List<XDDFChartData> chartSeries = iChart.getChartSeries();
        if(chartSeries.size() > 1){
            log.warn("暂不支持组合图表");
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
                matrix[row+1][col+1]=data[row][col];
            }
        }
        if(debug) {
            log.debug(MatrixUtil.visual(matrix));
        }
        XDDFChartData chartData = iChart.getChartSeries().get(0);//暂不支持复合图表
        int seriesCount = chartData.getSeriesCount();
        for(int i=0;i<seriesCount-series.length;i++){
            chartData.removeSeries(0);//清空模板里多余的旧数据。for循环在list
        }
        XDDFDataSource cat2 = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(1, categories.length, 0, 0));
        log.debug("category range: {}", cat2.getDataRangeReference());
        for(int s=0; s< series.length; s++) {
            XDDFNumericalDataSource val2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(1, categories.length, s+1, s+1));
            log.debug("series {} range: {}", s, val2.getDataRangeReference());
            if(s>=seriesCount){
                XDDFChartData.Series series1 = chartData.addSeries(cat2, val2);
                series1.setTitle(series[s].getLabel(), new CellReference(sheet.getRow(seriesRow).getCell(s+1)));
            }else {
                XDDFChartData.Series series1 = chartData.getSeries(s);
                series1.setTitle(series[s].getLabel(), new CellReference(sheet.getRow(seriesRow).getCell(s+1)));
                series1.replaceData(cat2, val2);
            }
        }
        iChart.plot(chartData);
        try {
            log.debug("after plot range: {}", iChart.getWorkbook().getSheetAt(workSheetIndex).getDimension().formatAsString());
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
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