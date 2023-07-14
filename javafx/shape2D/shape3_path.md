# Path

2023-07-13, 22:30
****
## 1. 简介

上一节讨论的各个 Shape 类适合于绘制简单形状。对复杂形状，则推荐使用 `Path` 类。

`Path` 定义形状的路径，一条路径包含多个子路径，每个子路径由特定路径元素组成，路径元素由 `PathElement` 类表示。

`PathElement` 有多个子类，定义不同的路径元素：

![](Pasted%20image%2020230713203914.png)

使用 `Path` 创建图形的过程，与用笔在纸上画画类似。首先，将笔移到纸上的特定点，移动笔绘制路径元素（比如横线 HLineTo）。

定义 `PathElement` 的坐标值可以采用相对或绝对值，使用 `absolute` 属性设置。默认采用绝对值：

- 绝对值指相对 node 的本地坐标系定义坐标
- 相对值指相对上一个 `PathElement` 的终点定义坐标

Path 定义了 3 个构造函数：

```java
Path()
Path(Collection<? extends PathElement> elements)
Path(PathElement... elements)
```

`getElements()` 返回 `Path` 包含的 `PathElement` 列表 `ObservableList<PathElement>`。

## 2. MoveTo

`MoveTo` 将指定的坐标作为当前点，相当于提起笔，放到指定位置。

`Path` 的第一个元素必须是 `MoveTo`，并且必须采用绝对坐标。`MoveTo` 定义了两个 double 属性 (x,y)。

构造函数：

```java
// 默认起点 (0.0, 0.0)
MoveTo mt1 = new MoveTo();

// 指定起点为 (10.0, 10.0)
MoveTo mt2 = new MoveTo(10.0, 10.0);
```

```ad-tip
`Path` 必须以 `MoveTo` 开始。`Path` 可以包含多个 `MoveTo`，每个 `MoveTo` 表示新的子路径起点。
```

## 3. LineTo

`LineTo` 从当前点到指定点绘制直线。包含 x, y 两个属性，指定直线终点。构造函数：

```java
// 默认终点 (0.0, 0.0)
LineTo lt1 = new LineTo();

// 指定终点 (10.0, 10.0)
LineTo lt2 = new LineTo(10.0, 10.0);
```

使用 MoveTo 和 LineTo，可以绘制进包含直线的形状。例如，创建一个三角形：

```java
Path triangle = new Path(new MoveTo(0, 0),
        new LineTo(0, 50),
        new LineTo(50, 50),
        new LineTo(0, 0));
```

![|500](Pasted%20image%2020230713205536.png)

`ClosePath` 在当前点和起点之间绘制直线，封闭路径。如果路径中有多个 `MoveTo`，`ClosePath` 在当前点和上个 `MoveTo` 的起点绘制直线。

使用 `ClosePath` 重绘上面的三角形：

```java
Path triangle = new Path(new MoveTo(0, 0),
                        new LineTo(0, 50),
                        new LineTo(50, 50),
                        new ClosePath());
```

**示例：** 绘制一个三角形和一个六角形

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

