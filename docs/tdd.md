# 关于单元测试和TDD
一直很想写单元测试，但是苦于不知道该怎么写。  
顺便吐槽一下，我相信绝对大多数国内的程序员都不写单元测试，并且相当大一部分人坚定地认为自己只是没时间写，
绝不承认自己压根就不懂得怎么写。

春节前刚好工作不那么繁忙，恰好在《极客时间》上看到徐昊老师的《徐昊·TDD项目实战70讲》，内心激动不已。  
网上搜索单元测试的教程，大多数就是教你怎么使用junit，介绍@Before等注解，介绍断言各种Assert。  
而徐昊老师这门课程才是真正教我们怎么去写单元测试（即使我们不用TDD的方式去做，也可以很好地指导我们写测试）。

一开始做这个项目的时候，我想着这个项目一定要开始尝试写单元测试。  
然后也确实写了一些测试代码。但是它非常地不“单元”。  
以`TextDataProcessor`为例，
```java
public class TextDataProcessor implements DataProcessor {

    @Override
    public void process(XSLFShape shape, DataSource dataSource) {
        XSLFAutoShape textFrame = (XSLFAutoShape) shape;
        List<XSLFTextParagraph> paragraphs = textFrame.getTextParagraphs();
        for (XSLFTextParagraph paragraph : paragraphs) {
            List<XSLFTextRun> portions = paragraph.getTextRuns();
            for (XSLFTextRun portion : portions) {
                String spel = portion.getRawText();
                log.debug("spel: {}", spel);
                SpelExpressionParser parser = new SpelExpressionParser();
                String text = String.valueOf(parser.parseExpression(spel, new TemplateParserContext()).getValue(dataSource.getEvaluationContext()));
                log.debug("spel: {}, text: {}", spel, text);
                portion.setText(text);
            }
        }
    }
}
```
下面是我写的测试代码。
```java
class TextDataProcessorTest {

    @Test
    void process() throws IOException {
        SimpleEngine engine = new SimpleEngine("src/test/resources/text.pptx");
        DataSource dataSource = new DataSource();
        dataSource.setVariable("name", "jinceon");
        engine.setDataSource(dataSource);
        engine.process();
        String outputFile = "src/test/resources/test-text.pptx";
        engine.save(outputFile);

        XMLSlideShow outputPpt = new XMLSlideShow(new FileInputStream(outputFile));
        List<XSLFShape> shapes = outputPpt.getSlides().get(0).getShapes();
        Assertions.assertEquals(2, shapes.size());
        String text1 = ((XSLFAutoShape)(shapes.get(0))).getText();
        Assertions.assertEquals("Hello, jinceon ", text1);
        String text2 = ((XSLFAutoShape)(shapes.get(1))).getText();
        Assertions.assertEquals("it can keep the style of variable jinceon", text2);
    }
}
```
我写的测试代码更像是功能测试的。它一点也不“单元”。  
我是从整个`SimpleEngine`的层面去写的，根本就不是针对`TextDataProcessor`这个模块。   
开始我也在反复思考，要不要改成仅测试`process(XSLFShape shape, DataSource dataSource)`这个函数。  
也反复看了`状态验证`和`行为验证`的章节，还有`Mock`的相关文档。  
某一天我再次翻看《实战项目一：命令行参数解析/07丨TDD中的测试（3）：集成测试还是单元测试？》这一章节的时候，突然就豁然开朗。  
我需要的“单元测试”就是**能提供快速反馈的低成本的研发测试（Developer Test）**，我压根就不需要纠结它到底是单元测试还是集成测试。  
如果我自己去构造一个XSLFShape对象，会很复杂；如果我用Mock来隔离XSLFShape，那只就能写成行为验证。  
而对于我这个引擎的功能来说，本来就不关注PPT的内部抽象，从用户视角来看，它关注的是最终的UI呈现效果。  
而且对于这个引擎来说，刚好也经历过一次内部实现从Aspose切换到Apache POI的过程。  
如果要改进我的设计，我其实就不应该在测试代码里过度依赖于Apache POI的一些类抽象（如XSLFShape），
这样未来如果再从Apache POI切换到别的实现时，就不会对代码实现和测试实现产生过多的影响。

