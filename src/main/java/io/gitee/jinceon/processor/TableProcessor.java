package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.*;
import io.gitee.jinceon.core.Table;
import io.gitee.jinceon.utils.MatrixUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.StringWriter;

@Order(80)
@Slf4j
public class TableProcessor implements Processor {
    @Override
    public boolean supports(IShape shape) {
        return shape instanceof ITable;
    }

    @Override
    public void process(IShape shape, DataSource dataSource) {
        ITable iTable = (ITable) shape;
        // wps has AlternativeTextTitle and AlternativeText
        // PowerPoint only has AlternativeText
        String spel = shape.getAlternativeText();
        SpelExpressionParser parser = new SpelExpressionParser();
        Table table = (Table) parser.parseExpression(spel).getValue(dataSource.getEvaluationContext());
        if (table == null) {
            log.debug("spel: {}, no associated with Table or Table is null, ignored", spel);
            return;
        }
        int dataRowCountOfUI = iTable.getRows().size();
        int dataColCountOfUI = iTable.getColumns().size();
        Object[][] tableData = table.getData();
        int rowCountOfData = table.getRowCount();
        int colCountOfData = table.getColumnCount();

        if (rowCountOfData > dataRowCountOfUI || colCountOfData > dataColCountOfUI) {
            log.error("ui is smaller than data %n {}", MatrixUtil.visual(tableData));
            throw new IllegalArgumentException(String.format("Table: %s, ui size is only %d*%d, actually need %d*%d, too small to fill in",
                    spel, dataRowCountOfUI, dataColCountOfUI, rowCountOfData, colCountOfData));
        }

        IRowCollection rows = iTable.getRows();
        StringWriter ui = new StringWriter();
        ui.append("before process, ").append(spel).append(" in ui display like: \n");
        for (int row = 0; row < rows.size(); row++) {
            IRow iRow = rows.get_Item(row);
            ui.append(row + "\t");
            for (int col = 0; col < iRow.size(); col++) {
                ICell cell = iRow.get_Item(col);
                ui.append(cell.getTextFrame().getText()).append("\t");
            }
            ui.append("\n");
        }
        log.debug(ui.toString());
        StringWriter sw = new StringWriter();
        sw.append(" writing data to table ").append(spel).append("\n");
        for (int row = 0; row < tableData.length; row++) {
            IRow iRow = rows.get_Item(row);
            sw.append(row + "\t");
            for (int col = 0; col < tableData[row].length; col++) {
                ICell cell = iRow.get_Item(col);
                sw.append(String.valueOf(tableData[row][col])).append("\t");
                if(tableData[row][col]!=null) {
                    cell.getTextFrame().setText(String.valueOf(tableData[row][col]));
                }
            }
            sw.append("\n");
        }
        log.debug(sw.toString());
    }
}
