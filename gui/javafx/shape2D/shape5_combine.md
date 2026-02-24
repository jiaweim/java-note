# 合并 Shapes

2023-07-14, 09:44
****
Shape 类提供了 3 个静态方法用于执行形状的并集、交集和差集：

```java
union(Shape shape1, Shape shape2)
intersect(Shape shape1, Shape shape2)
subtract(Shape shape1, Shape shape2)
```

这些方法返回一个新的 Shape。它们对 Shape 的区域进行操作，如果一个形状没有 stroke 和 fill，则面积为零：

- union() 合并两个形状的面积
- intersect() 取两个形状的交集
- subtract() 取两个形状的差集

**示例：** 使用 2 个 Circle 演示上面的三种操作

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class CombiningShapesTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle c1 = new Circle(0, 0, 20);
        Circle c2 = new Circle(15, 0, 20);

        Shape union = Shape.union(c1, c2);
        union.setStroke(Color.BLACK);
        union.setFill(Color.LIGHTGRAY);

        Shape intersection = Shape.intersect(c1, c2);
        intersection.setStroke(Color.BLACK);
        intersection.setFill(Color.LIGHTGRAY);

        Shape subtraction = Shape.subtract(c1, c2);
        subtraction.setStroke(Color.BLACK);
        subtraction.setFill(Color.LIGHTGRAY);

        HBox root = new HBox(union, intersection, subtraction);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Combining Shapes");
        stage.show();
    }
}
```

![|250](Pasted%20image%2020230714094419.png)

