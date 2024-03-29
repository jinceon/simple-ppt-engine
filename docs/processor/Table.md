# Table 表格
## Requirements 前置条件
[Data和UI绑定](../BindData.md)

## Example 示例
表格的核心是二维数组。  
因此填充数据的时候，只需要将一个二维数组按从上到下、从左到右的顺序依次填充即可。  
但是表格往往有表头标题栏（可能还有多行的、带分组的标题表头）、底部汇总栏等。
为了让构造二维数组的时候更方便，我们在表格的类实现上做了一些设计。
### Basic Table 基础表格
```
 1  1  1  1  1        → row
 2  2  2  2  2
 ↓
 col
```
```java
int[][] data = new int[2][5];
data[0] = new int[]{1,1,1,1,1};
data[1] = new int[]{2,2,2,2,2}; 
Table t = new Table(data);
// 当然，直接操作数组会很繁琐。更实际的例子一般是从数据库（或其他数据源）查出一个List。
List<User> users = findUsers();
class User {
    String name;
    String phone;
    String address;
}
String[] header = new String[]{ "name", "address", "phone"};
// 将 User 映射到 Object[] 的时候，用 header 来控制顺序
// Object[0] = user.name
// Object[1] = user.address
// Object[2] = user.phone
Table t1 = new Table();
t1.setData(header, users);
```

### Table with Headers 带表头（标题）的表格  

 table with header in the top  表头在表格顶部 
```java
 Table t = new Table(new Object[2][5]);
 Object[][] header = new Object[][]{"a","b","c","d","e"}; // Object[1][5]
 t.merge(Position.TOP, header);
```
<pre>
 a  b  c  d  e
 1  1  1  1  1
 2  2  2  2  2
</pre>
 ------------------------------------------------------------
 table with header in the left  表头在表格左侧  
```java
 Table t = new Table(new Object[3][4]);
 Object[][] header = new Object[][]{ {"a"},{"b"},{"c"}};// Object[3][1]
 t.merge(Position.LEFT, header);
```
<pre>
 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
</pre>
 ------------------------------------------------------------
 table with header in the left, and then another header in the top  
 先加一个左侧的表头，再在顶部加一个表头  
```java
 Table t = new Table(new Object[3][4]);
 t.merge(Position.LEFT, new Object[3][1]);
 t.merge(Position.TOP, new Object[1][5]);
```
<pre>
 A  B  C  D  E
 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
</pre>
 ------------------------------------------------------------
 table with header in the top, and then another header in the left  
 先在顶部加一个表头，再在左侧加一个表头  
```java
 Table t = new Table(new Object[3][4]);
 t.merge(Position.TOP, new Object[1][4])
 t.merge(Position.LEFT, new Object[4][1])
```

<pre>
 a  A  B  C  D
 b  1  2  3  4
 c  1  2  3  4
 d  1  2  3  4
 </pre>

### 复杂表格
其他的复杂表格，如多行表头、分组、底部带有合计汇总等，将表格合理拆分后，用merge方法合并成一个大表格即可。

### 动态表格（自动伸缩表格）
[for指令](../directive/for.md)  
假设模板UI上的表格大小是`4 * 4`。  
当传入的表格数据是 `5 * 5` 时，UI会自动扩展（引擎默认取最后一行、最后一列为模板复制）。  
当传入的表格数据是 `3 * 3` 时，UI会自动裁剪（引擎默认从最后一行、最后一列开始删除）。  
**注意1：引擎没有对表格样式（宽高）做特殊处理。**  
PPT 自己的逻辑是：  
1. 表格总高度随着行的增加/删除而变化。  
2. 表格总宽度不变，列宽自动按比例调整。

**注意2：引擎没有对表格内自动新增的单元格样式做处理。**  
如果你在设置样式的时候，是对整个表格设置的样式，那么新增的行也会自动生效。  

但如果你不是给表格设置，而是给单元格设置样式，那么新增的行里不会复制样式。

![for-table-template](../images/for-table-template.png)
另外，如果模板设置了字体，
![for-table-rendered](../images/for-table-rendered.png)
poi渲染的结果是丢失了字体，
![for-table-aspose-rendered](../images/for-table-aspose-rendered.png)
aspose渲染的结果会保留字体。  
如果要保留字体，商业用途的话，建议用aspose。  
如果用poi，就用`定制化函数钩子`重新设置字体。  
参见[表格导出，里面设置的字体大小样式，导出字体大小很大，这个怎么处理？](https://gitee.com/jinceon/simple-ppt-engine/issues/I9AQ4Q)

这种情况下，如果你的PPT页最多放一个5行的表格，建议设置模板的时候就插入5行，然后预先设置好样式。  
这样当实际传入少于5行时，也会自动清除多余的行。

但如果模板只放一行，原本是期望复制上一行的样式做为新增行样式的。
但实际未实现（aspose倒是支持，poi还是相对简陋）。  
考虑到实现成本，目前暂不考虑实现。

## 定制化函数钩子
表格是个非常常用的组件。  
同时也会有非常多的定制化需求，最常见的莫过于`合并单元格`。  
每次做一点小小的改动，都要去自定义processor，感觉确实太繁琐。
但是在engine内实现太具体的细节，又感觉不合适。  
因此增加了一个定制化函数钩子，可以在里面实现一些风格的定制化。  

```java
SimpleEngine engine = new SimpleEngine("src/test/resources/table-hook.pptx");
DataSource dataSource = new DataSource();
String[] headers = new String[]{
        "goods",
        "year",
        "price"
};
List<Trend> trends = new ArrayList<>();
trends.add(new Trend("cloth", 2021, 300));
trends.add(new Trend("cloth", 2022, 600));
trends.add(new Trend("cloth", 2023, 900));
trends.add(new Trend("drink", 2021, 400));
trends.add(new Trend("drink", 2022, 600));
trends.add(new Trend("drink", 2023, 800));

Table tableA = new Table();
tableA.setCustomizeFunction(table -> {
    table.mergeCells(1,3,0,0); // 合并第1列的2-4行单元格
    table.mergeCells(4,6,0,0); // 合并第1列的5-7行单元格
});
tableA.setData(headers, trends, Table.Direction.HORIZONTAL);
tableA.merge(Table.Position.TOP, new Object[1][1]);
dataSource.setVariable("tableA", tableA);

engine.setDataSource(dataSource);
engine.process();
String outputfile = "src/test/resources/test-table-hook.pptx";
engine.save(outputfile);
```
![自定义函数钩子实现合并单元格](../images/table-hook.png)