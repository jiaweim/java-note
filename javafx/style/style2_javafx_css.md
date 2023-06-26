# JavaFX CSS

2023-06-19, 18:46
****
## 1. JavaFX CSS 命名约定

JavaFX 采用的命名约定和 CSS 的样式类有所不同：

- 所有 `Control` 的 selector 都是小写
- 对单个单词的控件，如 `Button`，其 CSS 样式类名为 `button`
- 对多个单词的控件，如 `TextField`，单词之间以短线分开：`text-field`

属性命名：

- 属性名以 `-fx-` 开头。例如，CSS 样式表的的 `font-size` 在 JavaFX CSS 中变为 `-fx-font-size`
- 变量属性命名和变量命名规则相同。如 `textAlignment` 对应的样式表属性名为 `-fx-text-alignment`

## 2. 添加样式表

可以为 JavaFX 程序添加多个样式表。`Scene` 和 `Parent` 对象都支持添加样式表。两者都通过 `getStylesheets()` 返回保存样式表的 ObservableList 的引用。例如：

```java
// 为 scene 添加两个样式表：ss1.css 和 ss2.css
Scene scene = ...
scene.getStylesheets().addAll("file://.../resources/css/ss1.css", "file://.../resources/css/ss2.css");

// 为 VBox (Parent 子类) 添加样式表 vbox.css
VBox root = new VBox();
root.getStylesheets().add("file://.../vbox.css");
```

将 "..." 替换为正确路径即可。

## 3. 默认样式表

JavaFX 的默认样式由样式表 `modena.css` 定义，该文件位于 javafx.controls.jar 的 "com/sun/javafx/scene/control/skin/modena/modena.css"。

在 JavaFX 8 之前，默认样式为 Caspian，定义在 jfxrt.jar 文件的 "com/sun/javafx/scene/control/skin/caspian/caspian.css"。`Application` 类定义了两个字符串常量 `STYLESHEET_CASPIAN` 和 `STYLESHEET_MODENA`，对应两种主题。

通过 `Application` 设置整个应用的默认样式：

- `public static void setUserAgentStylesheet(String url)`
- `public static String getUserAgentStylesheet()`

例如，将默认样式设置为 Caspian:

```java
Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
```

## 4. 内联样式

设置 `Scene` 中 `Node` 的样式有两种方式：样式表或内联样式。前面介绍了样式表，下面说明内联样式。

`Node` 类的 `style` 属性定义内联样式（`StringProperty` 类型）：

- 设置内联样式： `setStyle(String inlineStyle)`
- 查询内联样式：`getStyle()`

内联样式和样式表的区别：

- 样式表中的样式包含 selector 和相关的 property-value
- 样式表可能影响 `Scene` 中 0 个或多个 `Node`，取决于与 selector 匹配的 Node 个数
- 内联样式没有 selector，只包含 property-value 集合
- 内联样式只影响设置的 `Node`

例如，使用内联样式设置按钮的文本为红色加粗：

```java
Button yesBtn = new Button("Yes");
yesBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
```

**示例：** 创建两个 `VBox`，设置 4.0px 蓝色边框，各保存三个按钮。将两个 `VBox` 放入 `HBox`，边框设置为 10.0px navy。

```java
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InlineStyles extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        Button cancelBtn = new Button("Cancel");

        // 为 Yes 按钮设置内联样式
        yesBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Button openBtn = new Button("Open");
        Button saveBtn = new Button("Save");
        Button closeBtn = new Button("Close");

        VBox vb1 = new VBox();
        vb1.setPadding(new Insets(10, 10, 10, 10));
        vb1.getChildren().addAll(yesBtn, noBtn, cancelBtn);

        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(10, 10, 10, 10));
        vb2.getChildren().addAll(openBtn, saveBtn, closeBtn);

        // 为 VBox 设置内联样式
        vb1.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");
        vb2.setStyle("-fx-border-width: 4.0; -fx-border-color: blue;");

        HBox root = new HBox();
        root.setSpacing(20);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getChildren().addAll(vb1, vb2);

        // 为 HBox 设置内联样式
        root.setStyle("-fx-border-width: 10.0; -fx-border-color: navy;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Inline Styles");
        stage.show();
    }
}
```

![[Pasted image 20230619175302.png]]
