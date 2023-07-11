# GridPane

2023-07-11, 10:53
****
## 1. 简介

GridPane 是最强大的布局容器之一，当然，强大也意味着复杂。

GridPane 以动态网格的形式布局 children，不同 rows 的高度，不同 columns 的宽度都可以不同。

GridPane 默认不显示网格线。为了方便 debug，可以显示网格线。下面有 3 个 GridPane:

- 第 1 个显示网格线，没有 child
- 第 2 个显示 cell 位置
- 第 3 个添加了 6 个 Buttons，其中一个 Button 占据 2 个 cells

![|500](Pasted%20image%2020230710161956.png)

GridPane 索引：

- row 索引从上到下增加，从 0 开始
- column 索引从左到右或从右到左，从 0 开始

如果 `nodeOrientation` 属性为 LEFT_TO_RIGHT，则最左 column 索引为  0；如果设置为 RIGHT_TO_LEFT，则最右 column 索引为 0.

## 2. 创建 GridPane

GridPane 只有一个无参构造函数。创建空 GridPane，0px spacing：

```java
GridPane gpane = new GridPane();
```

## 3. 显示网格线

GridPane 的 gridLinesVisible 属性为 BooleanProperty 类型，用于设置网格线的可见性，默认为 false。

GridPane 网格线一般只用于 debug，设置可见方式：

```java
GridPane gpane = new GridPane();
gpane.setGridLinesVisible(true); // Make grid lines visible
```

## 4. 添加 Children

GridPane 将 children 保存在 getChildren() 返回的 `ObservableList<Node>` 中，但是不应直接添加 children 到该 list，而是使用 GridPane 提供的特定方法。在添加 child 时至少要指定 cell 索引，即放置 child 的位置。

我们首先看一下直接将 children 添加到  `getChildren()` list 的效果。

**示例：** 直接将 3 个 Button 添加到 GridPane

3 个 Button 会重叠，都在 (0, 0) cell，按照添加的顺序渲染。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GridPaneChildrenList extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button b1 = new Button("One One One One One");
        Button b2 = new Button("Two Two Two");
        Button b3 = new Button("Three");

        GridPane root = new GridPane();

        // Add three buttons to the list of children of the GridPane directly
        root.getChildren().addAll(b1, b2, b3);

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Adding Children to a GridPane");
        stage.show();
    }
}
```

![|200](Pasted%20image%2020230710170000.png)

修复该问题的方法有两种：

- 设置 children 的位置
- 使用 GridPane 提供的特定方法添加 children

### 4.1. 设置 Children 位置

GridPane 提供了 3 个 static 方法设置 children 位置：

```java
public static void setColumnIndex(Node child, Integer value)
public static void setRowIndex(Node child, Integer value)
public static void setConstraints(Node child, int columnIndex, int rowIndex)
```

**示例：** 对上一个示例进行修改，设置 children 位置

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GridPaneChildrenPositions extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button b1 = new Button("One One One One One");
        Button b2 = new Button("Two Two Two");
        Button b3 = new Button("Three");

        GridPane root = new GridPane();

        // Add three buttons to the list of children of the GridPane directly
        root.getChildren().addAll(b1, b2, b3);

        // Set the cells the buttons
        GridPane.setConstraints(b1, 0, 0); // (c0, r0)
        GridPane.setConstraints(b2, 1, 0); // (c1, r0)
        GridPane.setConstraints(b3, 2, 0); // (c2, r0)

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Setting Positions for Children in a GridPane");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230710170450.png)

### 4.2. 添加 Children 的便捷方法

GridPane 包含如下添加 children 便捷方法：

```java
void add(Node child, int columnIndex, int rowIndex)
void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)
void addRow(int rowIndex, Node... children)
void addColumn(int columnIndex, Node... children)
```

add() 指定 column index, row index, colum span 和 row span 添加 child。

addRow() 在 rowIndex 对应的 row 按顺序添加 children，如果对应 row 已有 children，则 append 到后面。

```ad-warning
使用 addRow() 添加的 children 只占一个 cell。可以使用 GridPane 的 static 方法 setRowSpan(Node child, Integer value) 和 setColumnSpan(Node child, Integer value) 修改 child 的 span。在修改 child 的 span 后，需要修改其它受影响的 child 的 row 和 column indexes，避免重叠。
```

addColumn() 在 columnIndex 对应的 column 添加 children，如果对应 column 已有 children，则 append 到后面。其它性质同 addRow()。

**示例：** 下面创建 3 个 GridPane，使用 3 种不同的方式添加 4 个 Button

```java
// Add a child node at a time
GridPane gpane1 = new GridPane();
gpane1.add(new Button("One"), 0, 0); // (c0, r0)
gpane1.add(new Button("Two"), 1, 0); // (c1, r0)
gpane1.add(new Button("Three"), 0, 1); // (c0, r1)
gpane1.add(new Button("Four"), 1, 1); // (c1, r1)

