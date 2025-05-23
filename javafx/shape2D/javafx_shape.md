# Shape

2023-08-10
modify: 样式
2023-07-13
@author Jiawei Mao
***
## 概述

JavaFX 所有的 2D 形状类都放在 `javafx.scene.shape` 包中。2D 形状均扩展自抽象类 `Shape`，如下图所示：

<img src="images/Pasted%20image%2020230713184906.png" style="zoom: 33%;" />

`Shape` 类提供了几何形状对象的常用属性，定义边界线条和内部区域，需要如下属性：

| 属性             | 类型           | 说明              |
| ---------------- | -------------- | ---------------- |
| `fill`           | `Paint`          | 填充颜色，默认 `Color.BLACK`           |
| smooth           | Boolean        | 反锯齿平滑效果，默认 `true`            |
| strokeDashOffset | Double         | 虚线从虚线数组中开始的位置，默认 0    |
| strokeLineCap    | StrokeLineCap  | 线条末端形状，包括 `StrokeLineCap.BUTT, ROUND, SQUARE`，default=SQUARE     |
| strokeLineJoin   | StrokeLineJoin | 线条连接的方式，取值有 `StrkeLineJoin.BEVEL, MITER, ROUND`             |
| strokeMiterLimit | Double         | 对 `MITER` 线段连接方式，两条线段通过延伸外边框进行连接，线条角度很小时，延伸线可能很长，该属性用于指定 miter 长度和线条宽度的比值。default=10.0   |
| stroke           | Paint          | 线条边框颜色，默认 `null`。`Line`, `Polyline`, `Path` 例外，它们默认 `Color.BLACK`               |
| strokeType       | StrokeType     | stroke 沿着形状的边框绘制，strokeType 属性用于指定stroke相对边框的位置。有三种取值：`CENTERED`, `INSIDE`, `OUTSIDE`，默认为 `CENTERED`，即线条一半在边框内，一半在边框外.defaut=CENTERED |
| strokeWidth      | Double         | 线条宽度，默认 1.0px                                        |

`Shape` 尺寸不受父容器 layout 策略影响，只能修改其尺寸相关的属性。

### 示例

**示例：** `fill` 和 `stroke` 属性

创建 2 个 `Circle`：

- 第一个填充浅灰色，没有 stroke；
- 第二个填充黄色，2px black stroke

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ShapeTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a circle with a light gray fill and no stroke
        Circle c1 = new Circle(40, 40, 40);
        c1.setFill(Color.LIGHTGRAY);

        // Create a circle with an yellow fill and a black stroke of 2.0px
        Circle c2 = new Circle(40, 40, 40);
        c2.setFill(Color.YELLOW);
        c2.setStroke(Color.BLACK);
        c2.setStrokeWidth(2.0);

        HBox root = new HBox(c1, c2);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Shapes");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713190640.png" {width="200px" title=""}

**示例：** `Line` 的 strokeDashOffset, strokeLineCap, strokeWidth, strokeColor 等属性。

- `Line.setStroke()` 设置颜色
- `setStrokeWidth()` 设置 strokeWidth
- `setStrokeLineCap()` 设置 cap 样式

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DrawingLines extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 设置 Stage 窗口标题
        primaryStage.setTitle(" Drawing Lines");

        Group root = new Group();
        // Scene 默认填充白色，因为下面有一条白线，所以将其修改为灰色
        Scene scene = new Scene(root, 300, 150, Color.GRAY);

        // Red line
        Line redLine = new Line(10, 10, 200, 10);

        // setting common properties
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(10);
        redLine.setStrokeLineCap(StrokeLineCap.BUTT);

        // creating a dashed pattern
        redLine.getStrokeDashArray().addAll(10d, 5d, 15d, 5d, 20d);
        redLine.setStrokeDashOffset(0);

        root.getChildren().add(redLine);

        // White line
        Line whiteLine = new Line(10, 30, 200, 30);
        whiteLine.setStroke(Color.WHITE);
        whiteLine.setStrokeWidth(10);
        whiteLine.setStrokeLineCap(StrokeLineCap.ROUND);

        root.getChildren().add(whiteLine);

        // Blue line
        Line blueLine = new Line(10, 50, 200, 50);
        blueLine.setStroke(Color.BLUE);
        blueLine.setStrokeWidth(10);

        root.getChildren().add(blueLine);


        // slider min, max, and current value
        Slider slider = new Slider(0, 100, 0);
        slider.setLayoutX(10);
        slider.setLayoutY(95);

        // bind the stroke dash offset property
        redLine.strokeDashOffsetProperty().bind(slider.valueProperty());
        root.getChildren().add(slider);

        Text offsetText = new Text("Stroke Dash Offset: " + slider.getValue());
        offsetText.setX(10);
        offsetText.setY(80);
        offsetText.setStroke(Color.WHITE);

        // display stroke dash offset value
        slider.valueProperty().addListener((ov, curVal, newVal) -> {
            offsetText.setText("Stroke Dash Offset: " + newVal);
        });
        root.getChildren().add(offsetText);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

