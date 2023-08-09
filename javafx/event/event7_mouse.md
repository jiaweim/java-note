# 鼠标事件

2023-08-09, 13:48
modify: 修改样式
2023-06-26, 13:53
@author Jiawei Mao
****
## 1. 简介

鼠标事件由 `MouseEvent` 类定义。`MouseEvent` 定义了下列鼠标相关的事件类型常量。所有常量的类型都是 `EventType<MouseEvent>`。

`Node` 类为大多数鼠标事件类型定义了 `onXXX` 属性，可为特定鼠标事件添加事件处理器。

| 鼠标事件类型  | 说明  |
| -------- | ------ |
| `ANY`                  | 所有鼠标事件类型的超类型。如果一个节点想接收所有类型的鼠标事件，则应为该类型注册处理器。`InputEvent.ANY` 是该事件类型的超类型  |
| `MOUSE_PRESSED`        | 按下鼠标按钮生成。`MouseEvent.getButton()` 返回按下的按钮。鼠标按钮由枚举类 `MouseButton` 定义：`NONE`, `PRIMARY`, `MIDDLE`, `SECONDARY`    |
| `MOUSE_RELEASED`       | 释放鼠标按钮生成。该事件会传递到鼠标被按下的节点。如在 `Circle` 内按下鼠标按钮，拖动到 `Circle` 外释放，产生的 `MOUSE_RELEASED` 事件会传递到 `Circle`，而不是鼠标按钮释放位置的节点 |
| `MOUSE_CLICKED`        | 单击鼠标按钮生成。需要在同一个节点上按下并释放按钮才会生成    |
| `MOUSE_MOVED`          | 移动鼠标（不按下任何鼠标按钮）生成       |
| `MOUSE_ENTERED`        | 鼠标进入节点时生成。该事件没有事件捕获和冒泡阶段，即不会调用目标节点父节点的过滤器和处理器    |
| `MOUSE_ENTERED_TARGET` | 鼠标进入节点时生成。与 `MOUSE_ENTERED` 的差别在于，该事件具有事件捕获和冒泡阶段  |
| `MOUSE_EXITED`         | 鼠标离开节点时生成。该事件没有事件捕获和冒泡阶段，即只传递给目标节点，不会调用目标节点父节点的过滤器和处理器  |
| `MOUSE_EXITED_TARGET`  | 鼠标离开节点时生成。与 `MOUSE_EXITED` 的差别在于，该事件具有事件捕获和冒泡阶段   |
| `DRAG_DETECTED`        | 在单个节点上按下并拖动鼠标超过一定距离生成，距离阈值为平台特异性    |
| `MOUSE_DRAGGED`        | 按下鼠标按钮并拖动鼠标生成，无论鼠标拖动到什么地方，事件都传递到按下鼠标按钮的节点  |

## 2. 鼠标位置

`MouseEvent` 包含鼠标事件发生时鼠标的位置信息。

| 方法                       | 说明                          |
| -------------------------- | ----------------------------- |
| `getX()`, `getY()`             | 鼠标相对事件源节点的坐标      |
| `getSceneX()`, `getSceneY()`   | 鼠标相对节点所在 `Scene` 的坐标 |
| `getScreenX()`, `getScreenY()` | 鼠标相对节点所在屏幕的坐标    |

**示例：** 使用 `MouseEvent` 的方法获取鼠标位置

为 `Stage` 添加 `MOUSE_CLICKED` 事件处理器。运行程序，点击 `Stage` 任意位置，控制台会输出事件源、事件目标和鼠标位置信息。

```java {.line-numbers}
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MouseLocation extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle circle = new Circle(50, 50, 50);
        circle.setFill(Color.CORAL);

        Rectangle rect = new Rectangle(100, 100);
        rect.setFill(Color.TAN);

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(circle, rect);

        // 为 stage 添加 MOUSE_CLICKED 处理器
        stage.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> handleMouseMove(e));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Mouse Location");
        stage.show();
    }

    public void handleMouseMove(MouseEvent e) {
        String source = e.getSource().getClass().getSimpleName();
        String target = e.getTarget().getClass().getSimpleName();

        // 相对事件源的坐标
        double sourceX = e.getX();
        double sourceY = e.getY();

        // 相对 scene 的坐标
        double sceneX = e.getSceneX();
        double sceneY = e.getSceneY();

        // 相对屏幕的坐标
        double screenX = e.getScreenX();
        double screenY = e.getScreenY();

        System.out.println("Source=" + source + ", Target=" + target +
                ", Location:" +
                " source(" + sourceX + ", " + sourceY + ")" +
                ", scene(" + sceneX + ", " + sceneY + ")" +
                ", screen(" + screenX + ", " + screenY + ")");
    }
}
```

