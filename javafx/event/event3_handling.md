# 处理事件

2023-06-25, 19:56
****
## 1. 简介

处理事件，表示执行应用逻辑来响应事件的发生。应用逻辑包含在事件过滤器和事件处理器中，时 `EventHandler` 接口类型：

```java
public interface EventHandler<T extends Event> extends EventListener
    void handle(T event);
}
```

`EventHandler` 为泛型接口，位于 `javafx.event` 包中，扩展 `EventListener` 接口。其中 `handle()` 方法接受 `Event` 参数。

事件过滤器和事件处理器都是使用 `EventHanler` 接口。查看 `EventHandler` 实现类无法判断是过滤器还是处理器。实际上，一个 `EventHandler` 对象可以同为作为过滤器和处理器。过滤器和处理器的差别在于它们何时注册到节点：

- 在事件捕获阶段，执行过滤器的 `handle()` 方法
- 在事件冒泡节点，执行处理器的 `handle()` 方法

```ad-tip
处理事件：实现 `EventHandler` 接口，在 `handle()` 方法中编写应用程序逻辑，并将其注册为节点的过滤器或/和处理器。
```

## 2. 创建过滤器和处理器

创建事件过滤器或处理器，实现 `EventHandler` 接口即可。

对简单的事件处理逻辑，使用 lambda 表达式最佳：

```java
EventHandler<MouseEvent> aHandler = e -> /* Event handling code goes here */;
```

例如，创建一个 `MouseEvent` 处理器，打印鼠标事件类型：

```java
EventHandler<MouseEvent> mouseEventHandler =
        e -> System.out.println("Mouse event type: " + e.getEventType());
```

## 3. 注册过滤器和处理器

如果节点要处理特定类型的事件，需要为其注册事件过滤器和处理器:

- 当事件发生时，节点注册的过滤器和处理器的 `handle()` 方法会按照上一节介绍的规则被调用
- 当对该事件不再感兴趣，需要从该节点注销事件过滤器和处理器

JavaFX 提供了两种注册和注销事件处理器和过滤器的方法：

- `addEventFilter()`, `addEventHandler()` 和 `removeEventFilter()`,
`removeEventHandler()`
- `onXXX` 系列便捷属性

### 3.1. addXXX 和 removeXXX 方法

`Node`、`Scene` 和 `Window` 类中定义了 `addEventFilter()` 和 `addEventHandler()` 方法用于注册事件过滤器和处理器：

```java
<T extends Event> void addEventFilter(EventType<T> eventType,         
                EventHandler<? super T> eventFilter)
<T extends Event> void addEventHandler(EventType<T> eventType,
                EventHandler<? super T> eventHandler)
```

有些类，如 `MenuItem` 和 `TreeItem` 可以作为事件 target，然而它们不是 `Node` 子类，不支持过滤器。

例如，处理 `Circle` 的鼠标单击事件：

```java
import javafx.scene.shape.Circle;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

...
Circle circle = new Circle (100, 100, 50);
// 创建 MouseEvent 过滤器
EventHandler<MouseEvent> mouseEventFilter =
        e -> System.out.println("Mouse event filter has been called.");
// 创建 MouseEvent 处理器
EventHandler<MouseEvent> mouseEventHandler =
        e -> System.out.println("Mouse event handler has been called.");
// 为 Circle 的鼠标单击事件注册过滤器和处理器
circle.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventFilter);
circle.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
```

上面创建了两个 `EventHandler` 对象，均在控制台打印一条消息。最后两句将一个 `EventHandler` 注册为事件过滤器，另一个注册为事件处理器。

允许将一个 `EventHandler` 对象同时注册为过滤器和处理器。例如：

```java
// 创建 MouseEvent EventHandler 对象
EventHandler<MouseEvent> handler = e ->
        System.out.println("Mouse event filter or handler has been called.");
// 将其注册为 Circle 的鼠标单击事件的过滤器和处理器
circle.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
circle.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
```

```ad-tip
多次调用 `addEventFilter()` 和 `addEventHandler()` 可以为一个节点添加多个过滤器和处理器。
```

- 演示 `Circle` 的鼠标单击事件处理的完整程序

