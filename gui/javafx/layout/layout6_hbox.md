# HBox

2023-07-10, 10:02
****
## 1. 简介

HBox 单行排列子节点。功能包括：

- 设置相邻子节点之间的 space，默认为 0px
- 设置子节点的 margins
- 调整子节点尺寸

HBox 的 content area 默认宽度刚好以 prefWidth 显示所有子节点；默认高度为最高子节点的高度。

在 HBox 不能显式设置子节点位置，子节点位置由 HBox 自动计算。

## 2. 创建 HBox

```java
// 创建空 HBox，默认 space 为 0px
HBox hbox1 = new HBox();

// 创建空 HBox，指定 space 为 10px
HBox hbox2 = new HBox(10);

// 创建 HBox，包含 2 个 Button，指定 space 10px
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
HBox hbox3 = new HBox(10, okBtn, cancelBtn);
```

**示例：** HBox 的使用

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class HBoxTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        HBox root = new HBox(10); // 10px spacing
        root.getChildren().addAll(nameLbl, nameFld, okBtn, cancelBtn);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using HBox");
        stage.show();
    }
}
```

![|360](Pasted%20image%2020230709195441.png)

## 3. HBox Properties

HBox 定义了 3 个属性，如下所示：

|属性|类型|说明|
|---|---|---|
|`alignment`|`ObjectProperty<Pos>`|子节点相对 HBox content area 的对齐方式。如果垂直对齐方式为 `BASELINE`，忽略 `fillHeight` 属性，默认 `Pos.TOP_LEFT`|
|`fillHeight`|`BooleaProperty`|是否调整 resizable 子节点高度以填充 HBox 的整个高度，默认为true，`alignment=BASELINE` 忽略该属性|
|`spacing`|`DoubleProperty`|相邻子节点之间的 space，默认 0|

### 3.1. alignment

alignment 属性指定子节点在 HBox 的 content area 的对齐方式。默认情况下，HBox 的宽度刚好容下以 preferred width 显示的所有子节点。当 HBox 大于其 preferred size 时，就能体现 `alignment` 属性的效果。

**示例：** alignment 属性

创建 HBox，添加 2 个 Button，将 alignment 设置为 Pos.BOTTOM_RIGHT。

将 HBox 的 preferred size 设置得比容纳所有子节点的尺寸大一点。

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class HBoxAlignment extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        HBox hbox = new HBox(10);
        hbox.setPrefSize(200, 100);
        hbox.getChildren().addAll(okBtn, cancelBtn);

        // Set the alignment to bottom right
        hbox.setAlignment(Pos.BOTTOM_RIGHT);

        hbox.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.setTitle("Using HBox Alignment Property");
        stage.show();
    }
}
```

![|200](Pasted%20image%2020230709203057.png)

### 3.2. fillHeight

fillHeight 属性指定是否调整 resizable 子节点的高度，以填充 HBox 的整个高度。

```ad-note
`fillHeight` 属性只应用于可以调整垂直高度的子节点。
```

例如，Button 的 maximum height 默认为其 preferred height，在 HBox 中 Button 也不会高于其 preferred height，即使还有空余的垂直空间。如果希望 Button 在垂直方向扩展，可以将其 maximum height 设置为 `Double.MAX_VALUE`。

再比如，TextArea 默认可扩展，随着 HBox 高度增加，TextArea 高度会随之增加。如果不希望 resizable 子节点填充 HBox 的 content area 整个高度，将 fillHeight 属性设置为 false 即可。

**示例：** fillHeight 属性演示

在 HBox 中添加多个控件。

