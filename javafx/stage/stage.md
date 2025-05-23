# JavaFX Stage

2023-07-06
@author Jiawei Mao
****
## 简介

`Stage` 是 JavaFX 的顶层容器，用于托管 `Scene`，对桌面应用，`Stage` 就是 Window。`Scene` 包含所有的可视化组件。和主屏幕对应的主 `Stage` 由 JavaFx 平台创建，传递给 `Application.start(Stage s)`方法。还可以根据需要创建其它 `Stage`。

> [!TIP]
>
> `Stage` 是顶层容器不代表它必须单独显示，如在 Web 环境中，`Stage`内嵌在网页中显示。

下面是 `Stage` 的类图：

<img src="images/Pasted%20image%2020230704205908.png" style="zoom:50%;" />

`Window` 是窗口类容器的超类，包含窗口相关的通用功能：

- 隐藏和显示窗口：`show()` 和 `hide()` 方法
- 设置位置、高度和宽度：`x`, `y`, `width`, `height` 属性
- 透明度：opacity 属性

`setScene()` 设置 `Window` 托管的 `Scene`。

`Stage.close()` 效果与 `Window.hide(`) 一样。

`Stage` 的创建和修改必须在 JAT 线程进行。`Application.start()` 方法在 JAT 中调用，创建主 `Stage` 并传入该方法。主 `Stage` 默认不显示，需要显式调用 `show()` 方法。

## 显示主 Stage

JAT 线程在调用 `Platform.exit()` 方法或者所有 `stage` 被关掉才会终止。只有在所有非守护线程死掉，JVM才会终止。JavaFX 应用线程不是守护线程。

`Application.launch()` 在 JAT 线程终止时返回。

如果 `stage` 没有显示，调用 `close()` 方法无效，就无法终止 JAT 线程。关闭 JAT 线程的方法有：

- 调用 `Platform.exit()`
- 先调用 `show()`，然后调用 `close()`
- 关闭显示的窗口
- 直接通过OS操作终止

!!! note
    `Stage.close()` 和 `Window.hide()` 效果一样。如果窗口没有显示，调用 `close()` 无效。    

## 设置 Stage Bounds

stage bounds 由 x, y, width, height 四个属性决定。

**示例：** 空 stage

运行示例，可以看到窗口，标题栏、边框以及下面的空白区域。