@import "images/2023-06-26-09-28-51.png" {width="250px" title=""}

分别点击 `Circle`, `Rectangle` 和空白位置的输出：

```
Source=Stage, Target=Circle, Location: source(63.333333333333336, 60.666666666666664), scene(63.333333333333336, 60.666666666666664), screen(1213.3333333333333, 495.3333333333333)
Source=Stage, Target=Rectangle, Location: source(194.0, 60.666666666666664), scene(194.0, 60.666666666666664), screen(1344.0, 495.3333333333333)
Source=Stage, Target=HBox, Location: source(105.33333333333333, 7.333333333333333), scene(105.33333333333333, 7.333333333333333), screen(1255.3333333333333, 442.0)
```

可以发现，由于事件注册在 `Stage` 上，所以事件源都是 `Stage`，因此 `getX() `和 `getSceneX()` 返回的坐标总是一样。

## 3. 鼠标按钮表示

`javafx.scene.input` 包中的 `MouseButton` 枚举类表示鼠标按钮：

| MouseButton | 功能                     |
| ----------- | ------------------------ |
| `NONE`        | 表示无按钮               |
| `PRIMARY`     | 鼠标主按钮，一般为左按钮 |
| `MIDDLE`      | 中间键                   |
| `SECONDARY`   | 鼠标副按钮，一般为右按钮 |

鼠标的主按钮和副按钮的位置，取决于鼠标配置。对惯用右手的用户，一般左按钮为主按钮、右按钮为副按钮。对左撇子用户则相反。

## 4. 鼠标按钮状态

`MouseEvent` 保存了鼠标事件发生时鼠标按钮的状态。`MouseEvent` 的下列方法可用来查询鼠标按钮状态：

| 方法                           | 功能                         |
| ------------------------------ | ---------------------------- |
| `MouseButton getButton()`        | 鼠标事件对应的按钮           |
| `int getClickCount()`            | 鼠标点击次数                 |
| `boolean isPrimaryButtonDown()`  | 鼠标主按钮按下               |
| `boolean isMiddleButtonDown()`   | 鼠标中间按钮按下             |
| `bolean isSecondaryButtonDown()` | 鼠标副按钮按下               |
| `boolean isPopupTrigger()`       | 鼠标事件为平台的弹出菜单触发 |
| `boolean isStillSincePress()`    | 鼠标保持在某个位置没变       |

`getButton()` 很多时候都返回 `MouseButton.NONE`。例如，触屏上通过手指而非鼠标触发的鼠标事件，或者 `MOUSE_MOVED` 这类不需要按下按钮的事件。

`getButton()` 与 `isPrimaryButtonDown()`、`isSecondaryButtonDown()` 的差别：

- `getButton()` 返回**触发事件的按钮**，但并非所有鼠标事件由按钮触发。如果鼠标事件与按钮无关，`getButton()` 返回 `MouseButton.NONE`。
- 发生 `MOUSE_PRESSED` 事件，如果按下了主按钮，`isPrimaryButtonDown()` 返回 true，不管主按钮与该事件是否有关。

例如，假设先按主按钮（不松开），再按副按钮时，又生成一个 `MOUSE_PRESSED` 事件，此时 `getButton()` 返回 `MouseButton.SECONDARY`，但 `isPrimaryButtonDown()` 和 `isSecondaryButtonDown()` 都返回 `true`。

**弹出式菜单**（pop-up），也称为上下文菜单（context）或快捷菜单（shortcut）。例如，在浏览器中单击鼠标右键，会弹出一个菜单。不同平台触发弹出式菜单的方式不同，在 Windows 平台快捷键通常为 `Shift+F10`。

