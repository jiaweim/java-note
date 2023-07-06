# Scene 概述

2023-07-06, 10:45
****
## 1. 简介

Scene 包含 Stage 的可视化内容。javafx.scene.Scene 一次最多绑定一个 Stage。对已有 Stage 的 Scene，将其添加到另一个 Stage，会自动从上一个 Stage 分离。Stage 一次也只能有一个 Scene。

Scene 是 scene graph 的容器，scene graph 是所有可视化元素组成的树形结构。树的节点以 `javafx.scene.Node` 类表示。

scene graph 的树形结构如下所示：
![|500](Root%20Node.png)
JavaFX 对 scene graph 中的分支节点和叶节点都提供对应的类。

## 2. Node 类图

`javafx.scene.Nod`e 是所有节点的超类。下面是 nodes 的部分类图：

![](Pasted%20image%2020230706102448.png)
Scene 总有一个根节点。如果根节点 resizable，如 `Region` 及其子类，当 scene 调整大小，根节点也会随之调整大小。

Group 是 nonresizable 的 Parent 节点，也可以作为 Scene 的根节点，此时 scene graph 不会随 Scene 的大小的变化而变化，因此 scene graph 可能被截断，只显示一部分。

Parent 是 branch nodes 的基类。ImageView, Canvas, Shape 则是 leaf node 类型。

## 3. Scene 属性

Scene 常用属性

|类型|名称|说明|
|---|---|---|
|`ObjectProperty<Cursor>`|cursor|Scene 的鼠标光标|
|`ObjectProperty<Paint>`|fill|Scene 的填充背景|
|`ReadOnlyObjectProperty<Node>`|focusOwner|Scene 中持有焦点的 node|
|`ReadOnlyDoubleProperty`|height|Scene 高度|
|`ObjectProperty<Parent>`|root|scene graph 的根节点|
|`ReadOnlyDoubleProperty`|width|Scene 宽度|
|`ReadOnlyObjectProperty<Window>`|window|Scene 的 Window|
|`ReadOnlyDoubleProperty`|x|Scene 在 Window 的水平位置|
|`ReadOnlyDoubleProperty`|y|Scene 在 Window 的垂直位置|
