# Node 的位置和大小

2023-07-31, 22:08
add: Node 变换
2023-07-28, 13:34
modify: layoutX 和 translateX 的解释
2023-06-12
***
## 1. Node 位置

`layoutX`, `layoutY` 是添加到 `Node` 的 x 和 y 坐标的 transform，用于 layout。

`layoutX`, `layoutY`定义为 `Node` 当前 `layoutBounds` `(minX, minY)` (可能不为 0)到期望位置的 offsets。

例如，将 `textnode` 定位到 `(finalX, finalY)`：

```java
textnode.setLayoutX(finalX - textnode.getLayoutBounds().getMinX());
textnode.setLayoutY(finalY - textnode.getLayoutBounds().getMinY());
```

不减去 `layoutBounds` `(minX, minY)` 可能导致定位错误（`minX` 或 `minY` 不为 0 时）。

`Node.relocate(finalX, finalY)` 会自动计算 layoutX 和 layoutY，是设置 Node 位置的推荐方法，而非直接调用 `setLayoutX()` 和 `setLayoutY()`。

`Node` 的最终位移由两部分组成，对 y 坐标：`layoutY`+`translateY`

- `layoutY` 定义 `Node` 的稳定位置
- `translateY` 对 `Node` 位置进行动态调整

所以 `Node` 最终的位移为：

$$finalTranslationX = layoutX+translateX$$
$$finalTranslationY = layoutY+translateY$$

例如，将一个 node 的 boundingBox 的左上角放在 `finalX` 和 `finalY` 位置，其 `layoutX` 和 `layoutY` 值为：

```java
layoutX = finalX - node.getLayoutBounds().getMinX()
layoutY = finalY - node.getLayoutBounds().getMinY()
```

有时，设置 `Node` 的 `layoutX` 和 `layoutY` 属性无法将其定为到父节点的指定位置。如果遇到这种情况，请检查父节点类型。大多数父节点为 `Region` 的子类，它们有自己的定位策略，忽略子节点的 `layoutX` 和 `layoutY` 属性，如 `HBox` 和 `VBox`。

**示例：** 下面两个 button 的 `layoutX` 和 `layoutY` 属性被忽略

```java
Button b1 = new Button("OK");
b1.setLayoutX(20);
b1.setLayoutY(20);

Button b2 = new Button("Cancel");
b2.setLayoutX(50);
b2.setLayoutY(50);

VBox vb = new VBox();
vb.getChildren().addAll(b1, b2);
```

如果 `Node` 为托管节点，其父节点为 `Region`，则 region 会根据其 layout 策略设置 `Node` 的 `layoutX` 和 `layoutY`。

如果 `Node` 不是托管节点，或其父节点为 `Group`，则可以通过设置 `layoutX` 和 `layoutY` 设置 `Node` 位置。

```ad-tip
Pane 虽然是 Region 子类，但是它采用的绝对定位策略，所以也可以通过设置 layoutX 和 layoutY 设置 Node 位置。
```

例如：

```java
Button b1 = new Button("OK");
b1.setLayoutX(20);
b1.setLayoutY(20);

Button b2 = new Button("Cancel");
b2.setLayoutX(50);
b2.setLayoutY(50);

Group parent = new Group(); // 或 Pane parent = new Pane();
parent.getChildren().addAll(b1, b2);
```

## 2. Node大小

Node 可以分为两种类型，大小可调（_resizable_）和大小不可调（_nonresizable_）：

- _resizable_ 的 node 在layout 过程中可以被父节点调节大小
- _nonresizable_ 的 node，只能通过修改其属性调节大小

例如，`Button` 是 resizable 类型的 `Node`，将 Button 放在容器中，如 HBox，HBox 根据显示 Button 所需空间和 HBox 可用空间来调整 Button 大小，决定 Button 的最佳尺寸。

`Rectangle`  是 nonreziable 类型 `Node`，将其放在 `HBox` 中，`HBox` 不能调整它的大小，只能使用程序指定的尺寸。

```ad-note
*resizable* 类型的 `Node` 可以被父容器调整大小。*nonresizable* 类型的 Node 不能被父容器调整大小，只能通过修改其属性调整大小。
```