![](images/2023-08-10-12-37-34.png)

## 2D Shapes

下面对 JavaFX 提供各种 `Shape` 相关类进行详细介绍。

### Line

线段用 `javafx.scene.shape.Line` 类表示:

- `Line` 没有内部区域，`fill` 默认为 `null`，设置 `fill` 无效
- `stroke` 默认为 `Color.BLACK`
- `strokeWidth` 默认为 1.0

定义一个线段需要如下属性：

|属性|说明|
|---|---|
|startX	|线条起点 X 值|
|startY	|线条起点 Y 值|
|endX	|线条终点 X 值|
|endY	|线条终点 Y 值|

Line 有两个构造函数，构造时指定参数：

```java
Line line = new Line(100, 10, 10, 110)
```

构造后设置参数：

```java
Line line = new Line();
line.setStartX(100);
line.setStartY(10);
line.setEndX(10);
line.setEndY(110);
```

**示例：** 常见 Line

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class LineTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // It will be just a point at (0, 0)
        Line line1 = new Line();

        Line line2 = new Line(0, 0, 50, 0);
        line2.setStrokeWidth(1.0);

        Line line3 = new Line(0, 50, 50, 0);
        line3.setStrokeWidth(2.0);
        line3.setStroke(Color.RED);

        Line line4 = new Line(0, 0, 50, 50);
        line4.setStrokeWidth(5.0);
        line4.setStroke(Color.BLUE);

        HBox root = new HBox(line1, line2, line3, line4);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Lines");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713191555.png" {width="230px" title=""}

### Rectangle

矩形用 `Rectangle` 类表示。除了 Shape 的常规属性，矩形添加了如下属性：

|属性|说明|
|---|---|
|x|左上角坐标 X 值|
|y|左上角坐标 Y 值|
|width|宽度|
|height|高度|
|arcWidth|圆角水平直径, default = 0|
|arcHeight|圆角垂直直径，default = 0|

矩形的四个角默认为直角，`arcWidth` 和 `arcHeight` 属性将四个角改成圆角，其值是椭圆弧线的水平和垂直直径，如下图所示：

@import "images/Pasted%20image%2020230713191959.png" {width="400px" title=""}

`Rectangle` 提供了多个构造函数。`Rectangle` 的 x, y, width, height, arcWidth 和 arcHeigt 属性的默认值都是 0。构造函数如下：

```java
Rectangle()
Rectangle(double width, double height)
Rectangle(double x, double y, double width, double height)
Rectangle(double width, double height, Paint fill)
```

将 `Rectangle` 添加到大多数 layout 容器中，属性 x 和 y 都无效。在绝对布局 Pane 中可以看出效果。

**示例：** 在 `Pane` 中添加 2 个 `Rectangle`

2 个 `Rectangle`:

- 第 1 个使用默认的 x, y 值，即 0
- 第 2 个 x=120, y=20

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RectangleTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // x=0, y=0, width=100, height=50, fill=LIGHTGRAY, stroke=null
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);

        // x=120, y=20, width=100, height=50, fill=WHITE, stroke=BLACK 
        Rectangle rect2 = new Rectangle(120, 20, 100, 50);
        rect2.setFill(Color.WHITE);
        rect2.setStroke(Color.BLACK);
        rect2.setArcWidth(10);
        rect2.setArcHeight(10);

        Pane root = new Pane();
        root.getChildren().addAll(rect1, rect2);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Rectangles");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713192818.png" {width="250px" title=""}

### Circle

圆形由 `Circle` 类表示。`Circle` 使用如下属性定义圆：

|属性|说明|
|---|---|
|centerX	|原点 X 值|
|centerY	|原点 Y 值|
|radius|圆半径，default = 0|

centerX, centerY 和 radius 默认均为 0。`Circle` 提供了多个构造函数：

```java
Circle()
Circle(double radius)
Circle(double centerX, double centerY, double radius)
Circle(double centerX, double centerY, double radius, Paint fill)
Circle(double radius, Paint fill)
```

**示例：** `HBox` 中包含 2 个圆

