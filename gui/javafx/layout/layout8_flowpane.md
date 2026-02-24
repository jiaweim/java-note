# FlowPane

2023-07-10, 11:57
****
## 1. 简介

`FlowPane` 是一个简单的 layout，它以指定高度或宽度的 row 或 column 布局子节点：

- 对水平布局，可以指定 `prefWidth`，内容超过 `prefWidth` 会自动换行
- 对垂直布局，可以指定 `prefHeight`，内容超过 `prefHeight` 会自动换列

`FlowPane` 适合子节点相对位置不重要的情形，如显示一系列的图片或按钮。

`FlowPane` 对所有子节点以 prefSize 显示，rows 和 columns 可以有不同的高度或宽度。

> [!TIP]
>
> 水平显示的 `FlowPane` 可以从左到右，或从右到左排列子节点，由 `Node` 类中声明的 `nodeOrientation` 属性设置。默认值为 `NodeOrientation.LEFT_TO_RIGHT`，即从左到右排列子节点。如果希望从右到左，将其设置为 `NodeOrientation.RIGHT_TO_LEFT` 即可。
>
> 该设置适用于所有以行显示子节点的 layout，如 `HBox`, `TilePane` 等。

`FlowPane` 可以是水平和垂直方向。例如，添加 10 个 Buttons 到 `FlowPane`。

水平 FlowPane:

<img src="images/Pasted%20image%2020230710110610.png" alt="|400" style="zoom:67%;" />

垂直 FlowPane:

<img src="images/Pasted%20image%2020230710110630.png" alt="|400" style="zoom:67%;" />

## 2. 创建 FlowPane

`FlowPane` 提供了多个构造函数，指定方向（水平、垂直），spacing，初始子节点等：

```java
// 空水平 FlowPane, spacing=0px
FlowPane fpane1 = new FlowPane();

// 空垂直 FlowPane，spacing=0px
FlowPane fpane2 = new FlowPane(Orientation.VERTICAL);

// 空水平 FlowPane，水平 spacing=5px, 垂直 spacing=10px
FlowPane fpane3 = new FlowPane(5, 10);

// 空垂直 FlowPane，水平 spacing=5px，垂直 spacing=10px
FlowPane fpane4 = new FlowPane(Orientation.VERTICAL, 5, 10);

// 水平 FlowPane，包含 2 个 Buttons，spacing=0px
FlowPane fpane5 = new FlowPane(new Button("Button 1"), new Button("Button 2"));
```

**示例：** 创建 FlowPane，添加 10 个 Buttons，水平 spacing=5px，垂直 spacing=10px

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class FlowPaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        double hgap = 5;
        double vgap = 10;
        FlowPane root = new FlowPane(hgap, vgap);

        // Add ten buttons to the flow pane
        for (int i = 1; i <= 10; i++) {
            root.getChildren().add(new Button("Button " + i));
        }

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("A Horizontal FlowPane");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230710111239.png)

## 3. FlowPane Properties

FlowPane 类的如下属性用于布局其子节点。

| 属性               | 类型                          | 说明                                                                          |
| ------------------ | ----------------------------- | ----------------------------------------------------------------------------- |
| `alignment`        | `ObjectProperty<Pos>`         | rows 和 columns 相对 FlowPane content area 对齐方式。默认 Pos.TOP_LEFT        |
| `rowValignment`    | `ObjectProperty<VPos>`        | 水平 FlowPane 每行子节点的垂直对齐方式，在垂直 FlowPane 忽略该属性            |
| `columnHalignment` | `ObjectProperty<HPos>`        | 垂直 FlowPane 每列子节点的水平对齐方式，在水平 FlowPane 忽略该属性            |
| `hgap`, `vgap`     | `DoubleProperty`              | 指定子节点之间水平和垂直 gap，默认 0                                          |
| `orientation`      | `ObjectProperty<Orientation>` | FlowPane 的方向，默认 HORIZONTAL                                              |
| `prefWrapLength`   | `DoubleProperty`              | 水平 FlowPane 的 preferred width, 垂直 FlowPane 的 preferred height，默认 400 |

### 3.1. alignment

FlowPane 的 alignment 属性指定其内容的对齐方式。`Pos` 包含垂直和水平对齐。例如，Pos.TOP_LEFT 垂直方向（vpos）为 TOP，水平方向（hpos）为 LEFT。

- 在水平 FlowPane，每行使用 alignment 的 hpos 值，而所有行使用 vpos 对齐。
- 在垂直 FlowPane，每列使用 alignment 的 vpos 值，而所有列使用 hpos 对齐