- _resizable_ 的 node 包括 `Region`、`Control` 和 `WebView`等。  
- _nonresizable_ 的 node 包括 `Group`、`Text` 和 `Shape` 等节点，这些 node 只能通过设置 `width` 和 `height` 属性修改大小。

**如果知道一个 `Node` 是否 *resizable*？**

通过 `isResizable()`  方法可以知道是否 *resizable*。

示例：将一个 Button 和一个 Rectangle 添加到 HBo。演示 resizable 和 nonresizable Node 的差别。运行程序后，Button 随着 Stage 变窄会随之变小，最终显示为 `...`；而 Rectangle 不管怎么调整 Stage 大小，其尺寸不变：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ResizableNodeTest extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button btn = new Button("A big button");
        Rectangle rect = new Rectangle(100, 50);
        rect.setFill(Color.WHITE);
        rect.setStrokeWidth(1);
        rect.setStroke(Color.BLACK);

        HBox root = new HBox();
        root.setSpacing(20);
        root.getChildren().addAll(btn, rect);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Resizable Nodes");
        stage.show();

        System.out.println("btn.isResizable(): " + btn.isResizable());
        System.out.println("rect.isResizable(): " + rect.isResizable());
    }
}
```

```
btn.isResizable(): true
rect.isResizable(): false
```

![[Pasted image 20230612193908.png]]

### 2.1. Resizable Node

_resizable_ 节点的大小由两个条件确定：

- node 所在容器调节大小的策略
- node 自身的尺寸属性

每个容器都有其独特的调整大小策略，在布局管理器部分有专门说明。
_resizable_ 节点通过三个属性设置其大小范围：

- Preferred size，即最理想的高度和宽度
- Minimum size, 最小尺寸
- Maximum size, 最大尺寸，设置为 `Double.MAX_VALUE` 表示不限制最大尺寸

大部分 _resizable_ 根据其内容和属性自动计算其 preferred, minium 和 maximum 尺寸。该尺寸称为固定尺寸（_intrinsic_）。`Region` 和 `Control` 类定义了两个常量用于指定节点的固定尺寸：

- `USE_COMPUTED_SIZE`，根据内容和属性自动计算尺寸，`USE_COMPUTED_SIZE=-1`
- `USE_PREF_SIZE`，将 min 和 max 尺寸设置和 pref 一样，`USE_PREF_SIZE=Double.NEGATIVE_INFINITY`

绝大多数情况使用 `USE_COMPUTED_SIZE`，即自动计算的大小就可以。只有在不满足要求时，才覆盖自动计算的尺寸。

`Region` 和 `Control` 类有 6 个 `DoubleProperty` 类型的属性，用于定义宽度和高度的 preferred, min, max 尺寸：

- prefWidth
- prefHeight
- minWidht
- minHeight
- maxWidth
- maxHeight

这些属性默认值为 `USE_COMPUTED_SIZE`。

```ad-tip
大多时候使用 `USE_COMPUTED_SIZE` 效果都很好。当该设置不能满足要求时，才需要设置以上属性进行定制。
```

**如何获得 `Node` 实际的 preferred, min 和 max 大小？**

使用 `getPrefWidth()`, `getPrefHeight()`, `getMinWidth()`, `getMinHeight()`, `getMaxWidth()` 和 `getMaxHeight()` 方法**并不合适**。因此它们可能被设置为 `USE_COMPUTED_SIZE` 等值，而 `Node` 在内部计算实际大小。例如：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NodeSizeSentinelValues extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        // Override the intrinsic width of the cancel button
        cancelBtn.setPrefWidth(100);

        VBox root = new VBox();
        root.getChildren().addAll(okBtn, cancelBtn);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Overriding Node Sizes");
        stage.show();

        System.out.println("okBtn.getPrefWidth(): " + okBtn.getPrefWidth());
        System.out.println("okBtn.getMinWidth(): " + okBtn.getMinWidth());
        System.out.println("okBtn.getMaxWidth(): " + okBtn.getMaxWidth());

        System.out.println("cancelBtn.getPrefWidth(): " + cancelBtn.getPrefWidth());
        System.out.println("cancelBtn.getMinWidth(): " + cancelBtn.getMinWidth());
        System.out.println("cancelBtn.getMaxWidth(): " + cancelBtn.getMaxWidth());
    }
}
```

