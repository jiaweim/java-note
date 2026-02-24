# Separate

2023-07-25, 13:47
****
## 1. 简介

`Separator` 和 `SplitPane` 用于在视觉上分隔两个控件。

## 2. Separator

`Separator` 是分隔两组控件的水平或垂直线，常用于菜单和 `ComboBox`。

`Separator` 的默认构造函数创建水平 Separator。通过其它构造函数或 setOrientation() 方法可设置 separator 方向：

```java
// Create a horizontal separator
Separator separator1 = new Separator();

// Change the orientation to vertical
separator1.setOrientation(Orientation.VERTICAL);

// Create a vertical separator
Separator separator2 = new Separator(Orientation.VERTICAL);
```

Separator 会根据分配给它的空间自动调整尺寸。水平 Separator 在水平方向调整，垂直 Separator 在垂直方向调整。

Separator 通过 Region 实现，可以通过 CSS 修改其颜色和厚度。

### 2.1. Separator 属性

Separator 类包含 3 个属性：

- orientation
- halignment
- valignment

`orientation` 属性指定方向，可用值 `Orientation` enum: `HORIZONTAL` and `VERTICAL`。

halignment 属性指定垂直 separator 中分割线的水平对齐方式，水平 separator 忽略该属性。可用值 `HPos` enum: LEFT, CENTER, RIGHT，默认 CENTER.

`valignment` 属性指定水平 separator 分割线的垂直对齐方式，垂直 separator 忽略该属性。可用值 `VPos` enum: BASELINE, TOP, CENTER, BOTTOM，默认 CENTER。

### 2.2. Separator CSS

Separator 的 CSS 样式类名默认为 separator。

Separator 包含如下 CSS 属性：

- -fx-orientation
- -fx-halignment
- -fx-valignment

Separator 支持 horizontal 和 vertical 两个 CSS pseudo-classes，应用于水平和垂直 separators。

Separator 包含一个 line 子结构，为 Region 类型。Separator 中看到的线通过指定 line 子结构的 border 实现。例如：

```css
.separator > .line {
    -fx-border-style: solid;
    -fx-border-width: 1;
}
```

可以使用 image 作为 separator。为 separator 设置合适的尺寸，然后使用 image 作为 backgroun。

假设 separator.jpg image 文件和 CSS 文件在同一目录。下面的样式设置 separator 的 prefHeight 或 prefWidth：

```css
.separator {
    -fx-background-image: url("separator.jpg");
    -fx-background-repeat: repeat;
    -fx-background-position: center;
    -fx-background-size: cover;
}

.separator:horizontal {
    -fx-pref-height: 10;
}

.separator:vertical {
    -fx-pref-width: 10;
}
```

## 3. SplitPane

`SplitPane` 将多个 nodes 使用 divider 分开。用户可以拖动 divider 调整 nodes 尺寸。

`SplitPane` 的 nodes 可以是任意 Node 类型，通常是包含控件的 layoutPane。

下图是水平 `SplitPane`，左右两个各包含一个 VBox，每个 `VBox` 包含一个 `Label` 和一个 `TextArea`。

<img src="images/Pasted%20image%2020230725132916.png" style="zoom:67%;" />

创建 `SplitPane`:

```java
SplitPane sp = new SplitPane();
```

`SplitPane.getItems()` 返回 `ObservableList<Node>`，返回 `SplitPane` 中包含的 Nodes。可以通过该 list 添加 node:

```java
// Create panes
GridPane leftPane = new GridPane();
GridPane centerPane = new GridPane();
GridPane rightPane = new GridPane();

/* Populate the left, center, and right panes with controls here */

// Add panels to the a SplitPane
SplitPane sp = new SplitPane();
sp.getItems().addAll(leftPane, centerPane, rightPane);
```

`SplitPane` 默认水平排列 nodes。其 `orientation` 属性指定方向：

```java
// Place nodes vertically
sp.setOrientation(Orientation.VERTICAL);
```

divider 位置在 0 到 1 之间。0 表示最左或最上，1表示最右或最底。一个 divider 默认在 0.5，即中间位置。设置 divider 位置：

```java
setDividerPositions(double... positions)
setDividerPosition(int dividerIndex, double position)
```

`setDividerPositions()` 设置多个 dividers 的位置，参数与 dividers 个数必须相同。

`setDividerPosition()` 设置单个 divider 的位置。

`getDividerPositions()` 返回所有 dividers 的位置，double 数组类型。

SplitPane 在调整尺寸时，默认会调整其所含 nodes 的尺寸。使用 `setResizableWithParent()` 可以阻止 SplitPane 调整特定 node 的尺寸：

```java
// Make node1 non-resizable
SplitPane.setResizableWithParent(node1, false);
```

**示例：** SplitPane

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SplitPaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TextArea desc1 = new TextArea();
        desc1.setPrefColumnCount(10);
        desc1.setPrefRowCount(4);

        TextArea desc2 = new TextArea();
        desc2.setPrefColumnCount(10);
        desc2.setPrefRowCount(4);

        VBox vb1 = new VBox(new Label("Description1"), desc1);
        VBox vb2 = new VBox(new Label("Description2"), desc2);

        SplitPane sp = new SplitPane();
        sp.getItems().addAll(vb1, vb2);

        HBox root = new HBox(sp);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using SplitPane Controls");
        stage.show();
    }
}
```

<img src="images/Pasted%20image%2020230725134220.png" alt="|350" style="zoom:67%;" />

### 3.1. SplitPane CSS

SplitPane 的 CSS 样式类名默认为 split-pane。

SplitPane 包含一个 -fx-orientation CSS 属性，指定方向：horizontal 和 vertical。

SplitPane 支持 horizontal 和 vertical CSS pseudo-classes，应用于水平和垂直 SplitPane。

SplitPane 的 divider 为 split-pane-divider 子结构，StackPane 类型。

**示例：** 设置 divider background 为 blue，水平 SplitPane 的 prefWidth 5px；垂直 SplitPane 的 prefHeight 色湖之为 5px.

```css
.split-pane > .split-pane-divider {
    -fx-background-color: blue;
}

.split-pane:horizontal > .split-pane-divider {
    -fx-pref-width: 5;
}

.split-pane:vertical > .split-pane-divider {
    -fx-pref-height: 5;
}
```

split-pane-divider 包含一个 grabber 子结构，也是 StackPane 类型。水平 SplitPane 的 grabber 的 CSS 样式类名为 horizontal-grabber，垂直 SplitPane 的 grabber 的 CSS 样式类名为 vertical-grabber。grabber 在 divider 中间显示。