```java{.line-numbers}
import javafx.application.Application;
import javafx.stage.Stage;

public class BlankStage extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Blank Stage");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230705170307.png" {width="350px" title=""}

!!! tip
    当 `Stage` 没有 `Scene`，且没有显式设置位置和尺寸，其位置和尺寸由 platform 自动设置。

stage 默认在屏幕居中，`Window.centerOnScreen` 实现该逻辑：

- 水平居中
- 垂直方向，左上角为屏幕高度的 1/3 减去 stage 高度

**示例：** 添加 Scene

```java{.line-numbers}
import javafx.scene.Group;
import javafx.scene.Scene;
...
@Override
public void start(Stage stage) {
    stage.setTitle("Stage with an Empty Scene");
    Scene scene = new Scene(new Group());
    stage.setScene(scene);
    stage.show();
}
```

此时空白区域背景为 `Scene` 的默认背景颜色：白色。

**示例：** 添加 Button

```java{.line-numbers}
import javafx.scene.control.Button;
...
@Override
public void start(Stage stage) {
    stage.setTitle("Stage with a Button in the Scene");
    Group root = new Group(new Button("Hello"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
}
```

添加控件后，`Stage` 和位置和大小由 `Scene` 计算的尺寸确定。

@import "images/Pasted%20image%2020230705171115.png" {width="120px" title=""}

**示例：** 显式设置 `Stage` 尺寸

```java
@Override
public void start(Stage stage) {
    stage.setTitle("Stage with a Sized Scene");
    Group root = new Group(new Button("Hello"));
    Scene scene = new Scene(root, 300, 100);
    stage.setScene(scene);
    stage.show();
}
```

@import "images/Pasted%20image%2020230705171343.png" {width="250px" title=""}

此时 `Stage` 的 content 区域与指定大小一致。

**示例：** 显式设置 `Stage` 和 `Scene` 尺寸

```java{.line-numbers}
@Override
public void start(Stage stage) {
    stage.setTitle("A Sized Stage with a Sized Scene");
    Group root = new Group(new Button("Hello"));
    Scene scene = new Scene(root, 300, 100);
    stage.setScene(scene);
    stage.setWidth(400);
    stage.setHeight(100);
    stage.show();
}
```

@import "images/Pasted%20image%2020230705171549.png" {width="350px" title=""}

`Stage` 位置和尺寸设置规则：

- 如果没有 scene，bounds 由 platform 确定
- 如果有 Scene，没有 node，bounds 由 platform 确定。此时没有指定 Scene 尺寸
- 如果有 Scene，有 node，bounds 由 Scene 中的 node 确定。此时没有指定 Scene 尺寸，Stage 屏幕居中
- 如果有 Scene，且指定 Scene 尺寸，则 Scene 的 bounds 由指定的尺寸确定。Stage 居中。

!!! tip
    使用 `Window.sizeToScene()` 使 `Stage` 尺寸匹配 `Scene` 内容。如果在运行时修改了 `Scene` 尺寸，使用该方法同步 `Stage` 和 `Scene` 尺寸非常有用。

如果希望将 Stage 水平和垂直方向均居中，使用如下代码：

```java
Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
double x = bounds.getMinX() + (bounds.getWidth() - stage.getWidth())/2.0;
double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight())/2.0;
stage.setX(x);
stage.setY(y);
```

!!! warning
    `Stage` 的 bounds 在显示后才有，因此在 `Stage` 显示前使用上面的代码无效。    

下面的代码不能达到目的：

```java
@Override
public void start(Stage stage) {
    stage.setTitle("A Truly Centered Stage");
    Group root = new Group(new Button("Hello"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    
    // Wrong!!!! Use the logic shown below after the stage.show() call
    // At this point, stage width and height are not known. They are NaN.
    Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    double x = bounds.getMinX() + (bounds.getWidth() – stage.getWidth())/2.0;
    double y = bounds.getMinY() + (bounds.getHeight() – stage.getHeight())/2.0;
    stage.setX(x);
    stage.setY(y);
    stage.show();
}
```

## Stage 样式

`Stage` 的区域可以分为两块：内容区域（Content area）和装饰（Decoration）。

- 内容区域显示 `Scene` 的可视化组件
- 装饰部分包含标题栏和边框

标题栏及其内容根据平台不同有所差别，有些装饰还提供了额外功能。例如：

- 通过标题栏将 stage 拖放到不同的地方
- 标题栏上的最大化、最小化、还原和关闭按钮
- 边框可用于调整 stage 大小等功能

`Stage` 的 `style` 属性用于设置 `Stage` 的装饰部分和背景色，根据风格不同分为以下五类:

| `StageStyle`  | 说明                                                     |
| ------------- | -------------------------------------------------------- |
| `DECORATED`   | 白色背景，平台风格的装饰，默认选项                          |
| `UNDECORATED` | 白色背景，无装饰(无标题栏)                                 |
| `TRANSPARENT` | 透明背景，无装饰(无标题栏)                                 |
| `UNIFIED`     | 背景色和装饰一致，平台风格装饰，装饰和client area 之间无边框 |
| `UTILITY`     | 白色背景，最低的平台风格装饰配置                           |

!!! note
    stage 的样式仅仅指定装饰部分。而背景色由 Scene 的背景设置决定，默认为白色。因此，如果将 stage 的风格设置为 `TRANSPARENT`，对应的 stage 依然为白色背景，即 Scene 的背景色，要获得统一的透明 stage，需要调用 scene 的 `setFill(null)`方法。

使用 `Stage.initStyle(StageStyle style)` 设置 Stage 样式。样式设置要在 Stage 显示前完成，否则抛出 runtime 异常。

**示例：** Stage 样式

```java{.line-numbers}
import static javafx.stage.StageStyle.*;

public class StageStyleApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 用来显示 style 类型
        Label styleLabel = new Label("Stage Style");

        // 用来关闭 Stage
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        VBox root = new VBox();
        root.getChildren().addAll(styleLabel, closeButton);
        Scene scene = new Scene(root, 100, 70);
        stage.setScene(scene);

        // 有些样式标题不可见
        stage.setTitle("The Style of a Stage");

        /* Uncomment one of the following statements at a time */
        this.show(stage, styleLabel, DECORATED);
        //this.show(stage, styleLabel, UNDECORATED);
        //this.show(stage, styleLabel, TRANSPARENT);
        //this.show(stage, styleLabel, UNIFIED);
        //this.show(stage, styleLabel, UTILITY);
    }

    private void show(Stage stage, Label styleLabel, StageStyle style) {
        // 设置显示样式文本
        styleLabel.setText(style.toString());

        // 设置样式
        stage.initStyle(style);

        // 对透明样式，将 Scene 填充设置为 null
        // 这样Scene 区域也是透明的
        if (style == TRANSPARENT) {
            stage.getScene().setFill(null);
            stage.getScene().getRoot().setStyle(
                    "-fx-background-color: transparent");
        } else if (style == UNIFIED) {
            stage.getScene().setFill(Color.TRANSPARENT);
        }

        // Show the stage
        stage.show();
    }
}
```

- DECORATED 

@import "images/Pasted%20image%2020230705201607.png" {width="300px" title=""}

- UNDECORATED

@import "images/Pasted%20image%2020230705201701.png" {width="100px" title=""}

没有标题栏，无法拖动，也无法调整大小。

- TRANSPARENT

@import "images/Pasted%20image%2020230705201808.png" {width="100px" title=""}

完全透明，没有标题栏。因为桌面背景是红色，所以看着是红色。

- UNIFIED

@import "images/Pasted%20image%2020230705201859.png" {width="300px" title=""}

UNIFIED 和 DECORATED 主要差别：标题栏和内容之间没有边框，标题栏颜色不一样。

- UTILITY

@import "images/Pasted%20image%2020230705202202.png" {width="180px" title=""}

UTILITY 比较简洁，有标题栏，但是没有最小化、最大化按钮。

## 移动无装饰 Stage

拖动 Stage 的标题栏可以移动 Stage。但 UNDECORATED 和 TRANSPARENT 两种样式的 Stage 没有标题栏。此时可以通过鼠标事件实现拖动功能：

```java{.line-numbers}
public class DraggingStage extends Application {
    private Stage stage;
    private double dragOffsetX;
    private double dragOffsetY;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 保存 Stage 引用，方便处理鼠标事件
        this.stage = stage;

        Label msgLabel = new Label("Press the mouse button and drag.");
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        VBox root = new VBox();
        root.getChildren().addAll(msgLabel, closeButton);

        Scene scene = new Scene(root, 300, 200);

        // Set mouse pressed and dragged even handlers for the scene
        scene.setOnMousePressed(e -> handleMousePressed(e));
        scene.setOnMouseDragged(e -> handleMouseDragged(e));

        stage.setScene(scene);
        stage.setTitle("Moving a Stage");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    protected void handleMousePressed(MouseEvent e) {
        // getScreenX() 和 getScrrenY() 返回鼠标相对屏幕的位置
        this.dragOffsetX = e.getScreenX() - stage.getX();
        this.dragOffsetY = e.getScreenY() - stage.getY();
    }

    protected void handleMouseDragged(MouseEvent e) {
        // Move the stage by the drag amount
        stage.setX(e.getScreenX() - this.dragOffsetX);
        stage.setY(e.getScreenY() - this.dragOffsetY);
    }
}
```

@import "images/Pasted%20image%2020230705205524.png" {width="500px" title=""}

按下鼠标时，dragOffsetX 和 dragOffsetY 为鼠标相对 Stage 的位置。

拖动鼠标时，更新 Stage 位置。

## Stage 模态

GUI应用的窗口可以分为两类：模态（modal）和非模态（modeless）。

模态窗口显示时，用户无法使用其它窗口，直到模态窗口关闭；非模态窗口则没有此限制，可以在多个非模态窗口间随意切换。

JavaFX `Stage` 有三种模态类型，由 `Modality` enum 定义：

| Modal                        | 说明                                                    |
| ---------------------------- | ------------------------------------------------------ |
| `Modality.NONE`              | 默认值，不阻止其他窗口显示，效果等同于 modeless            |
| `Modality.WINDOW_MODAL`      | 阻止其 owner 内的其它窗口，如果没有owner，则和 NONE 效果相同 |
| `Modality.APPLICATION_MODAL` | 阻止该应用内所有其它的窗口                                 |

使用 `Stage.initModality(Modality m)`  方法设置 Stage 模态。

```java
Stage stage = new Stage();

stage.initModality(Modality.WINDOW_MODAL);
/* More code goes here.*/

stage.show();
```

!!! note
    与样式一样，模态的设置也要在 Stage 显示之前进行。

`Stage` 可以有一个 owner:

- owner 是另一个 `Window`
- 通过 `Stage.initOwner(Window owner)` 设置 owner
- owner 要在 stage 显示前设置

**当 owner 最小化、隐藏等，stage 也会随之最小化或隐藏**，owner 可以为 null。

**示例：** 有四个 stages: s1, s2, s3, s4。 
s1, s4 的 modal 为 `NONE`，没有owner；s1 是 s2 的 owner，s2 是 s3 的 owner（s1->s2, s2->s3）。四个 stages 显示后。

- 如果 s3 的 modality 为 `WINDOW_MODAL`，则 s3 和 s4 可同时显示，s3 和 s1, s2 不能同时显示，显示 s3，它会阻止 s1 和 s2 显示。
- 如果 s4 的 modality 为 `APPLICATION_MODAL`，则在 s4 关闭前，其它窗口都不能显示。

**示例：** `Stage` modality 演示

在 primrary stage 显示 6 个按钮，每个按钮以指定 owner 和 `Modality` 创建新的 `Stage`.

```java{.line-numbers}
import static javafx.stage.Modality.*;

public class StageModalityApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 每个按钮打开一个新的 Stage
        Button ownedNoneButton = new Button("Owned None");
        ownedNoneButton.setOnAction(e -> showDialog(stage, NONE));

        Button nonOwnedNoneButton = new Button("Non-owned None");
        nonOwnedNoneButton.setOnAction(e -> showDialog(null, NONE));

        Button ownedWinButton = new Button("Owned Window Modal");
        ownedWinButton.setOnAction(e -> showDialog(stage, WINDOW_MODAL));

        Button nonOwnedWinButton = new Button("Non-owned Window Modal");
        nonOwnedWinButton.setOnAction(e -> showDialog(null, WINDOW_MODAL));

        Button ownedAppButton = new Button("Owned Application Modal");
        ownedAppButton.setOnAction(e -> showDialog(stage, APPLICATION_MODAL));

        Button nonOwnedAppButton = new Button("Non-owned Application Modal");
        nonOwnedAppButton.setOnAction(e -> showDialog(null, APPLICATION_MODAL));

        VBox root = new VBox();
        root.getChildren().addAll(ownedNoneButton, nonOwnedNoneButton,
                ownedWinButton, nonOwnedWinButton,
                ownedAppButton, nonOwnedAppButton);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.setTitle("The Primary Stage");
        stage.show();
    }

    private void showDialog(Window owner, Modality modality) {
        // 以指定 owner 和 Modality 创建 Stage
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(modality);
        // label 用来显示 Modality 类型
        Label modalityLabel = new Label(modality.toString());
        Button closeButton = new Button("Close"); // 用来关闭 Stage
        closeButton.setOnAction(e -> stage.close());

        VBox root = new VBox();
        root.getChildren().addAll(modalityLabel, closeButton);
        Scene scene = new Scene(root, 200, 100);
        stage.setScene(scene);
        stage.setTitle("A Dialog Box");
        stage.show();
    }
}
```

## Stage 透明度

`Stage` 超类 `Window` 提供了透明度设置功能：

```java
public final void setOpacity(double value)

