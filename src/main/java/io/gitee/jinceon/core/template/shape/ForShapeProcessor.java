package io.gitee.jinceon.core.template.shape;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.model.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

/**
 * #for = items
 * 默认用最后一行、最后一列来做模板复制。
 * 删也是从最后一行、最后一列开始删除。
 * todo 自动伸缩的时候只操作数据局域，表头表尾不动
 * when `items` is null or empty array (list), slide will be removed
 */
@Order(1000)
@Slf4j
public class ForShapeProcessor implements ShapeProcessor {
    private static final String DIRECTIVE = "#for";
    @Override
    public boolean supports(String directive) {
        return directive.equals(DIRECTIVE);
    }

    @Override
    public Object parseDirective(String expression, DataSource dataSource) {
        SpelExpressionParser parser = new SpelExpressionParser();
        Object o = parser.parseExpression(expression).getValue(dataSource.getEvaluationContext());
        log.debug("expression:{}, value:{}", expression, o);
        return o;
    }

    @Override
    public void process(XSLFShape shape, Object context) {
        if(!(shape instanceof XSLFTable)){
            throw new UnsupportedOperationException("only supports `pptx Table` currently, `ppt Table` is under construction");
        }
        if(!(context instanceof Table)){
            throw new IllegalArgumentException("please use `Table.class`");
        }
        XSLFTable iTable = (XSLFTable) shape;
        Table table = (Table) context;
        List<XSLFTableRow> uiRows = iTable.getRows();
        int cols = iTable.getNumberOfColumns();
        int rowDifference = uiRows.size() - table.getRowCount();
        int colDifference = cols - table.getColumnCount();
        while(rowDifference < 0){
            // ui + row
            // iTable.addRow(); // 加的是一个空行
            iTable.insertRow(iTable.getNumberOfRows());
            rowDifference++;
            log.debug("ui行不够，加一行");
        }
        while(rowDifference > 0){
            // ui - row
            iTable.removeRow(rowDifference--);
            log.debug("ui行多了，删一行");
        }
        while(colDifference < 0){
            // ui + col
            iTable.addColumn(); // 加的是一个空列
            colDifference++;
            log.debug("ui列不够，加一列");
        }
        while(colDifference > 0){
            // ui - col
            iTable.removeColumn(colDifference--);
            log.debug("ui列多了，删一列");
        }
        // 把`可选文字`里的 `#for = #table` 里的指令部分 `#for = `去掉。
        // 目前不去也没关系，相当于多赋值了一个for变量，整个表达式的结果还是 table
    }
}
