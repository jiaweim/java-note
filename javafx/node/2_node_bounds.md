# Node 边框

2023-06-12
***
## 边框（Bounding Rectangle）

每个 node 都有一个几何形状，并且在其父节点的坐标系中有一个具体的位置。node的大小和位置统称为**边界**（bounds）。边界以一个完全包含节点的矩形定义，称其为**边框** (bonding box)。如下图所示：

![[Pasted image 20230612110515.png]]

不管 node 的形状如何，其边界都是矩形。在JavaFX中，边界由 `javafx.geometry.Bounds` 类定义。`Bounds`是抽象类，`BoundingBox` 为具体实现。`Bounds` 类可以处理 3D 空间的边界。其属性包括左上角最浅深度对应的坐标、宽度、高度和深度：

- `getMinX()`, `getMinY()`, `getMinZ()` 返回左上角坐标
- `getWidth()`, `getHeight()`, `getDepth()` 返回边框尺寸
- `getMaxX()`, `getMaxY()`, `getMaxZ()` 返回边框右下角最深点的坐标

在 2D 空间，`minX` 和 `minY` 定义了边框左上角的坐标，`maxX` 和 `maxY` 定义了边框右下角的坐标，z 坐标值均为 0。如下图所示：

![[Pasted image 20230612111408.png|500]]

`Bounds` 类的其它方法：

- `isEmpty()`, 边框的长、宽、高任意值为负，返回 true。
- `contains()`, 判断一个边框是否包含另一个边框
- `intersects()`, 判断两个边框是否相交

## Node bounds

`Node` 有 不同类型的 bounds。下图展示了三个包含文字的按钮：

![[Pasted image 20230612112549.png|300]]

三个按钮的特点：

- 第一个没有特效和转换
- 第二个有阴影特效
- 第三个有阴影特效和旋转变换

下图是三种按钮的边界：

![[Pasted image 20230612113241.png|300]]

可以看到，特效和转换都会影响 bounds。`Node` 在 scene graph 中有三种类型的 bounds，以三个只读属性定义：

- layoutBounds
- boundsInLocal
- boundsInParent

下图显示了 `Node` 的不同属性对 bounds 的影响：

![[Pasted image 20230612113535.png]]

Node 的不同属性对 bounds 的影响不同。有些 node 类型（如 Circlr, Rectangle）的 stroke 不为 0，非 0 stroke 在计算 bounds 时被视作 Node 几何形状的一部分。

| Bounds 类型    | 坐标空间      | 贡献                                         |
| -------------- | ------------- | -------------------------------------------- |
| layoutBounds   | Node (转换前) | Node 几何形状，非 0 stroke                   |
| boundsInLocal  | Node (转换前) | Node 几何形状，非 0 stroke，特效，裁剪       |
| boundsInParent | Parent        | Node 几何形状，非 0 stroke，特效，裁剪，转换 |

### layoutBounds

`layoutBounds` 属性根据 node 的几何形状计算而来，不包含特效、裁剪和变换。根据 node 是否 resizable，左上角坐标的计算方法有所不同：

- 对 resizable node（`Region`, `Control`, `WebView`），其左上角的坐标为 (0,0)。例如，button 的 `layoutBounds` 的 (`minX`, `minY`) 总是 (0,0)
- 对 nonresizable node (`Shape`, `Text`, `Group`)，左上角坐标根据 `Node` 的几何特性计算。

对 shape 或 `Text`，可以指定 node 中特定点 (x,y) 相对未转换坐标空间的位置。例如，可以指定矩形左上角的坐标 (x,y)，对应 `layoutBounds` 的左上角位置；对于圆，可以指定 `centerX`, `centerY` 和 `radius`，对应的 `layoutBounds` 的左上角坐标为 `(centerX – radius, centerY – radius)`。

`layoutBounds` 的 width 和 height 为 node 的 width 和 height。有些 node 可以设置 width 和 height，有些则会自动计算。

