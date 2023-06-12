# FlowPane

## 简介

`FlowPane` 是一个简单的 layout，它以指定高度或宽度的行或列布局子节点。对水平布局，可以指定换行首选宽度；对垂直布局，可以指定首选高度。

`FlowPane` 适合子节点相对位置不重要的情形，例如，显示一系列的图片或按钮。`FlowPane` 对所有子节点以首选大小显示，行和列可以有不同的高度和宽度。

```ad-tip
水平显示的 `FlowPane` 可以从左到右，或从右到左排列子节点，由 `Node` 类中声明的 `nodeOrientation` 属性控制。默认值为 `NodeOrientation.LEFT_TO_RIGHT`，即从左到右排列子节点。如果希望从右到左，将其设置为 `NodeOrientation.RIGHT_TO_LEFT` 即可。该设置适用于所有的 layout。
```

`FlowPane` 的方向



FlowPane(Orientation orientation, double hgap, double vgap)
创建FlowPane，横向排列，指定组件间水平和垂直距离