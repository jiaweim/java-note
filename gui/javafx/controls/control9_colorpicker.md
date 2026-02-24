# ColorPicker

2023-07-24, 14:25
*** *
## 1. 简介

`ColorPicker` 类似于 `ComboBox`，提供从标准调色板中选择颜色的功能，也可以使用内置颜色对话框创建颜色。`ColorPicker` 继承自 `ComboBoxBase<Color>`，因此也继承了 `ComboBoxBase` 的所有属性，详情可参考 `ComboBox` 部分。

`ColorPicker` 由三部分组成：

- `ColorPicker` 组件
- `Color` 面板
- 自定义颜色对话框

`ColorPicker` 组件的视图如所示：

![ColorPicker|300](2020-05-26-10-53-07.png)

- Color indicator 是一个矩形，显示当前选择的颜色
- Color label 为颜色的文本表示
    - 对标准颜色，显示名称
    - 否则显示颜色的 hex 值
- 点击 Arrow button 可以打开调色板

调色板如下所示：

![Color palette|350](2020-05-26-10-59-09.png)

调色板也是由三部分组成：

- 调色板主区域显示标准颜色
- 下方显示自定义颜色列表
- 打开自定义颜色对话框的链接

color palette 区域显示预定义的标准颜色。点击其中一个颜色，pop-up 关闭，点击的颜色被设置为 ColorPicker 的值。

custom color 区域显示一组自定义颜色。第一次打开 pop-up 时，没有 custom color 区域。

点击 Custom Color 链接打开 custom color 对话框，如下图所示：

![|450](Pasted%20image%2020230724141551.png)

可以使用 HSB, RGB 或 Web 格式定义颜色。也可以从左侧的 color area 或 color 垂直条选择颜色。点击 Save 按钮将自定义颜色。

## 2. 使用 ColorPicker

`ColorPicker` 包含两个构造函数，一个可以指定初始颜色，一个使用默认白色：

```java
// 默认白色
ColorPicker bgColor1 = new ColorPicker();

// 指定初始颜色
ColorPicker bgColor2 = new ColorPicker(Color.RED);
```

`value` 属性保存选择的颜色，如在面板中选择的颜色，也可以直接用代码设置。

```java
ColorPicker bgColor = new ColorPicker();
...
// Get the selected color
Color selectedCOlor = bgColor.getValue();

// Set the ColorPicker color to yellow
bgColor.setValue(Color.YELLOW);
```

`ColorPicker` 的 `getCustomColors()` 方法返回自定义颜色列表。需要注意，自定义颜色只对当前 `ColorPicker` 的当前会话。使用：

```java
ColorPicker bgColor = new ColorPicker();
...
// 添加自定义颜色
bgColor.getCustomColors().addAll(Color.web("#07FF78"), Color.web("#C2F3A7"));

...
// 获取自定义颜色
ObservableList<Color> customColors = bgColor.getCustomColors();
```

选择颜色后，会触发 `ActionEvent` 事件。例如，添加 ActionEvent handler，将选择的颜色设置为 Rectangle 的填充颜色：

```java
ColorPicker bgColor = new ColorPicker();
Rectangle rect = new Rectangle(0, 0, 100, 50);

// Set the selected color in the ColorPicker as the fill color of the Rectangle
bgColor.setOnAction(e -> rect.setFill(bgColor.getValue()));
```

**示例：** ColorPicker

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ColorPickerTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ColorPicker bgColor = new ColorPicker(Color.RED);

        // A Rectangle to show the selected color from the color picker
        Rectangle rect = new Rectangle(0, 0, 100, 50);
        rect.setFill(bgColor.getValue());
        rect.setStyle("-fx-stroke-width: 2; -fx-stroke: black;");

        // Add an ActionEvent handler to the ColorPicker, so we change
        // the fill color for the rectangle when we pick a new color
        bgColor.setOnAction(e -> rect.setFill(bgColor.getValue()));

        HBox root = new HBox(new Label("Color:"), bgColor, rect);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ColorPicker Controls");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230724142118.png)

## 3. 样式

`ColorPicker` 支持三种样式：

- combo-box
- button
- split-button

![looks|400](2020-05-26-12-14-51.png)

`ColorPicker` 包含两个字符串，对应 button 和 split-button 的 CSS 样式类名：

- `STYLE_CLASS_BUTTON`
- `STYLE_CLASS_SPLIT_BUTTON`

如果需要修改 `ColorPicker` 的样式，需要添加对应的样式类：

```java
// Use default combo-box look
ColorPicker cp = new ColorPicker(Color.RED);

// Change the look to button
cp.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);

// Change the look to split-button
cp.getStyleClass().add(ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
```

```ad-note
如果同时添加了 `STYLE_CLASS_BUTTON` 和 `STYLE_CLASS_SPLIT_BUTTON`，则自动选择 `STYLE_CLASS_BUTTON`。
```

## 4. CSS

`ColorPicker` 的默认 CSS 样式类名为 `color-picker`。通过 CSS 几乎可以自定义 `ColorPicker` 的所有部分的样式。

`-fx-color-label-visible` 用于设置 color label 是否可见，默认为 true。可以将其设置为不可见：

```css
.color-picker {
    -fx-color-label-visible: false;
}
```

color indicator 是一个矩形，其样式类名为 `picker-color-rect`。

color label 是一个 `Label`，其样式类名为 `color-picker-label`。

下面将 color label 设置为蓝色，并在 color indicator 添加了 2px 的黑色边框：

```css
.color-picker .color-picker-label {
    -fx-text-fill: blue;
}

.color-picker .picker-color .picker-color-rect {
    -fx-stroke: black;
    -fx-stroke-width: 2;
}
```

color palette 的样式类名为 `color-palette`，下面隐藏 color palette 的 hyperlink：

```css
.color-palette .hyperlink {
    visibility: hidden;
}
```