**那么，何时用到 `layoutBounds`？** 容器在为子节点分配空间时，需要用到 `layoutBounds`。例如：

```java  
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LayoutBoundsTest extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button b1 = new Button("Close");
        b1.setEffect(new DropShadow());

        Button b2 = new Button("Close");

        Button b3 = new Button("Close");
        b3.setEffect(new DropShadow());
        b3.setRotate(30);

        Button b4 = new Button("Close");

        VBox root = new VBox();
        root.getChildren().addAll(b1, b2, b3, b4);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Testing LayoutBounds");
        stage.show();

        System.out.println("b1=" + b1.getLayoutBounds());
        System.out.println("b2=" + b2.getLayoutBounds());
        System.out.println("b3=" + b3.getLayoutBounds());
        System.out.println("b4=" + b4.getLayoutBounds());
    }
}

```

这里在 `VBox` 中显示四个按钮：第一个带特效、第三个特效+旋转。从下面的输出可以发现，特效和旋转不影响 `layoutBounds`，四个按钮的 `layoutBounds` 相同。

![[Pasted image 20230612124624.png|150]]

```
b1=BoundingBox [minX:0.0, minY:0.0, minZ:0.0, width:47.333333333333336, height:23.333333333333332, depth:0.0, maxX:47.333333333333336, maxY:23.333333333333332, maxZ:0.0]
b2=BoundingBox [minX:0.0, minY:0.0, minZ:0.0, width:47.333333333333336, height:23.333333333333332, depth:0.0, maxX:47.333333333333336, maxY:23.333333333333332, maxZ:0.0]
b3=BoundingBox [minX:0.0, minY:0.0, minZ:0.0, width:47.333333333333336, height:23.333333333333332, depth:0.0, maxX:47.333333333333336, maxY:23.333333333333332, maxZ:0.0]
b4=BoundingBox [minX:0.0, minY:0.0, minZ:0.0, width:47.333333333333336, height:23.333333333333332, depth:0.0, maxX:47.333333333333336, maxY:23.333333333333332, maxZ:0.0]
```

如果希望完整显示上面带特效或变换的按钮，可以将 node 放在 `Group` 中，再将 `Group` 添加到容器。容器会查询 `Group` 的 `layoutBounds`，而 Group 的 `layoutBounds` 是其所有子节点 `boundsInParent` 的加和。`boundsInParent` 包含特效和变换部分空间。所以修改上例：

```java
root.getChildren().addAll(new Group(b1), b2, new Group(b3), b4);
```

就能完整显示 b1 和 b3 按钮。

### boundsInLocal

`boundsInLocal` 在 node 未转换的坐标空间计算，包含节点几何属性、特效和 clip，但不包含变换。

