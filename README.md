# simple-ppt-engine 简便PPT引擎
named it `simple-ppt-engine` doesnot mean itself is a simple engine,
but hope the way you use it is simple.  
之所以取名“简便PPT引擎”，不意味着它本身很简单，而是希望你使用它的方式非常简便。
## quick-start 快速起步

```java
import java.util.HashMap;

public class HelloPPT {
    public static void main(String[] args) {

        // 1. create engine instance 创建引擎
        SimpleEngine engine = new SimpleEngine("hello-ppt.pptx");
        
        // 2. add data to context 填充数据
        User user = new User("jinceon");
        Map props = new HashMap();
        props.put("key1", "value1");
        props.put("key2", "value2");
        engine.addContext("user", user);
        engine.addContext("props", props);

        // 3. render data to template 将数据渲染到模板上
        engine.process();
    }
}
```
## showcase 功能演示
### Text Style 文本样式
the style will be unchanged after replacing data to variables.   
变量替换成数据后，会保留渲染前变量的样式。

### Table 表格
easily render a collection (such as List) to a Table.  
便捷地将一个集合（如List）填充到表格上。

### Chart 图表
in fact we don't care about which kind of chart you actually use, we just manipulate the data table nested in the shape.  
事实上我们不关心你用的是饼图、直方图还是什么别的，我们只操作内置在PPT里的表格数据。

### Pagination 分页
when there are so many rows in a List and can not display in only one slide,
you can use `#pagination` directive in slide note to define a pagination to split them to several slides.  
当一个集合的数据很多一页PPT展示不完时，可以在ppt备注里使用`#pagination`指令来设置分页，每页展示N行并自动分成若干页。

### Define your own Processor 自定义处理器
implements an interface `Processor` and set an order use `@Order`.  
实现接口`Processor`，配合`@Order`合理设置处理顺序。
```java
@Order(Integer.MAX_VALUE)
public class MyProcessor implements Processor {
    
    @Override
    public boolean supports(Shape shape){
        return true;
    }
    
    @Override
    public void process(Shape shape, Context context){
        shape.color = "#00FF00";
    }
}
```