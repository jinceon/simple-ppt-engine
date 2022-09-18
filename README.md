# simple-ppt-engine 简便PPT引擎
named it `simple-ppt-engine` doesnot mean itself is a simple engine,
but hope the way you use it is simple.  
之所以取名“简便PPT引擎”，不是想说它本身很简单，而是希望你使用它的方式非常简便。
## quick-start 快速起步
### step-1 Prepare a template 准备PPT模板
![demo](docs/images/text-demo.png)

### step-2 Prepare data 准备数据
```java
import io.gitee.jinceon.core.DataSource;

import java.util.HashMap;

public class HelloPPT {
    public static void main(String[] args) {

        // 1. create engine instance 创建引擎
        SimpleEngine engine = new SimpleEngine("hello-ppt.pptx");

        // 2. add data to dataSource 填充数据
        DataSource dataSource = new DataSource();
        User user = new User("jinceon");
        Map props = new HashMap();
        props.put("key1", "value1");
        props.put("key2", "value2");
        dataSource.setVariable("user", user);
        dataSource.setVariable("props", props);
        engine.setDataSource(dataSource);

        // 3. render data to template 将数据渲染到模板上
        engine.process();
        
        // 4. save result
        engine.save("hello-ppt-renderd.pptx");
    }
}
```
### Screen SnapShot
![dmeo-output](docs/images/demo-output.png)

## Design Introduction 设计思路
[Introduction](docs/INTRODUCTION.md)

## Data和UI绑定
[Data和UI绑定](docs/BindData.md);

## ShowCase 功能演示
### Text Style 文本样式
the style will be unchanged after replacing data to variables.   
变量替换成数据后，会保留渲染前变量的样式。

[Text Documentation](docs/processor/Text.md)
### Table 表格
easily render a collection (such as List) to a Table.  
便捷地将一个集合（如List）填充到表格上。

[Table Documentation](docs/processor/Table.md)

### Chart 图表
in fact we don't care about which kind of chart you actually use, 
we just manipulate the data table nested in the shape.  
事实上我们不关心你用的是饼图、直方图还是什么别的，我们只操作内置在PPT里的表格数据。

[Chart Documentation](docs/processor/Chart.md)

### If 指令
[if指令](docs/directive/if.md)

### For 指令
[for指令](docs/directive/for.md)

### Define your own Processor 自定义处理器
1. let your class implements an interface. 自定义处理器实现3个接口中的一个。
    > DataProcessor, ShapeProcessor, SlideProcessor
2. set an order use `@Order`. 配合`@Order`合理设置处理顺序。
3. use `addProcess()` to add your processor into engine's processors list. 加入到引擎的处理器列表。

## RoadMap 产品蓝图
- [dynamic style 动态样式](docs/roadmap/dynamic-style.md)
- apache poi implementation 用开源的apache poi替换掉商业闭源的aspose

## Todo 待办
- unit test and coverage 单元测试及代码覆盖率

## FAQ 常见问题
[常见问题](docs/faq.md)