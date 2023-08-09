# Slider

2023-07-25, 14:33
****
## 1. 简介

Slider 允许用户通过在 track (滑轨) 拖动 thumb (旋钮)选择值。Slider 可以为水平或垂直。下图是水平 slider:

![|400](Pasted%20image%2020230725135112.png)

slider 有最小值和最大值，thumb 指向当前值。拖动 thumb 修改当前值。

Slider 默认构造函数的最小值、最大值和当前值分别为 0, 100, 0，水平方向：

```java
// Create a horizontal slider
Slider s1 = new Slider();
```

指定最小值、最大值和当前值：

```java
// Create a horizontal slider with the specified min, max, and value
double min = 0.0;
double max = 200.0;
double value = 50.0;
Slider s2 = new Slider(min, max, value);
```

## 2. 属性

Slider 包含许多属性。

orientation 属性指定 slider 方向：

```java
// Create a vertical slider
Slider vs = new Slider();
vs.setOrientation(Orientation.VERTICAL);
```

下面的属性与当前值和范围相关：

- min
- max
- value
- valueChanging
- snapToTicks

min, max 和 value 属性为 double 类型，表示最小值、最大值和当前值。slider 的当前值可以通过拖动 thumb 修改，或者使用 setValue() 方法。例如：

```java
Slider scoreSlider = new Slider();
scoreSlider.setMin(0.0);
scoreSlider.setMax(10.0);
scoreSlider.setValue(3.0);
```

在 slider 的 value 属性发生变化时，通常需要执行操作。需要为 value 属性添加 ChangeListener。

**示例：** 为 scoreSlider 控件添加 ChangeListener，当 value 属性发生变化输出新旧值

```java
scoreSlider.valueProperty().addListener(
    (ObservableValue<? extends Number> prop, Number oldVal, Number newVal) -> {
        System.out.println("Changed from " + oldVal + " to " + newVal);
    }
);
```

`valueChanging` 属性为 boolean 类型。用户按下 thumb 时为 true。当用户拖动 thumb，value 属性不断变化，valueChanging 属性为 true。该属性可以帮助用户避免重复执行操作，值变化时只执行一次操作。

`snapToTicks` 属性为 boolean 类型，默认 false。它指定 slider 的 value 属性是否总是与刻度线（tick marks）对齐。如果为 false，value 可以时 min 到 max 之间的任意值。

在 ChangeListener 中使用 valueChanging 属性要小心。用户眼中的一次更改可能触发多次 listeners。我们希望 `valueChanging` 从 true 变为 false 时再触发 `ChangeListener`，定义该逻辑：

```java
if (scoreSlider.isValueChanging()) {
    // Do not perform any action as the value changes
} else {
    // Perform the action as the value has been changed
}
```

当 `snapToTicks` 属性为 true 时，上面的逻辑才没问题。即只有 snapToTicks 为 true 时，valueChanging 属性从 true 变为 false，才会触发 value 的 ChangeListener。即上面的逻辑只有 snapToTicks=true 时才有效。

Slider 的下列属性定义 tick spacing:

- majorTickUnit
- minorTickCount
- blockIncrement

majorTickUnit 属性为 double 类型。指定 major tick 的间距，默认 25.

minorTickCount 属性为 integer 类型，指定两个 major tick 之间的 minor tick 数，默认 3.

可以使用方向键修饰 thumb 位置。blockIncrement 属性为 double 类型，指定键盘一次操作 thumb 移动的距离，默认为 10.

下面 2 个属性指定是否显示 tickMarks 和 tickLabels，默认为 false：

- showTickMarks
- showTickLabels

labelFormatter 属性为 `StringConverter<Double>` 类型，默认为 null。slider 使用默认 StringConverter 显示 major tick 的数值，即使用 major ticks 数值的 toString() 结果。

**示例：** 创建 slider，使用自定义 major tick labels

```java
Slider scoreSlider = new Slider();
scoreSlider.setShowTickLabels(true);
scoreSlider.setShowTickMarks(true);
scoreSlider.setMajorTickUnit(10);
scoreSlider.setMinorTickCount(3);
scoreSlider.setBlockIncrement(20);
scoreSlider.setSnapToTicks(true);

// Set a custom major tick formatter
scoreSlider.setLabelFormatter(new StringConverter<Double>() {
    @Override
    public String toString(Double value) {
        String label = "";
        if (value == 40) {
            label = "F";
        } else if (value == 70) {
            label = "C";
        } else if (value == 80) {
            label = "B";
        } else if (value == 90) {
            label = "A";
        }
        return label;
    }
        
    @Override
    public Double fromString(String string) {
        return null; // Not used
    }
});
```

