package org.apache.poi.xslf.usermodel;

public class ShapeHelper {
    public static String getAlternativeText(XSLFShape shape){
        return shape.getCNvPr().getDescr();
    }
}
