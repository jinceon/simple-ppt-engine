package org.apache.poi.xslf.usermodel;

public class ShapeHelper {
    public static String getAlternativeText(XSLFShape shape){
        return shape.getCNvPr().getDescr();
    }

    public static void setAlternativeText(XSLFShape shape, String text){
        shape.getCNvPr().setDescr(text);
    }
}