// Add a row at a time
GridPane gpane2 = new GridPane();
gpane2.addRow(0, new Button("One"), new Button("Two"));
gpane2.addRow(1, new Button("Three"), new Button("Four"));

// Add a column at a time
GridPane gpane3 = new GridPane();
gpane3.addColumn(0, new Button("One"), new Button("Three"));
gpane3.addColumn(1, new Button("Two"), new Button("Four"));
```

![|150](Pasted%20image%2020230710171527.png)

### 4.3. 指定行和列的 spans

一个 child 可以跨越多个行和列（默认 1 行 1 列），使用 rowSpan 和 colSpan 约束可以指定范围。可以在添加 child 时指定，也可以添加后用 GridPane 的方法设置：

```java
void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)
static void setColumnSpan(Node child, Integer value)
static void setConstraints(Node child, int columnIndex, int rowIndex, 
                           int columnspan, int rowspan)
```

`setConstraints()` 还有其它的重载版本，便于指定不同 span 值。

GridPane 有一个常量 REMAINING 用于指定 span，表示该 child 跨越余下的 columns 或 rows>

**示例：** span

在第一行添加 1 个 Label 和 1 个 TextField。
第二行第 1 列添加 1 个 TextArea，colSpan 设置为 REMAINING，这样 TextArea 会占据 2 列

```java
// 创建 GridPane，背景为 lightgray
GridPane root = new GridPane();
root.setGridLinesVisible(true);
root.setStyle("-fx-background-color: lightgray;");

// row 1: Label+TextField
root.addRow(0, new Label("First Name:"), new TextField());

// row 2: TextArea
TextArea ta = new TextArea();
ta.setPromptText("Enter your resume here");
ta.setPrefColumnCount(10);
ta.setPrefRowCount(3);
root.add(ta, 0, 1, GridPane.REMAINING, 1);
```

![|250](Pasted%20image%2020230710173411.png)

如果在第 1 行再添加 2 个 children:

```java
// Add a Label and a TextField to the first row
root.addRow(0, new Label("Last Name:"), new TextField());
```

此时 GridPane 有 4 列，TextArea 的 colSpan 为 REMAINING，所以会继续增加到 4 列：

![|400](Pasted%20image%2020230710173601.png)

## 5. 使用 GridPane 创建 GUI

下面使用 GridPane 创建如下所示的 UI：

![|350](Pasted%20image%2020230710174452.png)

该网格为 4 行 3 列，包含 7 个 children:

- 第一行：Label, TextField 和 OK Button
- 第二行：Label 和 Cancel Button
- 第三行：TextArea
- 第四行：Label

首先，创建这些 children:

```java
// A Label and a TextField
Label nameLbl = new Label("Name:");
TextField nameFld = new TextField();

// A Label and a TextArea
Label descLbl = new Label("Description:");
TextArea descText = new TextArea();
descText.setPrefColumnCount(20);
descText.setPrefRowCount(5);