public final double getOpacity()
```

opacity 值范围：`[0.0, 1.0]`。

0.0 表示完全透明，1.0 表示完全不透明。

并非所有 JavaFX runtime 平台都支持该选项，在不支持透明度的平台设置无效。

**示例：** 将 Stage 设置为半透明

```java
Stage stage = new Stage();
stage.setOpacity(0.5); // A half-translucent stage
```

## Stage 尺寸

Stage 的 `resizable` 属性用于设置是否可调整尺寸，默认为 `true`：

```java
public final void setResizable(boolean value)

public final boolean isResizable()
```

另外，可以使用下面四个方法显示 Stage 尺寸范围：

```java
public final void setMinHeight(double value)
public final void setMaxHeight(double value)

public final void setMaxWidth(double value)
public final void setMinWidth(double value)
```

```ad-tip
调用 `Stage.setResizable(false)` 防止用户调整 `Stage` 尺寸，但是依然可以通过编程方式调整。
```

**示例：** 将 `Stage` 全屏

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MaximizedStage extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Group()));
        stage.setTitle("A Maximized Stage");

        // 将 Stage 的位置和尺寸设置和 Screen 一样
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(visualBounds.getMinX());
        stage.setY(visualBounds.getMinY());
        stage.setWidth(visualBounds.getWidth());
        stage.setHeight(visualBounds.getHeight());

        stage.show();
    }
}
```

