# Image 图片
本引擎对图片的处理比较简单。  
先在ppt模板里随意插入一个图片，将长宽设置为自己需要的大小。  
然后在`可选文字`里设置好图片的数据源即可
(如下图的`#img1`对应Example里的`dataSource.setVariable("img1", png);`)。 

![image processor example](../images/image.png)
## Example 示例
```java
    SimpleEngine engine = new SimpleEngine("src/test/resources/image.pptx");
    DataSource dataSource = new DataSource();
    byte[] png = Files.readAllBytes(Paths.get("src/test/resources/image.png"));
    byte[] jpg = Files.readAllBytes(Paths.get("src/test/resources/image.jpg"));
    dataSource.setVariable("img1", png);
    dataSource.setVariable("img2", jpg);
    engine.setDataSource(dataSource);
    engine.process();
    String outputFile = "src/test/resources/test-image.pptx";
    engine.save(outputFile);
```
## Tips 提示
对图片的大小未做特殊处理，所以不管你的图片尺寸是怎样的，最终都会被渲染成`展位图`图片的大小。