// Two buttons
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
```

第 1 行的所有 children 都 span 1 个 cell。
第 2 行的 "Description" Label span 2 个 columns，Cancel Button span 1 个 column。
第 3 行的 TextArea span 2 个 columns。
第 4 行的 Label span  3 个 column。

设置所有 children 的 span:

```java
// Create a GridPane
GridPane root = new GridPane();

// Add children to the GridPane
root.add(nameLbl, 0, 0, 1, 1); // (c0, r0, colspan=1, rowspan=1)
root.add(nameFld, 1, 0, 1, 1); // (c1, r0, colspan=1, rowspan=1)
root.add(descLbl, 0, 1, 3, 1); // (c0, r1, colspan=3, rowspan=1)
root.add(descText, 0, 2, 2, 1); // (c0, r2, colspan=2, rowspan=1)
root.add(okBtn, 2, 0, 1, 1); // (c2, r0, colspan=1, rowspan=1)
root.add(cancelBtn, 2, 1, 1, 1); // (c2, r1, colspan=1, rowspan=1)

// Let the status bar start at column 0 and take up all remaning columns
// (c0, r3, colspan=REMAININg, rowspan=1)
root.add(statusBar, 0, 3, GridPane.REMAINING, 1);
```

如果将该 GridPane 添加到 Scene，会得到所需外观，但是 resizing 行为不符合预期。在 resizing 窗口时，children 不会随着 resizing。所以还需要设置部分 children 的 resizing 行为：

- OK 和 Cancel Button 保持不变
- "Name" TextField 应该水平增长
- "Description" TextArea 应该水平和垂直增长
- "Status" Label 应该水平增长

GridPane 默认 resizing children 以填充 cells。Button 的 max size 被固定为 preferred size，我们调整 OK Button 的 max size 使其足够大，从而可以扩展以完整填充 cell 宽度，最后应与 Cancel Button 的 preferred width 相同：

```java
// The max width of the OK button should be big enough, so it can fill the
// width of its cell
okBtn.setMaxWidth(Double.MAX_VALUE);
```

当调整 GridPane 尺寸时，GridPane 的 rows 和 columns 默认采用 preferred size。它们的水平和垂直 grow 约束指定有额外空间时，它们的增长行为。为了使 name, description, status bar 在 GridPane 增长时随之增长，需要设置它们的 hgrow 和 vgrow 约束：

```java
// The name field in the first row should grow horizontally
GridPane.setHgrow(nameFld, Priority.ALWAYS);

// The description field in the third row should grow vertically
GridPane.setVgrow(descText, Priority.ALWAYS);

// The status bar in the last row should fill its cell
statusBar.setMaxWidth(Double.MAX_VALUE);
```

当 GridPane 水平增长，nameFld 占据的第 2 column 会随之增长，占据增加的额外空间；同时使 description 和 status bar fields 填充第 2 列获得的额外宽度。

当 GridPane 垂直增长，description TextArea 占据的第 3 行会随之增长，占据增加的垂直空间。

下面为完整代码：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class GridPaneForm extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // A Label and a TextField 
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();

        // A Label and a TextArea
        Label descLbl = new Label("Description:");
        TextArea descText = new TextArea();
        descText.setPrefColumnCount(20);
        descText.setPrefRowCount(5);

        // Two buttons
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        // A Label used as a status bar
        Label statusBar = new Label("Status: Ready");
        statusBar.setStyle("-fx-background-color: lavender;" +
                "-fx-font-size: 7pt;" +
                "-fx-padding: 10 0 0 0;");

        // Create a GridPane and set its background color to lightgray
        GridPane root = new GridPane();
        root.setStyle("-fx-background-color: lightgray;");

        // Add children to the GridPane
        root.add(nameLbl, 0, 0, 1, 1);   // (c0, r0, colspan=1, rowspan=1)
        root.add(nameFld, 1, 0, 1, 1);   // (c1, r0, colspan=1, rowspan=1)
        root.add(descLbl, 0, 1, 3, 1);   // (c0, r1, colspan=3, rowspan=1)
        root.add(descText, 0, 2, 2, 1);  // (c0, r2, colspan=2, rowspan=1)
        root.add(okBtn, 2, 0, 1, 1);     // (c2, r0, colspan=1, rowspan=1)
        root.add(cancelBtn, 2, 1, 1, 1); // (c2, r1, colspan=1, rowspan=1)
        root.add(statusBar, 0, 3, GridPane.REMAINING, 1);

        /* Set constraints for children to customize their resizing behavior */

        // The max width of the OK button should be big enough, 
        // so it can fill the width of its cell
        okBtn.setMaxWidth(Double.MAX_VALUE);

        // The name field in the first row should grow horizontally
        GridPane.setHgrow(nameFld, Priority.ALWAYS);

        // The description field in the third row should grow vertically
        GridPane.setVgrow(descText, Priority.ALWAYS);

        // The status bar in the last should fill its cell 
        statusBar.setMaxWidth(Double.MAX_VALUE);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Creating Forms Using a GridPane");
        stage.show();
    }
}
```

