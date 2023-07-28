# Translation

2023-07-28, 13:50
****
## 1. 简介

平移（translation）将 node 的所有点在其 parent 坐标系沿指定方向移动指定距离。通过移动 node 的 local 坐标系实现。

下图是平移示意图：

- 平移前为实线
- 平移后为虚线
- local 坐标系中的点 P 在平移前后坐标不变
- local 坐标系原点在 parent 坐标系发生变化，从 (0, 0) 变为 (3, 2)

![|400](Pasted%20image%2020230726164738.png)

## 2. Translation 类

平移由 `Translate` 类实现，该类包含三个属性：`x`, `y`, `z`，表示平移后的 local 坐标系的原点坐标，默认为 0。

构造函数：

```java
Translate()
Translate(double x, double y)
Translate(double x, double y, double z)
```

对 `Group` 的变换应用于 `Group` 包含的所有 nodes。

`Node` 的 `layoutX`, `layoutY` 属性直接设置 Node 位置，不改变 local 坐标系。`translateX`, `translateY` 则对 Node 的 local 坐标系添加 transform。

- 一般来说 `layoutX` 和 `layoutY` 用于将 node 放在 `Scene` 指定地方
- translateX 和 translateY 则是为了实现动画效果

如果同时使用这两种方式，node 的 local 坐标系被平移，然后根据 `layoutX` 和 `layoutY` 将 node 放在一个新的坐标系。

## 3. 示例

创建 3 个 Rectangles:

- 默认都在 (0, 0) 位置：因为使用的 Pane
- 平移第二个和第二个矩形

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class TranslateTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect1 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect1.setStroke(Color.BLACK);

        Rectangle rect2 = new Rectangle(100, 50, Color.YELLOW);
        rect2.setStroke(Color.BLACK);

        Rectangle rect3 = new Rectangle(100, 50, Color.STEELBLUE);
        rect3.setStroke(Color.BLACK);

        // Apply a translation on rect2 using the transforms sequence
        Translate translate1 = new Translate(50, 10);
        rect2.getTransforms().addAll(translate1);

        // Apply a translation on rect3 using the translateX
        // and translateY proeprties
        rect3.setTranslateX(180);
        rect3.setTranslateY(20);

        Pane root = new Pane(rect1, rect2, rect3);
        root.setPrefSize(300, 80);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying the Translation Transformation");
        stage.show();
    }
}
```

![|300](Pasted%20image%2020230726172005.png)
