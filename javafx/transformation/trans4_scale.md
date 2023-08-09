# Scale

2023-07-28, 14:22
****
## 1. 简介

**缩放变换**对坐标系的坐标轴按指定比例进行缩放。这会导致 `Node` 在指定坐标轴方向拉伸或收缩。

## 2. Scale 类

`Scale` 类用 6 个属性描述缩放变换：

- x
- y
- z
- pivotX
- pivotY
- pivotZ

x, y, z 属性分别指定 x, y, z 轴的缩放因子，默认 1.0.

`pivotX`, `pivotY`, `pivotZ` 指定 pivot 坐标，默认为 0.

`Scale` 构造函数：

```java
Scale()
Scale(double x, double y)
Scale(double x, double y, double z)
Scale(double x, double y, double pivotX, double pivotY)
Scale(double x, double y, double z, double pivotX, double pivotY, double pivotZ)
```

使用 `Scale` 类或 `Node` 的 `scaleX`, `scaleY`, `scaleZ` 属性都可以实现缩放：

- `scaleX`, `scaleY`, `scaleZ` 属性定义 `Node` 沿 `layoutBounds` 中心的缩放因子。用于手动或动画来拉伸或收缩 `Node`。`layoutBounds` 默认不包含这些缩放因子，因为适合在应用所有 effects 和 transforms 后缩放整个 node。
- `scaleX`, `scaleY`, `scaleZ` 的 pivot 点为转换前 `Node` 的 `layoutBounds` 中心

## 3. scale 属性缩放

相关方法：

```java
public final DoubleProperty scaleXProperty()
public final double getScaleX()
public final void setScaleX(double value)

public final DoubleProperty scaleYProperty()
public final double getScaleY()
public final void setScaleY(double value)

public final DoubleProperty scaleZProperty()
public final double getScaleZ()
public final void setScaleZ(double value)
```

创建 2 个 Rectangles:

- 2 个 Rectangles 放在相同位置
- 1 个缩放，一个保持不变

将缩放前 `Rectangle` 的 `opacity` 设置为 0.5.

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ScaleTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect1.setStroke(Color.BLACK);
        rect1.setOpacity(0.5);

        Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect2.setStroke(Color.BLACK);

        // Apply a scale on rect2. Center of the Rectangle is the pivot point.
        rect2.setScaleX(0.5);
        rect2.setScaleY(0.5);

        Pane root = new Pane(rect1, rect2);
        root.setPrefSize(150, 60);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying the Scale Transformation");
        stage.show();
    }
}
```

![|200](Pasted%20image%2020230728141809.png)

## 4. Scale 类缩放

如果 pivot 不是 node 中心，缩放变化可能移动 node。

创建 2 个 Rectangles：

- 放在相同位置
- 一个缩放，一个不变

不缩放的  opacity 设置为 0.5.

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ScalePivotPointTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect1.setX(150);
        rect1.setStroke(Color.BLACK);
        rect1.setOpacity(0.5);

        Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect2.setX(150);
        rect2.setStroke(Color.BLACK);

        // Apply a scale on rect2. The origin of the local coordinate system
        // of rect4 is the pivot point
        Scale scale = new Scale(0.5, 0.5);
        rect2.getTransforms().addAll(scale);

        Pane root = new Pane(rect1, rect2);
        root.setPrefSize(300, 60);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying the Scale Transformation");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230728142155.png)

