# JScrollPane

## 简介

`JScrollPane` 提供在较小区域内显示大型组件的功能，通过滚动条显示当前不可见的部分。

`JScrollPane` 的结构如下所示：

<img src="./images/image-20240411201726862.png" alt="image-20240411201726862" style="zoom:45%;" />

- 2 个 `JScrollBar` 用于滚动；
- 2 个 `JViewport` 用于 row 和 column 标题；
- 4 个在角落显示的 `Component` 对象；
- 1 个中心的 `JViewport`。

所有这些组件由 `ScrollPaneLayout` 管理。

!!! caution
    `JScrollPane` 只支持滚动轻量级组件。不应该向容器添加重量级 AWT 组件。

## 创建 JScrollPane

`JScrollPane` 有 4 个构造函数：

```java
public JScrollPane()
JScrollPane scrollPane = new JScrollPane();

public JScrollPane(Component view)
Icon icon = new ImageIcon("largeImage.jpg");
JLabel imageLabel = new JLabel(icon);
JScrollPane scrollPane = new JScrollPane(imageLabel);

public JScrollPane(int verticalScrollBarPolicy, int horizontalScrollBarPolicy)
JScrollPane scrollPane = new 
    JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

public JScrollPane(Component view, int verticalScrollBarPolicy,
    int horizontalScrollBarPolicy)
JScrollPane scrollPane = new JScrollPane(imageLabel,
    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
```

构造函数提供了指定组件、水平和垂直滚动策略。

默认情况下，滚动条只在需要时显示。下表是 JScrollPane 中用于设置滚动策略的常量：

|策略|说明|
|---|---|
|VERTICAL_SCROLLBAR_AS_NEEDED|需要时才显示垂直滚动条|
|HORIZONTAL_SCROLLBAR_AS_NEEDED|需要时才显示水平滚动条| 
|VERTICAL_SCROLLBAR_ALWAYS|总是显示垂直滚动条|
|HORIZONTAL_SCROLLBAR_ALWAYS|总是显示水平滚动条|
|VERTICAL_SCROLLBAR_NEVER|从不显示滚动条|
|HORIZONTAL_SCROLLBAR_NEVER| 从不显示滚动条|

## 修改 Viewport view

如果在创建 JScrollPane 时指定了需要滚动的组件，那么只需显示 JScrollPane。如果在创建时没有关联组件，或者稍后向更改组件，那么有两种方式可以关联新的组件。

首先，可以通过修改 `viewportView` 属性设置：

```java
scrollPane.setViewportView(dogLabel);
```

第二种，先获取 JViewport 引用，然后设置其 view 属性：

```java
scrollPane.getViewport().setView(dogLabel);
```

两种方式本质相同、

## Scrollable 接口

AWT 组件，如 `List`，当选项太多而无法