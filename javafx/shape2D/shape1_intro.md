# Shape 概述

2023-07-13, 19:07
****
## 1. 简介

JavaFX 所有的 2D 形状类都放在 `javafx.scene.shape` 包中。2D 形状均扩展自抽象类 `Shape`，如下图所示：

![](Pasted%20image%2020230713184906.png)

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

Shape 的尺寸不受父容器 layout 策略的影响，只能修改其尺寸相关的属性。

## 2. 示例

创建 2 个 Circle：

- 第一个填充浅灰色，没有 stroke；
- 第二个填充换色，2px black stroke

```java
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

![|200](Pasted%20image%2020230713190640.png)

