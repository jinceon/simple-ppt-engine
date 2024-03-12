package io.gitee.jinceon.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import java.util.function.Consumer;
@Data
@AllArgsConstructor
public class Image {
    private byte[] bytes;

    private Consumer<XSLFPictureShape> customizeFunction;

    public Image(byte[] bytes){
        this.bytes = bytes;
    }
}
