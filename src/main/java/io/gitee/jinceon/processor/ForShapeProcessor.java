package io.gitee.jinceon.processor;

import com.aspose.slides.*;
import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.ShapeProcessor;
import io.gitee.jinceon.core.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

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
    public void process(IShape shape, Object context) {
        if(!(shape instanceof ITable)){
            throw new UnsupportedOperationException("only supports `Table` currently");
        }
        if(!(context instanceof Table)){
            throw new IllegalArgumentException("please use `Table.class`");
        }
        ITable iTable = (ITable) shape;
        Table table = (Table) context;
        IRowCollection uiRows = iTable.getRows();
        IColumnCollection uiCols = iTable.getColumns();
        int rowDifference = uiRows.size() - table.getRowCount();
        int colDifference = uiCols.size() - table.getColumnCount();
        IRow templateRow = uiRows.get_Item(uiRows.size() -1);
        IColumn templateCol = uiCols.get_Item(uiCols.size() -1);
        while(rowDifference < 0){
            // ui + row
            uiRows.addClone(templateRow, false);
            rowDifference++;
            log.debug("ui行不够，加一行");
        }
        while(rowDifference > 0){
            // ui - row
            uiRows.removeAt(rowDifference--, false);
            log.debug("ui行多了，删一行");
        }
        while(colDifference < 0){
            // ui + col
            uiCols.addClone(templateCol, false);
            colDifference++;
            log.debug("ui列不够，加一列");
        }
        while(colDifference > 0){
            // ui - col
            uiCols.removeAt(colDifference--, false);
            log.debug("ui列多了，删一列");
        }
        // 把`可选文字`里的 `#for = #table` 里的指令部分 `#for = `去掉。
        // 目前不去也没关系，相当于多赋值了一个for变量，整个表达式的结果还是 table
    }
}
