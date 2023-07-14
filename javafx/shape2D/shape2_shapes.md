# 2D Shapes

2023-07-13, 20:21
****
## 1. 简介

下面对 JavaFX 提供各种 Shape 相关类进行详细介绍。

## 2. Line

线段用 `Line` 类表示:

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

```java
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

![|230](Pasted%20image%2020230713191555.png)

## 3. Rectangle

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

![|400](Pasted%20image%2020230713191959.png)

Rectangle 提供了多个构造函数。Rectangle 的 x, y, width, height, arcWidth 和 arcHeigt 属性的默认值都是 0。构造函数如下：

```java
Rectangle()
Rectangle(double width, double height)
Rectangle(double x, double y, double width, double height)
Rectangle(double width, double height, Paint fill)
```

将 Rectangle 添加到大多数 layout 容器中，属性 x 和 y 都无效。在绝对布局 Pane 中可以看出效果。

**示例：** 在 Pane 中添加 2 个 Rectangle

2 个 Rectangle:

- 第 1 个使用默认的 x, y 值，即 0
- 第 2 个 x=120, y=20

```java
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

![|250](Pasted%20image%2020230713192818.png)

## 4. Circle

圆形由 Circle 类表示。Circle 使用如下属性定义圆：

|属性|说明|
|---|---|
|centerX	|原点 X 值|
|centerY	|原点 Y 值|
|radius|圆半径，default = 0|

centerX, centerY 和 radius 默认均为 0。Circle 提供了多个构造函数：

```java
Circle()
Circle(double radius)
Circle(double centerX, double centerY, double radius)
Circle(double centerX, double centerY, double radius, Paint fill)
Circle(double radius, Paint fill)
```

**示例：** HBox 中包含 2 个圆

HBox 不使用 centerX 和 centerY 设置 Circle 位置。添加到 Pane 才能看到这 2 属性的效果。

```java
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

![|200](Pasted%20image%2020230713193558.png)

## 5. Ellipse

椭圆由 Ellipse 类表示。定义椭圆所需属性（默认均为 0）：

|属性|说明|
|---|---|
|centerX|原点 X 值|
|centerY|原点 Y 值|
|radiusX|水平半径，default = 0|
|radiuxY|垂直半径，default = 0|

Ellipse 构造函数：

```java
Ellipse()
Ellipse(double radiusX, double radiusY)
Ellipse(double centerX, double centerY, double radiusX, double radiusY)
```

**示例：** Ellipse

创建 3 个 Ellipse，第 3 个 radiusX 和 radiusY 相同，所以实际是圆。

```java
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

![|350](Pasted%20image%2020230713194048.png)

## 6. Polygon

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

```java
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

![|350](Pasted%20image%2020230713195248.png)

## 7. Polyline

折线用 Polyline 类表示。折线定义方式和多边形类似，只是终点和起点不连接，没有构成封闭图形。不过 `fill` 属性通过假设该图形是封闭的来填充颜色。

Polyline 也提供了两种构造方式。

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

**示例：** 使用 Polyline 创建三角形、平行四边形和六边形

六边形是完整的，因为在默认添加了第一点的坐标，使其封闭。

```java
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

![|350](Pasted%20image%2020230713195855.png)

## 8. Arc

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

![|400](Pasted%20image%2020230713200256.png)

`type` 属性指定弧线封闭的方式，由 enum `ArcType` 指定：

- `ArcType.OPEN`, 不封闭弧线，默认值。
- `ArcType.CHORD`, 以一条直线连接弧线的起点和终点。
- `ArcType.ROUND`, 将弧线的起始点和原点连接。

下图是三种弧线类型：

![|350](Pasted%20image%2020230713200526.png)

```ad-warning
如果不指定 `stroke`，指定 `ArcType` 无效。
```


`fill` 属性，默认填充 `ArcType.CHORD` 区域。

**示例：** Arc

```java
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

![|350](Pasted%20image%2020230713200758.png)

## 9. QuadCurve

计算机中使用 [Bezier 曲线](https://en.wikipedia.org/wiki/B%C3%A9zier_curve) 绘制平滑曲线。`QuadCurve` 类表示包含一个控制点两个指定点的 Bezier 曲线。这 3 个点由 6 个属性 startX, startY, controlX, controlY, endX, endY 指定。

**示例：** 使用 QuadCurve 创建 2 个贝塞尔曲线

一个具有 stroke；另一个 stroke 为默认值 null，填充浅灰色。

```java
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

![|350](Pasted%20image%2020230713201423.png)

## 10. CubicCurve

CubicCurve 表示有 2 个控制点、2 个指定点的 Bezier 曲线。CubicCurve 通过 8 个属性指定这 4 个点：(startX, startY), (controlX1, controlY1), (controlX2, controlY2), (endX, endY)。

![|300](Pasted%20image%2020230713201902.png)

控制点，可以看做曲线切线的交点。

**示例：** 使用 CubicCurve 创建 2 条三次曲线

一条指定 stroke；另一条没有 stroke，用浅灰色填充。

```java
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

![|200](Pasted%20image%2020230713202118.png)

