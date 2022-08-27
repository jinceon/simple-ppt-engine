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