# 阴影特效

2023-08-11, 11:53
@author Jiawei Mao
****
## 1. 简介

阴影特效（shadowing effect）在输入对象上绘制阴影。JavaFX 支持三种类型的阴影特效：

- DropShadow
- InnerShadow
- Shadow

## 2. DropShadow

`DropShadow` 在 input 后面绘制一个阴影（一个模糊的图像），使输入看起来被抬高了，给输入 3D 视觉效果。input 可以是 node 或特效序列中的一个特效。

DropShadow 通过如下属性控制特效的尺寸、位置、颜色和质量：

- offsetX
- offsetY
- color
- blurType
- radius
- spread
- width
- height
- input

`DropShadow` 构造函数：

```java
DropShadow()
DropShadow(BlurType blurType, Color color, double radius, double spread,
            double offsetX, double offsetY)
DropShadow(double radius, Color color)
DropShadow(double radius, double offsetX, double offsetY, Color color)
```

offsetX 和 offsetY 属性指定阴影与 input 的相对位置，默认均为 0。正数表示向对应轴的正方形移动。

**示例：** offsetX 和 offsetY 为 10px 的 DropShadow。

```java
DropShadow dsEffect = new DropShadow();
dsEffect.setOffsetX(10);
dsEffect.setOffsetY(10);

Rectangle rect = new Rectangle(50, 25, Color.LIGHTGRAY);
rect.setEffect(dsEffect);
```

`offsetX` 和 `offsetY` 属性在 `DropShadow` 上的效果：

@import "images/2023-08-11-10-31-50.png" {width="600px" title=""}

`color` 属性指定 shadow 的颜色。默认为 `Color.BLACK`。例如，设置为红色：

```java
DropShadow dsEffect = new DropShadow();
dsEffect.setColor(Color.RED);
```

阴影模糊可以用不同算法实现。`blurType` 属性指定阴影的模糊算法类型。为 BlurType enum：

- ONE_PASS_BOX：使用 box-filter 遍历一次实现 shadow 模糊
- TWO_PASS_BOX：使用 box-filter 遍历两次实现 shadow 模糊
- THREE_PASS_BOX：使用 box-filter 遍历三次实现 shadow 模糊
- GAUSSIAN：使用高斯模糊函数实现 shadow 模糊

ONE_PASS_BOX 的效果最差，GAUSSIAN 质量最好。默认为 THREE_PASS_BOX，质量与 GAUSSIAN 接近，但速度更快。

**示例：** 将 blurType 设置为 GAUSSIAN

```java
DropShadow dsEffect = new DropShadow();
dsEffect.setBlurType(BlurType.GAUSSIAN);
```

radius 属性指定源 pixel 四周扩散的距离，如果 radius 为 0，shadow 边缘清晰。取值范围 0-127，默认 10。shadow 区域外的模糊效果通过混合阴影颜色和背景颜色实现。模糊颜色从边缘到 radius 范围淡出。

**示例：** 不同 radius 的 DropShadow

- 左侧 radius 为 0.0，因此边缘清晰
- 右侧 radius 为 10.0，在 shadow 边缘 10px 实现淡出效果

![](images/2023-08-11-10-57-22.png)

左侧特效的代码实现：

```java
DropShadow dsEffect = new DropShadow();
dsEffect.setOffsetX(10);
dsEffect.setOffsetY(10);
dsEffect.setRadius(0);

Rectangle rect = new Rectangle(50, 25, Color.LIGHTGRAY);
rect.setEffect(dsEffect);
```

spread 属性指定 radius 保持与 shadow 颜色相同的比例。radius 余下部分再实现淡出效果。spread 在 0.0 到 1.0 之间，默认为 0.0。

- 假设 DropShadow 的 radius 为 10.0，spread 为 0.60，shadow 颜色为 black。则源 pixel 前 6px 保持为黑色，后 4px 实现淡出效果
- 如果 spread 为 1.0，则阴影没有淡出效果，也就没有模糊视觉

**示例：** 下图三个矩形添加了 DropShadow，radius 为 10，spread 值不同

