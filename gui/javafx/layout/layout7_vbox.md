# VBox

2023-07-10, 10:53
****
## 1. 简介

VBox 单列排列子节点。功能包括：

- 设置相邻子节点之间的 space，默认为 0px
- 设置子节点的 margins
- 调整子节点尺寸

VBox 的 content area 默认高度刚好以 prefHeight 显示所有子节点；默认宽度为最宽子节点的高度。

VBox 不能显式设置子节点位置，子节点位置由 VBox 自动计算。

VBox 与 HBox 类似，只是一个横向、一个纵向。

## 2. 创建 VBox

```java
// 创建空 VBox，默认 spacing=0px
VBox vbox1 = new VBox();

// 创建空 VBox, spacing=10px
VBox vbox2 = new VBox(10);

// 创建 VBox，包含两个 Button，spacing=10px
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
VBox vbox3 = new VBox(10, okBtn, cancelBtn);
```

**示例：** 使用 VBox

添加 1 个 Label, 1 个 TextField，以及 2 个 Button 到 VBox。

子节点之间的 spacing=10px。设置 10px padding 以保持子节点与 VBox 的边距。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VBoxTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        VBox root = new VBox(10); // 10px spacing
        root.getChildren().addAll(nameLbl, nameFld, okBtn, cancelBtn);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using VBox");
        stage.show();
    }
}
```

![|250](Pasted%20image%2020230710101436.png)

## 3. VBox Properties

VBox 定义了 3 个属性，如下所示：

| 属性        | 类型                  | 说明                                                            |
| ----------- | --------------------- | --------------------------------------------------------------- |
| `alignment` | `ObjectProperty<Pos>` | 子节点相对 `VBox` content area 的对齐方式。默认 `Pos.TOP_LEFT`  |
| `fillWidth` | `BooleaProperty`      | 是否调整 resizable 子节点尺寸以填充 VBox 的整个宽度，默认为true |
| `spacing`   | `DoubleProperty`      | 相邻子节点之间的 space，默认 0                                  |

### 3.1. alignment

alignment 属性指定子节点在 VBox 的 content area 的对齐方式。VBox 的默认宽度刚好容下以 preferred height 显示的所有子节点。当 VBox 大于其 preferred size 时，就能体现 `alignment` 属性的效果。

**示例：** alignment 属性

主要内容：

- VBox 添加 2 个 Button
- alignment 设置为 Pos.BOTTOM_RIGHT，
- VBox 的 preferred size 略大于容下所有子节点所需尺寸

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VBoxAlignment extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        VBox vbox = new VBox(10);
        vbox.setPrefSize(200, 100);
        vbox.getChildren().addAll(okBtn, cancelBtn);

        // Set the alignment to bottom right
        vbox.setAlignment(Pos.BOTTOM_RIGHT);

        vbox.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.setTitle("Using VBox Alignment Property");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230710102547.png)

### 3.2. fillWidth

`fillWidth` 属性指定是否调整 resizable 子节点的宽度，以填充 `VBox` 的整个高度；或者保持为 prefWidth。fillWidth 默认为 true。

```ad-note
`fillWidth` 属性只应用于可以调整水平宽度的子节点。
```

例如，Button 的 maximum width 默认为其 preferred width，在 VBox 中 Button 也不会宽于其 preferred width，即使 VBox 还有空余的水平空间。如果希望 Button 在水平方向增长，可以将其 maximum width 设置为 `Double.MAX_VALUE`。

再比如，TextField 默认可水平增长，随着 VBox 宽度增加，TextField 宽度随之增加。如果不希望 resizable 子节点填充 VBox content area 整个宽度，将 fillWidth 属性设置为 false 即可。

**示例：** fillWidth 属性

主要内容：

- 在 VBox 中添加 4 个 Button
- 所有 Button 的 maximum width 设置为 `Double.MAX_VALUE`，是它们能水平增长

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VBoxFillWidth extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button b1 = new Button("New");
        Button b2 = new Button("New Modified");
        Button b3 = new Button("Not Modified");
        Button b4 = new Button("Data Modified");

        b1.setMaxWidth(Double.MAX_VALUE);
        b2.setMaxWidth(Double.MAX_VALUE);
        b3.setMaxWidth(Double.MAX_VALUE);
        b4.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(10, b1, b2, b3, b4);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using VBox fillWidth Property");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230710104007.png)

### 3.3. spacing

spacing 属性设置 VBox 中相邻子节点之间的垂直距离。默认为 0px，可以在 VBox 构造函数中设置，也可以使用 `setSpacing()` 方法设置。

## 4. Constraints

VBox 支持两种类型的约束: vgrow 和 margin，可以**为每个子节点单独设置**。

- vgrow，当 VBox 有额外垂直空间，该子节点是否增加高度。
- margin，子节点 layoutBounds 外额外的空间

设置方法：

- VBox 的 static 方法 `setVgrow()` 和 `setMargin()` 指定这两种约束
- 设置为 null 表示删除约束
- VBox.clearConstraints(Node child) 删除两个约束

### 4.1. vgrow

在 VBox 中，子节点默认宽度为 preferred heights。当 VBox 增加高度：

- 当子节点的 `vgrow`  设置为 ALWAYS，子节点随之增高
- 如果没有子节点设置 `vgrow`，则多余空间闲置

使用 VBox.setVgrow() 设置节点的 vgrow 值：

```java
VBox root = new VBox(10);
TextArea desc = new TextArea();

// Let the TextArea always grow vertically
root.setVgrow(desc, Priority.ALWAYS);
```

重置子节点的 vgrow 约束：

```java
// Stop the TextArea from growing horizontally
root.setVgrow(desc, null);
```

**示例：** 将 TextArea 的 vgow 设置为 Priority.ALWAYS,

TextArea 的高度会随 VBox 的高度增加而增加。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VBoxVGrow extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label descLbl = new Label("Descrption:");
        TextArea desc = new TextArea();
        desc.setPrefColumnCount(10);
        desc.setPrefRowCount(3);

        VBox root = new VBox(10);
        root.getChildren().addAll(descLbl, desc);

        // Let the TextArea always grow vertically
        VBox.setVgrow(desc, Priority.ALWAYS);

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Vertical Grow Priority in a VBox");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230710105244.png)

### 4.2. margins

使用 static VBox.setMargin() 设置子节点的 margins:

```java
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
VBox vbox = new VBox(okBtn, cancelBtn);

// Set margins for OK and cancel buttons
Insets margin = new Insets(5);
VBox.setMargin(okBtn, margin);
VBox.setMargin(cancelBtn, margin);
...
// Remove margins for okBtn
VBox.setMargin(okBtn, null);
```
