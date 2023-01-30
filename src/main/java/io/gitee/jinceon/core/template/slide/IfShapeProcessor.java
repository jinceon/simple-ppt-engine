package io.gitee.jinceon.core.template.slide;

import io.gitee.jinceon.core.DataSource;
import io.gitee.jinceon.core.Order;
import io.gitee.jinceon.core.template.shape.ShapeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * #if =  expression
 * when expression evaluate result equal false, shape will be removed.
 */
@Order(1000)
@Slf4j
public class IfShapeProcessor implements ShapeProcessor {
    private static final String DIRECTIVE = "#if";
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
        if(!Boolean.TRUE.equals(context)){
            // aspose 删除和隐藏 肉眼看起来效果似乎一样
            // 但poi 没有隐藏。remove后会导致data processor阶段的shape丢失，只能重新一次新的循环
            log.debug("#if=false set shape `{}` invisible", shape.getShapeName());
            shape.getParent().removeShape(shape);
        }
    }
}