`HBox` 不使用 `centerX` 和 `centerY` 设置 `Circle` 位置。添加到 `Pane` 才能看到这 2 个属性的效果。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CircleTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // centerX=0, centerY=0, radius=40, fill=LIGHTGRAY, stroke=null
        Circle c1 = new Circle(0, 0, 40);
        c1.setFill(Color.LIGHTGRAY);

        // centerX=10, centerY=10, radius=40. fill=YELLOW, stroke=BLACK
        Circle c2 = new Circle(10, 10, 40, Color.YELLOW);
        c2.setStroke(Color.BLACK);
        c2.setStrokeWidth(2.0);

        HBox root = new HBox(c1, c2);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Circle");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713193558.png" {width="200px" title=""}

### Ellipse

椭圆由 `Ellipse` 类表示。定义椭圆所需属性（默认均为 0）：

|属性|说明|
|---|---|
|centerX|原点 X 值|
|centerY|原点 Y 值|
|radiusX|水平半径，default = 0|
|radiuxY|垂直半径，default = 0|

`Ellipse` 构造函数：

```java
Ellipse()
Ellipse(double radiusX, double radiusY)
Ellipse(double centerX, double centerY, double radiusX, double radiusY)
```

**示例：** `Ellipse`

创建 3 个 `Ellipse`，第 3 个 `radiusX` 和 `radiusY` 相同，所以实际是圆。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

public class EllipseTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Ellipse e1 = new Ellipse(50, 30);
        e1.setFill(Color.LIGHTGRAY);

        Ellipse e2 = new Ellipse(60, 30);
        e2.setFill(Color.YELLOW);
        e2.setStroke(Color.BLACK);
        e2.setStrokeWidth(2.0);

        // Draw a circle using the Ellipse class (radiusX=radiusY=30)
        Ellipse e3 = new Ellipse(30, 30);
        e3.setFill(Color.YELLOW);
        e3.setStroke(Color.BLACK);
        e3.setStrokeWidth(2.0);

        HBox root = new HBox(e1, e2, e3);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Ellipses");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713194048.png" {width="350px" title=""}

### Polygon

多边形由 `Polygon` 类表示。该类没有定义任何 public 属性，通过 (x,y) 坐标数组指定多边形的各个角的坐标来定义多边形。

`Polygon` 类将坐标保存在 `ObservableList<Double>` 类中，通过 `getPoints()` 方法获得该列表。连续的每两个值表示一个坐标，所以要确保传入的参数为偶数个，否则出错，无法创建多边形。

Polygon 提供了 2 个构造函数，构造后提供坐标点：

```java
Polygon triangle1 = new Polygon();
triangle1.getPoints().addAll(50.0, 0.0,
        0.0, 100.0,
        100.0, 100.0);
```

构造时指定坐标点：

```java
Polygon triangle2 = new Polygon(50.0, 0.0,
        0.0, 100.0,
        100.0, 100.0);
```

**示例：** 使用 Polygon 创建三角形、平行四边形和六边形

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class PolygonTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Polygon triangle1 = new Polygon();
        triangle1.getPoints().addAll(50.0, 0.0,
                0.0, 50.0,
                100.0, 50.0);
        triangle1.setFill(Color.WHITE);
        triangle1.setStroke(Color.RED);

        Polygon parallelogram = new Polygon();
        parallelogram.getPoints().addAll(30.0, 0.0,
                130.0, 0.0,
                100.00, 50.0,
                0.0, 50.0);
        parallelogram.setFill(Color.YELLOW);
        parallelogram.setStroke(Color.BLACK);

        Polygon hexagon = new Polygon(100.0, 0.0,
                120.0, 20.0,
                120.0, 40.0,
                100.0, 60.0,
                80.0, 40.0,
                80.0, 20.0);
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.BLACK);

        HBox root = new HBox(triangle1, parallelogram, hexagon);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Polygons");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713195248.png" {width="350px" title=""}

### Polyline

折线用 `Polyline` 类表示。折线定义方式和多边形类似，只是终点和起点不连接，没有构成封闭图形。不过 `fill` 属性通过假设该图形是封闭的来填充颜色。

`Polyline` 也提供了两种构造方式。

构造后添加数据点：

```java
// Create an empty triangle and add vertices later
Polygon triangle1 = new Polygon();
triangle1.getPoints().addAll(
    50.0, 0.0,
    0.0, 100.0,
    100.0, 100.0,
    50.0, 0.0);
```

构造时设置数据点：

```java
// Create a triangle with vertices
Polygon triangle2 = new Polygon(
    50.0, 0.0,
    0.0, 100.0,
    100.0, 100.0,
    50.0, 0.0);
```

