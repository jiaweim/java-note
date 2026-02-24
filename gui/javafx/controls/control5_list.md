# List Controls

2023-07-21, 14:40
****
ToggleButton 和 RadioButton group 都可以给用户展示一组元素，特点：

- 两者所有元素总是可见，当选项较多或占用很多空间
- 不支持用户自己添加选项

JavaFX 提供了一些控件，允许用户从一组元素中选择一个或多个元素。与 buttons 相比占用空间更小：

- ChoiceBox
- ComboBox
- ListView
- ColorPicker
- DatePicker

`ChoiceBox` 从较小元素 list 中选择 item。ComboBox 为 ChoiceBox 的高级版，支持编辑和修改 list 中的元素。ListView 支持多选。

ColorPicker 从标准色板或自定义色板中选择颜色。

DatePicker 从弹窗日历中选择日期，也可以输入文本格式的日期。

类图如下：

![](Pasted%20image%2020230721144029.png)
