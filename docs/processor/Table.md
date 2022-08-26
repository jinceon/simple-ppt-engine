# Table 表格
表格的核心是二维数组。  
因此填充数据的时候，只需要将一个二维数组按从上到下、从左到右的顺序依次填充即可。  
但是表格往往有表头标题栏（可能还有多行的、带分组的标题表头）、底部汇总栏等。
为了让构造二维数组的时候更方便，我们在表格的类实现上做了一些设计。
## Basic Table 基础表格
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
List<User> users = new ArrayList<>();

```


 ------------------------------------------------------------
 table with header in the top
 Table t = new Table(new Object[2][5]);
 t.merge(Position.TOP, new Table(new Object[1][5]));
 a  b  c  d  e
 1  1  1  1  1
 2  2  2  2  2
 ------------------------------------------------------------
 table with header in the left
 Table t = new Table(new Object[3][4]);
 t.merge(Position.LEFT, new Object[3][1]))

 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
 ------------------------------------------------------------
 table with header in the left, and then another header in the top
 Table t = new Table(new Object[3][4]);
 t.merge(Position.LEFT, new Table(new Object[3][1])))
 t.merge(Position.TOP, new Table(new Object[1][5])))

 A  B  C  D  E
 a  1  2  3  4
 b  1  2  3  4
 c  1  2  3  4
 ------------------------------------------------------------
 table with header in the top, and then another header in the left
 Table t = new Table(new Object[3][4]);
 t.merge(Position.TOP, new Table(new Object[1][4])))
 t.merge(Position.LEFT, new Table(new Object[4][1])))

 a  A  B  C  D
 b  1  2  3  4
 c  1  2  3  4
 d  1  2  3  4
 </pre>