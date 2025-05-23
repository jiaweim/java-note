# 特效

2023-08-11, 09:59
@author Jiawei Mao
****
## 1. 简介

**特效**（effect）是一种过滤器，它接受一个或多个输入图形，对输入应用算法，然后输出。在 Node 上应用特效一般是为了创建更好看的界面。包括阴影、模糊、扭曲、辉光、反射等特效类型。

JavaFX 为条件特性，对平台不支持的特效，会自动忽略。Effect 抽象类为所有特效实现的基类，相关实现如下所示：

@import "images/2023-08-11-09-34-05.png" {width="350px" title="Effect"}

`Node` 类包含一个 `effect` 属性，用于指定应用于 node 的特效。默认为 `null`。

**示例：** 为 Text 应用阴影特效

```java
Text t1 = new Text("Drop Shadow");
t1.setFont(Font.font(24));
t1.setEffect(new DropShadow());
```

## 2. 示例

创建 `Text` nodes，分别应用特效。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EffectTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text t1 = new Text("Drop Shadow!");
        t1.setFont(Font.font(24));
        t1.setEffect(new DropShadow());

        Text t2 = new Text("Blur!");
        t2.setFont(Font.font(24));
        t2.setEffect(new BoxBlur());

        Text t3 = new Text("Glow!");
        t3.setFont(Font.font(24));
        t3.setEffect(new Glow());

        Text t4 = new Text("Bloom!");
        t4.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        t4.setFill(Color.WHITE);
        t4.setEffect(new Bloom(0.10));

        // Stack the Text node with bloom effect over a Reactangle
        Rectangle rect = new Rectangle(100, 30, Color.GREEN);
        StackPane spane = new StackPane(rect, t4);

        HBox root = new HBox(t1, t2, t3, spane);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying Effects");
        stage.show();
    }
}
```

<img src="images/2023-08-11-09-52-10.png" style="zoom:67%;" />

> [!TIP]
>
> 应用于 `Group` 的特效将应用于它所有的 children。也可以将多个特效串联起来，一个特效的输出作为下一个特效的输入。`Node` 的 `layoutBounds` 不受特效影响，但是 localBounds 和 boundsInParent 受影响。
