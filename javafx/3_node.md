# Node

## 属性

| 属性    | 说明                                                                                                              |
| ------- | ----------------------------------------------------------------------------------------------------------------- |
| id      | 节点的 id，用于在 scene graph 中查找特定 Node。如果节点 id 为 "myId"，则可以使用 scene.lookup("#myId") 查找该节点 |
| parent  | 节点的父节点。如果该节点还未添加到 Scene graph 中，则为 null                                                      |
| scene   | 节点所在的 scene。如果该节点还未添加到 scene 中，则为 null                                                        |
| style   | 该节点 CSS 的字符串表示                                                                                           |
| visible | 是否作为 scene graph 的内容进行渲染                                                                               |

### String ID

node 可以设置一个 id 值用于识别 node，对应 `idProperty` 属性，该属性类似于CSS的选择器，在使用 CSS 时，可以使用该 id 识别节点。

该 id 的唯一性，由用户自己把握。

```java
public Node lookup(String selector)
```

该 `Node` 功能：从当前 Node 出发，找到第一个和该选择器匹配的节点或子节点。如果当前是 `Parent` 类型，则其子节点也会被查找。例如，如果一个节点的 id 为 "myId"，则可以使用 `scene.lookup("#myId")` 找到该节点。

### focusTraversableProperty

指定 `Node` 是否为焦点（focus）遍历循环的一部分。当该值为 true，使用焦点遍历快键键可以将焦点从其它 `Node` 移到当前 `Node`，或者从当前 `Node` 移除。

在桌面系统中，一般使用 `TAB` 键向前移动焦点，使用 `SHIFT+TAB` 向后移动焦点。

在创建 `Scene` 后，焦点一般在 `focusTraversable`为 true 的 `Node` 上。

`Node#requestFocus()` 方法可以使 `Node` 获得输入焦点，并且 `Node` 所在的顶级窗口成为 focus 窗口。node 要获得焦点，首先必须是 scene 的 一部分，并且其所有的父容器都是可见且非 disabled 状态。

每个 scene 最多只有一个 node 获得 focus，不过获得焦点还不能够输入，除非 node 所在的 scene 归属的 `Stage` 可见且处于活动状态。

## 转换

可以对任何 Node 进行转换操作，转换包括平移（translation）、旋转（rotation）、缩放（scaling）和剪切（shearing）。

### 平移（translation）

Node 的坐标系原点沿着 x 或 y 轴移动。例如，在原点 (x=0, y=0) 绘制矩形 (width=100, height=50)，然后沿着 x 轴平移 10 (x=10)，则矩形出现在 (x=10, y=0)，矩形的长宽不变。注意，平移的是原点，而不是矩形的 x 值。

平移值一般采用整数值，这样保证了像素点的映射关系。

### 旋转（rotation）

坐标系绕着指定点旋转，使得 node 表现为旋转效果。以矩形为例，在(x=0, y=0) 的矩形(width=100, height=30)，顺时针绕原点 (pivotX=0, pivotY=0) 旋转90°，此时绘制的矩形就好像在 (x=0,y=0) （width=-30, height=100）的矩形。
注意，在旋转后，矩形的 x, y, width, heigth 都没有变化（依然是相对本地坐标空间），变化的是矩形的坐标空间。

### 缩放（scaling）

根据缩放因子，Node 被方法或缩小。缩放使得坐标空间的本地坐标乘上了缩放因子。和旋转一样，缩放是围绕一个轴点（pivot）进行的。

### 剪切（shearing）

也称为歪斜（skew），剪切变换旋转某个坐标轴，使得 x 和 y 轴不再垂直。

可以同时进行多个变换，通过 `ObservableList` 类型的 `transforms` 变量即可。

## CSS

Node 类包含的 `id`, `styleClass` 和 `style` 变量用于实现 CSS 个性化。`id` 和 `styleClass` 用于 CSS 样式表中识别 Node，`style` 变量包含直接用于 node 的风格设置。

[CSS 参考](https://openjfx.io/javadoc/11/javafx.graphics/javafx/scene/doc-files/cssref.html)