- TextArea 默认可扩展
- 将 Cancel Button 的 maximum height 设置为 `Double.MAX_VALUE`，使其能够垂直缩放
- CheckBox 用于设置 HBox 的 fillHeight 属性

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class HBoxFillHeight extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        HBox root = new HBox(10); // 10px spacing

        Label descLbl = new Label("Description:");
        TextArea desc = new TextArea();
        desc.setPrefColumnCount(10);
        desc.setPrefRowCount(3);

        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        // Let the Cancel button expand vertically
        cancelBtn.setMaxHeight(Double.MAX_VALUE);

        CheckBox fillHeightCbx = new CheckBox("Fill Height");
        fillHeightCbx.setSelected(true);

        // Add an event handler to the CheckBox, so the user can set the 
        // fillHeight property using the CheckBox
        fillHeightCbx.setOnAction(e ->
                root.setFillHeight(fillHeightCbx.isSelected()));

        root.getChildren().addAll(
                descLbl, desc, fillHeightCbx, okBtn, cancelBtn);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using HBox fillHeight Property");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230709212924.png)

![|400](Pasted%20image%2020230709213704.png)

可以发现，OK Button 总是保持为 preferred height，而 Cancel 在勾选 Fill Height 后，会随 HBox 的高度变化而变化。

### 3.3. spacing

spacing 属性设置 HBox 中相邻子节点之间的水平距离。默认为 0px，可以在 HBox 构造函数中设置，也可以使用 `setSpacing()` 方法设置。

## 4. Constraints

HBox 支持两种类型的约束: hgrow 和 margin，可以**为每个子节点单独设置**。

- hgrow 指定当 HBox 有额外水平空间，该子节点是否增加宽度。
- margin 指定子节点 layoutBounds 外额外的空间

设置方法：

- HBox 提供的 static 方法 `setHgrow()` 和 `setMargin()` 指定这两种约束
- 设置为 null 表示删除约束
- HBox.clearConstraints(Node child) 一次删除两个约束

### 4.1. hgrow

在 HBox 中，子节点默认宽度为 preferred widths。当 HBox 宽度增加：

- 当子节点的 `hgrow`  设置为 ALWAYS，子节点随之加宽
- 如果没有子节点设置 hgrow 约束，则多余空间闲置

使用 HBox.setHgrow() 设置节点的 hgrow 值：

```java
HBox root = new HBox(10);
TextField nameFld = new TextField();

// 使 TextField 始终水平增长
root.setHgrow(nameFld, Priority.ALWAYS);
```

设置为 null，恢复默认值：

```java
// Stop the TextField from growing horizontally
root.setHgrow(nameFld, null);
```

**示例：** 将 TextField 的 hgrow 设置为 `Priority.ALWAYS`，使其总是水平增长

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class HBoxHGrow extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();

        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        HBox root = new HBox(10);
        root.getChildren().addAll(nameLbl, nameFld, okBtn, cancelBtn);

        // 设置 hgrow
        HBox.setHgrow(nameFld, Priority.ALWAYS);

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Horizontal Grow Priority in an HBox");
        stage.show();
    }
}
```

![|340](Pasted%20image%2020230710094750.png)

水平拉伸窗口，其它控件保持 preferred 宽度，而 TextField 自动水平拉伸。

### 4.2. margins

margins 是节点 layoutBounds 外的额外 spaces。在 HBox 中设置 margins:

```java
Label nameLbl = new Label("Name:");
TextField nameFld = new TextField();
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");

HBox hbox = new HBox(nameLbl, nameFld, okBtn, cancelBtn);

// 为 4 个子节点设置 margin:
// 10px top, 2px right, 10px bottom, and 2px left
Insets margin = new Insets(10, 2, 10, 2);
HBox.setMargin(nameLbl, margin);
HBox.setMargin(nameFld, margin);
HBox.setMargin(okBtn, margin);
HBox.setMargin(cancelBtn, margin);
```

将对应 margin 设置为 null 删除子节点的 margin:

```java
// 删除 okBtn 的 margins
HBox.setMargin(okBtn, null);
```

`spacing` 属性和 margins 约束都会增加子节点之间的距离。

```ad-tip
margin 在子节点的 `layoutBounds` 外，不属于子节点属性，而是由 `HBox` 维护的一个布局属性。
```