![|500](Pasted%20image%2020230725141956.png)

**示例：** Slider

- 添加 1 个 Rectangle, 1 个 Label 和 3 个 Sliders
- 3 个 sliders 表示颜色的 RGB 组分
- 为 Sliders 添加 ChangeListener
- 修改 slider 值，计算新的颜色，设置为 Rectangel 的 fill

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SliderTest extends Application {

    Rectangle rect = new Rectangle(0, 0, 200, 50);
    Slider redSlider = getSlider();
    Slider greenSlider = getSlider();
    Slider blueSlider = getSlider();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add a ChangeListener to all sliders
        redSlider.valueProperty().addListener(this::changed);
        greenSlider.valueProperty().addListener(this::changed);
        blueSlider.valueProperty().addListener(this::changed);

        GridPane root = new GridPane();
        root.setVgap(10);
        root.add(rect, 0, 0, 2, 1);
        root.add(new Label("Use sliders to change the fill color"), 0, 1, 2, 1);
        root.addRow(2, new Label("Red:"), redSlider);
        root.addRow(3, new Label("Green:"), greenSlider);
        root.addRow(4, new Label("Blue:"), blueSlider);

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Slider Controls");
        stage.show();

        // Adjust the fill color of the rectangle
        changeColor();
    }

    public Slider getSlider() {
        Slider slider = new Slider(0, 255, 125);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(85);
        slider.setMinorTickCount(10);
        slider.setBlockIncrement(20);
        slider.setSnapToTicks(true);
        return slider;
    }

    // A change listener to track the change in color
    public void changed(ObservableValue<? extends Number> prop,
                        Number oldValue,
                        Number newValue) {
        changeColor();
    }

    public void changeColor() {
        int r = (int) redSlider.getValue();
        int g = (int) greenSlider.getValue();
        int b = (int) blueSlider.getValue();
        Color fillColor = Color.rgb(r, g, b);
        rect.setFill(fillColor);
    }
}
```

![|300](Pasted%20image%2020230725142236.png)

## 3. Slider CSS

Slider 的 CSS 样式类名默认为 slider。

Slider 包含如下 CSS 属性：

- -fx-orientation
- -fx-show-tick-labels
- -fx-show-tick-marks
- -fx-major-tick-unit
- -fx-minor-tick-count
- -fx-snap-to-ticks
- -fx-block-increment

Slider 支持 horizontal 和 vertical CSS pseudo-classes 分别应用于水平和垂直 sliders。

Slider 包含 3 个子结构：

- axis
- track
- thumb

axis 子结构为 NumberAxis 类型，显示 tick marks 和 tick labels。

**示例：** tick label color 设置为 blue，major tick length=15px，minor tick length=5px，major tick color=red, minor tick color=green

```css
.slider > .axis {
    -fx-tick-label-fill: blue;
    -fx-tick-length: 15px;
    -fx-minor-tick-length: 5px
}

.slider > .axis > .axis-tick-mark {
    -fx-stroke: red;
}

.slider > .axis > .axis-minor-tick-mark {
    -fx-stroke: green;
}
```

track 子结构为 StackPane 类型。

**示例：** 将 track 的 background color 设置为 red

```css
.slider > .track {
    -fx-background-color: red;
}
```

thumb 子结构为 StackPane 类型。thumb 看上去为圆形，因为其 background 为 radius。如果移除背景，看上去就是矩形。

**示例：** 删除 thumb 背景

```css
.slider .thumb {
    -fx-background-radius: 0;
}
```

**示例：** 将 thumb 子结构的 background 设置为 image

```css
.slider .thumb {
    -fx-background-image: url("thumb.jpg");
}
```

使用 `-fx-shape` 属性可以将 thumb 设置类任何形状。

**示例：** 将 thumb 设置为三角形

```css
/* An inverted triangle */
.slider > .thumb {
    -fx-shape: "M0, 0L10, 0L5, 10 Z";
}

/* A triangle pointing to the right, only if orientation is vertical */
.slider:vertical > .thumb {
    -fx-shape: "M0, 0L10, 5L0, 10 Z";
}
```

![|200](Pasted%20image%2020230725143232.png)

**示例：** 将 thumb 设置为矩形+三角形的样式

```css
/* An inverted triangle below a rectangle*/
.slider > .thumb {
    -fx-shape: "M0, 0L10, 0L10, 5L5, 10L0, 5 Z";
}

/* A triangle pointing to the right by the right side of a rectangle */
.slider:vertical > .thumb {
    -fx-shape: "M0, 0L5, 0L10, 5L5, 10L0, 10 Z";
}
```

![|200](Pasted%20image%2020230725143324.png)