**示例：** 使用 `Polyline` 创建三角形、平行四边形和六边形

六边形是完整的，因为在默认添加了第一点的坐标，使其封闭。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class PolylineTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Polyline triangle1 = new Polyline();
        triangle1.getPoints().addAll(50.0, 0.0,
                0.0, 50.0,
                100.0, 50.0,
                50.0, 0.0);
        triangle1.setFill(Color.WHITE);
        triangle1.setStroke(Color.RED);

        // Create an open parallelogram
        Polyline parallelogram = new Polyline();
        parallelogram.getPoints().addAll(30.0, 0.0,
                130.0, 0.0,
                100.00, 50.0,
                0.0, 50.0);
        parallelogram.setFill(Color.YELLOW);
        parallelogram.setStroke(Color.BLACK);

        Polyline hexagon = new Polyline(100.0, 0.0,
                120.0, 20.0,
                120.0, 40.0,
                100.0, 60.0,
                80.0, 40.0,
                80.0, 20.0,
                100.0, 0.0);
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.BLACK);

        HBox root = new HBox(triangle1, parallelogram, hexagon);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Polylines");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713195855.png" {width="350px" title=""}

### Arc

2D 弧线由 `Arc` 类表示，以椭圆的一部分来定义。定义弧线需要如下属性：

|属性|说明|
|---|---|
|centerX|椭圆原点 X 值|
|centerY|椭圆原点 Y 值|
|radiusX|椭圆水平半径，default = 0|
|radiuxY|椭圆垂直半径，default = 0|
|startAngle|弧线起始角度（从X轴开始逆时针方向扫过的角度)|
|length	|弧线扫过的角度，default=0.0|
|type	|指定弧线封闭的方式|

前面四个属性定义了一个椭圆，后面三个属性定义了椭圆上的一个扇形。如果将 length设置为 360，就得到完整的椭圆。

@import "images/Pasted%20image%2020230713200256.png" {width="400px" title=""}

`type` 属性指定弧线封闭的方式，由 enum `ArcType` 指定：

- `ArcType.OPEN`, 不封闭弧线，默认值。
- `ArcType.CHORD`, 以一条直线连接弧线的起点和终点。
- `ArcType.ROUND`, 将弧线的起始点和原点连接。

下图是三种弧线类型：

@import "images/Pasted%20image%2020230713200526.png" {width="350px" title=""}

```ad-warning
如果不指定 `stroke`，指定 `ArcType` 无效。
```


`fill` 属性，默认填充 `ArcType.CHORD` 区域。

**示例：** Arc

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class ArcTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // An OPEN arc with a fill
        Arc arc1 = new Arc(0, 0, 50, 100, 0, 90);
        arc1.setFill(Color.LIGHTGRAY);

        // An OPEN arc with no fill and a stroke
        Arc arc2 = new Arc(0, 0, 50, 100, 0, 90);
        arc2.setFill(Color.TRANSPARENT);
        arc2.setStroke(Color.BLACK);

        // A CHORD arc with no fill and a stroke
        Arc arc3 = new Arc(0, 0, 50, 100, 0, 90);
        arc3.setFill(Color.TRANSPARENT);
        arc3.setStroke(Color.BLACK);
        arc3.setType(ArcType.CHORD);

        // A ROUND arc with no fill and a stroke
        Arc arc4 = new Arc(0, 0, 50, 100, 0, 90);
        arc4.setFill(Color.TRANSPARENT);
        arc4.setStroke(Color.BLACK);
        arc4.setType(ArcType.ROUND);

        // A ROUND arc with a gray fill and a stroke
        Arc arc5 = new Arc(0, 0, 50, 100, 0, 90);
        arc5.setFill(Color.GRAY);
        arc5.setStroke(Color.BLACK);
        arc5.setType(ArcType.ROUND);

        HBox root = new HBox(arc1, arc2, arc3, arc4, arc5);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Arcs");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713200758.png" {width="350px" title="Arc"}

### QuadCurve

