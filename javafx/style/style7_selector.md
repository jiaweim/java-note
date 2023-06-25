# JavaFX CSS Selector

## 简介

样式表中每个样式都有一个关联的 selector，用于识别 scene graph 中的 node。JavaFX CSS 支持多种类型的 selector：class selectors
pseudo-class selectors, ID selector 等。

## Class Selector

`Node` 的 `styleClass` 变量为 `ObservableList<String>` 类型，包含 node 的 JavaFX 样式类名。

JavaFX 样式类名与 Java 类是两种不同的东西。JavaFX 样式类名是字符串类型，用在 CSS 样式表中。

- 可以为 node 指定多个 CSS 类名，例如，给 HBox 指定两个类名

```java
HBox hb = new HBox();
hb.getStyleClass().addAll("hbox", "myhbox");
```

样式 class selector 将关联的样式应用于与样式 class 名称相同的所有 node 上。

```ad-note
node 的样式类名不以点号 `.` 开头，而 CSS 样式类名均以 `.` 开头，匹配时忽略 `.`
```

- 应用样式

使用类选择器定义两个样式，第一个为 .hbox，表示应用于所有样式 名为 hbox 的 node；第二个为 .button，应用于样式类名为 button 的 nodes。

```css
.hbox {
    -fx-border-color: blue;
    -fx-border-width: 2px;
    -fx-border-radius: 5px;
    -fx-border-insets: 5px;
    -fx-padding: 10px;
    -fx-spacing: 5px;
    -fx-background-color: lightgray;
    -fx-background-insets: 5px;
}

.button {
    -fx-text-fill: blue;
}
```

将上面两个样式保存到 `resources\css\styleclass.css` 文件

```java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StyleClassTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameTf = new TextField("");
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> Platform.exit());

        HBox root = new HBox();
        root.getChildren().addAll(nameLbl, nameTf, closeBtn);

        // Set the styleClass for the HBox to "hbox"
        root.getStyleClass().add("hbox");

        Scene scene = new Scene(root);
        var url = getClass().getResource("/css/styleclass.css").toExternalForm();
        scene.getStylesheets().add(url);

        stage.setScene(scene);
        stage.setTitle("Using Style Class Selectors");
        stage.show();
    }
}
```

![](images/2023-06-21-11-27-04.png)

`Button` 包含一个默认样式类名 button，所以 "Close" 按钮文本为蓝色。

JavaFX 中大多数常用控件都有一个默认样式类名。将 JavaFX 类名转换为小写，在单词之间插入短线 `-`，就得到对应的 CSS  类名。例如，默认样式类型对应关系：button 对 Button，label 对 Label，hyperlink 对 Hyperlink，text-field 对 TextField，text-area 对 TextArea，check-box 对 CheckBox。

Java 容器类，如 Region, Pane, HBox, VBox 没有默认样式类名。因此，如果需要使用样式类选择器为它们添加样式，则要为它们添加样式类名。

```ad-tip
JavaFX 样式类名区分大小写。
```

那么，如何查询 node 的默认样式类名，以便在样式表中使用。有三种方法：

- 根据类名猜测
- 参考 [JavaFX CSS 参考指南](https://openjfx.io/javadoc/20/javafx.graphics/javafx/scene/doc-files/cssref.html)
- 代码输出

例如，打印 Button 的默认样式类名：

```java
Button btn = new Button();
ObservableList<String> list = btn.getStyleClass();

if (list.isEmpty()) {
    System.out.println("No default style class name");
} else {
    for(String styleClassName : list) {
        System.out.println(styleClassName);
    }
}
```

## root Node 的类选择器

scene 的 root node 的样式类名为 root。对被其它 node 继承的 CSS 属性，可以用 root 样式类选择器。

root node 是 scene graph 中所有 node 的父节点，**推荐**将 CSS 属性存储在 root node 中，这样 scene graph 中任何 node 都能找到它们。

- 定义 `resources\css\rootclass.css` 样式

```css
.root {
    -fx-cursor: hand;
    -my-button-color: blue;
}

.button {
    -fx-text-fill: -my-button-color;
}
```

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RootClassTest extends Application {

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        Label nameLbl = new Label("Name:");
        TextField nameTf = new TextField("");
        Button closeBtn = new Button("Close");

        HBox root = new HBox();
        root.getChildren().addAll(nameLbl, nameTf, closeBtn);

        Scene scene = new Scene(root);
        /* The root variable is assigned a default style class name "root" */
        var url = getClass().getResource("/css/rootclass.css").toExternalForm();
        scene.getStylesheets().add(url);

        stage.setScene(scene);
        stage.setTitle("Using the root Style Class Selector");
        stage.show();
    }
}
```

![](images/2023-06-21-13-25-20.png)

这里 root 类选择器声明了两个属性：`-fx-cursor` 和 `-fx-button-color`。

`-fx-cursor` 被所有 node 继承，因此所有 node 都是 `HAND` 样式光标，除非覆盖该设置。所以将鼠标移到 scene 的任意地方，除了 `TextField`，其它地方都是 `HAND` 光标。因为 TextField 覆盖了 `-fx-cursor` 属性，将其设置为 `TEXT` 光标。

`-my-button-color` 是一个 look-up 属性，在第二个样式中引用它设置按钮的文本颜色。

## ID Selector

`Node` 有一个名为 `id` 的 `StringProperty` 类型属性，可以为 scene graph 中每个 node 分配一个唯一的 id。id 的唯一性由开发者自己负责。

node 的 `id` 需要显式设置才能使用，主要用于基于 ID 选择器的 node 样式设置。

- 设置 Button 的 `id`

```java
Button b1 = new Button("Close");
b1.setId("closeBtn");
```

- 在样式表中 ID 选择器以 `#` 开头

在匹配样式表和 node 时，会移除 `#` 进行匹配，即 node id 不要带 `#`。

例如，CSS 文件 `resources\css\idselector.css` 内容如下：

```css
.button {
    -fx-text-fill: blue;
}

#closeButton {
    -fx-text-fill: red;
}
```

这里定义了两个样式：类选择器 ".button" 和 ID 选择器 `#closeButton`。

下面创建三个按钮，将 "closeButton" 的 id 设置为 "closeButton"：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class IDSelectorTest extends Application {

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        Button openBtn = new Button("Open");
        Button saveBtn = new Button("Save");

        Button closeBtn = new Button("Close");
        closeBtn.setId("closeButton");

        HBox root = new HBox();
        root.getChildren().addAll(openBtn, saveBtn, closeBtn);

        Scene scene = new Scene(root);
        var url = getClass().getResource("/css/idselector.css").toExternalForm();
        scene.getStylesheets().add(url);

        stage.setScene(scene);
        stage.setTitle("Using ID selectors");
        stage.show();
    }
}
```

![](images/2023-06-21-14-32-34.png)

这里给 "Close" 按钮指定了两个 `-fx-text-fill`，但是 ID 选择器优先级比类选择器高，所以 "Close" 按钮为红色。

## 组合 ID 和 Class 选择器

可以同时使用 ID 选择器和类选择器，匹配同时包含两者的 node:

```css
#closeButton.button {
    -fx-text-fill: red;
}
```

`#closeButton.button` 选择器匹配 ID 为 `closeButton`，且样式类为 `button` 的 nodes。

也可以反过来，效果一样：

```css
.button#closeButton {
    -fx-text-fill: red;
}
```

## 全选

`*` 匹配所有 nodes。该选择器优先级最低。