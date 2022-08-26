# Text 文本
文本处理是最简单的。  
将需要动态替换的文字用占位符代替即可。  
引擎使用SpEL（Spring Expression Language）来处理占位符的替换。  
假设文本框的内容是`你好, #{ #name }`，当我们给引擎增加一个变量`name=延春`时，文本框的文字最终会被替换成`你好，延春`。
![text processor example](../images/text1.png)
## Example 示例
```java
    SimpleEngine engine = new SimpleEngine("src/test/resources/text.pptx");
    DataSource dataSource = new DataSource();
    dataSource.setVariable("name", "jinceon");
    engine.setDataSource(dataSource);
    engine.process();
    engine.save("src/test/resources/test-text.pptx");
```
## Tips 提示
由于PPT文本框的文字可以设置各种各样的样式，为了保证替换占位符后的文字样式和之前一致，引擎在处理文本的时候需要更精细。  
让我们来理解几个概念。  
以本文开头的文本为例，我们把`Hello, #{ #name }`这一整串文字叫一个段落(Paragraph)。  
按样式将样式相同的部分叫做一个片段(Portion)，因此`Hello, #{ #name }`这里其实有2个片段，分别是`Hello,`和`#{ #name }`。
如果我们将`#{ #name }`中的部分文字用不同的样式，那么它们会被划分在不同的portion下（`#{ `、`#na`、`me }`），在替换变量时就会出错。
![text processor tips](../images/text2.png)