如下所示，显示一个 Button 的 `layoutBounds` 和 `boundsInLocal`：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoundsInLocalTest extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button b1 = new Button("Close");
        b1.setEffect(new DropShadow());

        VBox root = new VBox();
        root.getChildren().addAll(b1);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Testing LayoutBounds");
        stage.show();

        System.out.println("b1(layoutBounds)=" + b1.getLayoutBounds());
        System.out.println("b1(boundsInLocal)=" + b1.getBoundsInLocal());
    }
}
```

```
b1(layoutBounds)=BoundingBox [minX:0.0, minY:0.0, minZ:0.0, width:47.333333333333336, height:23.333333333333332, depth:0.0, maxX:47.333333333333336, maxY:23.333333333333332, maxZ:0.0]
b1(boundsInLocal)=BoundingBox [minX:-9.0, minY:-9.0, minZ:0.0, width:65.33332824707031, height:42.333335876464844, depth:0.0, maxX:56.33332824707031, maxY:33.333335876464844, maxZ:0.0]
```

`boundsInLocal` 包含特效部分。可以看到，`layoutBounds` 的左上角坐标为 (0.0, 0.0)，而 `boundsInLocal` 左上角坐标为 (-9.0, -9.0)。

**那么，何时用 `boundsInLocal`？**

当需要考虑特效和 clip 时使用 `boundsInLocal`。假设你有一个包含镜像特效的 `Text`，并且希望垂直居中。如果使用 `Text` 的 `layoutBounds`，则只居中文本部分，而不包含镜像。如果使用 `boundsInLocal`，则同时考虑镜像部分。

再比如，检查有影子的球的碰撞，如果当一个球位于另一个球的 bounds 中，包括特效部分，则应使用 `boundsInLocal`；如果仅在几何边界相交时发生碰撞，则应使用 `layoutBounds`。

### boundsInParent

`boundsInParent` 使用较少。下图时没有转换操作的图形及其 `boundsInLocal`:

![bounds](images/2019-06-05-15-59-51.png)

如果将该图形绕中心旋转 20°，得到如下结果：

![bounds](images/2019-06-05-16-00-03.png)

红色边界表示节点在其父节点的坐标空间中的 `boundsInParent`。而其 `boundsInLocal` 和第一个图一样，保持不变，绿色边框表示在 Node 的坐标空间的 `boundsInLocal`。

下图是两个矩形，一个带 stroke，一个不带。

- 矩形1 [x:10 y:10 width:100 height:100 strokeWdith:0] 的边界为 [x:10 y:10 width:100 height:100]。
- 矩形2 [x:10 y:10 width:100 height:100 strokeWidth:5] 的边界为 [x:7.5 y:7.5 width:105 height: 105] (stroke 默认居中，所以只计算一半的宽度) 。

由于两者没有转换，所以 `boundsInParent` 和 `boundsInLocal` 相同。

![bounds](2019-06-05-16-00-54.png)

## Group 的边界

Group 的 layoutBounds, boundsInLocal 和 boundsInParent 的计算与 node 不同。可以对 Group 的每个子节点分别应用特效、clip 和变换；也可以对 Group 应用特效、clip 和变换，它们会应用于 Group 的所有子节点。

- `Group` 的 `layoutBounds` 是其所有子节点的 `boundsInParent`的总和，包含直接应用于子节点的特效、clip和变换，但不包含应用于 Group 的特效、clip 和变换。
- `Group` 的 `boundsInLocal` 是其 `layoutBounds` 加上直接作用于 `Group` 的特效和 clip。
- `Group` 的 `boundsInParent` 是其 `boundsInLocal` 加上直接作用于 `Group` 的变换。

如果需要为包含特效、clip 和变换的 `Node` 分配空间，可以将该 `Node` 放在 `Group` 中。

假设你有一个博阿寒特效和变换的 Node，需要为特效分配空间，但不需要为变换分配空间，则可以在 `Node` 上应用特效，然后包装在 `Group` 中，然后在 `Group` 上应用变化。

## bounds 示例

下面通过一个矩形及其不同属性、特效和变换来展示如何计算 `Node` 的 `bounds`。完整代码可参考 [NodeBoundsApp](/src/main/java/mjw/study/javafx/node/NodeBoundsApp.java)。

- 首先创建一个 50x20 的矩形，放在矩形的 local 坐标空间的 (0,0) 位置

```java
Rectangle r = new Rectangle(0, 0, 50, 20);
r.setFill(Color.GRAY);
```

此时三种 bounds 相同：

```
layoutBounds[minX=0.0, minY=0.0, width=50.0, height=20.0]
boundsInLocal[minX=0.0, minY=0.0, width=50.0, height=20.0]
boundsInParent[minX=0.0, minY=0.0, width=50.0, height=20.0]
```

- Rectangle 是 nonresizable `Node`，所以指定的 (x,y) 对应 layoutBounds 的左上角位置

```java
Rectangle r = new Rectangle(75, 50, 50, 20);
```

此时三种 bounds 为：

```
layoutBounds[minX=75.0, minY=50.0, width=50.0, height=20.0]
boundsInLocal[minX=75.0, minY=50.0, width=50.0, height=20.0]
boundsInParent[minX=75.0, minY=50.0, width=50.0, height=20.0]
```

![[Pasted image 20230612162329.png|500]]

此时 `Node` 和 `Parent` 的坐标轴依然相同。所有的 bounds 都一样。

- 添加阴影特效

```java
Rectangle r = new Rectangle(75, 50, 50, 20);
r.setEffect(new DropShadow());
```

此时的矩形如下：

![[Pasted image 20230612162701.png|500]]

此时 Parent 和 Node 仍然一样。layoutBounds 没有变，为了包含阴影特效，boundsInLocal 和 boundsInParent 发生改变：

```
layoutBounds[minX=75.0, minY=50.0, width=50.0, height=20.0]
boundsInLocal[minX=66.0, minY=41.0, width=68.0, height=38.0]
boundsInParent[minX=66.0, minY=41.0, width=68.0, height=38.0]
```

- 添加平移变换

```java
Rectangle r = new Rectangle(75, 50, 50, 20);
r.setEffect(new DropShadow());
r.getTransforms().add(new Translate(150, 75));
```

如下图所示：

![[Pasted image 20230612163956.png|500]]

变换操作修改了 `Node` 的坐标空间，此时需要考虑三个坐标空间：父节点的坐标空间、转换前的坐标空间以及转换后的坐标空间。

`layoutBounds` 和 `boundsInLocal` 是相对 `Node` 的未转换坐标空间。而 `boundsInParent` 是相对父节点的坐标空间。取值如下：

```
layoutBounds[minX=75.0, minY=50.0, width=50.0, height=20.0]
boundsInLocal[minX=66.0, minY=41.0, width=68.0, height=38.0]
boundsInParent[minX=216.0, minY=116.0, width=68.0, height=38.0]
```

可以发现，变换操作不影响 `layoutBounds` 和 `boundsInLocal`。

- 添加旋转变换

```java
Rectangle r = new Rectangle(75, 50, 50, 20);
r.setEffect(new DropShadow());
r.getTransforms().addAll(new Translate(150, 75), new Rotate(30));
```

![[Pasted image 20230612171744.png]]

需要注意，平移和旋转变换应用于矩形的 local 坐标空间，矩形相对其转换后的 local 坐标空间位置不变。`layoutBounds` 和 `boundsInLocal`  值不变，因为没有修改其几何性质和特效。`boundsInParent` 发生改变，此时三种 bounds 为：

```
layoutBounds[minX=75.0, minY=50.0, width=50.0, height=20.0]
boundsInLocal[minX=66.0, minY=41.0, width=68.0, height=38.0]
boundsInParent[minX=167.66, minY=143.51, width=77.89, height=66.91]
```

- 最后，添加缩放和剪切变换

```java
Rectangle r = new Rectangle(75, 50, 50, 20);
r.setEffect(new DropShadow());
r.getTransforms().addAll(new Translate(150, 75), new Rotate(30),
						 new Scale(1.2, 1.2), new Shear(0.30, 0.10));
```

效果如下：

![[Pasted image 20230612173513.png]]
此时的 bounds 值如下：

```
layoutBounds[minX=75.0, minY=50.0, width=50.0, height=20.0]
boundsInLocal[minX=66.0, minY=41.0, width=68.0, height=38.0]
boundsInParent[minX=191.86, minY=171.45, width=77.54, height=94.20]
```

可以发现，`boundsInParent` 再次发生变化，`layoutBounds` 和 `boundsInLocal` 没有变化。

## 在坐标空间之间转换 bounds

如果需要将 Bounds 或点从一个坐标空间转换到另一个坐标空间。Node 提供了几个方法支持该功能。

- Local to parent
- Local to scene
- Parent to local
- Scene to local

