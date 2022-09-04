# Dynamic Style 动态样式
在工作中，曾经遇到过一些动态的样式需求。  
比如正数（增长）显示为红色字体，负数（降低）显示为绿色字体等。
甚至在旁边加一个向上、向下的箭头小图标。  
这些特殊的需求当然可以通过`自定义处理器`来实现。  
但是有没有办法做一些抽象呢？在不增加太大复杂度的前提下让使用者能更简便地实现。  
未想好怎样实现，先记录在此抛砖引玉。

## Text 文本
假设 percent > 0 显示红色， percent < 0 显示绿色。  
目前的想法是引擎不做处理。  
用户放置多个变量如（#positivePercent、#negativePercent），各设置不同的样式。  
percent > 0 时positivePercent = percent，negativePercent = ""
## Table 表格
假设某个单元格内的数据 > 0 显示红色， < 0 显示绿色。  
目前的想法是引擎做修改，增加一个样式对象。  
使用的时候单元格内容设置 
```java
table[row][col] = 123
```

改成  

```java
Formatter formatter = new Formatter(){
    @Override
    public void format(Object container, Object self){
        
    }
}
table[row][col] = new StyledValue(123, formatter)
```
## Chart 图表
实在太复杂，没想好。  
目前只能通过`自定义处理器`实现。