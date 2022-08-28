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

            this.slideProcessors.add(new DeleteSlideProcessor());
            this.slideProcessors.add(new HideSlideProcessor());
            this.slideProcessors.add(new PaginationSlideProcessor());

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
        ISlideCollection slides = presentation.getSlides();
        for(ISlide slide: slides.toArray()){
            //ppt下方备注备注
            String spel = StringUtils.trimAllWhitespace(slide.getNotesSlideManager().getNotesSlide().getNotesTextFrame().getText());
            if(StringUtils.hasText(spel)) {
                for (SlideProcessor slideProcessor : this.slideProcessors) {
                    if (slideProcessor.supports(spel)) {
                        Object context = slideProcessor.parseDirective(spel, dataSource);
                        slideProcessor.process(slide, context);
                        break;
                    }
                }
            }
            try{
                slide.getSlideNumber();
            }catch (Exception e){
                // when slide is deleted, get slide number will throw a NullPointerException
                continue;
            }
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
}
