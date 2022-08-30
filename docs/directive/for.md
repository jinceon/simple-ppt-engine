# for directive ( for 指令)
## 格式
```text
#for = #variable
```
例如
```text
#for = #users
```
## Slide 幻灯片
针对幻灯片，可以在`备注`里添加for指令。  
当`expression`执行为`true`时，幻灯片保留；为`false`时幻灯片删除。  
![for指令-幻灯片](../images/for-slide.png)
## Shape 形状(目前只支持Table 表格)
可以在`可选文本`里添加for指令，让表格实现自动伸缩。    
假设模板UI上的表格大小是`4 * 4`。  
当传入的表格数据是 `5 * 5` 时，UI会自动扩展（引擎默认取最后一行、最后一列为模板复制）。  
当传入的表格数据是 `3 * 3` 时，UI会自动裁剪（引擎默认从最后一行、最后一列开始删除）。
![for指令-表格](../images/for-shape.png)