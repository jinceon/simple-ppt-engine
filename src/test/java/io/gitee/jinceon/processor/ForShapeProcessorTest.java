package io.gitee.jinceon.processor;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.Table;
import io.gitee.jinceon.processor.data.Trend;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ForShapeProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/for-shape.pptx");
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
        tableA.setData(headers, trends, Table.Direction.VERTICAL);
        int thisYear = LocalDate.now().getYear();
        Object[][] dynamicHeader = new Object[][]{
                {null},{thisYear-2},{thisYear-1},{thisYear}
        };
        tableA.merge(Table.Position.LEFT, dynamicHeader);
        dataSource.setVariable("tableA", tableA);

        engine.setDataSource(dataSource);
        engine.process();
        String outputfile = "src/test/resources/test-for-shape.pptx";
        engine.save(outputfile);

        XMLSlideShow outputPpt = new XMLSlideShow(Files.newInputStream(Paths.get(outputfile)));
        List<XSLFSlide> slides = outputPpt.getSlides();
        for(int page=1;page<slides.size();page++){
            List<XSLFShape> shapes = slides.get(page).getShapes();
            XSLFTable uiOftableA = null;
            for(XSLFShape shape: shapes){
                if(shape instanceof XSLFTable){
                    uiOftableA = (XSLFTable)shape;
                }
            }
            Assertions.assertNotNull(uiOftableA);
            Assertions.assertEquals(uiOftableA.getNumberOfRows(), tableA.getRowCount());
            Assertions.assertEquals(uiOftableA.getNumberOfColumns(), tableA.getColumnCount());
            for(int col=0;col<trends.size();col++){
                Assertions.assertEquals(uiOftableA.getCell(0, col+1).getText(), trends.get(col).getGoods(), "not equal"+0+col);
                Assertions.assertEquals(uiOftableA.getCell(1, col+1).getText(), String.valueOf(trends.get(col).getTheYearBeforeLast()), "tableB not equal"+1+"-"+col);
                Assertions.assertEquals(uiOftableA.getCell(2, col+1).getText(), String.valueOf(trends.get(col).getLastYear()), "tableB not equal"+2+"-"+col);
                Assertions.assertEquals(uiOftableA.getCell(3, col+1).getText(), String.valueOf(trends.get(col).getThisYear()), "tableB not equal"+3+"-"+col);
            }
            for(int row=1;row<dynamicHeader.length;row++) {
                Assertions.assertEquals(uiOftableA.getCell(row, 0).getText(), String.valueOf(dynamicHeader[row][0]));
            }
        }

    }
}