点击 `Circle`，事件过滤器和处理器依次被调用。在 `Circle` 外点击鼠标，鼠标点击事件依然发生，但是看不到任何输出，因为没有为 `HBox`, `Scene` 和 `Stage` 注册事件过滤器或处理器。

```java
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class EventRegistration extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle circle = new Circle(100, 100, 50);
        circle.setFill(Color.CORAL);

        // 创建 MouseEvent 过滤器
        EventHandler<MouseEvent> mouseEventFilter =
                e -> System.out.println("Mouse event filter has been called.");

        // 创建 MouseEvent 处理器
        EventHandler<MouseEvent> mouseEventHandler =
                e -> System.out.println("Mouse event handler has been called.");

        // 为 Circle 的鼠标单击事件注册 MouseEvent 过滤器和处理器
        circle.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventFilter);
        circle.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);

        HBox root = new HBox();
        root.getChildren().add(circle);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Registering Event Filters and Handlers");
        stage.show();
        stage.sizeToScene();
    }
}
```

点击 `Circle`，输出：

```
Mouse event filter has been called.
Mouse event handler has been called.
```

- 从 `Circle` 移除事件过滤器

```java
// 创建 MouseEvent EventHandler 对象
EventHandler<MouseEvent> handler = e ->
        System.out.println("Mouse event filter or handler has been called.");
// 为 Circle 的鼠标单击事件注册相同的 EventHandler 对象为过滤器和处理器
circle.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
circle.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
...
// 注销过滤器和处理器
circle.removeEventFilter(MouseEvent.MOUSE_CLICKED, handler);
circle.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler);
```

注销 `EventHandler` 后，在事件发生时不再调用其 `handle()` 方法。

### 3.2. onXXX 便捷属性

`Node`, `Scene` 和 `Window` 类包含一些事件属性，用于存储某些事件类型的事件处理程序。

这些属性统一采用 `onXXX` 命名格式。例如，`onMouseClicked` 属性保存鼠标点击类型事件的事件处理程序；`onKeyTyped` 属性保存按键类型事件的事件处理程序。

使用这些属性的 `setOnXXX()` 方法为节点注册事件处理程序。例如，`setOnMouseClicked()` 为鼠标单击事件注册事件处理程序；`setOnKeyTyped()` 为按键事件注册事件处理程序。各种类中的 `setOnXXX()` 为注册事件处理器的便捷方法。

`onXXX` 便捷属性使用要点：

- 只支持注册事件处理器，不支持事件过滤器，注册事件过滤器使用 `addEventFilter()`
- 只支持注册一个事件处理器，要注册多个处理器，使用 `addEventHandler()`
- 只有一些常用的事件类型的属性，例如，`Node` 和 `Scene` 中有 `onMouseClicked` 属性，`Window` 中没有；`Window` 类有 `onShowing` 属性，`Node` 和 `Scene` 类没有

下例和上例相同。只是将 `addEventHandler` 替换为 `onMouseClicked`：

```java
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class EventHandlerProperties extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle circle = new Circle(100, 100, 50);
        circle.setFill(Color.CORAL);

        HBox root = new HBox();
        root.getChildren().add(circle);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using convenience event handler properties");
        stage.show();
        stage.sizeToScene();

        // 创建 MouseEvent 过滤器
        EventHandler<MouseEvent> eventFilter =
                e -> System.out.println("Mouse event filter has been called.");

        // 创建 MouseEvent 处理器
        EventHandler<MouseEvent> eventHandler =
                e -> System.out.println("Mouse event handler has been called.");

        // 使用 addEventFilter() 注册过滤器
        circle.addEventFilter(MouseEvent.MOUSE_CLICKED, eventFilter);

        // 使用 onMouseCicked 属性注册处理器
        circle.setOnMouseClicked(eventHandler);
    }
}
```

便捷事件属性没有提供单独**注销事件处理器**的方法。将该属性设置为 `null` 可以注销已经注册的事件处理器：

```java
// 注册处理器
circle.setOnMouseClicked(eventHandler);
...
// 注销处理器
circle.setOnMouseClicked(null);
```

定义 `onXXX` 事件属性的类还定义了 `getOnXXX()` 方法返回注册的处理器。如果没有注册任何处理器，返回 `null`。

