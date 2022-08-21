package io.gitee.jinceon.core;

import com.aspose.slides.*;
import io.gitee.jinceon.processor.ChartProcessor;
import io.gitee.jinceon.processor.PaginationProcessor;
import io.gitee.jinceon.processor.TableProcessor;
import io.gitee.jinceon.processor.TextProcessor;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class SimpleEngine {
    private boolean defaultProcessorsLoaded = false;
    private final List<Processor> processors = new ArrayList<>();
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
            this.processors.add(new ChartProcessor());
            this.processors.add(new PaginationProcessor());
            this.processors.add(new TableProcessor());
            this.processors.add(new TextProcessor());
            this.defaultProcessorsLoaded = true;
        }
        this.processors.sort(Comparator.comparingInt(o -> o.getClass().getAnnotation(Order.class).value()));
    }

    public List<Processor> getProcessors(){
        loadProcessors();
        return Collections.unmodifiableList(this.processors);
    }

    public void addProcessor(Processor processor){
        this.processors.add(processor);
    }

    public void process(){
        loadProcessors();
        ISlideCollection slides = presentation.getSlides();
        for(ISlide slide: slides.toArray()){
            IShapeCollection shapes = slide.getShapes();
            for(IShape shape: shapes.toArray()){
                for(Processor processor: this.processors){
                    if(processor.supports(shape)) {
                        processor.process(shape, this.dataSource);
                        break;
                    }
                }
            }
        }
    }
}
