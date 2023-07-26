# TitledPane

2023-07-24, 22:33
***
## 1. 简介

`TitledPane` 是 labeled 控件，继承自 `Labeled` 类。Labeled 控件可以包含 text 和 graphic，`TitledPane` 的 text 作为标题，graphic 也在标题显示。

除了 text 和 graphic，`TitledPane` 还包含内容，为 `Node` 类型。一般将包含多个控件的容器作为 `TitledPane` 的 content。

`TitledPane` 有折叠和展开两个状态：

- 折叠状态，只显示标题栏，隐藏其内容
- 展开状态，显示标题栏和内容

在标题栏中有一个箭头，用于指示是展开还是折叠。点击标题栏切换展开或折叠状态。如下所示：

![states|550](2020-05-17-15-52-00.png)
## 2. 创建

创建 TitledPane，然后设置标题和内容：

```java
// Create a TitledPane and set its title and content
TitledPane infoPane1 = new TitledPane();
infoPane1.setText("Personal Info");
infoPane1.setContent(new Label("Here goes the content."));
```

创建时提供标题和内容：

```java
// Create a TitledPane with a title and content
TitledPane infoPane2 = new TitledPane("Personal Info", new Label("Content"));
```

通过 `setGraphic()` 方法设置 graphic：

```java
String imageStr = "resources/picture/privacy_icon.png";
URL imageUrl = getClass().getClassLoader().getResource(imageStr);
Image img = new Image(imageUrl.toExternalForm());
ImageView imgView = new ImageView(img);
infoPane2.setGraphic(imgView);
```

## 3. 属性

`TitledPane` 包含四个属性：

- animated
- collapsible
- content
- expanded

`animated` 为 boolean 属性，用于指定折叠和展示时是否设置动画。默认为 true。

`collapsible` 属性也是 boolean 类型，表示 `TitledPane` 是否可折叠，默认为 true。对不可折叠的 `TitledPane`，标题栏不显示箭头。

`content` 属性可用于指定任何 node。在展开状态，`content` 内容可见。

`expanded` 为 boolean 属性，该属性为 true 时，`TitledPane` 为展开状态，否则为折叠状态。`TitledPane` 默认处于展开状态，通过 `setExpanded()` 方法设置展开。例如：

```java
// Set the state to expanded
infoPane2.setExpanded(true);
```

一般在 `Accordion` 控件中使用 `TitledPane`，该控件一次只显示一个 `TitledPane`，以节省空间。

也可以单独使用 `TitledPane`，方便对控件进行分组。

```ad-note
`TitledPane` 在展开和折叠时自动调整高度，因此不要在代码中设置 `minimum`, `preferred` 和 `maximum` heights，否则可能出错。
```

**示例：** TitledPane

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TitledPaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TextField firstNameFld = new TextField();
        firstNameFld.setPrefColumnCount(8);

        TextField lastNameFld = new TextField();
        lastNameFld.setPrefColumnCount(8);

        DatePicker dob = new DatePicker();
        dob.setPrefWidth(150);

        GridPane grid = new GridPane();
        grid.addRow(0, new Label("First Name:"), firstNameFld);
        grid.addRow(1, new Label("Last Name:"), lastNameFld);
        grid.addRow(2, new Label("DOB:"), dob);

        TitledPane infoPane = new TitledPane();
        infoPane.setText("Personal Info");
        infoPane.setContent(grid);

        String imageStr = getClass().getResource("/picture/privacy_icon.png").toExternalForm();
        Image img = new Image(imageStr);
        ImageView imgView = new ImageView(img);
        infoPane.setGraphic(imgView);

        HBox root = new HBox(infoPane);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using TitledPane Controls");
        stage.show();
    }
}
```

![|300](Pasted%20image%2020230724221435.png)

## 4. CSS

`TitledPane` 的默认 CSS 样式类名为 `titled-pane`。

`TitledPane` 添加了两个 boolean 类型样式属性：

- -fx-animated，展开和折叠是否带动画
- -fx-collapsible，是否可折叠

两个属性的默认值均为 true。

`TitledPane` 支持两个 CSS pseudo-classes:

- collapsed，TitlePane 折叠时应用
- expanded，TitlePane 展开时应用

`TitledPane` 包含两个子结构：

- title, `StackPane` 类型，包含标题栏内容，title 子结构包含 text 和 arrow-button 两个子结构
    - text 为 Label 类型，包含标题的 text 和 graphic
    - arrow-button 为 `StackPane` 类型包含 arrow 子结构，
        - arrow 子结构也是 `StackPane` 类型
- Content, StackPane 类型

**示例：** 为 TitledPane 应用 4 种不同的样式

```css
/* #1 */
.titled-pane > .title {
    -fx-background-color: lightgray;
    -fx-alignment: center-right;
}

/* #2 */
.titled-pane > .title > .text {
    -fx-font-size: 14px;
    -fx-underline: true;
}

/* #3 */
.titled-pane > .title > .arrow-button > .arrow {
    -fx-background-color: blue;
}

/* #4 */
.titled-pane > .content {
    -fx-background-color: burlywood;
    -fx-padding: 10;
}
```

样式 #1 将 title 的 background 设置为 lightgray，将 graphic 和 title 的对齐方式设置为 center-right。

样式 #2 将 title 的 font-size 设置为 14px，添加下划线。

样式 #3 将 arrow 的 background 设置为 blue。

样式 #4 将 content 的 background 设置为 burlywood；添加 padding 10px。

![](Pasted%20image%2020230724223336.png)

