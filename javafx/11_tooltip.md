# Tooltip

- [Tooltip](#tooltip)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [Tooltip 属性](#tooltip-%e5%b1%9e%e6%80%a7)
- [构造方法](#%e6%9e%84%e9%80%a0%e6%96%b9%e6%b3%95)
- [为 node 设置 `Tooltip`](#%e4%b8%ba-node-%e8%ae%be%e7%bd%ae-tooltip)
  - [方法一](#%e6%96%b9%e6%b3%95%e4%b8%80)
  - [方法二](#%e6%96%b9%e6%b3%95%e4%ba%8c)

## 简介

tool tip 是一个弹窗组件，用于显示 node 的额外信息，鼠标悬停在 node 上时显示其内容。如下图所示，黑框中为 tool tip 弹窗：

![](images/2019-12-03-20-43-51.png)

类：`Tooltip`

`Tooltip` 继承自 `PopupControl`，可以包含文本和图像。内部由 `Label` 实现。

## Tooltip 属性

| 属性           | 类型                  | 说明                                   |
| -------------- | --------------------- | -------------------------------------- |
| text           | `String`              | 要显示的文本                           |
| graphic        | `Node`                | 要显示的 icon                          |
| contentDisplay | enum `ContentDisplay` | graphic 相对 text 的位置，默认 `LEFT`  |
| textAlignment  | enum `TextAlignment`  | 文本对齐方式                           |
| textOverrun    | enum `OverrunStyle`   | 当空间不足以显示文本时采取的策略       |
| wrapText       | boolean               | 当文本过长时，是否换行，default=false  |
| graphicTextGap | double                | text 和 graphics 之间的距离，default=4 |
| font           | Font                  | 文本字体                               |
| activated      | boolean               | 鼠标悬停时为true，显示 tool tip        |

# 构造方法
```java
// Create a Tooltip with No text and no graphic
Tooltip tooltip1 = new Tooltip();
// Create a Tooltip with text
Tooltip tooltip2 = new Tooltip("Closes the window");
```

# 为 node 设置 `Tooltip`

## 方法一
使用 `Tooltip` 的静态方法 `install()` 为 node 设置 `Tooltip`：
```java
Button saveBtn = new Button("Save");
Tooltip tooltip = new Tooltip("Saves the data");
// Install a tooltip
Tooltip.install(saveBtn, tooltip);
...
// Uninstall the tooltip
Tooltip.uninstall(saveBtn, tooltip);
```

## 方法二
由于 `Tooltip` 使用十分频繁，所有也有简单的设置方法：
```java
Button saveBtn = new Button("Save");
// Install a tooltip
saveBtn.setTooltip(new Tooltip("Saves the data"));
...
// Uninstall the tooltip
saveBtn.setTooltip(null);
```