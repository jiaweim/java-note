# Pane

2023-08-10, 09:06
add: Region vs. Pane, Clipping
2023-07-07, 14:57
@author Jiawei Mao
****
## 1. 简介

`Pane` 是 `Region` 的子类，它公开了 `Parent` 的 `getChildren()` 方法。所以 `Pane` 及其子类可以添加子节点。

@import "images/Pasted%20image%2020230707143803.png" {width="300px" title=""}

`Pane` 为绝对位置布局，其特征为：

- 所有子节点默认位置在 (0, 0)，需要显式设置子节点位置。
- 将 resizable 子节点的尺寸设置为 preferred sizes

一般只在放置图表或图片时使用，其他情况均推荐使用布局管理器。

`Pane` 包含三个 size：

| |width|height|
|---|---|---|
|minimum|left + right insets|top + bottom insets|
|preferred|以 preferred width 显示所有子节点所需宽度|以 preferred heigth 显示所有子节点所需高度|
|maximum|`Double.MAX_VALUE`|`Double.MAX_VALUE`|

## 2. Region vs. Pane

`Region` 和 `Pane` 都用于将 resizable child nodes 调整到它们 prefSize。不调整 nodes 位置：

- `Region` 是 `Pane` 的超类
- `Region` 不允许通过 public API 操作 child nodes，而 `Pane` 支持该功能
- `Region.getChildren()` 为 `protected`，而 `Pane.getChildren()` 为 `public`
- `Region` 是为自定义控件准备的

## 3. Clipping

大多数 JavaFX layoutPanes 自动定位和设置 children 尺寸，因此裁剪任何可能超出容器边界的 child 内容不是问题。`Pane` 是个例外，它是 `Region` 的直接子类，也是所有其它 layoutPane 的基类。与其它 layoutPane 不同的是，Pane 不对 children 作额外安排，直接接受它们的大小和位置。

该性质使得 `Pane` 适合用作绘图面板，类似 `Canvas`，但是渲染的是用户自定义的 `Shape` 元素，而非直接绘图命令。问题是，绘图面板通常需要自动在边界外裁剪其内容，Canvas 默认支持，但 Pane 不是。

Pane 默认不裁剪其内容，因此 children 的 bounds 可能超出 Pane 的范围。为了正确使用 Pane 作为绘图面板，需要手动裁剪内容。

