# simple-ppt-engine 简便PPT引擎
之所以取名“简便PPT引擎”，是希望开发者使用起来非常简便。
## quick-start 快速起步

```java
import java.util.HashMap;

public class HelloPPT {
    public static void main(String[] args) {

        // 1. create engine instance 创建引擎
        SimpleEngine engine = new SimpleEngine();
        
        // 2. load template 加载模板
        Template template = new File("hello-ppt.pptx");

        // 3. add data to context 填充数据
        User user = new User("jinceon");
        Map props = new HashMap();
        props.put("key1", "value1");
        props.put("key2", "value2");
        engine.addContext("user", user);
        engine.addContext("props", props);
        
        // 4. render data to template 将数据渲染到模板上
        engine.process(template);
    }
}
```
## showcase 功能演示
### Text Style 文本样式
the style will be unchanged after replacing data to variables.   
变量替换成数据后，会保留渲染前变量的样式。

### Table 表格


### Chart