- spread=0.0 在整个 radius 实现模糊效果
- spread=0.5 前一半为 shadow 颜色，后一半实现模糊效果
- spread=1.0 整个 radius 设置为 shadow 颜色，没有模糊效果

@import "images/2023-08-11-11-07-03.png" {width="350px" title=""}

下面的代码实现上图 spread=0.5：

```java
DropShadow dsEfefct = new DropShadow();
dsEfefct.setOffsetX(10);
dsEfefct.setOffsetY(10);
dsEfefct.setRadius(10);
dsEfefct.setSpread(.50);

Rectangle rect = new Rectangle(50, 25, Color.LIGHTGRAY);
rect.setEffect(dsEfefct);
```

width 和 height 属性指定从源 pixel 到 shadow 颜色扩散的水平和垂直距离：

- width 和 height 的值在 0 到 255 之间
- 设置它们的值等价于设置 `radius` 属性，等于 `(2 * radius + 1)`，两者默认为 21.0
- 修改 radius 时，如果 width 和 height 属性没有绑定，它们会随之调整
- 修改 width 和 height 会改变 radius 的值，width 和 height 的均值等于 (2 * radius + 1)

**示例：** 下图 4 个矩形添加了 DropShadow 特效

@import "images/2023-08-11-11-15-36.png" {width="350px" title=""}

实现第 4 个矩形的代码：

```java
DropShadow dsEffect = new DropShadow();
dsEffect.setOffsetX(10);
dsEffect.setOffsetY(10);
dsEffect.setWidth(20);
dsEffect.setHeight(20);

Rectangle rect = new Rectangle(50, 25, Color.LIGHTGRAY);
rect.setEffect(dsEffect);
```

**示例：** DropShadow

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DropShadowTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(100, 50, Color.GRAY);
        DropShadow dsEffect = new DropShadow();
        rect.setEffect(dsEffect);

        GridPane controllsrPane = this.getControllerPane(dsEffect);
        BorderPane root = new BorderPane();
        root.setCenter(rect);
        root.setBottom(controllsrPane);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Experimenting with DropShadow Effect");
        stage.show();
    }

    private GridPane getControllerPane(final DropShadow dsEffect) {
        Slider offsetXSlider = new Slider(-200, 200, 0);
        dsEffect.offsetXProperty().bind(offsetXSlider.valueProperty());

        Slider offsetYSlider = new Slider(-200, 200, 0);
        dsEffect.offsetYProperty().bind(offsetYSlider.valueProperty());

        Slider radiusSlider = new Slider(0, 127, 10);
        dsEffect.radiusProperty().bind(radiusSlider.valueProperty());

        Slider spreadSlider = new Slider(0.0, 1.0, 0);
        dsEffect.spreadProperty().bind(spreadSlider.valueProperty());

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        dsEffect.colorProperty().bind(colorPicker.valueProperty());

        ComboBox<BlurType> blurTypeList = new ComboBox<>();
        blurTypeList.setValue(dsEffect.getBlurType());
        blurTypeList.getItems().addAll(BlurType.ONE_PASS_BOX,
                BlurType.TWO_PASS_BOX,
                BlurType.THREE_PASS_BOX,
                BlurType.GAUSSIAN);
        dsEffect.blurTypeProperty().bind(blurTypeList.valueProperty());

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(10);
        pane.addRow(0, new Label("OffsetX:"), offsetXSlider);
        pane.addRow(1, new Label("OffsetY:"), offsetYSlider);
        pane.addRow(2, new Label("Radius:"), radiusSlider,
                new Label("Spread:"), spreadSlider);
        pane.addRow(3, new Label("Color:"), colorPicker,
                new Label("Blur Type:"), blurTypeList);

        return pane;
    }
}
```

@import "images/2023-08-11-11-19-01.png" {width="450px" title=""}

## 3. InnerShadow

`InnerShadow` 的效果与 `DropShadow` 类似，它在输入的边缘绘制阴影，获得 3D 视觉效果。

输入可以是 node 或特效序列的上一个特效。

InnerShadow 的属性：

- offsetX
- offsetY
- color
- blurType
- radius
- choke
- width
- height
- input

`InnerShadow` 属性个数与 `DropShadow` 相同。只是 `DropShadow` 的 `spread` 属性替换为 `InnerShadow` 中的 `choke` 属性。`choke` 属性效果与 `spread` 类似。

构造函数：

```java
InnerShadow()
InnerShadow(BlurType blurType, Color color, double radius, double choke,
            double offsetX, double offsetY)
