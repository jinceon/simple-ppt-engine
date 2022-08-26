package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.*;
import io.gitee.jinceon.core.Table;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Order(80)
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
        Table table = (Table) parser.parseExpression(spel,
                new TemplateParserContext()).getValue(dataSource.getEvaluationContext());
        if (table == null) {
            return;
        }
        System.out.println("spel: " + spel + ", table: " + table);
        int dataRowCountOfUI = iTable.getRows().size();
        int dataColCountOfUI = iTable.getColumns().size();
        Object[][] tableData = table.getData();
        int rowCountOfData = tableData.length;
        int colCountOfData = tableData[0].length;

        if (rowCountOfData > dataRowCountOfUI || colCountOfData > dataColCountOfUI) {
            throw new IllegalArgumentException(String.format("ui size is only %d*%d, actually need %d*%d, too small to fill in",
                    dataRowCountOfUI, dataColCountOfUI, rowCountOfData, colCountOfData));
        }

        IRowCollection rows = iTable.getRows();
        for (int row = 0; row < rows.size(); row++) {
            IRow iRow = rows.get_Item(row);
            System.out.printf("%d \t", row);
            for (int col = 0; col < iRow.size(); col++) {
                ICell cell = iRow.get_Item(col);
                System.out.printf("'%s'\t", cell.getTextFrame().getText());
            }
            System.out.println();
        }

        for (int row = 0; row < tableData.length; row++) {
            IRow iRow = rows.get_Item(row);
            System.out.printf("%d \t", row);
            for (int col = 0; col < tableData[row].length; col++) {
                ICell cell = iRow.get_Item(col);
                System.out.printf("'%s'\t", tableData[row][col]);
                cell.getTextFrame().setText(String.valueOf(tableData[row][col]));
            }
            System.out.println();
        }

    }
}
