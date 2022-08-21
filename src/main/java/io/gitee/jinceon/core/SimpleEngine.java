package io.gitee.jinceon.core;

import com.aspose.slides.*;
import io.gitee.jinceon.processor.ChartProcessor;
import io.gitee.jinceon.processor.PaginationProcessor;
import io.gitee.jinceon.processor.TableProcessor;
import io.gitee.jinceon.processor.TextProcessor;

import java.io.InputStream;
import java.util.*;

public class SimpleEngine {
    private boolean defaultProcessorsLoaded = false;
    private final Map context = new HashMap();
    private final List<Processor> processors = new ArrayList<>();
    private final Presentation presentation;

    public SimpleEngine(String file){
        this.presentation = new Presentation(file);
    }

    public SimpleEngine(InputStream is){
        this.presentation = new Presentation(is);
    }

    public void addContext(String key, Object value){
        this.context.put(key, value);
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
            //notes in every slide 每一页的备注
            INotesSlideManager notesSlideManager = slide.getNotesSlideManager();
            ITextFrame notesTextFrame = notesSlideManager.getNotesSlide().getNotesTextFrame();

            IShapeCollection shapes = slide.getShapes();
            for(IShape shape: shapes.toArray()){

            }
        }
    }
}