如果鼠标事件触发弹出式菜单，`isPopupTrigger()` 返回 `true`。在该方法返回 `true` 时，通常让系统显示默认的弹出式菜单。

!!! tip
    JavaFX 提供了一个专门的上下文菜单输入事件。由 `javafx.scene.input` 包中的 `ContextMenuEvent` 类表示。如果需要处理上下文菜单事件，请使用该类。

## 5. GUI 应用中的迟滞

**迟滞**（*hysteresis*）是一种允许用户在一定时间或距离内输入的特性（不是延迟）。

用户输入可被接受的时间范围称为**滞后时间**（*hysteresis time*）；用户输入可被接受的区域称为**滞后区**（*hysteresis area*）。

滞后时间和滞后区与系统有关。例如，现代 GUI 应用可以通过双击鼠标按钮来调用，两次点击之间存在时间间隔，如果时间间隔在系统的滞后时间内，则两次单击被认为是双击，否则它们只是两次单独的单击。

在鼠标点击事件中，`MOUSE_PRESSED` 和 `MOUSE_RELEASED` 之间鼠标通常移动了一小段距离。如果鼠标移动的距离在系统允许的滞后区域内，`isStillSincePress()` 返回 `true`。在考虑鼠标拖动操作时`isStillSincePress(`) 很重要，如果该方法返回 true，由于鼠标移动距离在滞后距离内，可以忽略鼠标拖动事件。

## 6. 修饰键

修饰键（modifier key）用于改变其它键的行为。常见的修饰键包括 Alt, Shift, Ctrl, Meta, Caps Lock 以及 Num Lock。当然，并非所有平台支持所有这些修饰键，如 Meta 键在 Mac 上，Windows 没有。有些系统可以模拟修饰键的功能，例如，在 Windows 上用 `Win` 键模拟 `Meta` 键。

`MouseEvent` 包含鼠标事件发生时修饰键的状态信息。`MouseEvent` 中与修饰键相关的方法有：

| 方法                    | 说明                                                                               |
| ----------------------- | ---------------------------------------------------------------------------------- |
| `boolean isAltDown()`     | 鼠标事件中，按下了 Alt 键                                                          |
| `boolean isControlDown()` | 鼠标事件中，按下了 Ctrl 键                                                         |
| `isMetaDown()`            | 鼠标事件中，按下了 Meta 键                                                         |
| `isShiftDown()`           | 鼠标事件中，按下了 Shift 键                                                        |
| `isShortcutDown()`        | 鼠标事件中，按下了平台特异性的快捷键，在 Windows 上为 Ctrl 键，在 Mac 上为 Meta 键 |

## 7. 边框与鼠标事件

`Node` 有一个 `pickOnBounds` 属性，用于控制节点生成鼠标事件的方式。

节点可以是任何形状，但它的边界总是矩形：

- 如果 `pickOnBounds` 为 `true`，那么鼠标落在节点的边界上或边界内，就会生成相应的鼠标事件
- 如果 `pickOnBounds` 为 `false`（默认），鼠标在节点几何形状的边界上或边界内，才会触发相应的鼠标事件。

有些节点的 `pickOnBounds` 属性默认为 `true`，如 `Text`。

下图是一个圆的几何边界和边框。`pickOnBounds` 为 `false` 时，鼠标在圆的几何边界和边框之间的白色区域内不会触发圆的鼠标事件。

@import "images/2023-06-26-10-54-22.png" {width="400px"}

**示例：** 演示 `Circle` 的 `pickOnBounds` 效果

@import "images/2023-06-26-10-56-02.png" {width="250px" title=""}

该程序将一个 `Rectangle` 和一个 `Circle` 添加到 `Group`。`Rectangle` 在 `Circle` 之前添加，以保证 `Circle` 在上面。

