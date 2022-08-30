package io.gitee.jinceon.core;

import com.aspose.slides.*;
import io.gitee.jinceon.processor.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class SimpleEngine {
    private boolean defaultProcessorsLoaded = false;
    private final List<DataProcessor> dataProcessors = new ArrayList<>();
    private final List<SlideProcessor> slideProcessors = new ArrayList<>();
    private final List<ShapeProcessor> shapeProcessors = new ArrayList<>();
    private DataSource dataSource;
    private final Presentation presentation;

    static {
        License license = new License();
        try {
            InputStream  inputStream = new ClassPathResource("aspose-license.xml").getInputStream();
            license.setLicense(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleEngine(String file){
        this.presentation = new Presentation(file);
    }

    public SimpleEngine(InputStream is){
        this.presentation = new Presentation(is);
    }

    public void save(String outputFile){
        this.presentation.save(outputFile, SaveFormat.Pptx);
    }

    public void save(OutputStream os){
        this.presentation.save(os, SaveFormat.Pptx);
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
        if(processor instanceof SlideProcessor p){
            this.slideProcessors.add(p);
        }else if(processor instanceof ShapeProcessor p){
            this.shapeProcessors.add(p);
        }else if(processor instanceof SlideProcessor p){
            this.slideProcessors.add(p);
        }

    }

    public void process(){
        loadProcessors();
        for(ISlide slide: presentation.getSlides().toArray()){
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
        for(ISlide slide:presentation.getSlides().toArray()){
            IShapeCollection shapes = slide.getShapes();
            for(IShape shape: shapes.toArray()){
                for(DataProcessor dataProcessor: this.dataProcessors){
                    if(dataProcessor.supports(shape)) {
                        dataProcessor.process(shape, this.dataSource);
                        break;
                    }
                }
            }
        }
    }

    private static String getTextFromNotes(ISlide slide) {
        INotesSlide notes = slide.getNotesSlideManager().getNotesSlide();
        if(notes == null){
            return "";
        }
        return notes.getNotesTextFrame().getText();
    }
}
