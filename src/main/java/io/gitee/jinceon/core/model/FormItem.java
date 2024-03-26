package io.gitee.jinceon.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xslf.usermodel.XSLFTableCell;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
public class FormItem {

    private String text;

    private Consumer<XSLFTableCell> customizeFunction;

    public FormItem(String text){
        this.text = text;
    }

}