## 6. GridPane 属性

GridPane 属性如下表所示：

| 属性               | 类型                  | 说明                                                                  |
| ------------------ | --------------------- | --------------------------------------------------------------------- |
| `alignment`        | `ObjectProperty<Pos>` | GridPane 的 content 相对 content area 的对齐方式。默认 `Pos.TOP_LEFT` |
| `gridLinesVisible` | `BooleanProperty`     | 仅用于 debug。网格线是否可见，默认 false                              |
| `hgap`, `vgap`     | `BooleanProperty`     | hgap 指定相邻 columns 间距，vgap 指定相邻 rows 间距，默认 0           |

### 6.1. alignment

GridPane 的 alignment 属性控制其 content 相对 content area 的对齐方式。当 GridPane 的尺寸大于其 content，可以看出该属性的效果。

该属性的工作方式与 FlowPane 的 alignment 属性一样。

### 6.2. gridLinesVisible

gridLinesVisible 属性设置网格线的可见性。仅用于调试：

```java
GridPane gpane = new GridPane();
gpane.setGridLinesVisible(true); // Make grid lines visible
```

有时希望只显示网格、不显示 children，此时可以将 children 设置为不可见。GridPane 计算所有托管 children 的 size，不管它们是否可见。

**示例：** 创建 GridPane，将 gridLinesVisible 属性设置为 true。

添加 4 个 Button，将它们设置为不可见，并添加到 GridPane。

```java
GridPane root = new GridPane();

// Make the grid lines visible
root.setGridLinesVisible(true);

// Set the padding to 10px
root.setStyle("-fx-padding: 10;");

// Make the gridLInes
Button b1 = new Button("One");
Button b2 = new Button("Two");
Button b3 = new Button("Three");
Button b4 = new Button("Four and Five");

// Make all children invisible to see only grid lines
b1.setVisible(false);
b2.setVisible(false);
b3.setVisible(false);
b4.setVisible(false);

// Add children to the GridPane
root.addRow(1, b1, b2);
root.addRow(2, b3, b4);
```

效果：

![|150](Pasted%20image%2020230710200444.png)

### 6.3. hgap 和 vgap

使用 hgap 和 vgap 属性指定相邻 columns 或 rows 之间的间距。默认为 0.

**示例：** 演示 GridPane 的 hgap 和 vgap

