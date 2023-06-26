# 事件过滤器和处理器的执行顺序

2023-06-25, 20:55
****
## 1. 简介

事件过滤器和处理器的执行顺序：

- 过滤器在处理器之前执行。

过滤器按照从顶层的父级到事件 target 的从父到子的顺序执行；处理器按照从子到父相反的顺序执行。

- 对同一个节点，特定事件类型的过滤器和处理器在泛型类型之前执行。

例如，假设为一个节点注册了 `MouseEvent.ANY` 和 `MouseEvent.MOUSE_CLICKED` 处理器。两个事件类型的处理器都可以处理 `MOUSE_CLICKED` 事件。当在该节点上单击鼠标，先执行 `MouseEvent.MOUSE_CLICKED` 事件类型的处理器，再执行 `MouseEvent.ANY` 事件类型的处理器。注意，在 `MOUSE_CLICKED` 事件之前，先发生按下鼠标和释放鼠标事件，这些事件由 `MouseEvent.ANY` 事件类型的处理器处理。

- 一个节点上相同事件类型的多个过滤器或处理器的顺序不确定。

这个规则有个例外，使用 `addEventHandler()` 注册的处理器比使用 `setOnXXX()` 注册的处理器先执行。

## 2. 示例一

下面演示不同节点的事件过滤器和处理器的执行顺序。`Scene` 中的 `HBox` 包含一个 `Rectangle` 和一个 `Circle`。

```java
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class CaptureBubblingOrder extends Application {

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

        Scene scene = new Scene(root);

        // Create two EventHandlders
        EventHandler<MouseEvent> filter = e -> handleEvent("Capture", e);
        EventHandler<MouseEvent> handler = e -> handleEvent("Bubbling", e);

        // Register filters
        stage.addEventFilter(MOUSE_CLICKED, filter);
        scene.addEventFilter(MOUSE_CLICKED, filter);
        root.addEventFilter(MOUSE_CLICKED, filter);
        circle.addEventFilter(MOUSE_CLICKED, filter);

        // Register handlers
        stage.addEventHandler(MOUSE_CLICKED, handler);
        scene.addEventHandler(MOUSE_CLICKED, handler);
        root.addEventHandler(MOUSE_CLICKED, handler);
        circle.addEventHandler(MOUSE_CLICKED, handler);

        stage.setScene(scene);
        stage.setTitle("Event Capture and Bubbling Execution Order");
        stage.show();
    }

    public void handleEvent(String phase, MouseEvent e) {
        String type = e.getEventType().getName();
        String source = e.getSource().getClass().getSimpleName();
        String target = e.getTarget().getClass().getSimpleName();

        // Get coordinates of the mouse cursor relative to the event source
        double x = e.getX();
        double y = e.getY();

        System.out.println(phase + ": Type=" + type +
                ", Target=" + target + ", Source=" + source +
                ", location(" + x + ", " + y + ")");
    }
}
```

```
Capture: Type=MOUSE_CLICKED, Target=Circle, Source=Stage, location(83.33333333333333, 66.66666666666667)
Capture: Type=MOUSE_CLICKED, Target=Circle, Source=Scene, location(83.33333333333333, 66.66666666666667)
Capture: Type=MOUSE_CLICKED, Target=Circle, Source=HBox, location(83.33333333333333, 66.66666666666667)
Capture: Type=MOUSE_CLICKED, Target=Circle, Source=Circle, location(63.33333333333333, 46.66666666666667)
Bubbling: Type=MOUSE_CLICKED, Target=Circle, Source=Circle, location(63.33333333333333, 46.66666666666667)
Bubbling: Type=MOUSE_CLICKED, Target=Circle, Source=HBox, location(83.33333333333333, 66.66666666666667)
Bubbling: Type=MOUSE_CLICKED, Target=Circle, Source=Scene, location(83.33333333333333, 66.66666666666667)
Bubbling: Type=MOUSE_CLICKED, Target=Circle, Source=Stage, location(83.33333333333333, 66.66666666666667)

Capture: Type=MOUSE_CLICKED, Target=Rectangle, Source=Stage, location(205.33333333333334, 93.33333333333333)
Capture: Type=MOUSE_CLICKED, Target=Rectangle, Source=Scene, location(205.33333333333334, 93.33333333333333)
Capture: Type=MOUSE_CLICKED, Target=Rectangle, Source=HBox, location(205.33333333333334, 93.33333333333333)
Bubbling: Type=MOUSE_CLICKED, Target=Rectangle, Source=HBox, location(205.33333333333334, 93.33333333333333)
Bubbling: Type=MOUSE_CLICKED, Target=Rectangle, Source=Scene, location(205.33333333333334, 93.33333333333333)
Bubbling: Type=MOUSE_CLICKED, Target=Rectangle, Source=Stage, location(205.33333333333334, 93.33333333333333)
```

为 `Stage`, `Scene`, `HBox`, `Circle` 添加鼠标单击事件的过滤器和处理器。点击 `Circle`，输出过滤器和处理的执行顺序。输出包括：事件阶段、类型、target、事件源以及位置。

可以看到，当事件从一个节点传递到另一个节点，事件源随之变化。

位置是相对事件源的位置。每个节点都有自己的坐标系，所以鼠标单击一个点对不同节点的 (x,y) 值不同。

单击 `Rectangle` 可以看到与 `Circle` 相同的父路径。因为没有给 `Rectangle` 注册过滤器和处理器，所以看不到 `Rectangle` 的输出，但是事件发生后，依然按照捕获和冒泡两个顺序执行。

## 3. 示例二

下面演示一个节点多个处理器的执行顺序。为 `Circle` 注册三个处理器：

- 一个 `MouseEvent.ANY` 类型
- 一个使用 `addEventHandler()` 添加的 `MouseEvent.MOUSE_CLICKED` 类型
- 一个使用 `setOnMouseClicked()` 添加的 `MouseEvent.MOUSE_CLICKED` 类型

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class HandlersOrder extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle circle = new Circle(50, 50, 50);
        circle.setFill(Color.CORAL);

        HBox root = new HBox();
        root.getChildren().addAll(circle);
        Scene scene = new Scene(root);
        
        // 注册三个处理器
        // 这个最后执行
        circle.addEventHandler(MouseEvent.ANY, e -> handleAnyMouseEvent(e));

        // 这个最先执行
        circle.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> handleMouseClicked("addEventHandler()", e));

        // 这个老二
        circle.setOnMouseClicked(e -> handleMouseClicked("setOnMouseClicked()", e));

        stage.setScene(scene);
        stage.setTitle("Execution Order of Event Handlers of a Node");
        stage.show();
    }

    public void handleMouseClicked(String registrationMethod, MouseEvent e) {
        System.out.println(registrationMethod
                + ": MOUSE_CLICKED handler detected a mouse click.");
    }

    public void handleAnyMouseEvent(MouseEvent e) {
        // 输出 mouse-clicked 事件信息，忽略其它鼠标事件，如 mouse-pressed, mouse-released
        if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
            System.out.println("MouseEvent.ANY handler detected a mouse click.");
        }
    }
}
```

```
addEventHandler(): MOUSE_CLICKED handler detected a mouse click.
setOnMouseClicked(): MOUSE_CLICKED handler detected a mouse click.
MouseEvent.ANY handler detected a mouse click.
```
