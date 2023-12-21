# 局部管理器

- [局部管理器](#局部管理器)
  - [简介](#简介)
  - [SpringLayout](#springlayout)
  - [GroupLayout](#grouplayout)
  - [AbsoluteLayout](#absolutelayout)

## 简介

- [BoxLayout](layout_boxlayout.md)

## SpringLayout

Spring 通过定义组件的边距实现布局。边距使用 Spring 对象表示，每个 Spring 对象具有 4 个属性值：minimum, maximum, preferred, value，其中 value 是真实值。

## GroupLayout

把多个组件按区域划分到不同的 Group，再根据各个 Group 相对于水平轴和垂直轴的排列来管理。



## AbsoluteLayout

Absolute Layout或者Null Layout，以坐标x,y定制组件的绝对位置。

设置方法
- 选择Absolute Layout，拖放到 `JFrame` 或 `JPanel` 中。
- 界面上会实时显示当前的位置和组件大小。

