# Stroke

2023-08-10, 14:31
modify: 样式
2023-06-05
@author Jiawei Mao
****
## 1. 简介

Stroking 指绘制形状轮廓（描边）。`Shape` 类包含多个属性用于定义 stroke：

- stroke
- strokeWidth
- strokeType
- strokeLineCap
- stroleLineJoin
- strokeMiterLimit
- strokeDashOffset

## 2. stroke

`stroke` 属性指定 stroke 颜色。除了 `Line`, `Path` 和 `Polyline` 的默认颜色为 `Color.BLACK`，其它形状默认为 `null`。

## 3. strokeWidth

`strokeWidth` 属性指定线条宽度，默认 1.0px。

## 4. strokeType

stroke 沿着形状的边框绘制，`strokeType` 定义 `strokeWidth` 和边框的相对位置，由 enum `StrokeType` 定义，包括 `CENTERED`, `INSIDE` 和 `OUTSIDE` 三种类型，默认 `CENTERED`：

| StrokeType | 说明                                       |
| ---------- | ------------------------------------------ |
| `CENTERED` | `strokeWidth` 的一半在边框里，一半在边框外 |
| `INSIDE`   | `strokeWidth` 全在边框里                   |
| `OUTSIDE`  | `strokeWidth` 全在边框外                   |

`strokeWidth` 包含在 `layoutBounds` 中。

**示例：** 演示不同 strokeType

绘制 4 个正方形（50px）：

- 第一个没有 stroke，`layoutBounds` 为 $50\times 50$；
- 第二个 `strokeWidth` 为 4px，`INSIDE` 类型，`layoutBounds` 为 $50\times 50$；
- 第三个 `strokeWidth` 为 4px，`CENTERED` 类型，因此有 2px 在边框内，2px 在边框外，所以 `layoutBounds` 为 $54\times 54$；
- 第四个 `strokeWidth` 为 4px，类型为 `OUTSIDE`，即整个 stroke 都在边框外，所以 `layoutBounds` 为 $58\times 58$；

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class StrokeTypeTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle r1 = new Rectangle(50, 50);
        r1.setFill(Color.LIGHTGRAY);

        Rectangle r2 = new Rectangle(50, 50);
        r2.setFill(Color.LIGHTGRAY);
        r2.setStroke(Color.BLACK);
        r2.setStrokeWidth(4);
        r2.setStrokeType(StrokeType.INSIDE);

        Rectangle r3 = new Rectangle(50, 50);
        r3.setFill(Color.LIGHTGRAY);
        r3.setStroke(Color.BLACK);
        r3.setStrokeWidth(4);

        Rectangle r4 = new Rectangle(50, 50);
        r4.setFill(Color.LIGHTGRAY);
        r4.setStroke(Color.BLACK);
        r4.setStrokeWidth(4);
        r4.setStrokeType(StrokeType.OUTSIDE);

        HBox root = new HBox(r1, r2, r3, r4);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Different Stroke Types for Shapes");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230714095315.png" {width="250px" title=""}

## 5. strokeLineCap

`strokeLineCap` 定义未闭合路径或虚线片段末端的样式，可选样式为 `StrokeLineCap` enum 类型: 

| StrokeLineCap | 说明                                          |
| ------------- | --------------------------------------------- |
| `BUTT`        | 不添加装饰，默认                               |
| `ROUND`       | 在线段末端添加圆弧，圆弧半径为 strokeWidth 的一半|
| `SQUARE`      | 线段向外延伸，延伸长度为 `strokeWidth` 的一半   |

下图三条线都是未闭合 subpath，长度均为 100px，宽度为 10px：

- `BUTT` 类型的线段 layoutBounds 依旧为 100px
- `ROUND` 和 `SQUARE` 类型线段的 layoutBounds 的宽度增加到 110px

@import "images/Pasted%20image%2020230714100159.png" {width="200px" title=""}

`strokeLineCap` 属性用于未闭合的 subpaths。下图使使用未闭合 subpath 创建的 3 个三角形：

- 使用不同的 `strokeLineCap` 类型
- `SVGPath` 数据均为 "M50, 0L0, 50 M0, 50 L100, 50 M100, 50 L50, 0"
- fill 为 null
- strokeWidth 为 10px

@import "images/Pasted%20image%2020230714100633.png" {width="400px" title=""}

