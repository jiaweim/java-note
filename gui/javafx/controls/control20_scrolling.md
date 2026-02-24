# Scrolling

2023-07-25, 11:58
****
## 1. 简介

JavaFX 提供了两个支持滚动条的组件，`ScrollBar` 和 `ScrollPane`。它们一般不单独使用，而是为其它组件提供滚动条。

## 2. ScrollBar

`ScrollBar` 本身没有滚动条功能，它作为水平或垂直滚动条，让用户在一定范围内选择。开发者很少直接使用 `ScrollBar`，一般用于创建支持 scrolling 的组件，如 ScrollPane。

![|400](Pasted%20image%2020230725110646.png)

ScrollBar 包含 4 部分：

- Increment button，用于增加值
- Decrease button, 用于减少值
- thumb, 用于显示当前值
- track, thumb 滚动的范围

ScrollBar 默认构造创建水平 scrollbar，可以使用 setOrientation() 设置为垂直：

```java
// 创建水平 scrollbar
ScrollBar hsb = new ScrollBar();

// 创建垂直 scrollbar
ScrollBar vsb = new ScrollBar();
vsb.setOrientation(Orientation.VERTICAL);
```

min 和 max 属性表示 ScrollBar 的最小值和最大值，value 表示当前值。默认值分别为 0, 100, 0。设置：

```java
ScrollBar hsb = new ScrollBar();
hsb.setMin(0);
hsb.setMax(200);
hsb.setValue(150);
```

为 value 属性添加 ChangeListener 监听当前值的变化。

修改 value 的方式有三种：

- 代码 `setValue()`, `increment()`, `decrement` 方法
- 用于拖动 thumb
- 点击 increment 或 decrement 按钮

blockIncrement 和 unitIncrement 属性分别指定用户单击 track, increment 和 decrement buttons 时对 value 的调整值。一般将 blockIncrement 设置得比 unitIncrement 大。

ScrollBar 的 CSS 样式类名默认为 scroll-bar。ScrollBar 支持 2 个 CSS pseudo-classes: horizontal 和 vertical。

开发者很少直接使用 ScrollBar。而是为 ScrollPane 等组件提供滚动功能。如果需要为控件提供滚动功能，推荐使用 ScrollPane。

## 3. ScrollPane

`ScrollPane` 包含一个水平 `ScrollBar`、一个垂直 `ScrollBar`和一个 `content node`。

如果想为多个 node 提供滚动视图，可以将它们添加到一个 layoutPane 中，如 `GridPane`，然后将 layoutPane 作为 content node 添加到 `ScrollPane` 中。部分组件自带有滚动功能，如 `TextArea`，就是按照这种方式处理的。

创建 ScrollPane：

```java
Label poemLbl1 = ...
Label poemLbl2 = ...

// Create an empty ScrollPane
ScrollPane sPane1 = new ScrollPane();

// Set the content node for the ScrollPane
sPane1.setContent(poemLbl1);

// Create a ScrollPane with a content node
ScrollPane sPane2 = new ScrollPane(poemLbl2);
```

```ad-note
ScrollPane 根据 content 的 layoutBounds 的提供滚动功能。如果 content 使用了特效或变换，则需要将 content 包装在 Group 种，再将 Group 添加到 ScrollPane。
```

ScrollPane 包含许多属性，大多数不常用：

|属性|说明|
|---|---|
|content|内容，Node 类型|
|pannable|拖动，按住鼠标右键、左键或同时按住左右键，default=false|
|fitToHeight, fitToWidth|content node 是否 resized 以充满整个viewport, default=false|
|hbarPolicy, vbarPolicy|enum ScrollBarPolicy 类型，可用值有 `ALWAYS`, `AS_NEEDED`, `NEVER`，用于设置何时显示 scrollbar，`ALWAYS` 表示一直闲置scrollbar|
|hmin, hmax, hvalue|水平 scrollbar 的最小值、最大值和当前值|
|vmin, vmax, vvalue|垂直 scrollbar 的最小值、最大值和当前值|
|prefViewportHeight, prefViewportWidth|preferred height and width|
|viewportBounds|viewport 的实际 bounds|

content 属性为 Node 类型的 ObjectProperty，指定 content node。可以通过滚动条或 panning 来滚动 content：

- 对 panning，按下左键和/或右键再拖动鼠标
- ScrollPane 默认不支持 pannable，只能通过滚动条来滚动内容

pannable 属性为 boolean 类型，指定 ScrollPane 是否 pannable。调用 setPannable(true) 使 ScrollPane pannable。

fitToHeight 和 fitToWidth 属性指定是否调整 content node 尺寸以匹配 viewport 的尺寸，默认 false。当 content node 为 nonreziable，忽略这两属性。

hbarPolicy 和 vbarPolicy 属性为 `ScrollPane.ScrollBarPolicy` enum 类型，指定何时显示水平和垂直滚动条。可选值：ALWAYS, AS_NEEDED, NEVER。

- ALWAYS 表示一直显示滚动条
- AS_NEEDED 根据内容大小在需要时显示滚动条
- NEVER 不显示滚动条

hmin, hmax 和 hvalue 属性指定水平滚动条的 min, max 和 value 属性。vmin, vmax 和 vvalue 指定垂直滚动条的 min, max 和 value 属性。通常不设置这些属性，它们会根据 content 以及用户操作自动调整。

prefViewportHeight 和 prefViewportWidth 属性为 content node 的 viewport 的 prefHeight 和 prefWidth。

viewportBounds 属性为 Bounds 类型，是 viewport 的实际 bounds。

**示例：** ScrollPane

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ScrollPaneTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label poemLbl = new Label("I told her this; her laughter light\n" +
                "Is ringing in my ears;\n" +
                "And when I think upon that night\n" +
                "My eyes are dim with tears.");

        // Create a scroll pane with poemLbl as its content
        ScrollPane sPane = new ScrollPane(poemLbl);
        sPane.setPannable(true);

        HBox root = new HBox(sPane);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ScrollPane Controls");
        stage.show();
    }
}
```

![|300](Pasted%20image%2020230725115800.png)

### 3.1. CSS

ScrollPane 的 CSS 样式类名默认为 scroll-pane。

参考 modena.css 示例。
