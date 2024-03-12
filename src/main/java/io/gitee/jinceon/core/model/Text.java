package io.gitee.jinceon.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class Text {

    private String text;

    private Consumer<XSLFTextRun> customizeFunction;

    public Text(String text){
        this.text = text;
    }

}
