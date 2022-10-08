# Introduction 介绍
让我们从一个简单的PPT开始。  
新建一个PPT，在第一张幻灯片上添加一个文本框，文本框里写上“你好,延春”。  
这个过程里，我们涉及到的内容有：幻灯片(Slide)、形状(Shape)、数据(Data)。  
我们详细介绍下这些名词。

## 名词解释
1. 幻灯片 Slide  
   每一张幻灯片都是一个Slide对象
2. 形状 Shape  
   每一个文本框、每一个表格、每一个图表、一张图片都属于Shape
3. 数据 Data  
   文本框里的文字、表格里的数据、图表的数据，都属于Data

## Core Principles 核心原理
### Processing Order 处理逻辑

### 处理器
引擎将处理器抽象为两类：模板处理器 (TemplateProcessor) 和 数据处理器(DataProcess)。

同类型下所有处理器是互斥的，按`@Order`定义的顺序依次匹配，一个对象被匹配上的处理器执行后，不会继续执行其他的同类型处理器。  
所以同一个slide最多只会被一个SlideProcessor处理，同一个shape只会被最多一个ShapeProcessor和最多一个DataProcessor处理。

#### 模板处理器 TemplateProcessor
`模板处理器`只处理模板，而不处理数据。  

* 处理幻灯片的，我们叫它`幻灯片处理器(SlideProcessor)`。
比如增加一张幻灯片、删除一张幻灯片等。
* 处理形状的，我们叫它`形状处理器(ShapeProcessor)`。
它只处理形状(Shape)，如删除Shape（如删除一个图表）、修改Shape（如调整表格的行、列）。
增加shape的话需要你自定义处理器，主要是考虑到新增的Shape样式、摆放位置等较难控制，
所以我们的建议是将可能出现的Shape先做在模板里，不需要的时候删除即可。

#### 数据处理器 DataProcessor
`数据处理器`刚好相反，它只处理数据，而不修改模板。  
 
所以如果你想自定义一个处理器，你需要用`@Order`设置合适的顺序。  
当你在一个`5x4`的表格试图填充`6x5`的数据时，它会出错，因为空间不够。  
所以如果表格是动态的行、列，你需要在前面的模板处理阶段就先裁剪好合适的行、列。  
**别担心，针对表格我们内置了for指令以实现自动动态伸缩，见[for指令](directive/for.md)**  
引擎使用SpEL（Spring Expression Language）来设置数据源。  
比如一个文本框的文本`hello, #{ #name }`，`#{ #name }`表示用变量`name`的值来替换。  
比如一个表格，需要将其指向一个名为`tableA`的数据源，可以使用`#tableA`。
