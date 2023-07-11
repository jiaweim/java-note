# TilePane

2023-07-10, 16:05
****
## 1. 简介

TilePane 使用大小均匀的网格布局子节点，每个单元格成为 tile。TilePane 于 FlowPane 非常类似，只有一个区别：FlowPane 的 rows 和 columns 高、宽可以不一样，TilePane 所有 rows 高度相同，所有 columns 宽度相同。最宽子节点宽度和最高子节点的高度，定义单个网格的尺寸。

于 FlowPane 一样，TilePane 也有水平和垂直两种选择，默认为水平方向。

水平 TilePane:

![|400](Pasted%20image%2020230710144147.png)

垂直 TilePane:

![|400](Pasted%20image%2020230710144206.png)

通过 TilePane 的属性和约束来定义布局：

- 可以覆盖 tiles 的默认尺寸
- 定义 TilePane 整个内容的 alignment，默认为 Pos.TOP_LEFT
- 定义单个节点在 tile 中的 alignment，默认为 Pos.CENTER
- 相邻 rows 和 columns 的间距，默认 0px
- 可以指定水平 TilePane 的 preferred columns 数，垂直 TilePane 的 preferred rows 数。两者默认为 5

## 2. 创建 TilePane

构造 TilePane，指定方向、水平和垂直 spacing，以及初始 children:

```java
// 水平空 TilePane, spacing=0px
TilePane tpane1 = new TilePane();

// 垂直空 TilePane, spacing=0px
TilePane tpane2 = new TilePane(Orientation.VERTICAL);

// 水平空 TilePane，水平 spacing=5px, 垂直 spacing=10px
TilePane tpane3 = new TilePane(5, 10);

// 垂直空 TilePane，水平 spacing=5px, 垂直 spacing=10px
TilePane tpane4 = new TilePane(Orientation.VERTICAL, 5, 10);

// 水平 TilePane，提供 children
TilePane tpane5 = new TilePane(new Button("Button 1"), new Button("Button 2"));
```

**示例：** 创建 TilePane，添加 children

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.time.Month;

public class TilePaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        double hgap = 5.0;
        double vgap = 5.0;
        TilePane root = new TilePane(hgap, vgap);
        root.setPrefColumns(5);

        // Add 12 Buttons - each having the name of the 12 months
        for (Month month : Month.values()) {
            Button b = new Button(month.toString());
            b.setMaxHeight(Double.MAX_VALUE);
            b.setMaxWidth(Double.MAX_VALUE);
            root.getChildren().add(b);
        }

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("A Horizontal TilePane");
        stage.show();
    }
}
```

![|450](Pasted%20image%2020230710145203.png)

## 3. TilePane 属性

TilePane 提供了下表所属布局相关属性：

| 属性 | 类型 | 说明 |
| ---- | ---- | ---- |
|`alignment`|`ObjectProperty<Pos>`|`TilePane` 所有内容相对 content area 的对齐方式，默认 `Pos.TOP_LEFT`
|`tileAlignment`|`ObjectProperty<Pos>`|所有 children 相对所在 tile 的对齐方式，默认 `Pos.CENTER`
|`hgap`, `vgap`|`DoubleProperty`|hgap 指定水平间距，vgap 指定垂直间距。两者默认为 0
|`orientation`|`ObjectProperty<Orientation>`|TilePane 方向，水平或垂直，默认 `HORIZONTAL`
|`prefRows`|`IntegerProperty`|指定垂直 TilePane 的 preferred row 数，水平 TilePane 忽略该属性
|`prefColumns`|`IntegerProperty`|指定水平 TilePane 的 preferred column 数，垂直 TilePane 忽略该属性
|`prefTileWidth`|`DoubleProperty`|指定 tile 的 preferred width。默认为最宽 child 的宽度
|`prefTileHeight`|`DoubleProperty`|指定 tile 的 preferred height。默认为最高 child 的高度
|`tileHeight`|`ReadOnlyDoubleProperty`|tile 实际高度的 read-only 属性
|`tileWidth`|`ReadOnlyDoubleProperty`|tile 实际宽度的 read-only 属性

### 3.1. alignment 属性

TilePane 的 alignment 属性指定其内容相对 content area 的对齐方式。当 TilePane 大于其 content 时可看到效果。

该属性的工作方式与 FlowPane 相同。

### 3.2. tileAlignment 属性

tileAlignment 属性指定 tiles 中 children 的默认对齐方式：

- 该属性影响尺寸小于 tiles 的 children
- 可以被 alignment 约束覆盖

**示例：** tileAlignment

内容：

- 定义了 2 个 TilePane
- 第 1 个 tileAlignment 为 Pos.CENTER
- 第 2 个 tileAlignment 为 Pos.TOP_LEFT

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class TilePaneTileAlignment extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TilePane tileAlignCenter = createTilePane(Pos.CENTER);
        TilePane tileAlignTopRight = createTilePane(Pos.TOP_LEFT);

        HBox root = new HBox(tileAlignCenter, tileAlignTopRight);
        root.setFillHeight(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("The tileAlignment Property for TilePane");
        stage.show();
    }

    public TilePane createTilePane(Pos tileAlignment) {
        Button[] buttons = new Button[]{new Button("Tile"),
                new Button("are"),
                new Button("aligned"),
                new Button("at"),
                new Button(tileAlignment.toString())};

        TilePane tpane = new TilePane(5, 5, buttons);
        tpane.setTileAlignment(tileAlignment);
        tpane.setPrefColumns(3);
        tpane.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        return tpane;
    }
}
```