## Stage 全屏模式

Stage 的 fullScreen 属性用于设置全屏模式。

进入全屏模式时，JavaFX 会显示一个简短信息，说明如何退出全屏模式。

```java
public final void setFullScreen(boolean value)

public final boolean isFullScreen()
```

## 关闭确认

在关闭 Stage 时，显示一个确认框，根据用户点击的按钮执行不同操作。

`Window.show()` 直接返回，不适合实现该功能。`Stage.showAndWait()` 则会持续等待，直到 Stage 关闭，适合实现该功能。

!!! warning
    `Stage.showAndWait()` 必须在 JAT 调用。不能在 primary Stage 调用，否则抛出 runtime 异常。

假设有 2 个 Stage: s1, s2。

s1 通过 `s1.showAndWait()` 打开，在 s1 代码中调用 `s2.showAndWait()` 打开 s2。此时有两个嵌套事件循环，分别由 s1.showAndWait() 和 `s2.showAndWait()` 创建。当 s1 和 s2 都关闭， s1.showAndWait() 才会返回，s2 关闭 s2.showAndWait() 返回。

**示例：** showAndWait() 功能演示

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShowAndWaitApp extends Application {

    protected static int counter = 0;
    protected Stage lastOpenStage;
    
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        Button openButton = new Button("Open");
        openButton.setOnAction(e -> open(++counter));
        root.getChildren().add(openButton);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("The Primary Stage");
        stage.show();

        this.lastOpenStage = stage;
    }

    private void open(int stageNumber) {
        Stage stage = new Stage();
        stage.setTitle("#" + stageNumber);

        Button sayHelloButton = new Button("Say Hello");
        sayHelloButton.setOnAction(e -> System.out.println("Hello from #" + stageNumber));

        Button openButton = new Button("Open");
        openButton.setOnAction(e -> open(++counter));

        VBox root = new VBox();
        root.getChildren().addAll(sayHelloButton, openButton);
        Scene scene = new Scene(root, 200, 200);
        stage.setScene(scene);
        stage.setX(this.lastOpenStage.getX() + 50);
        stage.setY(this.lastOpenStage.getY() + 50);
        this.lastOpenStage = stage;

        System.out.println("Before stage.showAndWait(): " + stageNumber);

        // Show the stage and wait for it to close
        stage.showAndWait();

        System.out.println("After stage.showAndWait(): " + stageNumber);
    }
}
```

递归调用 `open(int stageNumber)`，创建新的 Stage。

以任意顺序关闭 Stage，通过顺序可以看到 Stage.showAndWait() 的返回顺序。

```ad-tip
JavaFX 没有内置对话框功能，可以通过 `Stage.showAndWait()` 实现，设置适当的 Modality 即可。
```

## Stage 尺寸

JavaFX UI 控件的创建和显示之间存在时间差。在创建 UI 控件时，不管是通过 API 还是 FXML，都还不知道窗口的尺寸。在屏幕上显示时，窗口尺寸才可用，通过 `Stage.onShown` 属性可以监听窗口显示的事件。

为了说明这一点，下面程序显示窗口显示前后 Stage 的尺寸。

```java{.line-numbers}
import static javafx.geometry.Pos.CENTER;

