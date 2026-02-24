# Rotation

2023-07-28, 14:02
****
## 1. 简介

在旋转变换中，坐标轴以 pivot 为中心进行旋转，坐标系中的点映射到新的坐标系。绕 Z 轴旋转30°的示意图：

<img src="images/Pasted%20image%2020230726172217.png" alt="|300" width="300" />

## 2. Rotate 类

`Rotate` 类通过如下五个属性控制旋转：

- angle，旋转的角度，默认为 0.0，正值表示顺时针旋转
- axis，旋转的轴，可取值 `Rotate.Z_AXIS`, `Rotate.X_AXIS`, `Rotate.Y_AXIS`，默认为 `Z_AXIS`
- pivotX, pivotY, pivotZ

`pivotX`, `pivotY`, `pivotZ` 定义了旋转的点，默认值均为 0.0.

Rotate 构造函数：

```java
Rotate()
Rotate(double angle)
Rotate(double angle, double pivotX, double pivotY)
Rotate(double angle, double pivotX, double pivotY, double pivotZ)
Rotate(double angle, double pivotX, double pivotY, double pivotZ, Point3D axis)
Rotate(double angle, Point3D axis)
```

## 3. rotate transform 示例

创建 2 个 Rectangles

- 2 个 Rectangles 放在同一个位置
- 第二个 Rectangle 透明度设为 0.5
- 第二个 Rectangle 顺时针旋转 30°

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class RotateTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect1.setStroke(Color.BLACK);

        Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect2.setStroke(Color.BLACK);
        rect2.setOpacity(0.5);

        // Apply a rotation on rect2. The rotation angle is 30 degree clockwise
        // (0, 0) is the pivot point
        Rotate rotate = new Rotate(30, 0, 0);
        rect2.getTransforms().addAll(rotate);

        Pane root = new Pane(rect1, rect2);
        root.setPrefSize(300, 80);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying the Rotation Transformation");
        stage.show();
    }
}
```

![|300](Pasted%20image%2020230726173122.png)

pivot 在原点时旋转的效果很明显。

**示例：** 将 pivot 设为 (100, 0)

```java
Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
rect1.setY(20);
rect1.setStroke(Color.BLACK);
Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
rect2.setY(20);
rect2.setStroke(Color.BLACK);
rect2.setOpacity(0.5);

// Apply a rotation on rect2. The rotation angle is 30 degree anticlockwise
// (100, 0) is the pivot point.
Rotate rotate = new Rotate(-30, 100, 0);
rect2.getTransforms().addAll(rotate);
```

![|500](Pasted%20image%2020230726173548.png)
Rectangle 左上角坐标为 (0, 20)。将 (100, 0) 作为 pivot 旋转第二个矩形。

## 4. rotate 属性示例

也可以使用 `Node` 的 `rotate` 和 `rotationAxis` 属性进行旋转操作，两者分别指定旋转的角度和坐标轴，原始 `layoutBounds` 中心作为 pivot。

```ad-note
transforms 中默认 pivot 是 Node 的 local 坐标系原点，而 Node 的 rotate 属性使用转换前 Node 的 layoutBounds 中心作为 pivot。
```

使用 Node 的 rotate 属性将 Rectangle 旋转 30°。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RotatePropertyTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect1.setStroke(Color.BLACK);

        Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect2.setStroke(Color.BLACK);
        rect2.setOpacity(0.5);

        // Use the rotate proeprty of the node class
        rect2.setRotate(30);

        Pane root = new Pane(rect1, rect2);
        root.setPrefSize(300, 80);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying the Rotation Transformation");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230728140220.png)