![](Pasted%20image%2020230710150857.png)

### 3.3. hgap 和 vgap 属性

hgap 和 vgap 属性指定相邻 columns 和 rows 的间距，默认为 0.

可以在 TilePane 构造函数中指定，也可以使用 setHgap(double hg) 和 setVgap(double vg) 方法指定。

### 3.4. Orientation 属性

orientation 属性指定 TilePane 的方向。默认为 `Orientation.HORIZONTAL`。可以在构造函数中指定，也可以使用 `setOrientation()` 方法：

```java
// Create a horizontal TilePane
TilePane tpane = new TilePane();
...
// Change the orientation of the TilePane to vertical
tpane.setOrientation(Orientation.VERTICAL);
```

### 3.5. prefRows 和 prefColumns 属性

prefRows 属性指定垂直 TilPane 的 preferred rows 数，水平 TilePane 忽略该属性。

prefColumns 属性指定水平 TilePane 的 preferred columns 数，垂直 TilePane 忽略该属性。

prefRows 和 prefColumns 默认为 5。

这两个属性仅用于计算 TilePane 的 preferred size。将 TilePane resize 为其它尺寸后，实际 rows 和 columns 数可能不同。

```ad-tip
TilePane 的 prefRows 和 prefColumns 属性的功能与 FlowPane 的 prefWrapLength 功能一样。
```

### 3.6. prefTileWidth 和 prefTileHeight 属性

TilePane 根据最宽和最高 children 来计算 tile 的 preferred size。使用 prefTileWidth 和 prefTileHeight 属性可以覆盖计算的尺寸。

这俩属性的默认值为  `Region.USE_COMPUTED_SIZE`。TilePane 会尝试 resize 其 children 以填充 tile（如果 children 的 min 和 max sizes 允许）。

```java
// Create a TilePane and set its preferred tile width and height to 40px
TilePane tpane = new TilePane();
tpane.setPrefTileWidth(40);
tpane.setPrefTileHeight(40);
```

### 3.7. tileWidth 和 tileHeight 属性

tileWidth 和 tileHeight 为 read-only 属性，返回 tile 的实际尺寸。

如果指定了 prefTileWidth 和 prefTileHeight 属性，它们返回设置的值；否则返回计算的 tile 尺寸。

## 4. TilePane 约束

TilePane 同样支持为每个 children 设置 alignment 和 margin 约束。

alignment 约束定义 child 相对 tile 的对齐方式。注意以下三者的区别：

- TilePane 的 alignment 属性
- TilePane 的 tileAlignment 属性
- TilePane 对每个 children 的 alignment 约束

alignment 属性指定 content (所有 children) 相对 content area 的对齐方式。即将 TilePane 的 content 作为一个整体处理。

tileAlignment 属性指定 children 相对其 tile 的默认对齐方式。修改该属性影响所有 children.

alignment 约束设置单个 child 在 tile 内的对齐方式。它只影响设置该约束的 child，覆盖 tileAlignment 属性设置的默认对齐方式。

```ad-tip
TilePane 的 tileAlignment 属性默认值为 Pos.CENTER，而所有 children 的 alignment 约束默认为 null。
```

使用 static TilePane.setAlignment(Node child, Pos value) 设置 children 的 alignment 约束。使用 static getAlignment(Node child) 查询 alignment 约束：

```java
// Place a Text node in the top left corner in a tile
Text topLeft = new Text("top-left");
TilePane.setAlignment(topLeft, Pos.TOP_LEFT);
TilePane root = new TilePane();
root.getChildren().add(topLeft);
...
// Get the alignment of the topLeft node
Pos alignment = TilePane.getAlignment(topLeft);
```

**示例：** 添加 5 个 Button 到 TilePane。

- Button "Three" 的 alignment 约束设置为  Pos.BOTTOM_RIGHT
- 其它 Button 使用默认的 tile alignment，为 Pos.CENTER

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class TilePaneAlignmentConstraint extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button b12 = new Button("One\nTwo");
        Button b3 = new Button("Three");
        Button b4 = new Button("Four");
        Button b5 = new Button("Five");
        Button b6 = new Button("Six");

        // Set the tile alignment constraint on b3 to BOTTOM_RIGHT
        TilePane.setAlignment(b3, Pos.BOTTOM_RIGHT);

        TilePane root = new TilePane(b12, b3, b4, b5, b6);
        root.setPrefColumns(3);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Alignment Constraints in TilePane");
        stage.show();
    }
}
```

![|250](Pasted%20image%2020230710155336.png)

使用 static `TilePane.setMargin(Node child, Insets value)` 设置 children 的 margins 约束。static `TilePane.getMargin(Node child)` 返回指定 child 的 margin：

```java
// Set 10px margin around the topLeft child node
TilePane.setMargin(topLeft, new Insets(10));
...
// Get the margin of the topLeft child node
Insets margin = TilePane.getMargin(topLeft);
```

设置为 null 重置约束为默认值。使用 static `TilePane.clearConstraints(Node child)` 重置指定 child 的所有约束：

```java
// Clear the tile alignment and margin constraints for the topLeft child node
TilePane.clearConstraints(topLeft);
```

重置 child 约束后，对齐属性为 `tileAlignment` 属性的当前值，margin 为 0px。

