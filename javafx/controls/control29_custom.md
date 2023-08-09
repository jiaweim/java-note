# 自定义控件

## 简介

当现有控件无法满足需求，就需要自定义实现新的控件。

JavaFX 提供了多种创建自定义控件的方法：

- 使用 CSS 重新定义控件样式
- 组合已有控件
- 扩展已有控件
- 使用 `Control`+Skin 类
- 使用 `Region` 类
- 使用 `Canvas` 类

## 使用 CSS 重新定义控件样式

例如，我们想将 `CheckBox` 的样式更改为 MaterialDesign 类型。

因为我们只想重新设计 `CheckBox`，所以需要查看 CheckBox 当前 CSS 样式。为此，需要查看 modena.css 文件，该文件包含 JavaFX 所有控件的默认 CSS 样式。在 openjfx GitHub 可以找到 [modena.css](https://raw.githubusercontent.com/openjdk/jfx/master/modules/javafx.controls/src/main/resources/com/sun/javafx/scene/control/skin/modena/modena.css) 文件。

在 modena.css 之前是 caspian.css 文件，两个文件结构不同：

- modena.css 进行了很多优化，但并不是为了增加可读性
- caspian.css 中的样式按照控件进行分组，因此很容易找到 CheckBox 的所有样式
- modena.css 中 CheckBox 的样式分散在不同位置

所以需要从 modena.css 文件中将 CheckBox 的相关样式收集起来，复制到一个单独的 css 文件，例如 restyled.css。该文件类似于：

```css
.check-box > .box {}
    -fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;
    -fx-background-insets: 0 0 -1 0, 0, 1, 2;
    -fx-background-radius: 3px, 3px, 2px, 1px;
    -fx-padding: 0.333333em 0.666667em 0.333333em 0.666667em;
    -fx-text-fill: -fx-text-base-color;
    -fx-alignment: CENTER;
    -fx-content-display: LEFT;
}
.check-box:hover > .box {
    -fx-color: -fx-hover-base;
}
.check-box:armed .box {
    -fx-color: -fx-pressed-base;
}
.check-box:focused > .box {
    -fx-background-color: -fx-focus-color, -fx-inner-border, -fx-body-color, -fx-faint-focus-color, -fx-body-color;
    -fx-background-insets: -0.2, 1, 2, -1.4, 2.6;
    -fx-background-radius: 3, 2, 1, 4, 1;
}
.check-box:disabled {
    -fx-opacity: 0.4;
}
.check-box:show-mnemonics > .mnemonic-underline {
    -fx-stroke: -fx-text-base-color;
}
.check-box:selected > .box > .mark,
.check-box:indeterminate  > .box > .mark {
    -fx-background-color: -fx-mark-highlight-color, -fx-mark-color;
    -fx-background-insets: 1 0 -1 0, 0;
}
.check-box {
    -fx-label-padding: 0.0em 0.0em 0.0em 0.416667em; /* 0 0 0 5 */
    -fx-text-fill: -fx-text-background-color;
}
.check-box > .box {
    -fx-background-radius: 3, 2, 1;
    -fx-padding: 0.166667em 0.166667em 0.25em 0.25em; /* 2 2 3 3 */
}
.check-box > .box > .mark {
    -fx-background-color: null;
    -fx-padding: 0.416667em 0.416667em 0.5em 0.5em; /* 5 5 6 6 */
    -fx-shape: "M-0.25,6.083c0.843-0.758,4.583,4.833,5.75,4.833S14.5-1.5,15.917-0.917c1.292,0.532-8.75,17.083-10.5,17.083C3,16.167-1.083,6.833-0.25,6.083z";
}
.check-box:indeterminate > .box {
    -fx-padding: 0;
}
.check-box:indeterminate  > .box > .mark {
    -fx-shape: "M0,0H10V2H0Z";
    -fx-scale-shape: false;
    -fx-padding: 0.666667em;
}
```

总的来说，JavaFX CSS 与 Web CSS 非常相似，除了基于 CSS 2.1  设计，所有属性包含 `-fx-` 前缀，支持遍历。

下面实现 [MaterialDesign](https://material.io/develop/web/components/input-controls/checkboxes#checkboxes "MaterialDesign") 样式的 `CheckBox`，需要对现有 CSS 样式进行一些修改：

![](Pasted%20image%2020230727124128.png)

- 取消渐变，使 UI 更扁平化
- 不同的 checkmark
- 勾选时填充背景
- 不同 focus indicator

取消渐变很容易，只需要定义一些颜色来代替渐变。为此，在 restyled.css 文件中定义如下遍历：

```css
.check-box {
    -material-design-color: #3f51b5;
    -material-design-color-transparent-12: #3f51b51f;
    -material-design-color-transparent-24: #3f51b53e;
    -material-design-color-transparent-40: #3f51b566;
    ...
}
```

这样就能在 CSS 文件的 `.check-box` 中使用 `-material-design-color`。

### 修改 checkmark

checkmark 是使用 `-fx-shape` 定义的 SVGPath。modena.css 中的定义：

```css
.check-box > .box > .mark {
    -fx-background-color: null;
    -fx-padding: 0.416667em 0.416667em 0.5em 0.5em; /* 5 5 6 6 */
    -fx-shape: "M-0.25,6.083c0.843-0.758,4.583,4.833,5.75,4.833S14.5-1.5,15.917-0.917c1.292,0.532-8.75,17.083-10.5,17.083C3,16.167-1.083,6.833-0.25,6.083z";
}
```

上面的 `-fx-shape` 定义了如下形状

![|40](Pasted%20image%2020230727142313.png)
现在自定义一个 SVGPath，替换原有的 -fx-shape:

```css
.check-box > .box > .mark {
    -fx-background-color: null;
    -fx-padding: 0.45em;
    -fx-scale-x: 1.1;
    -fx-scale-y: 0.8;
    -fx-shape: "M9.998,13.946L22.473,1.457L26.035,5.016L10.012,21.055L0.618,11.688L4.178,8.127L9.998,13.946Z";
}
```

![|40](Pasted%20image%2020230727142830.png)
上面还调整了 scale-x 和 scale-y 属性使 checkmark 和 CheckBox 更匹配。

### 修改 box

下面要取消 CheckBox 的渐变背景吗，即取消 modena.css 中定义的线性渐变，将背景设置为透明，并设置一个边框：

```css
.check-box > .box {
    -fx-background-color: transparent;
    -fx-background-insets: 0;
    -fx-border-color: #0000008a;
    -fx-border-width: 2px;
    -fx-border-radius: 2px;
    -fx-padding: 0.083333em; /* 1px */
    -fx-text-fill: -fx-text-base-color;
    -fx-alignment: CENTER;
    -fx-content-display: LEFT;
}
```

- 当勾选复选框时，填充 box

```css
.check-box:selected > .box {
    -fx-background-color: -material-design-color;
    -fx-background-radius: 2px;
    -fx-background-insets: 0;
    -fx-border-color: transparent;
}
```

这里简单地填充背景，并将 border 设置为透明。

### CheckBox 状态

接下来就是 CheckBox 在  hover, focused 和 selected 状态的样式。原始 MaterialDesign CheckBox 在 CheckBox 周围画一个圆。

使用 CSS 很容易实现该样式，在 focused 状态添加圆的 CSS：

```css
.check-box:focused > .box {
    -fx-background-color: #6161613e, transparent;
    -fx-background-insets: -14, 0;
    -fx-background-radius: 1024;
}
```

这个状态圆比 CheckBox 要大一点。为了实现该效果，这里定义了两个 background colors `-fx-background-color: #6161613e, transparent;`

## 扩展已有控件

## 扩展 Region

`Region` 是 `Control` 和 `Pane` 的父类，是一个 resizable 的 `Parent` node，可以通过 CSS 设置样式。扩展该类是实现自定义控件的最直观的方法。

`Region` 和 Control+Skin 的主要区别在于，基于 Region 的控件同时包含逻辑和 UI，而 Control+Skin 的逻辑和 UI 分离，逻辑在 Control 中，UI 在 Skin 中。

在 Mac 的每个窗口上有三个按钮（红、黄、绿），可用来关闭、最小化和缩放窗口。MacOS 的深色模式还不直接支持 JavaFX，如果在 MacOS 深色模式运行 JavaFX，JavaFX 依然为浅色模式。

所以，我们现在自定义一个 MacOS 深色模式的按钮。按钮样式如下：

![|100](Pasted%20image%2020230807144222.png)

所有我们需要创建一个显示彩色圆圈的控件，鼠标移动到它上面，会显示一个符号。

如果要**复现**现有控件，可以将控件的截图作为背景图片加载到矢量绘图程序，然后在截图上绘制副本。这样可以获得正确的尺寸、位置和颜色：

![|400](Pasted%20image%2020230807144526.png)

确定圆的大小、距离和颜色后，还需要添加符号。这比较耗时，因为我们必须手动画符号。

通常我们使用 JavaFX 提供的 :hover pseudo class 来显示/隐藏按钮上的符号，但这里需要自定义 :hover pseudo class，因为在 MacOS 中，当鼠标悬停在一个按钮上，所有按钮都会显示它们的符号。在代码中，我们将按钮放在 HBox 中，并添加一个 `MouseListener`，当鼠标悬停在 `HBox` ，触发显示符号。

该控件由两个元素组成，Circle 和 Region。Circle 继承自 Shape，没有 -fx-background-color 和 -fx-border-color，但有 -fx-fill 和  -fx-stroke。

该控件的一个好处是不需要调整它的大小，它的尺寸保持不变。

首先，定义变量：

```java
public class RegionControl extends Region {

    public enum Type { CLOSE, MINIMIZE, ZOOM }

    private static final double PREFERRED_WIDTH = 12;
    private static final double PREFERRED_HEIGHT = 12;
    
    private static final double MINIMUM_WIDTH = 12;
    private static final double MINIMUM_HEIGHT = 12;
    
    private static final double MAXIMUM_WIDTH = 12;
    private static final double MAXIMUM_HEIGHT = 12;
    
    private static final PseudoClass CLOSE_PSEUDO_CLASS = 
                                    PseudoClass.getPseudoClass("close");
    private static final PseudoClass MINIMIZE_PSEUDO_CLASS = 
                                    PseudoClass.getPseudoClass("minimize");
    private static final PseudoClass ZOOM_PSEUDO_CLASS = 
                                    PseudoClass.getPseudoClass("zoom");
    private static final PseudoClass HOVERED_PSEUDO_CLASS = 
                                    PseudoClass.getPseudoClass("hovered");
    private static final PseudoClass PRESSED_PSEUDO_CLASS = 
                                    PseudoClass.getPseudoClass("pressed");
    
    private BooleanProperty hovered;
    private static String userAgentStyleSheet;
    private ObjectProperty<Type> type;
    private double size;
    private double width;
    private double height;
    private Circle circle;
    private Region symbol;
    private Consumer<MouseEvent> mousePressedConsumer;
    private Consumer<MouseEvent> mouseReleasedConsumer;
```

这里为三种不同的状态定义了一个 enum 类，并为每种状态创建了一个 `PseudoClass`。并为 hovered 和 pressed 状态创建了 PseudoClass。

还提供了 hovered 和 type 属性，便于从外部设置这些属性。

因为该控件是一个按钮，所以还添加了鼠标按钮和释放的 Consumer，以便后续添加自定义 handler。

构造函数：

```java
public RegionControl() {
        this(Type.CLOSE);
}

public RegionControl(final Type type) {
    this.type = new ObjectPropertyBase<>(type) {
        @Override protected void invalidated() {
            switch(get()) {
                case CLOSE -> {
                    pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, true);
                    pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, false);
                    pseudoClassStateChanged(ZOOM_PSEUDO_CLASS, false);
                }
                case MINIMIZE -> {
                    pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, false);
                    pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, true);
                    pseudoClassStateChanged(ZOOM_PSEUDO_CLASS, false);
                }
                case ZOOM -> {
                    pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, false);
                    pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, false);
                    pseudoClassStateChanged(ZOOM_PSEUDO_CLASS, true);
                }
            }
        }
        @Override public Object getBean() { return RegionControl.this; }
        @Override public String getName() { return "type"; }
    };
    
    this.hovered = new BooleanPropertyBase() {
        @Override protected void invalidated() { pseudoClassStateChanged(HOVERED_PSEUDO_CLASS, get()); }
        @Override public Object getBean() { return RegionControl.this; }
        @Override public String getName() { return "hovered"; }
    };

    pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, Type.CLOSE == type);
    pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, Type.MINIMIZE == type);
    pseudoClassStateChanged(ZOOM_PSEUDO_CLASS, Type.ZOOM == type);

    initGraphics();
    registerListeners();
}
```

在构造函数中，在 type 属性的 invalidated() 方法中位 type 设置了 pseudo-classes。

为了确保正确初始化 type，在构造使用中使用给定的 type 参数调用 pseudoClassStateChanged()。

initGraphics 和 registerListeners 实现：

```java
private void initGraphics() {
    if (Double.compare(getPrefWidth(), 0.0) <= 0 
        || Double.compare(getPrefHeight(), 0.0) <= 0 
        || Double.compare(getWidth(), 0.0) <= 0 
        || Double.compare(getHeight(), 0.0) <= 0) {
        if (getPrefWidth() > 0 && getPrefHeight() > 0) {
            setPrefSize(getPrefWidth(), getPrefHeight());
        } else {
            setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        }
    }

    getStyleClass().add("region-based");

    circle = new Circle();
    circle.getStyleClass().add("circle");
    circle.setStrokeType(StrokeType.INSIDE);

    symbol = new Region();
    symbol.getStyleClass().add("symbol");

    getChildren().setAll(circle, symbol);
}

private void registerListeners() {
    widthProperty().addListener(o -> resize());
    heightProperty().addListener(o -> resize());
    
    addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
        pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, true);
        if (null == mousePressedConsumer) { return; }
        mousePressedConsumer.accept(e);
    });
    addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
        pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, false);
        if (null == mouseReleasedConsumer) { return; }
        mouseReleasedConsumer.accept(e);
    });
}
```

在 initGraphics 中添加了 Circle 和 Region，并分别设置了样式类名。

在 registerListeners 中添加了常见的 size listeners。

## 使用 Canvas

为了理解 Canvas 及其优点，可以参考 [JavaFX 的渲染机制](../scene/scene2_rendering_mode.md)。

在 Canvas 上的


## 参考

- https://foojay.io/today/custom-controls-in-javafx-part-i/