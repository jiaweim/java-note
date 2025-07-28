# JavaFX CSS

2023-08-14, 11:13
modify: 样式
2023-07-27, 10:01
add: 样式表资源和示例
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

可以为 JavaFX 程序添加多个样式表。`Scene` 和 `Parent` 对象都支持添加样式表。两者都通过 `getStylesheets()` 返回保存样式表的 `ObservableList` 的引用。例如：

```java
// 为 scene 添加两个样式表：ss1.css 和 ss2.css
Scene scene = ...
scene.getStylesheets().addAll("file://.../resources/css/ss1.css", "file://.../resources/css/ss2.css");

// 为 VBox (Parent 子类) 添加样式表 vbox.css
VBox root = new VBox();
root.getStylesheets().add("file://.../vbox.css");
```

将 "..." 替换为正确路径即可。

```ad-important
创建完整的 laf 需要成百上千的代码，以便为 JavaFX 的每个 UI 控件设置样式。因此，最好从默认 laf 开始，然后用 `getStylesheets().add()` 覆盖样式。
```

模板代码：

```java
Application.setUserAgentStylesheet(null); // defaults to Modena

// apply custom look and feel to the scene. 
scene.getStylesheets()
     .add(getClass().getResource("my_cool_skin.css").toExternalForm());
```

## 3. 默认样式表

`Application` 类定义了两个字符串常量 `STYLESHEET_CASPIAN` 和 `STYLESHEET_MODENA`，对应两种主题：

- JavaFX 的默认样式由样式表 `modena.css` 定义，该文件位于 javafx.controls.jar 的 "com/sun/javafx/scene/control/skin/modena/modena.css"
- 在 JavaFX 8 之前，默认样式为 Caspian，定义在 jfxrt.jar 文件的 "com/sun/javafx/scene/control/skin/caspian/caspian.css"

通过 `Application` 设置整个应用的默认样式：

```java
public static void setUserAgentStylesheet(String url)
public static String getUserAgentStylesheet()
```

**示例：** 将默认样式设置为 Caspian:

```java
Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
```

## 4. 内联样式

设置 `Scene` 中 `Node` 的样式有两种方式：

- 样式表
- 内联样式

前面介绍了样式表，下面说明内联样式。

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

```java{.line-numbers}
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

@import "images/Pasted image 20230619175302.png" {width="200px" title=""}

## 5. 样式表资源

### 5.1 Native laf

Claudine Zillmann 创建了 native Mac OS X 风格的 skin [AquaFX](http://aquafx-project.com/)。

![](images/Pasted%20image%2020230726200740.png)

Pedro Duque Vieira 创建了 Windows Metro 风格的样式 [JMetro](https://pixelduke.com/java-javafx-theme-jmetro/)。

JMetro 下载地址： https://github.com/JFXtras/jfxtras-styles 

JMetro 提供了浅色和深色两个主题。例如，下面是 `CheckBox` 的浅色和深色主题：

@import "images/Pasted%20image%2020230726201620.png" {width="250px" title=""}

@import "images/Pasted%20image%2020230726201628.png" {width="250px" title=""}

### 5.2 Web laf

目前最流行的 Web UI 样式 (laf) 是 Google 的 Material Design  和 Twitter 的 Bootstrap。

Material Design (Google)

|项目|网址|
|---|---|
|GluonHQ|https://gluonhq.com/products/mobile/|
|JFoenix|https://github.com/sshahine/JFoenix|
|MaterialFX|https://github.com/palexdev/MaterialFX|

Bootstrap (Twitter)

|项目|网址|
|---|---|
|jbootx|https://github.com/dicolar/jbootx|
|BootstrapFX|https://github.com/kordamp/bootstrapfx|

## 6. 示例

```java{.line-numbers}
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LookNFeelChooser extends Application {

    @Override
    public void init() {
        Font.loadFont(LookNFeelChooser.class
                .getResourceAsStream("Roboto-Thin.ttf"), 10);
        Font.loadFont(LookNFeelChooser.class
                .getResourceAsStream("Roboto-Light.ttf"), 10);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        BorderPane root = new BorderPane();
        // 加载 FXML 文件，作为 root 的 center
        Parent content = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("lnf_demo.fxml")));
        Scene scene = new Scene(root, 650, 550, Color.WHITE);
        root.setCenter(content);

        // 创建菜单
        MenuBar menuBar = new MenuBar();

        // file menu
        Menu fileMenu = new Menu("_File");
        MenuItem exitItem = new MenuItem("Exit");
        // 加个快捷键
        exitItem.setAccelerator(new KeyCodeCombination(
                                KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        exitItem.setOnAction(ae -> Platform.exit());

        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);

        // Look and feel menu
        Menu lookNFeelMenu = new Menu("_Look 'N' Feel");
        lookNFeelMenu.setMnemonicParsing(true);
        menuBar.getMenus().add(lookNFeelMenu);
        root.setTop(menuBar);

        // Look and feel selection
        MenuItem caspianMenuItem = new MenuItem("Caspian");
        caspianMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(STYLESHEET_CASPIAN);
        });
        MenuItem modenaMenuItem = new MenuItem("Modena");
        modenaMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(STYLESHEET_MODENA);
        });
        MenuItem style1MenuItem = new MenuItem("Control Style 1");
        style1MenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                    .add(getClass().getResource("controlStyle1.css").toExternalForm());
        });
        MenuItem style2MenuItem = new MenuItem("Control Style 2");
        style2MenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                    .add(getClass().getResource("controlStyle2.css").toExternalForm());
        });

        MenuItem skyMenuItem = new MenuItem("Sky LnF");
        skyMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null);
            scene.getStylesheets()
                    .add(getClass().getResource("sky.css").toExternalForm());
        });

        MenuItem flatRedMenuItem = new MenuItem("FlatRed");
        flatRedMenuItem.setOnAction(ae -> {
            scene.getStylesheets().clear();
            setUserAgentStylesheet(null); // 重置为默认 theme，即 modena.css
            scene.getStylesheets()
                    .add(getClass().getResource("flatred.css").toExternalForm());
        });

        lookNFeelMenu.getItems()
                .addAll(caspianMenuItem,
                        modenaMenuItem,
                        style1MenuItem,
                        style2MenuItem,
                        skyMenuItem,
                        flatRedMenuItem);

        primaryStage.setTitle("Look N Feel Chooser");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