```java {.line-numbers}
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PickOnBounds extends Application {

    private CheckBox pickonBoundsCbx = new CheckBox("Pick on Bounds");
    Circle circle = new Circle(50, 50, 50, Color.LIGHTGRAY);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(100, 100);
        rect.setFill(Color.RED);

        Group group = new Group();
        group.getChildren().addAll(rect, circle);

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(group, pickonBoundsCbx);

        // 为 circle 和 rectangle 添加 MOUSE_CLICKED 处理器
        circle.setOnMouseClicked(e -> handleMouseClicked(e));
        rect.setOnMouseClicked(e -> handleMouseClicked(e));

        // 为 checkbox 添加 action 处理器
        pickonBoundsCbx.setOnAction(e -> handleActionEvent(e));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Pick on Bounds");
        stage.show();
    }

    public void handleMouseClicked(MouseEvent e) {
        String target = e.getTarget().getClass().getSimpleName();
        String type = e.getEventType().getName();
        System.out.println(type + " on " + target);
    }

    public void handleActionEvent(ActionEvent e) {
        if (pickonBoundsCbx.isSelected()) {
            circle.setPickOnBounds(true);
        } else {
            circle.setPickOnBounds(false);
        }
    }
}
```

`Rectangle` 填充为红色，`Circle` 填充为灰色。红色区域正好是 `Circle` 几何形状和边框之间的区域。

复选框用于控制 `Circle` 的 `pickOnBounds` 属性。勾选时 `pickOnBounds` 为 true。

点击灰色区域时，`Circle` 必然接受鼠标点击事件。点击**红色区域**时：

- 勾选复选框，`Circle` 接收事件
- 不勾选复选框，`Rectangle` 接收事件

## 8. 鼠标事件穿透

`Node` 类有一个 `mouseTransparent` 属性，用于控制节点及其子节点是否接收鼠标事件。

对比 `pickOnBounds` 和 `mouseTransparent` 属性：

- 前者确定生成节点鼠标事件的区域，后者确定节点及其子节点是否生成鼠标事件。
- `pickOnBounds` 只影响设置它的节点，`mouseTransparent` 影响设置它的节点及其所有子节点。

**示例：** 演示 `Circle` 的 `mouseTransparent` 属性

界面和上个例子类似。勾选复选框时，`Circle` 的 `mouseTransparent` 属性为 `true`。

@import "images/2023-06-26-11-31-43.png" {width="250px" title=""}

```java {.line-numbers}
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MouseTransparency extends Application {

    private CheckBox mouseTransparentCbx = new CheckBox("Mouse Transparent");
    Circle circle = new Circle(50, 50, 50, Color.LIGHTGRAY);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(100, 100);
        rect.setFill(Color.RED);

        Group group = new Group();
        group.getChildren().addAll(rect, circle);

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(group, mouseTransparentCbx);

        // 为 circle 和 rectangle 添加 MOUSE_CLICKED 处理器
        circle.setOnMouseClicked(e -> handleMouseClicked(e));
        rect.setOnMouseClicked(e -> handleMouseClicked(e));

        // 为 checkbox 添加 action 处理器
        mouseTransparentCbx.setOnAction(e -> handleActionEvent(e));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Mouse Transparency");
        stage.show();
    }

    public void handleMouseClicked(MouseEvent e) {
        String target = e.getTarget().getClass().getSimpleName();
        String type = e.getEventType().getName();
        System.out.println(type + " on " + target);
    }

    public void handleActionEvent(ActionEvent e) {
        if (mouseTransparentCbx.isSelected()) {
            circle.setMouseTransparent(true);
        } else {
            circle.setMouseTransparent(false);
        }
    }
} 
```

说明：

- 勾选复选框时，点击 `Circle` 的灰色区域，鼠标点击事件被传递到 `Rectangle`，因为 `Circle` 是鼠标透明的，使得鼠标事件通过（此时 `Circle` 收不到鼠标事件）
- 取消勾选复选框，点击 `Circle` 的灰色区域，鼠标事件发动到 `Circle`
- 点击红色区域，事件传递到 `Rectangle`，因为 `Circle` 的 `pickOnBounds` 属性默认为 `false`

## 9. 合成鼠标事件

鼠标、触控板和触摸屏都可以生成鼠标事件。触摸屏上的操作生成的鼠标事件称为**合成鼠标事件**（*synthesized mouse events*）。