显示网格线，方便查看 gaps

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GridPaneHgapVgap extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label fnameLbl = new Label("First Name:");
        TextField fnameFld = new TextField();
        Label lnameLbl = new Label("Last Name:");
        TextField lnameFld = new TextField();
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        // The Ok button should fill its cell
        okBtn.setMaxWidth(Double.MAX_VALUE);

        // Create a GridPane and set its background color to lightgray
        GridPane root = new GridPane();
        root.setGridLinesVisible(true); // Make grid lines visible
        root.setHgap(10); // hgap = 10px
        root.setVgap(5);  // vgap = 5px
        root.setStyle("-fx-padding: 10;-fx-background-color: lightgray;");

        // Add children to the GridPane
        root.addRow(0, fnameLbl, fnameFld, okBtn);
        root.addRow(1, lnameLbl, lnameFld, cancelBtn);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using hgap and vgap Properties for a GridPane");
        stage.show();
    }
}
```


![|400](Pasted%20image%2020230710203427.png)

## 7. 自定义 Columns 和 Rows

使用 columns 和 row 约束自定义 columns 和 rows。例如：

- 指定 width/height 的计算方式。是根据 content 计算，还是固定 width/height，或者该 width/height 的百分比
- children 是否填充 column/row 的 width/height
- 当 GridPane 大于 preferred width/height，column/row 是否 grow

`ColumnConstraints` 类表示 column 约束，`RowConstraints` 类表示 row 约束。

**ColumnConstraints 类的属性**

- `fillWidth`: `BooleanProperty`

指定 column 中 children 是否增长超过其 prefWidth 以填充 column width，默认 true。

- `halignment`: `ObjectProperty<HPos>`

指定 column 中 children 默认水平对齐方式。默认值为 null。column 中所有 children 默认水平对齐方式为 `HPos.LEFT`。为单个 child 设置的约束可覆盖该默认值。

-  `hgrow`: `ObjectProperty<Priority>` 

指定 column 水平 grow 策略。当 GridPane 宽度大于 prefWidth 时，使用该属性为 column 分配更多空间。设置 percentWidth 属性时忽略该属性。

- `minWidth`, `prefWidth`, `maxWidth`: `DoubleProperty`

指定 column 的 minWidth, prefWidth 以及 maxWidth。指定 percentWidth 属性时忽略该属性。

这 3 个属性的默认值为 `USE_COMPUTED_SIZE`。column 的 minWidth 是该 column 所有 children 的 minWidth 的最大值；prefWidth 是该 column 所有 children 的 prefWidth 的最大值；maxWidth 是所有 children 的 maxWidth 的最小值。

- `percentWidth`: `DoubleProperty`

指定 column 宽度相对 GridPane content area 宽度的百分比。设置该属性后，minWidth, prefWidth, maxWidth 和 hgrow 属性都失效。

**RowConstraints 类的属性**

- `fillHeight`: `BooleanProperty`

指定 row 中 children 是否增长超过其 prefHeight 以填充 row height，默认 true。

- `valignment`: `ObjectProperty<HPos>`

指定 row 中 children 默认垂直对齐方式。默认值为 null。row 中所有 children 默认水平对齐方式为 `VPos.CENTER`。为单个 child 设置约束可覆盖该默认值。

- `vgrow`:  `ObjectProperty<Priority>`

指定 row 垂直 grow 策略。当 GridPane 高度大于 prefHeight，使用该属性为 row 分配更多空间。设置 `percentHeight` 属性时忽略该属性。

- `minHeight`, `prefHeight`, `maxHeight`: `DoubleProperty`

指定 row 的 minHeight, prefHeight 以及 maxHeight。指定 `percentHeight` 属性时忽略该属性。

这 3 个属性的默认值为 `USE_COMPUTED_SIZE`。row 的 minHeight 是该 row 所有 children minHeight 的最大值；prefHeight 是该 row 所有 children prefHeight 的最大值；maxHeight 是所有 children 的 maxHeight 的最小值。

- percentHeight: DoubleProperty

指定 row 高度相对 GridPane content area 高度的百分比。设置该属性后，minHeight, prefHeight, maxHeight 和 vgrow 属性都失效。

ColumnConstraints 和 RowConstraints 都提供了多个构造函数。无参构造函数使用默认属性值创建：

```java
// 使用默认属性值创建 ColumnConstraints
ColumnConstraints cc1 = new ColumnConstraints();

// 将 percentWidth 设置为 30%，水平对齐设置为 CENTER
cc1.setPercentWidth(30);
cc1.setHalignment(HPos.CENTER);
```

使用如下构造函数创建固定宽度/高度的 column/row：

```java
// 创建 ColumnConstraints 固定 column 宽度 100px
ColumnConstraints cc2 = new ColumnConstraints(100);

