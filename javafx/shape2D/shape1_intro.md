# Shape 概述

2023-08-10, 14:40
modify: 样式
2023-07-13, 19:07
@author Jiawei Mao
****
## 1. 简介

JavaFX 所有的 2D 形状类都放在 `javafx.scene.shape` 包中。2D 形状均扩展自抽象类 `Shape`，如下图所示：

@import "images/Pasted%20image%2020230713184906.png" {width="px" title=""}

`Shape` 类提供了几何形状对象的常用属性，定义边界线条和内部区域，需要如下属性：

| 属性             | 类型           | 说明              | 
| ---------------- | -------------- | ---------------- |
| fill             | Paint          | 填充颜色，默认 `Color.BLACK`           | 
| smooth           | Boolean        | 反锯齿平滑效果，默认 `true`            |                
| strokeDashOffset | Double         | 虚线从虚线数组中开始的位置，默认 0    |
| strokeLineCap    | StrokeLineCap  | 线条末端形状，包括 `StrokeLineCap.BUTT, ROUND, SQUARE`，default=SQUARE     |
| strokeLineJoin   | StrokeLineJoin | 线条连接的方式，取值有 `StrkeLineJoin.BEVEL, MITER, ROUND`             |
| strokeMiterLimit | Double         | 对 `MITER` 线段连接方式，两条线段通过延伸外边框进行连接，线条角度很小时，延伸线可能很长，该属性用于指定 miter 长度和线条宽度的比值。default=10.0   |
| stroke           | Paint          | 线条边框颜色，默认 `null`。`Line`, `Polyline`, `Path` 例外，它们默认 `Color.BLACK`               |
| strokeType       | StrokeType     | stroke 沿着形状的边框绘制，strokeType 属性用于指定stroke相对边框的位置。有三种取值：`CENTERED`, `INSIDE`, `OUTSIDE`，默认为 `CENTERED`，即线条一半在边框内，一半在边框外.defaut=CENTERED |
| strokeWidth      | Double         | 线条宽度，默认 1.0px                                        |                 |

`Shape` 尺寸不受父容器 layout 策略影响，只能修改其尺寸相关的属性。

## 2. 示例

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

@import "images/2023-08-10-12-37-34.png" {width="300px" title=""}