如果鼠标事件是通过触摸屏生成的合成事件，`MouseEvent.isSynthesized()` 返回 `true`。

当手指在触摸屏上拖动时，会同时生成 scrolling-gesture 事件和 mouse-dragged 事件。根据 `isSynthesized()` 返回值可以检测鼠标事件是通过触摸屏上拖动手指生成，还是拖动鼠标生成。

## 10. Mouse-Entered 和 Mouse-Exited 事件

下面四个鼠标事件类型用于处理鼠标进入或离开节点的事件：

- `MOUSE_ENTERED`
- `MOUSE_EXITED`
- `MOUSE_ENTERED_TARGET`
- `MOUSE_EXITED_TARGET`

这里有两套处理鼠标进入和离开节点的事件类型。两者触发机制相同，传递机制不同。

当鼠标进入节点，生成 `MOUSE_ENTERED` 事件；当鼠标离开节点，生成 `MOUSE_EXITED` 事件。这两个事件不经过捕获和冒泡阶段，即直接交付给目标节点，不经过任何父节点，因此也不会触发父节点的过滤器和处理器方法。

**示例：** 演示 `MOUSE_ENTERED` 和 `MOUSE_EXITED` 事件的传递

@import "images/2023-06-26-14-31-40.png" {width="150px" title=""}

`HBox` 中包含一个 `Circle`，为 `Circle` 和 `HBox` 添加 `MOUSE_ENTERED` 和 `MOUSE_EXITED` 事件处理器。

```java {.line-numbers}
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_EXITED;

public class MouseEnteredExited extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle circle = new Circle(50, 50, 50);
        circle.setFill(Color.GRAY);

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(circle);

        // 创建 mouse 事件处理器
        EventHandler<MouseEvent> handler = e -> handle(e);

        // 为 HBox 添加 MOUSE_ENTERED 和 MOUSE_ENTERED 处理器
        root.addEventHandler(MOUSE_ENTERED, handler);
        root.addEventHandler(MOUSE_EXITED, handler);

        // 为 Circle 添加 MOUSE_ENTERED 和 MOUSE_EXITED 处理器
        circle.addEventHandler(MOUSE_ENTERED, handler);
        circle.addEventHandler(MOUSE_EXITED, handler);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Mouse Entered and Exited Events");
        stage.show();
    }

    public void handle(MouseEvent e) {
        String type = e.getEventType().getName();
        String source = e.getSource().getClass().getSimpleName();
        String target = e.getTarget().getClass().getSimpleName();
        System.out.println("Type=" + type + ", Target=" + target + ", Source=" + source);
    }
}
```

说明：

- 鼠标进入白色区域，生成 `HBox` 的 `MOUSE_ENTERED` 事件
- 鼠标进入 `Circle` 灰色区域，生成 `Circle` 的 `MOUSE_ENTERED` 事件
- 事件源和事件目标总是相同，说明这些事件没有经过捕获和冒泡阶段

比较有意思的是，从 `Circle` 灰色区域移动到 `HBox` 白色区域，不会生成 `HBox` 的 `MOUSE_ENTERED` 事件，即 `Circle` 区域被认为在 `HBox` 中。

如果需要鼠标事件经过捕获和冒泡阶段，方便父节点应用过滤器并提供默认响应，`MOUSE_ENTERED_TARGET` 和 `MOUSE_EXITED_TARGET` 事件类型提供了该特性。

`MOUSE_ENTERED` 和 `MOUSE_EXITED` 是 `MOUSE_ENTERED_TARGET` 和 `MOUSE_EXITED_TARGET` 的子类型。

如果一个节点对其子节点的鼠标事件感兴趣，则为 `MOUSE_ENTERED_TARGET` 类型事件添加过滤器和处理器。子节点可以添加 `MOUSE_ENTERED` 和/或 `MOUSE_ENTERED_TARGET` 的事件过滤器和处理器:

- 当鼠标进入子节点，父节点收到 `MOUSE_ENTERED_TARGET` 事件
- 在事件进入子节点（目标节点）之前，事件类型被修改为 `MOUSE_ENTERED`