```
okBtn.getPrefWidth(): -1.0
okBtn.getMinWidth(): -1.0
okBtn.getMaxWidth(): -1.0
cancelBtn.getPrefWidth(): 100.0
cancelBtn.getMinWidth(): -1.0
cancelBtn.getMaxWidth(): -1.0
```

这里将 `cancelBtn`  的首选宽度设置为 100px，如下图所示。上面的输出也证明，这些方法对了解 `Node` 的实际大小用处不大。

![[Pasted image 20230612200107.png|150]]

使用 `Node` 类的如下方法获得 `Node` 的实际大小：

- double prefWidth(double height)
- double prefHeight(double width)
- double minWidth(double height)
- double minHeight(double width)
- double maxWidth(double height)
- double maxHeight(double width)

在这里，可以看到获得 `Node` 真实大小的一个变化。要获得宽度，需要传入其高度值，反之亦然。在 JavaFX 中，大多数 Node 的宽度和高度是独立的，但也有高度依赖于宽度，或相反的情况。当高度和宽度之间存在依赖关系，称该 Node 具有 content bias。如果高度依赖于宽度，称为水平 content bias；如果宽度依赖于高度，称为垂直 content bias。一个 Node 不能同时具有水平和垂直 content bias，这将导致循环依赖。

`Node` 的 `getContentBias()` 返回 Node 的 content bias。返回 enum 类型 `javafx.geometry.Orientation`，包含两个常数：`HORIZONTAL` 和 `VERTICAL`。不具有 content bias 的 `Node`，如 `Text` 和 `ChoiceBox`，该方法返回 `null`。

`Labeled` 的子类，如 `Label`, `Button` 和 `CheckBox`，在启用 `text` 换行属性后，都具有水平 content bias。有些 `Node` 的 content bias 取决于方向，例如，如果 `FlowPane` 的方向为 `HORIZONTAL`，则为 `HORIZONTAL` content bias；如果方向为 `VERTICAL`，则为 `VERTICAL` content bias。

在布局子节点时，应该用上面的 6 种方法来获取节点大小。如果 Node 不具有 content bias，则传入 -1。例如，`ChoiceBox` 不具有 content bias，可以用如下方法获取其首选大小：

```java
ChoiceBox choices = new ChoiceBox();
...
double prefWidth = choices.prefWidth(-1);
double prefHeight = choices.prefHeight(-1);
```

对具有 content bias 的 Node，需要传入具有 bias 维度值来获取另一个值。例如，Button 具有 `HORIZONTAL` content bias，可以传入 -1 来获取 width，然后使用返回的 width 值获取 height：

```java
Button b = new Button("Hello JavaFX");
// 启用 Button 的 text wrap，其 content bias 属性从
// null (default) 变为 HORIZONTAL
b.setWrapText(true);
...
double prefWidth = b.prefWidth(-1);
double prefHeight = b.prefHeight(prefWidth);
```

如果 Button 没有启用 text wrap，则可以传入 -1 来获取 prefWidth() 和 prefHeight()。

获取用于 layout 的节点的宽度和高度的通用方法如下：

```java
Node node = get the reference of the node;
...
double prefWidth = -1;
double prefHeight = -1;
Orientation contentBias = b.getContentBias();
if (contentBias == HORIZONTAL) {
	prefWidth = node.prefWidth(-1);
	prefHeight = node.prefHeight(prefWidth);
} else if (contentBias == VERTICAL) {
	prefHeight = node.prefHeight(-1);
	prefWidth = node.prefWidth(prefHeight);
} else {
	// contentBias is null
	prefWidth = node.prefWidth(-1);
	prefHeight = node.prefHeight(-1);
}
```


现在知道如何获取 Node 的首选尺寸、最小尺寸和最大尺寸的指定值和实际值。这些值表示 Node 尺寸的范围。在容器中 layout node 时，容器会尝试以首选尺寸布局 node。但是，根据容器布局策略和 node 的指定大小，node 可能设置为首选尺寸。容器会在将 node 尺寸设置为指定范围，设置的尺寸称为当前尺寸（current size）。

