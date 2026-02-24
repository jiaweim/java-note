# ToolBar

2023-07-25, 20:23
****
## 1. 简介

ToolBar 用于显示一组 nodes，在屏幕上提供常用操作。ToolBar 包含的常用操作，一般在菜单和 context-menu 也有。

ToolBar 可以包含多种类型的 nodes：

- Button 和 ToggleButton 最常见
- Separator 常在 ToolBar 中用来分组
- Button 较小，一般采用 16x16px 的 icons

当 ToolBar 空间不足以显示所有 nodes，会出现一个 overflow  button，用来导航到隐藏的 nodes。

ToolBar 可以为水平或垂直方向。下图显示 2 个水平 toolbar，第一个没有 overflow，第二个包含 overflow。

![|500](Pasted%20image%2020230725200240.png)

**示例：** 创建 4 个 Button，后面会用到

```java
Button rectBtn = new Button("", new Rectangle(0, 0, 16, 16));
Button circleBtn = new Button("", new Circle(0, 0, 8));
Button ellipseBtn = new Button("", new Ellipse(8, 8, 8, 6));
Button exitBtn = new Button("Exit");
```

ToolBar 将包含的控件保存在 `ObservableList<Node>` 中，`getItems()` 返回该 list。

## 2. 创建 ToolBar

ToolBar 的默认构造函数创建空的 toolbar:

```java
ToolBar toolBar = new ToolBar();
toolBar.getItems().addAll(circleBtn, ellipseBtn, new Separator(), exitBtn);
```

构造时指定 nodes:

```java
ToolBar toolBar = new ToolBar(rectBtn, circleBtn, ellipseBtn,
                            new Separator(),
                            exitBtn);
```

ToolBar 的 orientation 属性指定方向：horizontal 或 vertical，默认水平。设置方向：

```java
// Create a ToolBar and set its orientation to VERTICAL
ToolBar toolBar = new ToolBar();
toolBar.setOrientation(Orientation.VERTICAL);
```

```ad-tip
toolbar 中的 separator 的方向，默认 CSS 会自动调整。为 toolbar 的 items 提供 tool-tip 是好习惯。
```

**示例：** ToolBar

创建 ToolBar：

- 添加 4 个 nodes
- 点击 item，绘制对应 shape
- Exit item 退出应用

```java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ToolBarTest extends Application {

    // A canvas to draw shapes
    Canvas canvas = new Canvas(200, 200);

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        // Create ToolBar items
        Button rectBtn = new Button("", new Rectangle(0, 0, 16, 16));
        Button circleBtn = new Button("", new Circle(0, 0, 8));
        Button ellipseBtn = new Button("", new Ellipse(8, 8, 8, 6));
        Button exitBtn = new Button("Exit");

        // Set tooltips
        rectBtn.setTooltip(new Tooltip("Draws a rectangle"));
        circleBtn.setTooltip(new Tooltip("Draws a circle"));
        ellipseBtn.setTooltip(new Tooltip("Draws an ellipse"));
        exitBtn.setTooltip(new Tooltip("Exits application"));

        // Add ActionEvent handlers for items
        rectBtn.setOnAction(e -> draw("Rectangle"));
        circleBtn.setOnAction(e -> draw("Circle"));
        ellipseBtn.setOnAction(e -> draw("Ellipse"));
        exitBtn.setOnAction(e -> Platform.exit());

        ToolBar toolBar = new ToolBar(rectBtn, circleBtn, ellipseBtn,
                new Separator(),
                exitBtn);
        BorderPane root = new BorderPane();
        root.setTop(new VBox(new Label("Click a shape to draw."), toolBar));
        root.setCenter(canvas);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ToolBar Controls");
        stage.show();
    }

    public void draw(String shapeType) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 200, 200); // First clear the canvas
        gc.setFill(Color.TAN);

        if (shapeType.equals("Rectangle")) {
            gc.fillRect(0, 0, 200, 200);
        } else if (shapeType.equals("Circle")) {
            gc.fillOval(0, 0, 200, 200);
        } else if (shapeType.equals("Ellipse")) {
            gc.fillOval(10, 40, 180, 120);
        }
    }
}
```

![|250](Pasted%20image%2020230725201423.png)

## 3. ToolBar CSS

ToolBar 的 CSS 的样式类名默认为 tool-bar。

`-fx-orientation` CSS 属性指定方向：*horizontal* 或 *vertical* 。

ToolBar   支持 horizontal 和 vertical CSS pseudo-classes，分别应用于水平和垂直 ToolBar。

ToolBar 使用容器排列 items。水平 ToolBar 容器为 HBox，垂直 ToolBar 容器为 VBox。容器的 CSS  样式类名为 `container`。可以使用 HBox 和 VBox 的 CSS 属性定制 contanier。

`-fx-spacing` CSS 属性指定相邻元素的间距，为 toolbar 或 container 指定该属性效果一样。

**示例：** 下面两个样式相同

```css
.tool-bar {
    -fx-spacing: 2;
}

.tool-bar > .container {
    -fx-spacing: 2;
}
```

toolbar 包含一个 `tool-bar-overflow-button` 子结构，表示溢出按钮，为 StackPane 类型。

`tool-bar-overflow-button`  包含一个 arrow 子结构，表示溢出按钮的箭头，也是 StackPane。
