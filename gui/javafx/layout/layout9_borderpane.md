# BorderPane

2023-07-10, 13:23
****
## 1. 简介

BorderPane 将其布局区域划分为 5 部分：top, right, bottom, left, center

- 每个区域最多放 1 个节点
- 不放节点（null）的区域不分配空间

每个区域最多只能放 1 个节点。下图在每个区域放置 1 个 Button：

<img src="images/Pasted%20image%2020230710120035.png" alt="|350" style="zoom:67%;" />

典型的 Windows 应用使用 5 个区域：

- top: 菜单或工具栏
- bottom: 状态栏
- left: 导航面板
- right:  详细信息
- center: 主面板

BorderPane 正好满足该布局要求，所以 BorderPane 常用于 Scene 的 root 节点。

BorderPane 对子节点的 resizing 策略：

- top 和 bottom 区域的子节点调整为 preferred heights。增长宽度以填充整个水平空间（前提是子节点的 maximum widths 允许）
- left 和 right 区域的子节点调整为 preferred widths。增长高度以填充垂直方向空间（maximum heights 足够）
- center 区域的子节点填充余下余下空间

如果 BorderPane 小于其 preferred size，子节点可能重叠。子节点按照添加顺序渲染，所以重叠规则取决于子节点的添加顺序。

## 2. 创建 BorderPane

```java
// Create an empty BorderPane
BorderPane bpane1 = new BorderPane();

// Create a BorderPane with a TextArea in the center
TextArea center = new TextArea();
BorderPane bpane2 = new BorderPane(center);

// Create a BorderPane with a Text node in each of the five regions
Text center = new Text("Center");
Text top = new Text("Top");
Text right = new Text("Right");
Text bottom = new Text("Bottom");
Text left = new Text("Left");
BorderPane bpane3 = new BorderPane(center, top, right, bottom, left);
```

BorderPane 提供了 5 个属性：top, right, bottom, left, center，存储 5 个区域添加的子节点。例如，分别设置 5 个区域的子节点：

```java
// Create an empty BorderPane and add a text node in each of the five regions
BorderPane bpane = new BorderPane();
bpane.setTop(new Text("Top"));
bpane.setRight(new Text("Right"));
bpane.setBottom(new Text("Bottom"));
bpane.setLeft(new Text("Left"));
bpane.setCenter(new Text("Center"));
```

```ad-warning
不要用 `BorderPane.getChildren()` 返回的 `ObservableList<Node>` 添加子节点。添加到该 list 无效。
```

**示例：** BorderPane

主要内容：

- 创建 BorderPane，为 right, bottom 和 center 区域设置子节点
- center 区域：2 个 Label, 1 个 TextField，1 个 TextArea
- 


```java
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BorderPaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Set the top and left child nodes to null
        Node top = null;
        Node left = null;

        // Build the content nodes for the center region
        VBox center = getCenter();

        // Create the right child node
        Button okBtn = new Button("Ok");
        Button cancelBtn = new Button("Cancel");

        // Make the OK and cancel buttons the same size
        okBtn.setMaxWidth(Double.MAX_VALUE);
        VBox right = new VBox(okBtn, cancelBtn);
        right.setStyle("-fx-padding: 10;");

        // Create the bottom child node
        Label statusLbl = new Label("Status: Ready");
        HBox bottom = new HBox(statusLbl);
        BorderPane.setMargin(bottom, new Insets(10, 0, 0, 0));
        bottom.setStyle("-fx-background-color: lavender;" +
                "-fx-font-size: 7pt;" +
                "-fx-padding: 10 0 0 0;");

        BorderPane root = new BorderPane(center, top, right, bottom, left);
        root.setStyle("-fx-background-color: lightgray;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using a BorderPane");
        stage.show();
    }

    private VBox getCenter() {
        // A Label and a TextField in an HBox
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();
        HBox.setHgrow(nameFld, Priority.ALWAYS);
        HBox nameFields = new HBox(nameLbl, nameFld);

        // A Label and a TextArea
        Label descLbl = new Label("Description:");
        TextArea descText = new TextArea();
        descText.setPrefColumnCount(20);
        descText.setPrefRowCount(5);
        VBox.setVgrow(descText, Priority.ALWAYS);

        // Box all controls in a VBox
        VBox center = new VBox(nameFields, descLbl, descText);

        return center;
    }
}
```

![|400](Pasted%20image%2020230710130952.png)

## 3. BorderPane Properties

BorderPane 定义了 5 个属性：top, right, bottom, left, center。它们都是 `ObjectProperty<Node>` 类型，保存 5 个区域节点的引用。

BorderPane 的 5 个区域并非必须要有节点，没有节点的区域不分配空间。

## 4. BorderPane Constraints

BorderPane 可以为每个区域子节点设置 alignment 和 margin。

alignment 相对所在区域定义。默认对齐策略：

- top: `Pos.TOP_LEFT`
• bottom: `Pos.BOTTOM_LEFT`
• left: `Pos.TOP_LEFT`
• right: `Pos.TOP_RIGHT`
• center: `Pos.CENTER`

使用 static `BorderPane.setAlignment(Node child, Pos value)` 设置：

```java
BorderPane root = new BorderPane();
Button top = new Button("OK");
root.setTop(top);

// 将 OK Button 放在 top right (默认 top left)
BorderPane.setAlignment(top, Pos.TOP_RIGHT);

// 查看 top node 的对齐方式
Pos alignment = BorderPane.getAlignment(top);
```

使用 static BorderPane.setMargin(Node child, Insets value) 设置 margins：

```java
// Set 10px margin around the top child node
BorderPane.setMargin(top, new Insets(10));

// Get the margin of the top child node
Insets margin = BorderPane.getMargin(top);
```


设置为 null 重置 constraints。使用 static BorderPane.clearConstraints(Node child) 重置指定子节点的所有 constraints：

```java
// Clear the alignment and margin constraints for the top child node
BorderPane.clearConstraints(top);
```

