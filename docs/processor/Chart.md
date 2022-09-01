# Chart 图表
## Requirements 前置条件
[Data和UI绑定](../BindData.md)

## Caution 特别注意
![chart](../images/chart.png)
`A2:A5`是categories，`B1:D1`是series。  
`B2:D5`才是data部分（上图马克笔标记部分）

调用`setData(List list)`来设置数据的时候，请注意data只应包含数据。  
考虑到java的习惯，我们也提供了`setDataWithCategories(List list, String categoryField)`来方便使用。