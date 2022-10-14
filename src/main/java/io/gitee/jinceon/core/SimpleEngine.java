package io.gitee.jinceon.core;

import io.gitee.jinceon.processor.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class SimpleEngine {
    private boolean defaultProcessorsLoaded = false;
    private final List<DataProcessor> dataProcessors = new ArrayList<>();
    private final List<SlideProcessor> slideProcessors = new ArrayList<>();
    private final List<ShapeProcessor> shapeProcessors = new ArrayList<>();
    private DataSource dataSource;
    private XMLSlideShow presentation;

    public SimpleEngine(String file){
        try {
            this.presentation = new XMLSlideShow(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleEngine(InputStream is){
        try {
            this.presentation = new XMLSlideShow(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String outputFile){
        try {
            this.presentation.write(new FileOutputStream(outputFile));
        } catch (IOException e) {
            log.error("write to file failed", e);
        }
    }

    public void save(OutputStream os){
        try {
            this.presentation.write(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private void loadProcessors(){
        if(!this.defaultProcessorsLoaded) {
            this.dataProcessors.add(new ChartDataProcessor());
            this.dataProcessors.add(new TableDataProcessor());
            this.dataProcessors.add(new TextDataProcessor());

            this.slideProcessors.add(new IfSlideProcessor());
            this.slideProcessors.add(new ForSlideProcessor());

            this.shapeProcessors.add(new IfShapeProcessor());
            this.shapeProcessors.add(new ForShapeProcessor());

            this.defaultProcessorsLoaded = true;
        }
        this.dataProcessors.sort(Comparator.comparingInt(o -> o.getClass().getAnnotation(Order.class).value()));
        this.slideProcessors.sort(Comparator.comparingInt(o -> o.getClass().getAnnotation(Order.class).value()));
        this.shapeProcessors.sort(Comparator.comparingInt(o -> o.getClass().getAnnotation(Order.class).value()));
    }

    public List<DataProcessor> getDataProcessors(){
        loadProcessors();
        return Collections.unmodifiableList(this.dataProcessors);
    }

    public List<SlideProcessor> getSlideProcessors() {
        loadProcessors();
        return Collections.unmodifiableList(this.slideProcessors);
    }

    public List<ShapeProcessor> getShapeProcessors() {
        loadProcessors();
        return Collections.unmodifiableList(this.shapeProcessors);
    }

    public void addProcessor(Processor processor){
        if(processor instanceof SlideProcessor){
            this.slideProcessors.add((SlideProcessor) processor);
        }else if(processor instanceof ShapeProcessor){
            this.shapeProcessors.add((ShapeProcessor) processor);
        }else if(processor instanceof DataProcessor){
            this.dataProcessors.add((DataProcessor) processor);
        }

    }

    public void process(){
        loadProcessors();
        for(XSLFSlide slide: new ArrayList<>(presentation.getSlides())){
            //ppt下方备注备注
            String spel = getTextFromNotes(slide);
            int spliter = spel.indexOf("=");
            // spel = “#if = true”，前面至少要有#号+至少一个字符才有意义
            if(spliter > 2) {
                String directive = StringUtils.trimAllWhitespace(spel.substring(0, spliter));
                String expression = spel.substring(spliter+1);//等号本身不算
                for (SlideProcessor slideProcessor : this.slideProcessors) {
                    if (slideProcessor.supports(directive)) {
                        Object context = slideProcessor.parseDirective(expression, dataSource);
                        slideProcessor.process(slide, context);
                        break;
                    }
                }
            }
        }
        //不能在同一个循环，因为可能发生slide的增删，已经不是同一批幻灯片了
        for(XSLFSlide slide:presentation.getSlides()){
            List<XSLFShape> shapes = new ArrayList<>(slide.getShapes());
            for(XSLFShape shape: shapes){
                 String spel = ShapeHelper.getAlternativeText(shape);
                int spliter = spel.indexOf("=");
                // spel = “#if = true”，前面至少要有#号+至少一个字符才有意义
                if(spliter > 2) {
                    String directive = StringUtils.trimAllWhitespace(spel.substring(0, spliter));
                    String expression = spel.substring(spliter + 1);//等号本身不算
                    for (ShapeProcessor shapeProcessor : this.shapeProcessors) {
                        if (shapeProcessor.supports(directive)) {
                            Object context = shapeProcessor.parseDirective(expression, dataSource);
                            shapeProcessor.process(shape, context);
                            break;
                        }
                    }
                }
            }
            // poi 无法隐藏shape。remove后会导致data processor阶段的shape丢失，只能重新一次新的循环
            List<XSLFShape> shapesAfterShapeProcess = slide.getShapes();
            for(XSLFShape shape: shapesAfterShapeProcess) {
                for (DataProcessor dataProcessor : this.dataProcessors) {
                    if (dataProcessor.supports(shape)) {
                        dataProcessor.process(shape, this.dataSource);
                        break;
                    }
                }
            }
        }
    }

    private static String getTextFromNotes(XSLFSlide slide) {
        XSLFNotes notes = slide.getNotes();
        if(notes == null){
            return "";
        }
        for (XSLFShape shape : notes) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape txShape = (XSLFTextShape) shape;
                for (XSLFTextParagraph xslfParagraph : txShape.getTextParagraphs()) {
                    log.debug("get notes {}", xslfParagraph.getText());
                    return xslfParagraph.getText();
                }
            }
        }
        return "";
    }
}
