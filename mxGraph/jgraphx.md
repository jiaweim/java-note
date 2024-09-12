# JGraphX

## 简介

JGraphX 是 Java Swing 版本的 mxGraph。

JGraphX 的包名为 `com.mxgraph`，主类为 `mxGraph`。

JGraphX 的特点：

1. 支持丰富的样式；
2. 支持交互：拖拽、缩放、平移等
3. layout 算法，如分层布局、树形布局、圆形布局
4. 导入和导出

## JGraphX 框架

`mxGraphModel` 为 graph 模型类，`mxGraph` 为主类，主要操作通过 `mxGraph` 类执行。即 `mxGraph` 中添加了 `mxGraphModel` 的代理方法。

JGraphX 使用事务系统更改模型。在 HelloWorld 示例中，可以看到：

```java
// Adds cells to the model in a single step
graph.getModel().beginUpdate(); // 开始修改模型
try {
   Object v1 = graph.insertVertex(parent, null, "Hello,", 20, 20, 80, 30);
   Object v2 = graph.insertVertex(parent, null, "World!", 200, 150, 80, 30);
   Object e1 = graph.insertEdge(parent, null, "", v1, v2);
} finally {
   // Updates the display
   graph.getModel().endUpdate(); // 完成修改
}
```

这里添加了 2 个 vertices 和 1 个 edge。

关键 API：

- `mxGraphModel.beginUpdate()`，开始一个新事务或新的子事务；
- `mxGraphModel.endUpdate()`，完成一个事务或子事务；
- `mxGraph.addVertex()`，添加一个新的 vertex 到指定 parent-cell；
- `mxGraph.addEdge()`，添加一个新的 edge 到指定 parent-cell

> [!NOTE]
>
> 从技术上讲，不需要使用 beginUpdate 和 endUpdate 包围修改调用。在 update 范围以外的修改会立刻生效，并立刻发出通知。更新范围是为了控制事件通知和撤销操作的粒度。

将修改内容放在 try...finally 中可以确保更新完成，即使修改模型操作出错。为了更好调试，建议在修改模型时使用此范式。

## 事务模型

JGraphX 事务支持嵌套，即模型中有一个计数器，每次调用 `beginUpdate` +1，每次 `endUpdate` -1。在增加到至少 1 之后，当该计数再次达到 0，认为模型事务完成，出发模型更改的事件通知。

这意味着每个更新模型的代码段都应该放在 begin/end 内。撤销操作、自动布局等均需要该特性。

### 修改模型方法

以下方法会修改 graph 模型，应该放在 update score 中：

- add(parent, child, index)
- remove(cell)
- setCollapsed(cell, collapsed)
- setGeometry(cell, geometry)
- setRoot(root)
- setStyle(cell, style)
- setTerminal(cell, terminal, isSource)
- setTerminals(edge,source,target)
- setValue(cell, value)
- setVisible(cell, visible)

### 插入 Cell

HelloWorld 中的 graph 包含 3 个 cells，即 2 个 vertices 和 1 个 edge。

- `mxGraph.insertVertex(parent, id, value, x, y, width, height, style)`：创建并插入一个新的 vertex
- `mxGraph.insertEdge(parent, id, value, source, target, style)`：创建并插入一个新的 edge

`mxGraph.insertVertex()` 会创建一个 `mxCell` 对象，其参数：

- `parent`，group 结构中的直接 parent-cell。这里跳过，直接使用 `graph.getDefaultParent()` 作为默认 parent-cell；
- `id`，cell 的全局识别符，String 类型。如果不想自己维护 id，可以传入 `null` 并保证 `mxGraphModel.isCreateIds()` 返回 true 即可，这样模型会自动创建 id。
- `value`，cell 的值，如果使用 String，它会作为 vertex 或 edge 的标签。
- x, y, width, height，作为 vertex 左上角位置，宽度和高度。
- `style`，应用于 vertex 的样式。

## mxCell

