package io.gitee.jinceon.core;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class DataSource {
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

    public void setVariable(String name, Object value){
        this.evaluationContext.setVariable(name, value);
    }

    public EvaluationContext getEvaluationContext(){
        return this.evaluationContext;
    }
}
