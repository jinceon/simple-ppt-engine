package io.gitee.jinceon.core.data;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.model.FormItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.List;

@Order(70)
@Slf4j
public class FormDataProcessor implements DataProcessor {
    @Override
    public boolean supports(XSLFShape shape) {
        if(!(shape instanceof XSLFTable)){
            return false;
        }
        String text = ShapeHelper.getAlternativeText(shape);
        return StringUtils.hasText(text) && text.startsWith("#form");
    }

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
        XSLFTable iTable = (XSLFTable)shape;
        String formSpel = ShapeHelper.getAlternativeText(shape);
        Object rootObject = null;
        SpelExpressionParser parser = new SpelExpressionParser();
        if(formSpel.contains("=")){
            // #form = #user，把 #user 作为 rootObject
            rootObject = parser.parseExpression(formSpel).getValue(dataSource.getEvaluationContext());
        }

        List<XSLFTableRow> rows = iTable.getRows();
        for (XSLFTableRow iRow : rows) {
            List<XSLFTableCell> cells = iRow.getCells();
            for (XSLFTableCell cell : cells) {
                String spel = cell.getText();
                if(StringUtils.hasText(spel) && spel.contains("#{")){
                    log.debug("spel: {}", spel);
                    Object text = parser.parseExpression(spel, new TemplateParserContext())
                            .getValue(dataSource.getEvaluationContext(), rootObject);
                    log.debug("spel: {}, text: {}", spel, text);
                    // todo 更合适的做法是用renderText，考虑到普适性的同时为降低复杂度，先这样
                    if(text instanceof String){
//                        cell.setText((String) text);
                        TextHelper.setText(cell, (String) text);
                    }else if(text instanceof FormItem){
                        FormItem txt = (FormItem)text;
//                        cell.setText(txt.getText());
                        TextHelper.setText(cell, txt.getText());
                        if(txt.getCustomizeFunction() != null){
                            txt.getCustomizeFunction().accept(cell);
                        }
                    }
                }
            }
        }
    }
}