计算机中使用 [Bezier 曲线](https://en.wikipedia.org/wiki/B%C3%A9zier_curve) 绘制平滑曲线。`QuadCurve` 类表示包含一个控制点两个指定点的 Bezier 曲线。这 3 个点由 6 个属性 startX, startY, controlX, controlY, endX, endY 指定。

**示例：** 使用 `QuadCurve` 创建 2 个贝塞尔曲线

一个具有 stroke；另一个 stroke 为默认值 null，填充浅灰色。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.stage.Stage;

public class QuadcurveTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        QuadCurve qc1 = new QuadCurve(0, 100, 20, 0, 150, 100);
        qc1.setFill(Color.TRANSPARENT);
        qc1.setStroke(Color.BLACK);

        QuadCurve qc2 = new QuadCurve(0, 100, 20, 0, 150, 100);
        qc2.setFill(Color.LIGHTGRAY);

        HBox root = new HBox(qc1, qc2);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using QuadCurves");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713201423.png" {width="350px" title="QuadCurve"}

### CubicCurve

`CubicCurve` 表示有 2 个控制点、2 个指定点的 Bezier 曲线。`CubicCurve` 通过 8 个属性指定这 4 个点：(startX, startY), (controlX1, controlY1), (controlX2, controlY2), (endX, endY)。

@import "images/Pasted%20image%2020230713201902.png" {width="300px" title=""}

控制点，可以看做曲线切线的交点。

**示例：** 使用 `CubicCurve` 创建 2 条三次曲线

一条指定 stroke；另一条没有 stroke，用浅灰色填充。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.stage.Stage;

public class CubicCurveTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        CubicCurve cc1 = new CubicCurve(0, 50, 20, 0, 50, 80, 50, 0);
        cc1.setFill(Color.TRANSPARENT);
        cc1.setStroke(Color.BLACK);

        CubicCurve cc2 = new CubicCurve(0, 50, 20, 0, 50, 80, 50, 0);
        cc2.setFill(Color.LIGHTGRAY);

        HBox root = new HBox(cc1, cc2);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using CubicCurves");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230713202118.png" {width="200px" title="CubicCurve"}

## Path

上一节讨论的各个 Shape 类适合于绘制简单形状。对复杂形状，则推荐使用 `Path` 类。

`Path` 定义形状的路径，一条路径包含多个子路径，每个子路径由特定路径元素组成，路径元素由 `PathElement` 类表示。

`PathElement` 有多个子类，定义不同的路径元素：

<img src="images/Pasted%20image%2020230713203914.png" style="zoom:50%;" />

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

### 2. MoveTo

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

### 3. LineTo

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

<img src="images/Pasted%20image%2020230713205536.png" alt="|500" style="zoom:50%;" />

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

<img src="images/Pasted%20image%2020230713210013.png" alt="|200" style="zoom: 67%;" />

### 4. HLineTo 和 VLineTo

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

### 5. ArcTo

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

<img src="images/Pasted%20image%2020230713212148.png" alt="|400" style="zoom:50%;" />

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

<img src="images/Pasted%20image%2020230713214221.png" alt="|400" style="zoom:50%;" />

### 6. QuadCurveTo

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

<img src="images/Pasted%20image%2020230713214909.png" alt="|200" style="zoom:50%;" />

### 7. CubicCurveTo

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

<img src="images/Pasted%20image%2020230713215309.png" alt="|200" style="zoom:50%;" />

### 8. ClosePath

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

<img src="images/Pasted%20image%2020230713220217.png" alt="|200" style="zoom:50%;" />


### 9. Path 填充规则

Path 可以用来绘制非常复杂的图形。有时很难确定一个点是在形状内还是形状外。`Path` 类的 `fillRule` 属性用于确定一个点到底是形状内还是形状外。`FillRule` enum 只有两个值：

- `NON_ZERO`
- `EVEN_ODD`

如果一个点在形状内，会被指定的 fill 渲染。

下图是用 Path 创建的 2 个三角形：

<img src="images/Pasted%20image%2020230713220842.png" alt="|150" style="zoom:50%;" />

**stroke 的方向**对于确定一个点是否在图形内很重要。上面的图形可以采用不同的方向绘制：

<img src="images/Pasted%20image%2020230713221018.png" alt="|300" style="zoom:50%;" />

在 Shape-1 中，两个三角形的 strokes 都是逆时针；在 Shape-2 中，一个逆时针，一个顺时针。

`Path` 的填充规则是：从考察点绘制射线，保证射线和图形相交。

对 `NON_ZERO` 填充规则，如果和射线相交的边的数量在顺时针和逆时针的方向相同，则该点在图形外，否则在图形内。如下图所示，`NON_ZERO` 能以计数器的形式实现：逆时针线段相交，计数+1，和顺时针线段相交，计数-1，最终为 0，表示该点在图形外；不为 0，表示该点在图形内：

<img src="images/Pasted%20image%2020230713221847.png" alt="|400" style="zoom:50%;" />


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

<img src="images/Pasted%20image%2020230713222855.png" style="zoom:50%;" />