**示例：** 在 HBox 中显示 3 个 FlowPane，每个 FlowPane 使用不同的 alignment

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FlowPaneAlignment extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        FlowPane fp1 = createFlowPane(Pos.BOTTOM_RIGHT);
        FlowPane fp2 = createFlowPane(Pos.BOTTOM_LEFT);
        FlowPane fp3 = createFlowPane(Pos.CENTER);

        HBox root = new HBox(fp1, fp2, fp3);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("FlowPane Alignment");
        stage.show();
    }

    private FlowPane createFlowPane(Pos alignment) {
        FlowPane fp = new FlowPane(5, 5);
        fp.setPrefSize(200, 100);
        fp.setAlignment(alignment);
        fp.getChildren().addAll(new Text(alignment.toString()),
                new Button("Button 1"),
                new Button("Button 2"),
                new Button("Button 3"));

        fp.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        return fp;
    }
}
```

![|600](Pasted%20image%2020230710112903.png)

### 3.2. rowValignment, columnHalignment

FlowPane 使用 preferred size 显示子节点。rows 和 columns 尺寸可能不同。使用 rowValignment 和 columnHalignment 属性设置每行或每列的对齐方式。

在水平 `FlowPane`，单个 row 包含的子节点高度可能不同：

- row 的高度为最高子节点的 preferred height
- `rowValignment` 属性指定每行子节点的垂直对齐方式
- `VPos` enum: `BASELINE`, `TOP`, `CENTER` 和 `BOTTOM`
- 如果子节点的 maximum height 允许垂直增长，子节点会增长以填充 row 的高度
- 如果 rowValignment 属性为 VPos.BASELINE，子节点尺寸被设置为 preferred height，而不是填充整个 row

在垂直 FlowPane，单个 column 包含的子节点宽度可能不同：

- column 的宽度为最宽子节点的 preferred width
- columnHalignment 属性指定每列子节点的水平对齐方式
- HPos enum: LEFT, RIGHT, CENTER
- 如果子节点的 maximum width 允许水平增长，子节点会增长以填充 column 整个宽度

**示例：** 在 HBox 中显示 3 个 FlowPane。

前 2 个 FlowPanes 为水平方向，第 3 个为垂直方向。

```java
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Orientation.VERTICAL;

public class FlowPaneRowColAlignment extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        FlowPane fp1 = createFlowPane(HORIZONTAL, VPos.TOP, HPos.LEFT);
        FlowPane fp2 = createFlowPane(HORIZONTAL, VPos.CENTER, HPos.LEFT);
        FlowPane fp3 = createFlowPane(VERTICAL, VPos.CENTER, HPos.RIGHT);

        HBox root = new HBox(fp1, fp2, fp3);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("FlowPane Row and Column Alignment");
        stage.show();
    }

    private FlowPane createFlowPane(Orientation orientation,
                                    VPos rowAlign,
                                    HPos colAlign) {
        // Show the row or column alignment value in a Text
        Text t = new Text();
        if (orientation == Orientation.HORIZONTAL) {
            t.setText(rowAlign.toString());
        } else {
            t.setText(colAlign.toString());
        }

        // Show the orientation of the FlowPane in a TextArea
        TextArea ta = new TextArea(orientation.toString());
        ta.setPrefColumnCount(5);
        ta.setPrefRowCount(3);

        FlowPane fp = new FlowPane(orientation, 5, 5);
        fp.setRowValignment(rowAlign);
        fp.setColumnHalignment(colAlign);
        fp.setPrefSize(175, 130);
        fp.getChildren().addAll(t, ta);
        fp.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        return fp;
    }
}
```

![|600](Pasted%20image%2020230710114209.png)

### 3.3. hgap, vgap

在水平 FlowPane，hgap 属性指定子节点字节的水平间距。vgap 指定 rows 之间的间距。

在垂直 FlowPane，hgap 属性指定 columns 之间的间距，vgap 指定单列中子节点之间的垂直间距。

设置方法：

```java
// 5px hgap, 10px vgap
FlowPane fpane = new FlowPane(5, 10);
...
// 设置为 15px hgap, 25px vgap
fpane.setHgap(15);
fpane.setVgap(25);
```

### 3.4. orientation

orientation 属性指定 FlowPane 的方向：

- Orientation.HORIZONTAL 表示按行排列，为默认选项
- Orientation.VERTICAL 表示按列排列

使用构造函数或 setter 方法设置：

```java
// 默认水平
FlowPane fpane = new FlowPane();
...
// 修改方向
fpane.setOrientation(Orientation.VERTICAL);
```

### 3.5. prefWrapLength

prefWrapLength 属性：

- 在水平 FlowPane 中表示 preferred width
- 在垂直 FlowPane 中表示 preferred height

prefWrapLength 用于计算 FlowPane 的 preferred size，默认为 400.

prefWrapLength 可以看作 FlowPane 的建议尺寸。如果该值小于最大子节点的 preferred width 或 height，FlowPane 忽略该值。

## 4. Content Bias

水平 FlowPane 的行数依赖于宽度，垂直 FlowPane 的列数依赖于高度。

在获取 FlowPane 的尺寸时，要考虑该因素。