**如何获得当前尺寸？**

`Region` 和 `Control` 类定义了两个只读属性：width 和 height，持有 node 的当前尺寸。例如，将 `Button` 放在 `HBox` 中，打印 `Button` 的尺寸信息，修饰属性，再次打印尺寸信息。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class NodeSizes extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button btn = new Button("Hello JavaFX!");

        HBox root = new HBox();
        root.getChildren().addAll(btn);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sizes of a Node");
        stage.show();

        // 打印 button 尺寸
        System.out.println("Before changing button properties:");
        printSizes(btn);

        // 修改 button 属性
        btn.setWrapText(true); // 启用 text wrap，content bias 变为 HORIZONTAL
        btn.setPrefWidth(80);
        stage.sizeToScene();

        // 打印 button 尺寸
        System.out.println("\nAfter changing button properties:");
        printSizes(btn);

    }

    public void printSizes(Button btn) {
        System.out.println("btn.getContentBias() = " + btn.getContentBias());

		// 返回指定的首选宽度和高度，默认为 USE_COMPUTED_SIZE=-1
        System.out.println("btn.getPrefWidth() = " + btn.getPrefWidth() +
                ", btn.getPrefHeight() = " + btn.getPrefHeight());

		// 返回指定的最小宽度和高度，默认为 USE_COMPUTED_SIZE=-1
        System.out.println("btn.getMinWidth() = " + btn.getMinWidth() +
                ", btn.getMinHeight() = " + btn.getMinHeight());

		// 返回指定的最大宽度和高度，默认为 USE_COMPUTED_SIZE=-1
        System.out.println("btn.getMaxWidth() = " + btn.getMaxWidth() +
                ", btn.getMaxHeight() = " + btn.getMaxHeight());

		// 返回实际的首选宽度和高度
        double prefWidth = btn.prefWidth(-1);
        System.out.println("btn.prefWidth(-1) = " + prefWidth +
                ", btn.prefHeight(prefWidth) = " + btn.prefHeight(prefWidth));

		// 返回实际的最小宽度和高度
        double minWidth = btn.minWidth(-1);
        System.out.println("btn.minWidth(-1) = " + minWidth +
                ", btn.minHeight(minWidth) = " + btn.minHeight(minWidth));

		// 返回实际的最大宽度和高度
        double maxWidth = btn.maxWidth(-1);
        System.out.println("btn.maxWidth(-1) = " + maxWidth +
                ", btn.maxHeight(maxWidth) = " + btn.maxHeight(maxWidth));

		// 返回当前宽度和高度
        System.out.println("btn.getWidth() = " + btn.getWidth() +
                ", btn.getHeight() = " + btn.getHeight());
    }
}
```

```
Before changing button properties:
btn.getContentBias() = null
btn.getPrefWidth() = -1.0, btn.getPrefHeight() = -1.0
btn.getMinWidth() = -1.0, btn.getMinHeight() = -1.0
btn.getMaxWidth() = -1.0, btn.getMaxHeight() = -1.0
btn.prefWidth(-1) = 91.837890625, btn.prefHeight(prefWidth) = 23.0
btn.minWidth(-1) = 24.666015625, btn.minHeight(minWidth) = 23.0
btn.maxWidth(-1) = 91.837890625, btn.maxHeight(maxWidth) = 23.0
btn.getWidth() = 92.0, btn.getHeight() = 23.333333333333332