// 创建 RowConstraints 固定 row 高度 80px
RowConstraints rc2 = new RowConstraints(80);
```

将 prefWidth 设置为期望宽度，将 minWidth 和 maxWidth 设置为 prefWidth，也可以实现固定 column 宽度：

```java
// 创建 ColumnConstraints 固定 column 宽度为 100px
ColumnConstraints cc3 = new ColumnConstraints();
cc3.setPrefWidth(100);
cc3.setMinWidth(Region.USE_PREF_SIZE);
cc3.setMaxWidth(Region.USE_PREF_SIZE);
```

将 column width 设置为 GridPane 宽度的 30%，将 column 中 children 水平对齐设置为 CENTER：

```java
ColumnConstraints cc4 = new ColumnConstraints();
cc4.setPercentWidth(30); // 30% width
cc4.setHalignment(HPos.CENTER);
```

在 GridPane 中，不同 columns 宽度的计算方式可能不同。有些 columns 可能设置为 percentage，有些采用 fixed sizes，有些根据 content 计算 sizes。此时 percentage 优先。例如，如果有 3 个 columns，其中 2 个采用 percentage 设置宽度，另一个采用 fixed width，则先为采用 percentage 的 column 设置宽度，然后为采用 fixed width 的 column 设置宽度。（row 情况类似）

```ad-note
所有 columns 的 percentage width 加和可能超过 100。例如，允许将 4 个 columns 的宽度都设置为 30%。此时，percentage 值被当作权重值，每个 column 的宽度为 30/120，即 25%。
```

GridPane 将 columns 和 rows 的约束值保存在 ColumnConstraints 和 RowConstraints 的 ObservableList 中。使用 getColumnConstraints() 和 getRowConstraints() 返回该 list。list 中特定 index 处的元素保存 GridPane 中相同 index 处的约束。例如，ColumnConstraints 中 list 的第 1 个元素保存第 1 个 column 的约束。

可以设置特定 column/row 的约束。余下 columns/rows 的约束根据默认值自动计算。

下面创建 3 个 ColumnConstraints 对象，设置它们的属性，并添加到 GridPane 的 column 约束 list。使用 RowConstraints 对象设置 row 约束的逻辑一致。

```java
// fixed width: 100px
ColumnConstraints cc1 = new ColumnConstraints(100);

// width 设置为 30%，水平对齐 center
ColumnConstraints cc2 = new ColumnConstraints();
cc2.setPercentWidth(30);
cc1.setHalignment(HPos.CENTER);

// width 设置为 50%
ColumnConstraints cc3 = new ColumnConstraints();
cc3.setPercentWidth(50);

// 为 GridPane 设置 column 约束
GridPane root = new GridPane();
root.getColumnConstraints().addAll(cc1, cc2, cc3);
```

**示例：** 在 GridPane 中使用 column 和 row 约束

```java
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class GridPaneColRowConstraints extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        root.setStyle("-fx-padding: 10;");
        root.setGridLinesVisible(true);

        // Add children
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button b = new Button(col + " " + row);
                root.add(b, col, row);
            }
        }

        // Set the fixed width for the first column to 100px
        ColumnConstraints cc1 = new ColumnConstraints(100);

        // Set the percent width for the second column to 30% and 
        // the horizontal alignment to center
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(35);
        cc2.setHalignment(HPos.CENTER);

        // Set the percent width for the third column to 50%
        ColumnConstraints cc3 = new ColumnConstraints();
        cc3.setPercentWidth(35);

        // Add all column constraints to the column constraints list
        root.getColumnConstraints().addAll(cc1, cc2, cc3);

        // Create two RowConstraints objects
        RowConstraints rc1 = new RowConstraints();
        rc1.setPercentHeight(35);
        rc1.setValignment(VPos.TOP);

        RowConstraints rc2 = new RowConstraints();
        rc2.setPercentHeight(35);
        rc2.setValignment(VPos.BOTTOM);

        // Add RowConstraints for the first two rows
        root.getRowConstraints().addAll(rc1, rc2);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Setting Column/Row Constraints");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230710214825.png)

