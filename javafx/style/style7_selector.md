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

- 定义 resources\css\rootclass.css 样式

```css
.root {
    -fx-cursor: hand;
    -my-button-color: blue;
}

.button {
    -fx-text-fill: -my-button-color;
}
```

## ID Selector

设置方法：

```java
Button b1 = new Button("Close");
b1.setId("closeBtn");
```

使用语法：

```css
#closeButton {
    -fx-text-fill: red;
}
```

### CSS 属性类型

JavaFX 支持如下类型:
| 类型       | 说明                                                                               |
| ---------- | ---------------------------------------------------------------------------------- |
| inherit    | 从 parent 继承属性值                                                               |
| boolean    | true,false                                                                         |
| string     | 单引号或双引号，包含引号需要转义，换行符（\A or \0000a）                           |
| number     | 整数或实数，以及单位，px(pixels), mm(millimeters), cm(centimeters), in(inches), pt | (points), pc(picas), em or ex. |
| angle      | 角度，单位有 deg(degrees), rad(radians), grad(gradients), turn(turns)              |
| point      | 空格分隔两个数，如 `0 0`                                                           |
| color-stop | 用于指定 linear 或 radical color gradients                                         |
| URI        | URI                                                                                |
| effect     | 特效                                                                               |
| font       | 字体                                                                               |
| paint      | 颜色                                                                               |

### URI

通过 `url(address)` 函数指定，其中相对地址是相对当前CSS文件的路径。

```css
.image-view {
    -fx-image: url("http://jdojo.com/myimage.png");
}
```

### effect

Drop shadow 和 inner shadow 特效可以通过 dropshadow() 和 innershadow() 两个CSS 函数指定：

```css
dropshadow(<blur-type>, <color>, <radius>, <spread>, <x-offset>, <y-offset>)
innershadow(<blur-type>, <color>, <radius>, <choke>, <x-offset>, <y-offset>)
```

`<blur-type>`： Gaussian, one-pass-box, three-pass-box, two-pass-box。

### font

字体包括四个属性：family, size, style, weight。指定CSS属性的方法有两种：

- 分别以四个CSS属性指定四个值：-fx-font-family, -fx-font-size, -fx-font-style, and -fx-font-weight.
- 以 `-fx-font` 指定所有四个属性
分别指定：

```css
.my-font-style {
    -fx-font-family: "serif";
    -fx-font-size: 20px;
    -fx-font-style: normal;
    -fx-font-weight: bolder;
}
```

同时指定：

```css
.my-font-style {
    -fx-font: italic bolder 20px "serif";
}
```

### paint

paint 类型用于指定颜色。指定方式有：

- linear-gradient() 函数
- radial-gradient() 函数
- 各种颜色值和函数

例：

```css
.my-style {
    -fx-fill: linear-gradient(from 0% 0% to 100% 0%, black 0%, red 100%);
    -fx-background-color: radial-gradient(radius 100%, black, red);
}
```

combo-box
extends combo-box-base
list-cell, ListCell instance used to show the selection in the button area of a non-editable ComboBox.
text-input, a TextField instance used to show the selection and allow input in the button area of an editable ComboBox.
