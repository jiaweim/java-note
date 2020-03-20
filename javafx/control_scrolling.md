# CONTENTS
- [CONTENTS](#contents)
- [简介](#%e7%ae%80%e4%bb%8b)
- [ScrollBar](#scrollbar)
- [ScrollPane](#scrollpane)
# 简介
JavaFX 提供了两个支持滚动条的组件，`ScrollBar` 和 `ScrollPane`。它们一般不单独使用，而是为其他组件提供滚动条

# ScrollBar
`ScrollBar` 本身没有滚动条功能，它作为水平或垂直滚动条部分，让用户在一定范围内选择。开发者很少直接使用 `ScrollBar`，一般用于创建支持 scrolling 的组件，如 ScrollPane。

![](2019-06-05-15-31-11.png)

一个 ScrollBar 包含四个组成部分：
- Increment button，用于增加值
- Decrease button, 用于减少值
- thumb, 用于显示当前值
- track, thumb 滚动的范围

ScrollBar 包含一个最大值，最小值和一个当前值，默认分别为 100, 0, 0.
修改值的方式有三种：
- 代码 `setValue()`, `increment()`, `decrement` 方法
- 用于拖动 thumb
- 点击 increment 或 decrement 按钮

构造函数
```java
ScrollBar sb = new ScrollBar(); // 默认横向
sb.setOrientation(Orientation.VERTICAL); // 设置为纵向
```
# ScrollPane
`ScrollPane` 包含一个水平 `ScrollBar`、一个垂直 `ScrollBar`和一个 `content node`。

如果你想为多个 node 提供 scrollable view，可以将它们添加到一个 layout pane 中，如 GridPane，然后将 layout pane 作为 content node 添加到 ScrollPane 中。部分组件自带有 scrolling 功能，如 TextArea，就是按照这种方式处理的。

ScrollPane 包含许多属性：
|属性|说明|
|---|---|
|content|内容，Node 类型|
|pannable|拖动，按住鼠标右键、左键或同时按住左右键，default=false|
|fitToHeight, fitToWidth|content node 是否 resized 以充满整个viewport, default=false|
|hbarPolicy, vbarPolicy|enum ScrollBarPolicy 类型，可用值有 `ALWAYS`, `AS_NEEDED`, `NEVER`，用于设置何时显示 scrollbar，`ALWAYS` 表示一直闲置scrollbar|
|hmin, hmax, hvalue|水平 scrollbar 的最小值、最大值和当前值|
|vmin, vmax, vvalue|垂直 scrollbar 的最小值、最大值和当前值|
|prefViewportHeight, prefViewportWidth|preferred height and width|
|viewportBounds|viewport 的实际 bounds|