column 1 宽度为 100px fixed 宽度。column 2 和 3 宽度为 35%。

当 (35%+35%+100px) 小于 GridPane 宽度，多出空余水平空间。

column 2 的水平对齐方式为 center，所以 column 2 中所有 Button 水平居中。

column 1 和 3 水平对齐方式为  left，即默认设置。

这里有 3 行，但是上面只为 row 1 和 2 设置约束，row 3 会根据其内容自动计算。

在设置 column/row 约束时，不能跳过中间的 column/row。即需要从第 1 行/列开始依次设置约束，设置为 null 会抛出 `NullPointerException`。如果希望跳过自定义某个 row/column 的约束，使用无参构造函数创建默认约束。

下面对前 3 个 columns 设置 column 约束，其中第 2 个 column 使用默认约束：

```java
// With 100px fixed width
ColumnConstraints cc1 = new ColumnConstraints(100);

// Use all default settings
ColumnConstraints defaultCc2 = new ColumnConstraints();

// With 200px fixed width
ColumnConstraints cc3 = new ColumnConstraints(200);

GridPane gpane = new GridPane();
gpane.getColumnConstraints().addAll(cc1, defaultCc2, cc3);
```

## 8. GridPane Children 约束

下表列出了 GridPane 中可以为 children 设置的约束。上面已讨论 column/row index 和 span 约束，这里讨论余下部分。GridPane 包含两套 static 方法设置这些约束：

- `setConstraints()`
- `setXxx(Node child, CType cvalue)` 方法，其中 `Xxx` 为约束名称，CType 为类型

将约束设置为 null，即重置 child 约束。

| 约束 | 类型 | 说明 |
| ---- | ---- | ---- |
|`columnIndex`|`Integer`|child 起始  column index，默认 0|
|`rowIndex`|`Integer`|child 起始 row index，默认 0|
|`columnSpan`|`Integer`|child span column 数，默认 1|
|`rowSpan`|`Integer`|child span row 数，默认 1|
|`halignment`|`HPos`|child 水平对齐方式|
|`valignment`|`VPos`|child 垂直对齐方式|
|`hgrow`|`Priority`|child 水平 grow 策略|
|`vgrow`|`Priority`|child 垂直 grow 策略|
|`margin`|`Insets`|child layoutBounds 外的 margins|

### 8.1. halignment 和 valignment

`halignment` 和 `valignment` 约束指定 child 在其 layout 区域的对齐方式。默认为 `HPos.LEFT` 和 `VPos.CENTER`。

可以在 column/row 设置对齐方式，影响包含的所有 children，也可以为 child 单独设置：

- 在 column/row 和 child 都没有设置对齐方式，则使用默认值
- 在 column/row 设置，在 child 没有设置，则使用 column/row 上设置的对齐方式
- 在 column/row 和 child 上都设置了对齐方式，则使用为 child 设置的值

**示例：** 演示对齐规则的选择

在 1 个 column 中添加了 3 个 Button：

- column 设置 halignment 约束为 HPos.RIGHT，覆盖默认值 HPos.LEFT
- Button "Two" 设置 halignment 约束为 HPos.CENTER
- row 1, 2 的 valignment 设置为 VPos.TOP
- row 3 的 valignment 设置为 VPos.CENTER
- Button "One" 设置 valignment 为 VPos.BOTTOM，覆盖为 row 设置的值



```java
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class GridPaneHValignment extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) {
		GridPane root = new GridPane();
		root.setStyle("-fx-padding: 10;");
		root.setGridLinesVisible(true);

		// Add three buttons to a column
		Button b1 = new Button("One");
		Button b2 = new Button("Two");
		Button b3 = new Button("Three");
		root.addColumn(0, b1, b2, b3);

		// Set the column constraints
		ColumnConstraints cc1 = new ColumnConstraints(100);
		cc1.setHalignment(HPos.RIGHT);
		root.getColumnConstraints().add(cc1);

		// Set the row constraints
		RowConstraints rc1 = new RowConstraints(40);
		rc1.setValignment(VPos.TOP);

		RowConstraints rc2 = new RowConstraints(40);
		rc2.setValignment(VPos.TOP);

		RowConstraints rc3 = new RowConstraints(40);
		root.getRowConstraints().addAll(rc1, rc2, rc3);

		// Override the halignment for b2 set in the column
		GridPane.setHalignment(b2, HPos.CENTER);

		// Override the valignment for b1 set in the row
		GridPane.setValignment(b1, VPos.BOTTOM);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("halignemnt and valignment Constraints");
		stage.show();
	}
}

```

