# Scene

- [Scene](#scene)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [Focus Owner](#focus-owner)

2020-05-19, 09:03
***

## 简介

Scene 是 JavaFX scene graph的顶级容器，是 scene graph 的容器，scene graph 是所有节点的树形结构表示，包含Stage中显示的所有元素。

Scene 常用属性
|Type|Name|Property and Description|
|---|---|---|
|ObjectProperty<Cursor>|cursor|It defines the mouse cursor for the scene|
|ObjectProperty<Paint>|fill|It defines the background fill of the Scene|
|ReadOnlyObjectProperty<Node>|focusOwner|It defines the node in the Scene that owns the focus|
|ReadOnlyDoubleProperty|height|It defines the height of the Scene|
|ObjectProperty<Parent>|root|It defines the root Node of the scene graph|
|ReadOnlyDoubleProperty|width|It defines the width of the Scene|
|ReadOnlyObjectProperty<Window>|window|It defines the Window for the Scene|
|ReadOnlyDoubleProperty|x|It defines the horizontal location of the Scene on the window|
|ReadOnlyDoubleProperty|y|It defines the vertical location of the Scene on the window|

## Focus Owner

在 Scene 中，一次只能有一个 node 获得聚焦。`Scene` 的 `focusOwner` 属性记录当前聚焦的 Node。另外，`focusOwner` 是只读属性，如果需要聚焦到某个node，可以调用 `Node` 的 `requestFocus()` 方法。

使用 `Scene` 的 `getFocusOwner()` 方法返回当前聚焦的 node，当没有聚焦任何 node 时，该方法返回 `null`。

另外要理解 focus owner 和持有 focus 的差别，对多个 Scene，可以有多个 focus owner，但是最多只有一个 Node 持有 focus。通过 `Node` 的 `focused` 属性可以查看是否持有 focus。具体判断逻辑：

```java
Scene scene;
...
Node focusOwnerNode = scene.getFocusOwner();
if (focusOwnerNode == null) {
// The scene does not have a focus owner
} else if (focusOwnerNode.isFocused()) {
// The focus owner is the one that has the focus
} else {
// The focus owner does not have the focus
}
```