public class PathTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Path triangle = new Path(new MoveTo(0, 0),
                new LineTo(0, 50),
                new LineTo(50, 50),
                new ClosePath());

        Path star = new Path();
        star.getElements().addAll(
                new MoveTo(30, 0),
                new LineTo(0, 30),
                new LineTo(60, 30),
                new ClosePath(),/* new LineTo(30, 0), */
                new MoveTo(0, 10),
                new LineTo(60, 10),
                new LineTo(30, 40),
                new ClosePath() /*new LineTo(0, 10)*/);

        HBox root = new HBox(triangle, star);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Paths");
        stage.show();
    }
}
```

![|200](Pasted%20image%2020230713210013.png)

## 4. HLineTo 和 VLineTo

`HLineTo` 从当前点到指定 x 坐标绘制水平线，终点 y 坐标与起点相同。HLineTo 的 x 属性指定终点：

```java
// 从 (x,y) 到 (50, y) 的水平线
HLineTo hlt = new HLineTo(50);
```

VLineTo 从当前点到指定 y 坐标绘制垂直线，终点 x 坐标与起点相同。VLineTo 的 y 属性指定终点：

```java
// 从 (x,y) 到 (x,50) 的垂直线
VLineTo vlt = new VLineTo(50);
```

```ad-tip
LineTo 是 HLineTo 和 VLineTo 的通用版本。
```

## 5. ArcTo

ArcTo 在当前点和指定点之间绘制弧线。需要如下属性定义：

|属性|说明|
|---|---|
|radiusX, radiusY|指定椭圆的水平和垂直半径|
|x, y|弧线的终点|
|XAsixRotation|相对椭圆的 x 轴旋转的度数|
|largeArcFlag||
|sweepFlag||

largeArcFlag 和 sweepFlag 属性为 boolean 类型，默认为 false。

给定两个点，在椭圆上的位置可能有两种：

![|400](Pasted%20image%2020230713212148.png)

这样就有 4 条弧线。

在椭圆上，一个点到另一个点，可以走大弧线，也可以走小弧线。当 largeArcFlag 为 true 时，使用大弧线。

选择大弧线或小弧线后，还要选择椭圆。椭圆的选择由 `sweepFlag` 属性决定。从起点到终点绘制选择的弧线，一个椭圆为顺时针，一个为逆时针。sweepFlag 为 true 时，选择顺时针椭圆；否则选择逆时针椭圆。

|largeArcFlag|sweepFlag|Arc Type|Ellipse|
|---|---|---|---|
|true|true|Larger|Ellipse-2|
|true|false|Larger|Ellipse-1|
|false|true|Smaller|Ellipse-1|
|false|false|Smaller|Ellipse-2|

**示例：** 

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class ArcToTest extends Application {

    private ArcTo arcTo;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create the ArcTo path element
        arcTo = new ArcTo();

        // Use the arcTo element to build a Path
        Path path = new Path(new MoveTo(0, 0),
                new VLineTo(100),
                new HLineTo(100),
                new VLineTo(50),
                arcTo);

        BorderPane root = new BorderPane();
        root.setTop(this.getTopPane());
        root.setCenter(path);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ArcTo Path Elements");
        stage.show();
    }

    private GridPane getTopPane() {
        CheckBox largeArcFlagCbx = new CheckBox("largeArcFlag");
        CheckBox sweepFlagCbx = new CheckBox("sweepFlag");
        Slider xRotationSlider = new Slider(0, 360, 0);
        xRotationSlider.setPrefWidth(300);
        xRotationSlider.setBlockIncrement(30);
        xRotationSlider.setShowTickMarks(true);
        xRotationSlider.setShowTickLabels(true);

        Slider radiusXSlider = new Slider(100, 300, 100);
        radiusXSlider.setBlockIncrement(10);
        radiusXSlider.setShowTickMarks(true);
        radiusXSlider.setShowTickLabels(true);

        Slider radiusYSlider = new Slider(100, 300, 100);
        radiusYSlider.setBlockIncrement(10);
        radiusYSlider.setShowTickMarks(true);
        radiusYSlider.setShowTickLabels(true);

        // Bind ArcTo properties to the control data
        arcTo.largeArcFlagProperty().bind(largeArcFlagCbx.selectedProperty());
        arcTo.sweepFlagProperty().bind(sweepFlagCbx.selectedProperty());
        arcTo.XAxisRotationProperty().bind(xRotationSlider.valueProperty());
        arcTo.radiusXProperty().bind(radiusXSlider.valueProperty());
        arcTo.radiusYProperty().bind(radiusYSlider.valueProperty());

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(10);
        pane.addRow(0, largeArcFlagCbx, sweepFlagCbx);
        pane.addRow(1, new Label("XAxisRotation"), xRotationSlider);
        pane.addRow(2, new Label("radiusX"), radiusXSlider);
        pane.addRow(3, new Label("radiusY"), radiusYSlider);

        return pane;
    }
}
```

![|400](Pasted%20image%2020230713214221.png)

## 6. QuadCurveTo

QuadCurveTo 从当前点到指定点之间绘制一条二次曲线。通过  4 个属性指定终点和控制点：x, y, controlX 和 controlY。

QuadCurveTo 提供了 2 个构造函数：

```java
QuadCurveTo()
QuadCurveTo(double controlX, double controlY, double x, double y)
```

**示例：** 控制点 (10, 100)，终点 (0, 0)

```java
Path path = new Path(
    new MoveTo(0, 0),
    new VLineTo(100),
    new HLineTo(100),
    new VLineTo(50),
    new QuadCurveTo(10, 100, 0, 0));
```

![|200](Pasted%20image%2020230713214909.png)

## 7. CubicCurveTo

CubicCurveTo 绘制三次曲线。使用 6 个属性指定终点和 2 个控制点：x, y, controlX1, controlY1, controlX2, controlY2。

CubicCurveTo 提供了 2 个构造函数：

```java
CubicCurveTo()
CubicCurveTo(double controlX1, double controlY1, 
             double controlX2, double controlY2, 
             double x, double y)
```

**示例：** (10, 100) 和 (40, 80) 作为控制点，(0, 0) 作为终点

```java
Path path = new Path(
    new MoveTo(0, 0),
    new VLineTo(100),
    new HLineTo(100),
    new VLineTo(50),
    new CubicCurveTo(10, 100, 40, 80, 0, 0));
```