![|120](Pasted%20image%2020230711101127.png)

### 8.2. hgrow 和 vgrow

hgrow 和 vgrow 约束分别指定 col 和 row 的水平与垂直 grow 策略。

这 2 约束也可以使用 `ColumnConstraints` 和 `RowConstraints` 指定。cols 和 rows 默认不 grow：

- 如果没有为 col/row 和 children 设置 grow 约束，增大 GridPane 尺寸时 column/row 不 grow
- 如果为 column/row 设置了 grow 约束，使用 `ColumnConstraints` 和 `RowConstraints` 中设置的值，不管是否为 children 设置 grow 约束
- 如果没有为 column/row 设置 grow 约束，则采用所含 children 的最大值作为 column/row 约束。例如，一个 column 包含 3 个 children，没有设置 column 约束。child-1 `hgrow` 为 Priority.NEVER，child-2 hgrow 为 Priority.ALWAYS，child-3 hgrow 为 Priority.SOMETIMES，此时 3 个 children 的 hgrow 最大值为 `Priority.ALWAYS`，将其作为整个 column 的 hgrow 约束。ALWAYS 的值最大，SOMETIMES 次之，NEVER 值最小
- 如果 column/row 设置了 fixed 或 percentage width/height，忽略 hgrow/vgrow 约束

**示例：** 演示上述 grow 选择规则

当窗口水平拉伸，col-2 会随之 grow，col-1 保持不变。两个 col 添加 6 个 Button：

- col-1 hgrow 设置为  Priority.NEVER，col 优先级比 children 高，所以 col-1 不会 grow
- col-2 没有设置 hgrow，而该 col 的 children 设置了不同类型的 hgrow: ALWAYS, NEVER 和 SOMETIMES，col-2 采用最大值 ALWAYS

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class GridPaneHVgrow extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        root.setStyle("-fx-padding: 10;");
        root.setGridLinesVisible(true);

        // Add three buttons to a column
        Button b1 = new Button("One");
        Button b2 = new Button("Two");
        Button b3 = new Button("Three");
        Button b4 = new Button("Four");
        Button b5 = new Button("Five");
        Button b6 = new Button("Six");

        root.addColumn(0, b1, b2, b3);
        root.addColumn(1, b4, b5, b6);

        // Set the column constraints
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.NEVER);
        root.getColumnConstraints().add(cc1);

        // Set three different hgrow priorities for children in the second
        // column. The highest priority, ALWAYS, will be used.
        GridPane.setHgrow(b4, Priority.ALWAYS);
        GridPane.setHgrow(b5, Priority.NEVER);
        GridPane.setHgrow(b6, Priority.SOMETIMES);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("hgrow and vgrow Constraints");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230711103703.png)

### 8.3. margin

static `GridPane.setMargin(Node child, Insets value)` 设置 children 的 margins。static GridPane.getMargin(Node child) 查询 margin。

```java
// Set 10px margin around the b1 child node
GridPane.setMargin(b1, new Insets(10));
...
// Get the margin of the b1 child node
Insets margin = GridPane.getMargin(b1);
```

将 margin 设置为 null 表示重置为默认值，默认为 0.

### 8.4. 清除约束

static `GridPane.clearConstraints(Node child)` 清除 child 的所有约束 (columnIndex, rowIndex, columnSpan, rowSpan, halignment, valignment, hgrow, vgrow, margin)：

```java
// Clear all constraints for the b1 child node
GridPane.clearConstraints(b1);
```
