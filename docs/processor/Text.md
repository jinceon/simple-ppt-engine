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

中英文输入、大小写切换的时候，经常会出现单词被默认切割成2个portion的情况。  
比如模板输入 #{ #nickName } 大概率在调试的时候会发现它被切成`#{ #` 和 `nickName`等等，即使它们样式已经是一样。
遇到这种情况，建议把`#{ #nickName }`这几个字符选中，重新设置一次字体，或者设置下加粗再取消加粗，这样大概率就可以强制将它们合并为一个portion。