![|200](Pasted%20image%2020230713215309.png)

## 8. ClosePath

ClosePath 关闭当前 subpath。一条 Path 可能包含多个 subpaths，因此也可以有多个 ClosePath。

ClosePath 在当前点和当前 subpath 的起点之间绘制一条直线。

- 如果 ClosePath 后跟着 MoveTo，此时 MoveTo 是下一个 subpath 的起点
- 如果 ClosePath 后不是 MoveTo，则下一个 subpath 的起点与闭合的 subpath 的起点相同

**示例：** 使用 2 个 subpaths 创建 2 个三角形

```java
Path p1 = new Path(
    new MoveTo(50, 0),
    new LineTo(0, 50),
    new LineTo(100, 50),
    new ClosePath(),
    new MoveTo(90, 15),
    new LineTo(40, 65),
    new LineTo(140, 65),
    new ClosePath());
p1.setFill(Color.LIGHTGRAY);
```

![|200](Pasted%20image%2020230713220217.png)


## 9. Path 填充规则

Path 可以用来绘制非常复杂的图形。有时很难确定一个点是在形状内还是形状外。`Path` 类的 `fillRule` 属性用于确定一个点到底是形状内还是形状外。`FillRule` enum 只有两个值：

- `NON_ZERO`
- `EVEN_ODD`

如果一个点在形状内，会被指定的 fill 渲染。

下图是用 Path 创建的 2 个三角形：

![|150](Pasted%20image%2020230713220842.png)

**stroke 的方向**对于确定一个点是否在图形内很重要。上面的图形可以采用不同的方向绘制：

![|300](Pasted%20image%2020230713221018.png)

在 Shape-1 中，两个三角形的 strokes 都是逆时针；在 Shape-2 中，一个逆时针，一个顺时针。

`Path` 的填充规则是：从考察点绘制射线，保证射线和图形相交。

对 `NON_ZERO` 填充规则，如果和射线相交的边的数量在顺时针和逆时针的方向相同，则该点在图形外，否则在图形内。如下图所示，`NON_ZERO` 能以计数器的形式实现：逆时针线段相交，计数+1，和顺时针线段相交，计数-1，最终为 0，表示该点在图形外；不为 0，表示该点在图形内：

![|400](Pasted%20image%2020230713221847.png)


在上图中，左图，逆时针相交数目为 6，不为 0，在图形内；右图计数为 0，在图形外。

 和 `NON_ZERO` 类似，`EVEN_ODD` 也是从考察点绘制射线。如果相交边数为偶数，则在图形外；否则在图形内。

按照该规则，上图的中点均在图形外，因为射线与路径的相交边数为 6，为偶数。

**示例：** 填充规则演示

绘制 4 个 paths:

- 前 2 个采用 NON_ZERO fill 规则
- 后 2 个采用 EVEN_ODD fill 规则
- 1, 3 采用逆时针绘制 2 个三角形
- 2, 4 其中 1 个逆时针，另一个顺时针

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class PathFillRule extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Both triangles use a couterclockwise stroke
        PathElement[] pathEleemnts1 = {new MoveTo(50, 0),
                new LineTo(0, 50),
                new LineTo(100, 50),
                new LineTo(50, 0),
                new MoveTo(90, 15),
                new LineTo(40, 65),
                new LineTo(140, 65),
                new LineTo(90, 15)};

        // One traingle uses a clockwise stroke and 
        // another uses a couterclockwise stroke
        PathElement[] pathEleemnts2 = {new MoveTo(50, 0),
                new LineTo(0, 50),
                new LineTo(100, 50),
                new LineTo(50, 0),
                new MoveTo(90, 15),
                new LineTo(140, 65),
                new LineTo(40, 65),
                new LineTo(90, 15)};

        /* Using the NON-ZERO fill rule by default */
        Path p1 = new Path(pathEleemnts1);
        p1.setFill(Color.LIGHTGRAY);

        Path p2 = new Path(pathEleemnts2);
        p2.setFill(Color.LIGHTGRAY);

        /* Using the EVEN_ODD fill rule */
        Path p3 = new Path(pathEleemnts1);
        p3.setFill(Color.LIGHTGRAY);
        p3.setFillRule(FillRule.EVEN_ODD);

        Path p4 = new Path(pathEleemnts2);
        p4.setFill(Color.LIGHTGRAY);
        p4.setFillRule(FillRule.EVEN_ODD);

        HBox root = new HBox(p1, p2, p3, p4);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Fill Rules for Paths");
        stage.show();
    }
}
```

![](Pasted%20image%2020230713222855.png)

