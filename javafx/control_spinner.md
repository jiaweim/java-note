# Spinner

- [Spinner](#spinner)
  - [简介](#%e7%ae%80%e4%bb%8b)

2020-05-16, 16:39
***

## 简介

单行文本字段，允许用户从有序序列中选择数值或对象。Spinner 一般提供上下箭头，用于选择值。从键盘的上下箭头也可以循环选择值。spinner 也允许直接输入值，虽然 combo boxes 也提供类似的功能，但是 spinner 不需要 drop down list。

Spinner 的值通过 `SpinnerValueFactory` 定义。SpinnerValueFactory 类提供的常用类型：

- IntegerSpinnerValueFactory
- DoubleSpinnerValueFactory
- ListSpinnerValueFactory

Spinner 有一个 TextField 子node，用于显示及修改当前值。不过 Spinner 默认不可编辑，通过 editable 属性设置可否编辑。