InnerShadow(double radius, Color color)
InnerShadow(double radius, double offsetX, double offsetY, Color color)
```

**示例：** 创建 1 个 Text 和 2 个 Rectangle

对 3 个 Node 应用 InnerShadow。

@import "images/2023-08-11-11-32-22.png" {width="500px" title=""}

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InnerShadowTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        InnerShadow is1 = new InnerShadow();
        is1.setOffsetX(3);
        is1.setOffsetY(6);

        Text t1 = new Text("Inner Shadow");
        t1.setEffect(is1);
        t1.setFill(Color.RED);
        t1.setFont(Font.font(null, FontWeight.BOLD, 36));

        InnerShadow is2 = new InnerShadow();
        is2.setOffsetX(3);
        is2.setOffsetY(3);
        is2.setColor(Color.GRAY);
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect1.setEffect(is2);

        InnerShadow is3 = new InnerShadow();
        is3.setOffsetX(-3);
        is3.setOffsetY(-3);
        is3.setColor(Color.GRAY);
        Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect2.setEffect(is3);

        HBox root = new HBox(wrap(t1, is1), wrap(rect1, is2), wrap(rect2, is3));
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying InnerShadow Effect");
        stage.show();
    }

    private VBox wrap(Shape s, InnerShadow in) {
        Text t = new Text("offsetX=" + in.getOffsetX() + "\n" +
                "offsetY=" + in.getOffsetY());
        t.setFont(Font.font(10));

        VBox box = new VBox(10, s, t);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
```

## 4. Shadow

Shadow 特效与 DropShadow 和 InnerShadow 不同，它直接修改 input，将其转换为 shadow。通常将 Shadow 与原输入结合，可以创建更丰富的阴影效果：

- 给 Node 应用浅色 Shadow 特效，然后将其叠加到原 Node 的副本，从而获得辉光效果
- 创建深色 Shadow 特效，并将它放在原始 Node 的后面，获得 DropShadow 效果

Shadow 的属性：

- color
- blurType
- radius
- width
- height
- input

这些属性的含义与 DropShadow 中同名属性相同。

Shadow 构造函数：

```java
Shadow()
Shadow(BlurType blurType, Color color, double radius)
Shadow(double radius, Color color)
```

**示例：** Shadow

创建 3 个 Text，分别应用 shadow：

- 第一个为 shadow
- 第二个在原始 node 上面叠加 shadow
- 第三个在原始 node 下面叠加 shadow

@import "images/2023-08-11-11-50-24.png" {width="500px" title=""}

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShadowTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a Shadow of a Text node
        Text t1 = new Text("Shadow");
        t1.setFont(Font.font(36));
        t1.setEffect(new Shadow());

        // Create a Glow effect using a Shadow
        Text t2Original = new Text("Glow");
        t2Original.setFont(Font.font(36));
        Text t2 = new Text("Glow");
        t2.setFont(Font.font(36));
        Shadow s2 = new Shadow();
        s2.setColor(Color.YELLOW);
        t2.setEffect(s2);
        StackPane glow = new StackPane(t2Original, t2);

        // Create a DropShadow effect using a Shadow 
        Text t3Original = new Text("DropShadow");
        t3Original.setFont(Font.font(36));
        Text t3 = new Text("DropShadow");
        t3.setFont(Font.font(36));
        Shadow s3 = new Shadow();
        t3.setEffect(s3);
        StackPane dropShadow = new StackPane(t3, t3Original);

        HBox root = new HBox(t1, glow, dropShadow);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Shadow Effect");
        stage.show();
    }
}
```