After changing button properties:
btn.getContentBias() = HORIZONTAL
btn.getPrefWidth() = 80.0, btn.getPrefHeight() = -1.0
btn.getMinWidth() = -1.0, btn.getMinHeight() = -1.0
btn.getMaxWidth() = -1.0, btn.getMaxHeight() = -1.0
btn.prefWidth(-1) = 80.0, btn.prefHeight(prefWidth) = 38.0
btn.minWidth(-1) = 24.666015625, btn.minHeight(minWidth) = 23.0
btn.maxWidth(-1) = 80.0, btn.maxHeight(maxWidth) = 38.0
btn.getWidth() = 80.0, btn.getHeight() = 38.0
```

下面 *resizable* node 尺寸相关方法的总结。

**Region, Control 属性**

- prefWidth
- prefHeight
- minWidth
- minHeight
- maxWidth
- maxHeight

定义首选、最小和最大尺寸。被设置为哨兵值。使用它们覆盖默认值。

**Node 方法**

- double prefWidth(double h)
- double prefHeight(double w)
- double minWidth(double h)
- double minHeight(double w)
- double maxWidth(double h)
- double maxHeight(double w)

通过它们获得 `Node` 的实际大小。如果 `Node` 没有 content bias，传入 -1 作为参数。如果 `Node` 有 content bias，则传入另一个维度的实际值作为参数。这些方法没有对应的属性。

**Region, Control 属性**

- width
- height

resizable node 当前宽度和高度的只读属性。

**Region, Control 方法**

- void setPrefSize(double w, double h)
- void setMinSize(double w, double h)
- void setMaxSize(double w, double h)

覆盖默认的 `USE_COMPUTED_SIZE` 的宽度和高度。

**Node 方法**

- void resize(double w, double h)

将 `Node` 的大小调整为指定的宽度和高度。由父节点在 layout 期间调用。一般不会在代码中直接使用。如果需要设置 `Node` 大小，应使用 `setMinSize()`, `setPrefSize()` 或 `setMaxSize()`。该方法对 *nonresizable* node 无效。

- void autosize()

对 resizable node，将其 layoutBounds 设置为当前首选 width 和 height。此方法考虑 content bias。对 *nonresizable* node 无效。

### 2.2. Nonresizable Node

*Nonresizable* Node 在 layout 期间不会被父容器调整大小。但可以通过设置其属性调整大小。

Node 类中定义了几个与大小相关的方法。对 nonresizable Node 这些方法不起作用，只能返回当前大小。例如：

- 在 nonresizable node 上调用 `resize(double w, double h)` 无效
- 在 nonresizable node 上调用 `prefWidth(double h)`, `minWidth(double h)` 和 `maxWidth(double h)` 返回 layoutBounds 的宽度，`prefHeight(double w)`, `minHeight(double w)` 和 `maxHeight(double w)` 返回 layoutBounds 的高度。

nonresizable node 没有 content bias，因此上面的方法以 -1 为参数。

## 3. Node 变换

Node 类中定义了一个 private 内部类 NodeTransformation，包含 Node 常用的变换参数。

- translateX
- translateY
- translateZ
- scaleX
- scaleY
- scaleZ
- rotate
- rotationAxis
- localToParentTransform
- localToSceneTransform

下面演示平移、缩放和渲染这三种最常见的变换。定义基本类：

```java
public class TransformApp extends Application {

    private Parent createContent() {
        Rectangle box = new Rectangle(100, 50, Color.BLUE);

        transform(box);

        return new Pane(box);
    }

    private void transform(Rectangle box) {
        // we will apply transformations here
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent(), 300, 300, Color.GRAY));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

![|300](Pasted%20image%2020230731214840.png)

在 JavaFX 中，变换可以发生在 X, Y 或 Z 轴。下面演示 2D 情况，只考虑 X 和 Y 轴。

### 3.1 Translate

在 x 轴方向移动 100px，在 y 轴方向移动 200px：

```java
private void transform(Rectangle box) {  
    box.setTranslateX(100);  
    box.setTranslateY(200);  
}
```

![|300](Pasted%20image%2020230731215137.png)

### 3.2 Scale

在 x 轴方向放大 1.5 倍，y 轴方向放大 1.5 倍。

```java
private void transform(Rectangle box) {
    // previous code

    box.setScaleX(1.5);
    box.setScaleY(1.5);
}
```

![|300](Pasted%20image%2020230731220026.png)

### 3.3 Rotate

在 2D 空间，只能沿着 Z 轴旋转。例如，旋转 30°：

```java
private void transform(Rectangle box) {
    // previous code

    box.setRotate(30);
}
```

![|300](Pasted%20image%2020230731220734.png)
