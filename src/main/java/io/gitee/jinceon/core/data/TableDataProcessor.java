package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.Table;
import io.gitee.jinceon.utils.MatrixUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.StringWriter;
import java.util.List;

@Order(80)
@Slf4j
public class TableDataProcessor implements DataProcessor {
    @Override
    public boolean supports(XSLFShape shape) {
        if(!(shape instanceof XSLFTable)){
            return false;
        }
        String text = ShapeHelper.getAlternativeText(shape);
        return StringUtils.hasText(text) && text.contains("#");
    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
        // wps has AlternativeTextTitle and AlternativeText
        // PowerPoint only has AlternativeText
        XSLFTable iTable = (XSLFTable)shape;
        String spel = ShapeHelper.getAlternativeText(shape);
        SpelExpressionParser parser = new SpelExpressionParser();
        Table table = (Table) parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        if (table == null) {
            log.debug("spel: {}, no associated with Table or Table is null, ignored", spel);
            return;
        }
        int dataRowCountOfUI = iTable.getNumberOfRows();
        int dataColCountOfUI = iTable.getNumberOfColumns();
        Object[][] tableData = table.getData();
        int rowCountOfData = table.getRowCount();
        int colCountOfData = table.getColumnCount();

        if (rowCountOfData > dataRowCountOfUI || colCountOfData > dataColCountOfUI) {
            log.error("ui is smaller than data %n {}", MatrixUtil.visual(tableData));
            throw new IllegalArgumentException(String.format("Table: %s, ui size is only %d*%d, actually need %d*%d, too small to fill in",
                    spel, dataRowCountOfUI, dataColCountOfUI, rowCountOfData, colCountOfData));
        }

        List<XSLFTableRow> rows = iTable.getRows();
        StringWriter ui = new StringWriter();
        ui.append("before process, ").append(spel).append(" in ui display like: \n");
        if(log.isDebugEnabled()) {
            for (int row = 0; row < rows.size(); row++) {
                XSLFTableRow iRow = rows.get(row);
                ui.append(String.valueOf(row)).append("\t");
                List<XSLFTableCell> cells = iRow.getCells();
                for (XSLFTableCell cell: cells) {
                    ui.append(cell.getText()).append("\t");
                }
                ui.append("\n");
            }
            log.debug(ui.toString());
        }
        StringWriter sw = new StringWriter();
        sw.append(" writing data to table ").append(spel).append("\n");
        for (int row = 0; row < tableData.length; row++) {
            XSLFTableRow iRow = rows.get(row);
            sw.append(String.valueOf(row)).append("\t");
            for (int col = 0; col < tableData[row].length; col++) {
                List<XSLFTableCell> cells = iRow.getCells();
                XSLFTableCell cell = cells.get(col);
                sw.append(String.valueOf(tableData[row][col])).append("\t");
                if(tableData[row][col]!=null) {
                    cell.setText(String.valueOf(tableData[row][col]));
                }
            }
            sw.append("\n");
        }
        log.debug(sw.toString());
        if(table.getCustomizeFunction() != null){
            table.getCustomizeFunction().apply(iTable);
        }
    }
}
