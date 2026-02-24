# ButtonBar

## 简介

`ButtonBar` 是一个 `HBox`，额外添加操作系统特异性的按钮位置。换言之，可以使用 `setButtonData(Node, ButtonData)` 对任何 `Node` 进行注释，然后通过 `getButtons()` 添加该 `Node`。`ButtenBar` 根据 `ButtonData` 信息确定按钮顺序。



