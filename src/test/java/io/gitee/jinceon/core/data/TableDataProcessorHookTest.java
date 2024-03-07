package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.SimpleEngine;
import io.gitee.jinceon.core.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xslf.usermodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TableDataProcessorHookTest {

    @Data
    @AllArgsConstructor
    static
    class Trend{
        private String goods;
        private int year;
        private int price;
    }
    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/table-hook.pptx");
        DataSource dataSource = new DataSource();
        String[] headers = new String[]{
                "goods",
                "year",
                "price"
        };
        List<Trend> trends = new ArrayList<>();
        trends.add(new Trend("cloth", 2021, 300));
        trends.add(new Trend("cloth", 2022, 600));
        trends.add(new Trend("cloth", 2023, 900));
        trends.add(new Trend("drink", 2021, 400));
        trends.add(new Trend("drink", 2022, 600));
        trends.add(new Trend("drink", 2023, 800));

        Table tableA = new Table();
        tableA.setCustomizeFunction(table -> {
            table.mergeCells(1,3,0,0);
            table.mergeCells(4,6,0,0);
            return null;
        });
        tableA.setData(headers, trends, Table.Direction.HORIZONTAL);
        tableA.merge(Table.Position.TOP, new Object[1][1]);
        dataSource.setVariable("tableA", tableA);

        engine.setDataSource(dataSource);
        engine.process();
        String outputfile = "src/test/resources/test-table-hook.pptx";
        engine.save(outputfile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputfile));
        XSLFSlide slide = outputPpt.getSlides().get(0);
        List<XSLFShape> shapes = slide.getShapes();
        XSLFTable uiOftableA = null;
        for(XSLFShape shape: shapes){
            if(shape instanceof XSLFTable){
                String tableId = ShapeHelper.getAlternativeText(shape);
                if("#tableA".equals(tableId)){
                    uiOftableA = (XSLFTable)shape;
                }
            }
        }
        Assertions.assertNotNull(tableA);
        XSLFTableCell cellCloth = uiOftableA.getCell(1, 0);
        Assertions.assertEquals("cloth", cellCloth.getText());
        Assertions.assertEquals(3, cellCloth.getRowSpan());

        XSLFTableCell cellDrink = uiOftableA.getCell(4, 0);
        Assertions.assertEquals("drink", cellDrink.getText());
        Assertions.assertEquals(3, cellDrink.getRowSpan());
    }
}