## 6. strokeLineJoin

strokeLineJoin 属性指定如何连接某个 path 中两个连续 PathElement。取值为 enum `StrokeLineJoin`: `MITER`, `BEVEL`, `ROUND`，默认 MITER。

- BEVEL 使用实现连接 pathElements 的外角
- MITER 延伸两个 pathElements 的外边缘，直到相交
- ROUND 使用 strokeWidth 的一半为半径生成圆角连接

下图显示 3 个三角形，SVG path 数据 "M50, 0L0, 50 L100, 50 Z"。fill 为 null，strokeWidth 为 10px：

@import "images/Pasted%20image%2020230714101339.png" {width="400px" title=""}

## 7. strokeMiterLimit

MITER 延伸两个 path elements 的外边缘直到相交。如果两个 path elements 的骄傲都很小，则相交连接的长度可能会很长。`strokeMiterLimit` 属性用于限制连接的长度。

strokeMiterLimit 指定 miter 长度和 strokeWidth 的壁纸。miter 长度是两个 path elements 连接的最内点和最外点之间的距离。如果在 strokeMiterLimit 范围内两个 path elements 无法相交，则改用 BEVEL。

strokeMiterLimit 默认为 10，即 miter length 最多到 strokeWidth 的 10 倍。

@import "images/2019-06-05-16-14-57.png" {width="300px" title=""}

当两条线段的夹角很小时，`MITER` 连接方式会使延伸距离很长，A 是完整的延伸长度，以该延伸长度和 stroke width 的比值为单位（4.65）。如果延伸长度大于 `strokeMiterLimit`，则直接截断。

**示例：** 创建 2 个三角形

两个都使用 MITER：

- 第 1 个三角形 strokeMiterLimit 为 2.0
- 第 2 个三角形使用 strokeMiterLimit 默认值，即 10.0

因为 strokeWidth 为 10px，所以第 1 个三角形在尝试连接两条 pathElements 的外边缘时最多延伸 20px，因为在 20px 能无法连上，所以改用 BEVEL。

```java{.line-numbers}
SVGPath t1 = new SVGPath();
t1.setContent("M50, 0L0, 50 L100, 50 Z");
t1.setStrokeWidth(10);
t1.setFill(null);
t1.setStroke(Color.BLACK);
t1.setStrokeMiterLimit(2.0);

SVGPath t2 = new SVGPath();
t2.setContent("M50, 0L0, 50 L100, 50 Z");
t2.setStrokeWidth(10);
t2.setFill(null);
t2.setStroke(Color.BLACK);
```

@import "images/Pasted%20image%2020230714102331.png" {width="350px" title=""}

## 8. 虚线

stroke 默认采用实线。定义虚线，需要指定 dashPattern 和 dashOffset。

`dashPattern` 是类型为 `ObservableList<Double>` 的 double 数组。`Shape.getStrokeDashArray()` 返回该数组。dashPattern 数组以下面两个两个元素交替指定 dash 和 gap 长度：

1. dash-length
2. gap

`Shape.strokeDashOffset()` 返回 dashOffset 值，指定从 dashPattern 什么地方开始画虚线。

**示例：** dash

下面定义两个 `Polygon`，使用相同的 dash pattern，不同的 dash offset：

- 第一个 offset 为 0，为默认值。第一个矩形的第一段 dash 长度为 15px，为 dashPattern 的第一个元素
- 第二个 offset 为 20.0，即从 dashPattern 的 20px 后才开始画虚线。前面两个元素 15px dash 和 5px gap 刚好 20px，所以从第 3 个元素开始

```java{.line-numbers}
Polygon p1 = new Polygon(0, 0, 100, 0, 100, 50, 0, 50, 0, 0);
p1.setFill(null);
p1.setStroke(Color.BLACK);
p1.getStrokeDashArray().addAll(15.0, 5.0, 5.0, 5.0);

Polygon p2 = new Polygon(0, 0, 100, 0, 100, 50, 0, 50, 0, 0);
p2.setFill(null);
p2.setStroke(Color.BLACK);
p2.getStrokeDashArray().addAll(15.0, 5.0, 5.0, 5.0);
p2.setStrokeDashOffset(20.0);
```

@import "images/Pasted%20image%2020230605150248.png" {width="px" title=""}
