# AnchorPane

2023-07-11, 12:42
****
## 1. 简介

AnchorPane 将 children 的 4 个边以指定距离锚定到自身的 4 个边。如下所示：

![|450](Pasted%20image%2020230711105836.png)

AnchorPane 应用场景：

- 沿 AnchorPane 一条边或多条边对齐 children
- resizing AnchorPane 时拉伸 children

children 的 edges 与 Anchor edges 的距离称为 *anchor* 约束。例如，children 的 top edges 与 AnchorPane 的 top edge 的距离称为 `topAnchor`。最多可以为 child 指定 4 个 anchor 约束值：`topAnchor`, `rightAnchor`, `bottomAnchor`, `leftAnchor`。

如果将 child 固定到相对的两个边（top/bottom 或 left/right），则在 AnchorPane resizing 时，children 随之 resizing。

```ad-note
 anchor 距离是从 AnchorPane content area 的边到 children 的边。如果 AnchorPane 设置了 border 和 padding，需要考虑进去。
```

## 2. 创建 AnchorPane

使用无参构造函数创建 AnchorPane:

```java
AnchorPane apane1 = new AnchorPane();
```

创建时指定 children:

```java
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
AnchorPane apane2 = new AnchorPane(okBtn, cancelBtn);
```

创建后添加 children:

```java
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
AnchorPane apane3 = new AnchorPane();
apane3.getChildren().addAll(okBtn, cancelBtn);
```

使用 `AnchorPane` 有两个注意事项：

- `AnchorPane` 中 children 默认在 (0, 0)。通过指定 children 的 anchor 约束调整位置
- `AnchorPane` 的 prefSize 根据 children prefSizes 和 anchor 约束计算而来。使用所有 children 的 prefWidth+leftAnchor+rightAnchor 的最大值作为 prefWidth

**示例：** `AnchorPane`

添加 2 个 `Button` 到 `AnchorPane`，一个 `Button` 文本长，一个 `Button` 文本短。先添加长 `Button`。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AnchorPaneDefaults extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button bigBtn = new Button("This is a big button.");
        Button smallBtn = new Button("Small button");

        // Create an AnchorPane with two buttons
        AnchorPane root = new AnchorPane(bigBtn, smallBtn);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Defaults in AnchorPane");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230711113713.png)

## 3. 设置 Children 约束

下表是 `AnchorPane` 的 children 的约束项。再次强调，anchor 值从 `AnchorPane` 的 content area 开始计算，而不是 `layoutBounds`。

| 约束           | 类型     | 说明          |
| -------------- | -------- | ------------- |
| `topAnchor`    | `Double` | top anchor    |
| `rightAnchor`  | `Double` | right anchor  |
| `bottomAnchor` | `Double` | bottom anchor |
| `leftAnchor`   | `Double` | left anchor   |

`AnchorPane` 有 4 个 `static` 方法设置这 4 类 anchor。将 anchor 设置为 `null` 表示删除约束。

```java
// Create a Button and anchor it to top and left edges at 10px from each
Button topLeft = new Button("Top Left");

AnchorPane.setTopAnchor(topLeft, 10.0); // 10px from the top edge
AnchorPane.setLeftAnchor(topLeft, 10.0); // 10px from the left edge

AnchorPane root = new AnchorPane(topLeft);
```

使用 static `clearConstraints(Node child)` 清除 4 个约束。

`setXxxAnchor(Node child, Double value)` 的第 2 个参数为 Double 类型，如果传入 int 值会报错：

```java
Button b1 = new Button("A button");
AnchorPane.setTopAnchor(b1, 10); // An error: 10 is an int, not a double
```

**示例：** AnchorPane 约束

添加 2 个 Button 到 AnchorPane：

- 第 1 个 Button 具有 top 和 left anchors
- 第 2 个 Button 具有 bottom 和 right anchors

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AnchorPaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button topLeft = new Button("Top Left");
        AnchorPane.setTopAnchor(topLeft, 10.0);
        AnchorPane.setLeftAnchor(topLeft, 10.0);

        Button bottomRight = new Button("Botton Right");
        AnchorPane.setBottomAnchor(bottomRight, 10.0);
        AnchorPane.setRightAnchor(bottomRight, 10.0);

        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(topLeft, bottomRight);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using an AnchorPane");
        stage.show();
    }
}
```

AnchorPane 的初始尺寸不足以显示 2 个 Button，所以它们出现了重叠。"Bottom Right" Button 较宽，AnchorPane 根据其 prefWidth 和 anchors 值计算 AnchorPane 的 prefWidth。

![|150](Pasted%20image%2020230711123432.png)

Resizing 窗口后：

![|300](Pasted%20image%2020230711123457.png)

如果将 child 固定到 AnchorPane 相对的两条边，AnchorPane 会调整 child 尺寸以维持 anchor 值不变。

**示例：** left+right anchor

将一个 Button 添加到 AnchorPane，设置 left+right anchors。当 AnchorPane 宽度增加，为了保持 anchor 值不边，Button 也会随之加宽。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AnchorPaneStretching extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button leftRight = new Button("A button");
        AnchorPane.setTopAnchor(leftRight, 10.0);
        AnchorPane.setLeftAnchor(leftRight, 10.0);
        AnchorPane.setRightAnchor(leftRight, 10.0);

        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(leftRight);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Streching Children in an AnchorPane");
        stage.show();
    }
}
```

下面是拉伸前后的效果：

![|120](Pasted%20image%2020230711124046.png)

![|400](Pasted%20image%2020230711124105.png)