下面通过一个示例来解释 Pane 的裁剪行为：

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class PaneDemo extends Application {

    static final double BORDER_RADIUS = 4;

    static Border createBorder() {
        return new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                        new CornerRadii(BORDER_RADIUS), BorderStroke.THICK));
    }

    static Shape createShape() {
        final Ellipse shape = new Ellipse(50, 50);
        shape.setCenterX(80);
        shape.setCenterY(80);
        shape.setFill(Color.LIGHTCORAL);
        shape.setStroke(Color.LIGHTCORAL);
        return shape;
    }

    static Region createDefault() {
        final Pane pane = new Pane(createShape());
        pane.setBorder(createBorder());
        pane.setPrefSize(100, 100);
        return pane;
    }

    /**
     * Clips the children of the specified {@link Region} to its current size.
     * This requires attaching a change listener to the region’s layout bounds,
     * as JavaFX does not currently provide any built-in way to clip children.
     *
     * @param region the {@link Region} whose children to clip
     * @param arc    the {@link Rectangle#arcWidth} and {@link Rectangle#arcHeight}
     *               of the clipping {@link Rectangle}
     * @throws NullPointerException if {@code region} is {@code null}
     */
    public static void clipChildren(Region region, double arc) {

        final Rectangle outputClip = new Rectangle();
        outputClip.setArcWidth(arc);
        outputClip.setArcHeight(arc);
        region.setClip(outputClip);

        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
    }

    static Region createClipped() {
        final Pane pane = new Pane(createShape());
        pane.setBorder(createBorder());
        pane.setPrefSize(100, 100);

        // clipped children still overwrite Border!
        clipChildren(pane, 3 * BORDER_RADIUS);

        return pane;
    }

    static Region createNested() {
        // create drawing Pane without Border or size
        final Pane pane = new Pane(createShape());
        clipChildren(pane, BORDER_RADIUS);

        // create sized enclosing Region with Border
        final Region container = new StackPane(pane);
        container.setBorder(createBorder());
        container.setPrefSize(100, 100);
        return container;
    }

    /**
     * Starts the {@link PaneDemo} application.
     *
     * @param primaryStage the primary {@link Stage} for the application
     */
    @Override
    public void start(Stage primaryStage) {

        final Button btnDefault = new Button("_Default");
        btnDefault.setPrefWidth(60);
        final Button btnClipped = new Button("_Clipped");
        btnClipped.setPrefWidth(60);
        final Button btnNested = new Button("_Nested");
        btnNested.setPrefWidth(60);

        final VBox buttons = new VBox(btnDefault, btnClipped, btnNested);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(16));
        buttons.setSpacing(8);

        final StackPane output = new StackPane(createDefault());
        output.setPadding(new Insets(8, 40, 40, 8));

        btnDefault.setOnAction(t -> output.getChildren().set(0, createDefault()));
        btnClipped.setOnAction(t -> output.getChildren().set(0, createClipped()));
        btnNested.setOnAction(t -> output.getChildren().set(0, createNested()));

        final Scene scene = new Scene(new HBox(buttons, output));
        HBox.setHgrow(output, Priority.ALWAYS);

        primaryStage.setTitle("Pane Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

### 3.1. 默认行为

运行上面的示例，如下所示：    

@import "images/2023-08-10-16-26-05.png" {width="250px" title=""}

将 Ellipse 放在 Pane 中：

- Ellipse 半径为 50
- 位置为 (80, 80)
- Pane 的 prefSize 为 (100, 100)，所以装不下 Ellipse
- 为 Pane 设置了边框，方便查看
- 将 Pane 的 size 与 Window 绑定

可以发现，Ellipse 覆盖了 parent 的 Border，并向外突出。下面是生成默认视图的代码：

```java{.line-numbers}
static final double BORDER_RADIUS = 4;

static Border createBorder() {
    return new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                    new CornerRadii(BORDER_RADIUS), BorderStroke.THICK));
}

static Shape createShape() {
    final Ellipse shape = new Ellipse(50, 50);
    shape.setCenterX(80);
    shape.setCenterY(80);
    shape.setFill(Color.LIGHTCORAL);
    shape.setStroke(Color.LIGHTCORAL);
    return shape;
}

static Region createDefault() {
    final Pane pane = new Pane(createShape());
    pane.setBorder(createBorder());
    pane.setPrefSize(100, 100);
    return pane;
}
```

### 3.2. 简单裁剪

Region 并没有提供自动裁剪子元素到当前大小的选项。需要使用 Node 定义的 clipProperty，并根据 layoutBounds 的变化手动更新。clipChildren 演示如何使用：

```java{.line-numbers}
/**
 * Clips the children of the specified {@link Region} to its current size.
 * This requires attaching a change listener to the region’s layout bounds,
 * as JavaFX does not currently provide any built-in way to clip children.
 *
 * @param region the {@link Region} whose children to clip
 * @param arc    the {@link Rectangle#arcWidth} and {@link Rectangle#arcHeight}
 *               of the clipping {@link Rectangle}
 * @throws NullPointerException if {@code region} is {@code null}
 */
public static void clipChildren(Region region, double arc) {
    final Rectangle outputClip = new Rectangle();
    outputClip.setArcWidth(arc);
    outputClip.setArcHeight(arc);
    region.setClip(outputClip);

    region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
        outputClip.setWidth(newValue.getWidth());
        outputClip.setHeight(newValue.getHeight());
    });
}

static Region createClipped() {
    final Pane pane = new Pane(createShape());
    pane.setBorder(createBorder());
    pane.setPrefSize(100, 100);

    // clipped children still overwrite Border!
    clipChildren(pane, 3 * BORDER_RADIUS);

    return pane;
}
```

@import "images/2023-08-10-17-01-25.png" {width="250px" title=""}

这看起来好一些，Ellipse 不再突出到 Pane 外面，但是仍然覆盖了它的 Border。另外，我们必须手动为裁剪矩形估计一个圆角，以反应边界的圆角，这里估计为 `3 * BORDER_RADIUS`，因为 Border 指定的 corner 半径是它的内部半径，而外部半径会更大，大多少取决于 Border 的厚度。（如果需要，可以精确计算外径，这里跳过这一步）

### 3.3. 嵌套 Pane

在指定裁剪区时能够排除可见的 Border 区域？肯定不是 Pane 自身，因为 clipping 区域影响 Border，如果直接缩小 clipping 区域，那么 Border 直接不可见。因此，为了实现该功能，创建两个嵌套的 Pane：

- 内部 Pane 不设置 Border，可以精确剪切到 bounds
- 外部用 StackPane 定义 Border，同时可以调整 Pane 大小
- 即将 Border 通过嵌套的 StackPane 实现

```java
static Region createNested() {
    // create drawing Pane without Border or size
    final Pane pane = new Pane(createShape());
    clipChildren(pane, BORDER_RADIUS);

    // create sized enclosing Region with Border
    final Region container = new StackPane(pane);
    container.setBorder(createBorder());
    container.setPrefSize(100, 100);
    return container;
}
```

@import "images/2023-08-10-17-11-49.png" {width="250px" title=""}

也不再需要估计裁剪矩形的圆角半径。

## 4. 示例

创建 `Pane`，添加 2 个 `Button`。

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");
        okBtn.relocate(10, 10);
        cancelBtn.relocate(60, 10);

        Pane root = new Pane();
        root.getChildren().addAll(okBtn, cancelBtn);
        root.setStyle("-fx-border-style: solid inside;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: red;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Panes");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230707145114.png" {width="120px" title=""}

- 设置 `Pane` 的 prefSize

```java
Pane root = new Pane();
root.setPrefSize(300, 200); // 300px wide and 200px tall
```

- 让 `Pane` 根据子节点的 prefSize 重新计算自身的 prefSize

```java{.line-numbers}
Pane root = new Pane();

// Set the preferred size to 300px wide and 200px tall
root.setPrefSize(300, 200);

/* Do some processing... */

// Set the default preferred size
root.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
```

!!! tip
    `Pane` 不会裁剪其内容，因此子节点可能在其边界外显示。    
