# if directive ( if 指令)
## 格式
```text
#if = expression
```
例如
```text
#if = #user
当user为false或null时删除，其他情况（含数字0、空字符串""、空数组或空列表[]等）则保留
```

## Slide 幻灯片
针对幻灯片，可以在`备注`里添加if指令。  
当`expression`执行为`true`时，幻灯片保留；为`false`时幻灯片删除。
![if指令-幻灯片](../images/if-slide.png)
## Shape 形状
针对形状，可以在`可选文本`里添加if指令。  
当`expression`执行为`true`时，形状保留；为`false`时形状隐藏。
![if指令-形状](../images/if-shape.png)