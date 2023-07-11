# StackPane

2023-07-10, 14:33
****
## 1. 简介

`StackPane` 使用堆栈存储节点，子节点按照添加顺序渲染。

下图以 `StackPane` 为 root，依次添加 Rectangle 和 Text。因为后添加 Text，所有在 Rectangle 上显示：

![|250](Pasted%20image%2020230710133310.png)

```ad-tip
使用 StackPane 叠加不同类型的 nodes，可以创建很多有意思的 GUI。
```

StackPane 的 preferred width 为最宽子节点的宽度。preferred height 为最高字节点的高度。

StackPane 会裁剪 content，因此子节点可能渲染到 StackPane 外面。

对 maximum size 允许的子节点，StackPane 调整其大小以填充整个 content area。子节点相对 content area 默认 center 对齐。

## 2. 创建 StackPane

```java
// 创建空 StackPane
StackPane spane1 = new StackPane();

// 添加 1 个 Rectangle 和 1 个 Text 到 StackPane
Rectangle rect = new Rectangle(200, 50);
rect.setFill(Color.LAVENDER);
Text text = new Text("A Rectangle");
spane1.getChildren().addAll(rect, text);

// 创建包含 1 个 Rectangle 和 1 个 Text 的 StackPane
Rectangle r = new Rectangle(200, 50);
r.setFill(Color.LAVENDER);
StackPane spane2 = new StackPane(r, new Text("A Rectangle"));
```

**示例：** 创建 StackPane，添加 1 个 Rectangle 和 1 个 Text

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StackPaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a Rectangle and a Text
        Rectangle rect = new Rectangle(200, 50);
        rect.setStyle("-fx-fill: lavender;" +
                "-fx-stroke-type: inside;" +
                "-fx-stroke-dash-array: 5 5;" +
                "-fx-stroke-width: 1;" +
                "-fx-stroke: black;" +
                "-fx-stroke-radius: 5;");

        Text text = new Text("A Rectangle");

        // Create a StackPane with a Rectangle and a Text
        StackPane root = new StackPane(rect, text);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using StackPane");
        stage.show();
    }
}
```

![|300](Pasted%20image%2020230710134612.png)

使用 StackPane，添加节点的顺序很重要。下面的两个语句产生的结果不同：

```java
// Text 在 Rectangle 上
spane1.getChildren().addAll(rect, text);

// Rectangle 在 Text 上
spane1.getChildren().addAll(text, rect);
```

如果 Text 比 Rectangle 小，当 Rectangle 在 Text 上，则看不到 Text。如果 Text 比 Rectangle 大，则可以看到 Text 在 Rectangle 外的部分。

**示例：** 展示 StackPane 对重叠的处理

HBox 包含 5 个 StackPane:

- 第 1 个，Text 在 Rectangle 上，都可见
- 第 2 个，Rectangle 在 Text 上，Text 被盖住不可见
- 第 3 个，Rectangle 在 Text 上，将 Rectangle 设置为半透明，所以 Text 可见
- 第 4 个，Rectangle 在 Text 上，Text 比 Rectangle 大，超出 Rectangle 的部分可见
- 第 5  个，Rectangle 在 Text 上，Text 比 Rectangle 大，将 Rectangle 设置为半透明，所以 Rectangle 部分 Text 可见性为 50%，超出部分为 100% 可见。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StackPaneOverlayTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane textOverRect = createStackPane("Hello", 1.0, true);
        StackPane rectOverText = createStackPane("Hello", 1.0, false);
        StackPane transparentRectOverText = createStackPane("Hello", 0.5, false);
        StackPane rectOverBigText = createStackPane("A bigger text", 1.0, false);
        StackPane transparentRectOverBigText =
                createStackPane("A bigger text", 0.5, false);

        // Add all StackPanes to an HBox
        HBox root = new HBox(textOverRect,
                rectOverText,
                transparentRectOverText,
                rectOverBigText,
                transparentRectOverBigText);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Overlaying Rules in StackPane");
        stage.show();
    }

    public StackPane createStackPane(String str, double rectOpacity, boolean rectFirst) {
        Rectangle rect = new Rectangle(60, 50);
        rect.setStyle("-fx-fill: lavender;" + "-fx-opacity: " + rectOpacity + ";");

        Text text = new Text(str);

        // Create a StackPane
        StackPane spane = new StackPane();

        // add the Rectangle before the Text if rectFirst is true. 
        // Otherwise add the Text first
        if (rectFirst) {
            spane.getChildren().addAll(rect, text);
        } else {
            spane.getChildren().addAll(text, rect);
        }

        spane.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        return spane;
    }
}
```

