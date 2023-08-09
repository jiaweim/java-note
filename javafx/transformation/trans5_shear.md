# Shear

2023-07-28, 14:54
****
## 1. 简介

剪切变换（shear）围绕 pivot 旋转 node 的 local 坐标系，因此坐标轴之间不再垂直。矩形 Node 变换后成为平行四边形。

## 2. Shear 类

Shear 类使用 4 个属性描述剪切变换：

- x
- y
- pivotX
- pivotY

(pivotX,pivotY) 指定 pivot 点，默认为 0, 0。shear 不影响 pivot 坐标。

假设 node 中有一个点 (x1, y1)，shear 变换后坐标为 (x2, y2)，两者关系：

```java
x2 = pivotX + (x1 - pivotX) + x * (y1 - pivotY)
y2 = pivotY + (y1 - pivotY) + y * (x1 - pivotX)
```

上面的 (x1, y1) 和 (x2, y2) 都是转换前 node local 坐标系中的值。

如果 (x1, y1) 作为 pivot，则 x2=pivotX=x1, y2=pivotY=y1，即 pivot 不受 shear 变换影响。

构造函数：

```java
Shear()
Shear(double x, double y)
Shear(double x, double y, double pivotX, double pivotY)
```

```ad-tip
Shear 变换只能通过 transforms 实现，Node 没有提供相关属性。
```

## 3. 示例

x, y 都取 0.5，pivot 采用默认 (0, 0)。

```java
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;

public class ShearTest extends Application {

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

        // Apply a shear on rect2. The x and y multipliers are 0.5 and
        // (0, 0) is the pivot point.
        Shear shear = new Shear(0.5, 0.5);
        rect2.getTransforms().addAll(shear);

        Group root = new Group(rect1, rect2);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying the Shear Transformation");
        stage.show();
    }
}
```

![](Pasted%20image%2020230728144904.png)

采用其它 pivot:

```java
Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
rect1.setX(100);
rect1.setStroke(Color.BLACK);

Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
rect2.setX(100);
rect2.setStroke(Color.BLACK);
rect2.setOpacity(0.5);

// Apply a shear on rect2. The x and y multipliers are 0.5 and
// (100, 50) is the pivot point.
Shear shear = new Shear(0.5, 0.5, 100, 50);
rect2.getTransforms().addAll(shear);
```

![|200](Pasted%20image%2020230728145219.png)

我们来算一下剪切后的右上角坐标：

```java
x1 = 200
y1 = 0
pivotX = 100
pivotY = 50
x = 0.5
y = 0.5

x2 = pivotX + (x1 - pivotX) + x * (y1 - pivotY)
   = 100 + (200 - 100) + 0.5 * (0 - 50)
   = 175
   
y2 = pivotY + (y1 - pivotY) + y * (x1 - pivotX)
   = 50 + (0 -50) + 0.5 * (200 - 100)
   = 50
```