public class StartVsShownJavaFXApp extends Application {

    private DoubleProperty startX = new SimpleDoubleProperty();
    private DoubleProperty startY = new SimpleDoubleProperty();
    private DoubleProperty shownX = new SimpleDoubleProperty();
    private DoubleProperty shownY = new SimpleDoubleProperty();

    @Override
    public void start(Stage primaryStage) throws Exception {

        Label startLabel = new Label("Start Dimensions");
        TextField startTF = new TextField();
        startTF.textProperty().bind(
                Bindings.format("(%.1f, %.1f)", startX, startY)
        );

        Label shownLabel = new Label("Shown Dimensions");
        TextField shownTF = new TextField();
        shownTF.textProperty().bind(
                Bindings.format("(%.1f, %.1f)", shownX, shownY)
        );

        GridPane gp = new GridPane();
        gp.add(startLabel, 0, 0);
        gp.add(startTF, 1, 0);
        gp.add(shownLabel, 0, 1);
        gp.add(shownTF, 1, 1);
        gp.setHgap(10);
        gp.setVgap(10);

        HBox hbox = new HBox(gp);
        hbox.setAlignment(CENTER);

        VBox vbox = new VBox(hbox);
        vbox.setAlignment(CENTER);

        Scene scene = new Scene(vbox, 480, 320);

        primaryStage.setScene(scene);

        // before show()...I just set this to 480x320, right?
        startX.set(primaryStage.getWidth());
        startY.set(primaryStage.getHeight());

        primaryStage.setOnShown((evt) -> {
            shownX.set(primaryStage.getWidth());
            shownY.set(primaryStage.getHeight());  // all available now
        });

        primaryStage.setTitle("Start Vs. Shown");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

@import "images/Pasted%20image%2020230731225832.png" {width="400px" title=""}

## alwaysOnTop

`Stage.alwaysOnTop` 属性指定当前 Stage 是否总是在其它窗口上面:

- 如果已有其它窗口设置 alwaysOnTop，那么这些采用 alwaysOnTop 窗口的相对顺序不确定（取决于平台）
- 如果存在安全管理器，则 `javafx.util.FXPermission` 必须包含 "setWindowAlwaysOnTop" 此属性才能发挥作用
- 如果 Application 没有权限，设置该属性无效，设置后也会恢复为 false
- 部分平台不支持该属性

设置方法：

```java
primaryStage.setAlwaysOnTop(true);
```

##  Window

`javafx.stage.Window` 的类图如下：

<img src="images/Pasted%20image%2020230704205908.png" style="zoom:50%;" />

`Window` 为顶层窗口，承载 `Scene`。如上图所示，`Stage`, `PopupWindow` 等都属于顶层窗口。

只能在 JAT 创建和修改 `Window` 对象。


## Screen

`javafx.stage.Screen` 类用于获得屏幕的详细信息，如 DPI，尺寸、设置等。如果有多个屏幕，则一个称为主屏幕（primary），其他的为副屏幕。

### 获得主屏幕

```java
Screen primaryScreen = Screen.getPrimary();
```

### 获得所有屏幕

```java
ObservableList<Screen> screenList = Screen.getScreens();
```

### 屏幕分辨率

```java
Screen primaryScreen = Screen.getPrimary();
double dpi = primaryScreen.getDpi();
```

### 屏幕大小

```java
// 获得屏幕大小
Rectangle2D getBounds()

// 获得屏幕可视区域大小
Rectangle2D getVisualBounds()
```

**可视区域**排除了系统本地窗口使用的区域（如任务栏和菜单栏）之后，屏幕剩余的区域，所以一般来说，可视区域小于整个屏幕区域。

如果一个桌面跨越多个屏幕，则副屏的 bounds 是相对主屏幕定义的。例如，如果一个桌面跨越两个屏幕，主屏幕左上角坐标为 (0, 0)，宽度为 1600，则副屏幕左上角的坐标为 (1600, 0)。

### 示例

!!! note
    虽然 `Screen` 类的API没有明说，但是 `Screen` 只能在 JavaFX 程序中使用。即在 JavaFX launcher 启动后才能使用，但是不需要在 JAT 线程中，在 `init()` 方法中也能使用。

```java{.line-numbers}
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScreenDetailsApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        ObservableList<Screen> screenList = Screen.getScreens();
        System.out.println("Screens Count: " + screenList.size());

        // Print the details of all screens
        for (Screen screen : screenList) {
            print(screen);
        }

        Platform.exit();
    }

    public void print(Screen s) {
        System.out.println("DPI: " + s.getDpi());

        System.out.print("Screen Bounds: ");
        Rectangle2D bounds = s.getBounds();
        print(bounds);

        System.out.print("Screen Visual Bounds: ");
        Rectangle2D visualBounds = s.getVisualBounds();
        print(visualBounds);
        System.out.println("-----------------------");
    }

    public void print(Rectangle2D r) {
        System.out.format("minX=%.2f, minY=%.2f, width=%.2f, height=%.2f%n",
                r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }
}
```

```
Screens Count: 1
DPI: 93.0
Screen Bounds: minX=0.00, minY=0.00, width=2560.00, height=1440.00
Screen Visual Bounds: minX=0.00, minY=0.00, width=2560.00, height=1392.00
-----------------------
```