mxCell 表示 vertex 和 edge。mxCell 许多方法与模型相同，主要区别在于：使用模型会创建事件通知和支持撤销，使用 cell 修改则没有修改记录。

创建新的 cell，至少需要三个信息：value, geometry, style。

### Styles

定义 cell 的可视化样式。

mxStyleSheet 包含一个 styles 对象，该对象定义样式名称到具体样式数组的映射。

<img src="./images/mx_man_styles.png" alt="img" style="zoom: 80%;" />

#### 设置 cell 样式

如果不喜欢默认样式，可以在创建 cell 时（insertVertex, insertEdge）或用 model.setStyle() 定义样式。

1. 已经创建了 ROUNDED 样式，应用到 vertex

```java
Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30, "ROUNDED");
```

2. 使用 ROUNDED 样式创建新的 vertex，覆盖 stroke 和 fill 颜色

```java
Object v1 = graph.insertVertex(parent, null, "Hello",  20, 20, 80, 30, 
                               "ROUNDED;strokeColor=red;fillColor=green");
```

3. 创建一个新的 vertex，没有全局样式，只有 local stroke 和 fill 颜色

```java
Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30, 
                               ";strokeColor=red;fillColor=green");
```

4. 创建一个 vertex，使用 defaultVertex 样式，但是使用其它 fill 颜色

```java
Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30, 
                               "defaultVertex;fillColor=blue");
```

注意，这里必须显式命名默认样式。不过不以分号开头，则使用默认样式。

`mxGraph` 类也提供了修改 cell 样式的函数：

- `mxGraph.setCellStyle(style, cells)`：在 begin/end scope 中，设置 cell 数组的样式
- `mxGraph.getCellStyle(cell)`：返回指定 cell 的样式，合并 local-style 和 default-cell

#### 创建新的 global 样式

可以按照下面的模板创建 ROUNDED global 样式，并用 mxStyleSheet 注册：

```java
mxStylesheet stylesheet = graph.getStylesheet();
Hashtable<String, Object> style = new Hashtable<String, Object>();
style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
style.put(mxConstants.STYLE_OPACITY, 50);
style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
stylesheet.putCellStyle("ROUNDED", style);
```

### Geometry

在 HelloWorld 示例中，可以看到在 insertVertex 方法的参数包含 vertex 的位置和大小。在 java 中，坐标系统是 x 向右正，y 向下为正，对 graph，则是相对 mxGraph 所在容器的绝对位置。

之所以使用单独的 `mxGeometry` 类，而不是直接使用 `mxRectangle` 类保存该信息，是因为 edge 也有几何信息。

edge 的宽度和高度被忽略，x 和 y 与标签的位置有关。此外，edge 有控制点的概念。

<img src="./images/mx_man_edge_routing.png" alt="img" style="zoom:80%;" />

> 该 edge 有 2 个控制点

geometry 还有两个重要概念，相对定位和偏移（offset）。

#### 相对定位

vertex 的 x 和 y 默认是相对 parent 的边框 offset。parent 和 group 的概念后面会介绍。如果一个 cell 没有 parent，graph 的容器就是其 parent。

<img src="./images/mx_man_non_relative_pos.png" alt="img" style="zoom:80%;" />

> 非相对 vertex 定位

在非相对定位模式，edge-label 的位置是相对 graph origin 的绝对 offset。

<img src="./images/mx_man_non_realtive_edge_pos.png" alt="img" style="zoom:80%;" />



## Hello World

```java
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;

public class HelloWorld extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    public HelloWorld() {
        super("Hello, World!");

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
                    30);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
                    80, 30);
            graph.insertEdge(parent, null, "Edge", v1, v2);
        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public static void main(String[] args) {
        HelloWorld frame = new HelloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }
}
```

<img src="./images/image-20240820191736179.png" alt="image-20240820191736179" style="zoom: 67%;" />

## 参考

- https://jgraph.github.io/mxgraph/docs/manual_javavis.html