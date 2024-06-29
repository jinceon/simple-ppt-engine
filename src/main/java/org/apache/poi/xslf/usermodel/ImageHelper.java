package org.apache.poi.xslf.usermodel;

public class ImageHelper {
    public static void fixImage(XSLFPictureShape picture, XSLFPictureData pictureData) {
        String relId = picture.getSheet().getRelationId(pictureData);
        picture.getBlip().setExtLst(null);
        picture.getBlip().setEmbed(relId);
    }
}