@import "images/Pasted%20image%2020230727085645.png" {width="600px" title=""}

## 7. StyleableProperty

`StyleableProperty<T>` 允许通过 CSS 设置 javafx.beans.property 的样式。当控制需要可以通过 CSS 设置的属性时非常有用。

`StyleableProperty` 按照**优先级从低到高**依次设置样式：

1. `javafx.application.Application.setUserAgentStylesheet(String)` 设置的默认 样式表
2. 通过代码设置的样式，如 `javafx.scene.Node.setOpacity(double)`
3. 用户自定义样式表的样式，包括 `javafx.scene.Scene.getStylesheets()` 和 `javafx.scene.Parent.getStylesheets()`
4. 通过 `javafx.scene.Node.setStyle(String`) 设置的样式

### CssMetaData

`CssMetaData` 提供 CSS 样式信息，并为 CSS 提供了设置属性值的函数。它封装了属性名、CSS 值转换的类型和属性的默认值。

`CssMetaData` 在 css 文件和 `StyleableProperty` 之间起到桥接作用。CssMetaData 和 StyleableProperty 之间一一对应。

通常，一个 `Node` 的 `CssMetaData` 会包含它 ancestors 的 CssMetaData。例如，`Rectangle` 的 `CssMetaData` 包含 `Shape` 和 `Node` 的 `CssMetaData`。在 CSS 处理过程中，CSS 引擎遍历 `Node` 的 `CssMetaData`，查找每个属性解析后的值，并为 `StyleableProperty` 设置值。

`Node.getCssMetaData()` 返回 `List<CssMetaData>`。该方法经常被调用，返回一个 static list，而不是每次调用重新创建。按照约定，包含 `CssMetaData` 的控件类会实现一个 static 方法 `getClassCssMetaData()`，而 `getCssMetaData()` 直接返回 `getClassCssMetaData()`。`getClassCssMetaData()` 是为了**方便子类包含其父类的 `CssMetaData`**。

**示例：** 典型实现

```java{.line-numbers}
// StyleableProperty
private final StyleableProperty<Color> color =
    new SimpleStyleableObjectProperty<>(COLOR, this, "color");

// Typical JavaFX property implementation
public Color getColor() {
    return this.color.getValue();
}
public void setColor(final Color color) {
    this.color.setValue(color);
}
public ObjectProperty<Color> colorProperty() {
    return (ObjectProperty<Color>) this.color;
}

// CssMetaData
private static final CssMetaData<MY_CTRL, Paint> COLOR =
    new CssMetaData<MY_CTRL, Paint>("-color", PaintConverter.getInstance(), Color.RED) {

    @Override
    public boolean isSettable(MY_CTRL node) {
        return node.color == null || !node.color.isBound();
    }

    @Override
    public StyleableProperty<Paint> getStyleableProperty(MY_CTRL node) {
        return node.color;
    }
};

private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
    // Fetch CssMetaData from its ancestors
    final List<CssMetaData<? extends Styleable, ?>> styleables =
        new ArrayList<>(Control.getClassCssMetaData());
    // Add new CssMetaData
    styleables.add(COLOR);
    STYLEABLES = Collections.unmodifiableList(styleables);
}

// Return all CssMetadata information
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return STYLEABLES;
}

@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
    return getClassCssMetaData();
}
```

### StyleablePropertyFactory

创建 StyleableProperty 和 CssMetaData 需要很多样本代码。`StyleablePropertyFactory`  极大简化了 `StyleableProperty` 及其对应的 CssMetaData 的创建。

```java
// StyleableProperty
private final StyleableProperty<Color> color =
    new SimpleStyleableObjectProperty<>(COLOR, this, "color");

// Typical JavaFX property implementation
public Color getColor() {
    return this.color.getValue();
}
public void setColor(final Color color) {
    this.color.setValue(color);
}
public ObjectProperty<Color> colorProperty() {
    return (ObjectProperty<Color>) this.color;
}

// StyleablePropertyFactory
private static final StyleablePropertyFactory<MY_CTRL> FACTORY =
    new StyleablePropertyFactory<>(Control.getClassCssMetaData());

// CssMetaData from StyleablePropertyFactory
private static final CssMetaData<MY_CTRL, Color> COLOR =
    FACTORY.createColorCssMetaData("-color", s -> s.color, Color.RED, false);

// Return all CssMetadata information from StyleablePropertyFactory
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return FACTORY.getCssMetaData();
}

@Override public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
    return getClassCssMetaData();
}
```

`StyleableProperty` 的类图如下：

@import "images/Pasted%20image%2020230807164011.png" {width="px" title=""}

为了在控件上使用 `StyleableProperty`，需要使用 `StyleableProperty` 创建新的 `CssMetaData`。为控件创建的 `CssMetaData` 需要添加从父类获取的 `List<CssMetaData>`。getControlCssMetaData() 返回该 list。