因此，在单个事件中，目标节点收到 `MOUSE_ENTERED` 事件，它的所有父节点收到 `MOUSE_ENTERED_TARGET` 事件。因为 `MOUSE_ENTERED` 是 `MOUSE_ENTERED_TARGET` 的子类型，所以目标节点上的任一事件处理器都可以处理该事件。以上同样适用于 `MOUSE_EXITED` 事件。

在父节点事件处理器中，有时需要区分是父节点自身还是子节点触发了 `MOUSE_ENTERED_TARGET` 事件。可以在事件过滤器或处理器中查看 `Event.getTarget()` 返回的目标节点是否为父节点进行判断。

**示例：** `MOUSE_ENTERED_TARGET` 和 `MOUSE_EXITED_TARGET` 演示

@import "images/2023-06-26-13-44-28.png" {width="250px" title=""}

```java {.line-numbers}
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import static javafx.scene.input.MouseEvent.*;

public class MouseEnteredExitedTarget extends Application {

    private CheckBox consumeCbx = new CheckBox("Consume Events");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle circle = new Circle(50, 50, 50);
        circle.setFill(Color.GRAY);

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(circle, consumeCbx);

        // 创建 mouse 事件处理器
        EventHandler<MouseEvent> circleHandler = e -> handleCircle(e);
        EventHandler<MouseEvent> circleTargetHandler =
                e -> handleCircleTarget(e);
        EventHandler<MouseEvent> hBoxTargetHandler = e -> handleHBoxTarget(e);

        // 为 HBox 添加 MOUSE_ENTERED_TARGET 和 MOUSE_EXITED_TARGET 事件过滤器
        root.addEventFilter(MOUSE_ENTERED_TARGET, hBoxTargetHandler);
        root.addEventFilter(MOUSE_EXITED_TARGET, hBoxTargetHandler);

        // 为 Circle 添加 MOUSE_ENTERED_TARGET 和 MOUSE_EXITED_TARGET 事件处理器
        circle.addEventHandler(MOUSE_ENTERED_TARGET, circleTargetHandler);
        circle.addEventHandler(MOUSE_EXITED_TARGET, circleTargetHandler);

        // 为 Circle 添加 MOUSE_ENTERED 和 MOUSE_ENTERED 事件处理器
        circle.addEventHandler(MOUSE_ENTERED, circleHandler);
        circle.addEventHandler(MOUSE_EXITED, circleHandler);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Mouse Entered Target and Exited Target Events");
        stage.show();
    }

    public void handleCircle(MouseEvent e) {
        print(e, "Circle Handler");
    }

    public void handleCircleTarget(MouseEvent e) {
        print(e, "Circle Target Handler");
    }

    public void handleHBoxTarget(MouseEvent e) {
        print(e, "HBox Target Filter");
        if (consumeCbx.isSelected()) {
            e.consume();
            System.out.println("HBox consumed the " + e.getEventType() + " event");
        }
    }

    public void print(MouseEvent e, String msg) {
        String type = e.getEventType().getName();
        String source = e.getSource().getClass().getSimpleName();
        String target = e.getTarget().getClass().getSimpleName();
        System.out.println(msg + ": Type=" + type +
                ", Target=" + target +
                ", Source=" + source);
    }
}
```

添加 `Circle` 和 `CheckBox` 到 `HBox`:

- 为 `HBox` 添加 `MOUSE_ENTERED_TARGET` 和 `MOUSE_EXITED_TARGET` 事件过滤器
- 为 `Circle` 同时添加两套事件处理器

运行程序：

- 不勾选复选框，鼠标进入或离开 `Circle`，`HBox` 收到 `MOUSE_ENTERED_TARGET` 和 `MOUSE_EXITED_TARGET` 事件。`Circle` 收到 `MOUSE_ENTERED` 和 `MOUSE_EXITED` 事件
- 勾选复选框，`HBox` 收到 `MOUSE_ENTERED_TARGET` 和 `MOUSE_EXITED_TARGET` 事件，并消耗事件，`Circle` 没有收到任何事件
- 鼠标进入或离开 `HBox`，即白色区域，`HBox` 收到 `MOUSE_ENTERED` 和 `MOUSE_EXITED` 事件，Circle 没有收到任何事件