![|500](Pasted%20image%2020230710135050.png)

## 3. StackPane Properties

StackPane alignment 属性为 `ObjectProperty<Pos>` 类型，定义**所有子节点**在 content area 的对齐方式，默认为 Pos.CENTER。

单个子节点的对齐方式可以通过 alignment 约束来设置。如下一节所示。

除了重叠节点外，StackPane 还有其它用途。如在正中心显示文本，可以将 Text 放在 StackPane 中，将 StackPane 作为 Scene 的 root，这样 Text 会自动居中。如果没有 StackPane，你需要通过 binding 来保持 text 居中。

**示例：** 在 HBox 中放置 5 个 StackPane，每个 StackPane 都包含 Rectangle+Text。

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StackPaneAlignment extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane topLeft = createStackPane(Pos.TOP_LEFT);
        StackPane topRight = createStackPane(Pos.TOP_RIGHT);
        StackPane bottomLeft = createStackPane(Pos.BOTTOM_LEFT);
        StackPane bottomRight = createStackPane(Pos.BOTTOM_RIGHT);
        StackPane center = createStackPane(Pos.CENTER);

        double spacing = 10.0;
        HBox root = new HBox(spacing,
                topLeft,
                topRight,
                bottomLeft,
                bottomRight,
                center);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using StackPane");
        stage.show();
    }

    public StackPane createStackPane(Pos alignment) {
        Rectangle rect = new Rectangle(80, 50);
        rect.setFill(Color.LAVENDER);

        Text text = new Text(alignment.toString());
        text.setStyle("-fx-font-size: 7pt;");

        StackPane spane = new StackPane(rect, text);
        spane.setAlignment(alignment);
        spane.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        return spane;
    }
}
```

![](Pasted%20image%2020230710142005.png)

Rectangle 较大，所以 StackPane 的 preferred size 为 Rectangle 的 size，Text 较小，根据 alignment 在 content area 不同位置显示。

## 4. 约束

StackPane 也支持 alignment 和 margin 两个约束。其中 alignment 定义子节点相对 StackPane content area 的位置。

这里要注意 StackPane 的 alignment 属性和 alignment 约束的差别：

- alignment 属性设置所有子节点的默认对齐方式
- alignment 约束设置单个节点的对齐方式，覆盖默认值

所以，在绘制子节点时，JavaFX 优先使用 StackPane 的 alignment 约束对齐子节点，如果没有设置 alignment 约束，则使用 alignment 属性。

```ad-tip
`StackPane` 的 `alignment` 属性默认为 `Pos.CENTER`，而 alignment 约束的默认值为 `null`。
```

使用 static `StackPane.setAlignment(Node child, Pos value)` 设置 alignment 约束；使用 static StackPane.getAlignment(Node child) 查询约束。

```java
// Place a Text node in the top left corner of the StackPane
Text topLeft = new Text("top-left");
StackPane.setAlignment(topLeft, Pos.TOP_LEFT);
StackPane root = new StackPane(topLeft);

// Get the alignment of the topLeft node
Pos alignment = StackPane.getAlignment(topLeft);
```

**示例：** StackPane 的 alignment 约束

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StackPaneAlignmentConstraint extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(200, 60);
        rect.setFill(Color.LAVENDER);

        // Create a Text node with the default CENTER alignment
        Text center = new Text("Center");

        // Text 约束：TOP_LEFT
        Text topLeft = new Text("top-left");
        StackPane.setAlignment(topLeft, Pos.TOP_LEFT);

        // Text 约束：BOTTOM_LEFT
        Text bottomRight = new Text("bottom-right");
        StackPane.setAlignment(bottomRight, Pos.BOTTOM_RIGHT);

        StackPane root = new StackPane(rect, center, topLeft, bottomRight);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("StackPane Alignment Constraint");
        stage.show();
    }
}
```

![|250](Pasted%20image%2020230710143045.png)

使用 static `StackPane.setMargin(Node child, Insets value)` 设置 margins：

```java
// Set 10px margin around the topLeft child node
StackPane.setMargin(topLeft, new Insets(10));

// Get the margin of the topLeft child node
Insets margin = StackPane.getMargin(topLeft);
```

设置为 null 表示重置为默认值。使用 static StackPane.clearConstraints(Node child) 重置所有约束：

```java
// Clear the alignment and margin constraints for the topLeft child node
StackPane.clearConstraints(topLeft);
```

重置所有约束后，StackPane 使用 alignment 属性，0px margins。
