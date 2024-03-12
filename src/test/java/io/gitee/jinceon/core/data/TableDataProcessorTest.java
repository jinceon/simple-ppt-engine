package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.model.Table;
import io.gitee.jinceon.processor.data.Trend;
import org.apache.poi.xslf.usermodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class TableDataProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/table.pptx");
        DataSource dataSource = new DataSource();
        String[] headers = new String[]{
                "goods",
                "theYearBeforeLast",
                "lastYear",
                "thisYear"
        };
        List<Trend> trends = new ArrayList<>();
        trends.add(new Trend("cloth",100,200,300));
        trends.add(new Trend("drink",400,500,600));
        trends.add(new Trend("food",700,800,900));

        Table tableA = new Table();
        tableA.setData(headers, trends, Table.Direction.HORIZONTAL);
        tableA.merge(Table.Position.TOP, new Object[1][1]);
        dataSource.setVariable("tableA", tableA);

        Table tableB = new Table();
        tableB.setData(headers, trends, Table.Direction.VERTICAL);
        int thisYear = LocalDate.now().getYear();
        Object[][] dynamicHeader = new Object[][]{
                {null},{thisYear-2},{thisYear-1},{thisYear}
        };
        tableB.merge(Table.Position.LEFT, dynamicHeader);
        dataSource.setVariable("tableB", tableB);

        engine.setDataSource(dataSource);
        engine.process();
        String outputfile = "src/test/resources/test-table.pptx";
        engine.save(outputfile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputfile));
        XSLFSlide slide = outputPpt.getSlides().get(0);
        List<XSLFShape> shapes = slide.getShapes();
        XSLFTable uiOftableA = null;
        XSLFTable uiOftableB = null;
        for(XSLFShape shape: shapes){
            if(shape instanceof XSLFTable){
                String tableId = ShapeHelper.getAlternativeText(shape);
                if("#tableA".equals(tableId)){
                    uiOftableA = (XSLFTable)shape;
                }else if("#tableB".equals(tableId)){
                    uiOftableB = (XSLFTable)shape;
                }
            }
        }
        Assertions.assertNotNull(tableA);
        for(int row=0;row<trends.size();row++){
            Assertions.assertEquals(uiOftableA.getCell(row+1, 0).getText(), trends.get(row).getGoods(), "tableA not equal:"+(row+1)+"-"+0);
            Assertions.assertEquals(uiOftableA.getCell(row+1, 1).getText(), String.valueOf(trends.get(row).getTheYearBeforeLast()),"tableA not equal"+(row+1)+"-"+1);
            Assertions.assertEquals(uiOftableA.getCell(row+1, 2).getText(), String.valueOf(trends.get(row).getLastYear()),"tableA not equal"+(row+1)+"-"+2);
            Assertions.assertEquals(uiOftableA.getCell(row+1, 3).getText(), String.valueOf(trends.get(row).getThisYear()),"tableA not equal"+(row+1)+"-"+3);
        }
        Assertions.assertNotNull(tableB);
        for(int col=0;col<trends.size();col++){
            Assertions.assertEquals(uiOftableB.getCell(0, col+1).getText(), trends.get(col).getGoods(), "tableB not equal"+0+col);
            Assertions.assertEquals(uiOftableB.getCell(1, col+1).getText(), String.valueOf(trends.get(col).getTheYearBeforeLast()), "tableB not equal"+1+"-"+col);
            Assertions.assertEquals(uiOftableB.getCell(2, col+1).getText(), String.valueOf(trends.get(col).getLastYear()), "tableB not equal"+2+"-"+col);
            Assertions.assertEquals(uiOftableB.getCell(3, col+1).getText(), String.valueOf(trends.get(col).getThisYear()), "tableB not equal"+3+"-"+col);
        }
        for(int row=1;row<dynamicHeader.length;row++) {
            Assertions.assertEquals(uiOftableB.getCell(row, 0).getText(), String.valueOf(dynamicHeader[row][0]));
        }
    }
}