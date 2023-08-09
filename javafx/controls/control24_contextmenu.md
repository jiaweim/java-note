# ContextMenu

2023-07-25, 19:47
****
## 1. 简介

ContextMenu 是一个 pop-up 控件，一般称为上下文菜单或弹出式菜单。

ContextMenu 默认隐藏，用户必须发出指令才会显示，比如点击鼠标右键。选择后，ContextMenu 再次隐藏。用户可以通过点击 ContextMenu bounds 外或按 Esc 键关闭 ContextMenu。

context-menu 有一个可用性问题，用户很难知道它的存在。非技术用户往往不习惯通过鼠标右键进行选择，对这些用户可以使用工具栏或 Buttons 提供相同选项。

ContextMenu.getItems() 返回 `ObservableList<MenuItem>`，为 context-menu 包含的 menu-items。

**示例：** ContextMenu

创建三个 menu-items。

```java
MenuItem rectItem = new MenuItem("Rectangle");
MenuItem circleItem = new MenuItem("Circle");
MenuItem ellipseItem = new MenuItem("Ellipse");
```

ContextMenu 的默认构造函数创建空 menu，需要添加 menu-items:

```java
ContextMenu ctxMenu = new ContextMenu();
ctxMenu.getItems().addAll(rectItem, circleItem, ellipseItem);
```

创建 ContextMenu 时指定 menu-items:

```java
ContextMenu ctxMenu = new ContextMenu(rectItem, circleItem, ellipseItem);
```

context-menu 一般为控件提供常用功能，如为文本输入控件提供 Cut, Copy 和 Paste 功能。

部分控件有默认的 context-menu，它们一般有 contextMenu 属性，通过 `setContextMenu()` 设置自定义 context-menu。

**示例：** 为 TextField 设置 context-menu

```java
ContextMenu ctxMenu = ...
TextField nameFld = new TextField();
nameFld.setContextMenu(ctxMenu);
```

```ad-tip
激活 empty context-menu 不显示。如果想禁用控件的默认 context-menu，将其 contextMenu 属性设置为空的 ContextMenu 即可。
```

非 Control 类型的 Node 没有 contextMenu 属性。对这些 node 需要使用 ContextMenu 的 show() 方法为它们设置 context-menu。show() 可以完全控制 context-menu 显示的位置，在 Control 中也可以用来微调 context-menu 的位置。show() 为重载方法：

```java
void show(Node anchor, double screenX, double screenY)
void show(Node anchor, Side side, double dx, double dy)
```

第一个版本使用相对屏幕的坐标显示 anchor 的 context-menu。通常，在鼠标单击事件中显示 context-menu，而 MouseEvent 提供了 getScreenX() 和 getScreenY() 方法返回鼠标指针相对屏幕的坐标。

**示例：** 在屏幕坐标 (100, 100) 显示 canvas 的 context-menu

```java
Canvas canvas = ...
ctxMenu.show(canvas, 100, 100);
```

第二个版本指定 context-menu 相对 anchor 的位置：

- side 为 enum Side 类型：TOP, RIGHT, BOTTOM, LEFT，指定 context-menu 在 anchor 哪一边
- dx 和 dy 参数指定 context-menu 相对 anchor 坐标系统的位置

side 类似于对 anchor 的坐标系进行平移，dx 和 dy 是相对平移后坐标系的坐标：

![](Pasted%20image%2020230725174441.png)
side 参数：

- 注意，`side` 参数的 `LEFT` 和 `RIGHT` 值与 `anchor` 的方向有关，对方向为 RIGHT_TO_LEFT 的 node，LEFT 表示 node 的右边。
- 当 side 参数为 TOP, LEFT 或 null，dx 和 dy 是相对 node 原始坐标系的值。

**示例：** 在 anchor node 的左上角显示 context-menu

```java
ctxMenu.show(anchor, Side.TOP, 0, 0);
```

**示例：** 在 anchor 的左下角显示 context-menu

```java
ctxMenu.show(anchor, Side.BOTTOM, 0, 0);
```

dx 和 dy 的值可以是负数。

**示例：** 在 anchor 左上角上方10px 处显示 context-menu

```java
ctxMenu.show(myAnchor, Side.TOP, 0, -10);
```

ContextMenu 的 hide() 方法隐藏 context-menu。

通常选择 menu-item 后 ContextMenu  自动隐藏；如果使用 CustomMenuItem，且 hideOnClick 属性为 true，此时需要调用 hide() 方法来隐藏 context-menu。

通常需要为 context-menu 的 menu-item 添加 ActionEvent handler。ContextMenu 包含一个 onAction 属性，为 ActionEvent handler，如果设置了 ContextMenu 的 ActionEvent handler，那么每次激活 menu-item，都会调用该 handler。

**示例：** ContextMenu

- 显示 Label 和 Canvas
- 右键 Canvas，显示包含 3 个 menu-items 的 ContextMenu：Rectangle, Circle 和 Ellipse
- 选择 menu-item，在 Canvas 绘制对应的形状

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ContextMenuTest extends Application {
	// A canvas to draw shapes
	Canvas canvas = new Canvas(200, 200);

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) {
		// Add mouse click event handler to the canvas to show the context menu
		canvas.setOnMouseClicked(e -> showContextMenu(e));

		BorderPane root = new BorderPane();  
		root.setTop(new Label("Right click below to display a context menu."));
		root.setCenter(canvas);        
		root.setStyle("-fx-padding: 10;" + 
		              "-fx-border-style: solid inside;" + 
		              "-fx-border-width: 2;" +
		              "-fx-border-insets: 5;" + 
		              "-fx-border-radius: 5;" + 
		              "-fx-border-color: blue;");

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Using Context Menus");
		stage.show();
	}

	public void showContextMenu(MouseEvent me) {
		// Show menu only on right click
		if (me.getButton() == MouseButton.SECONDARY) { 
			MenuItem rectItem = new MenuItem("Rectangle");
			MenuItem circleItem = new MenuItem("Circle");
			MenuItem ellipseItem = new MenuItem("Ellipse"); 
			rectItem.setOnAction(e -> draw("Rectangle"));
			circleItem.setOnAction(e -> draw("Circle"));
			ellipseItem.setOnAction(e -> draw("Ellipse")); 
			ContextMenu ctxMenu = 
					new ContextMenu(rectItem, circleItem, ellipseItem);
			ctxMenu.show(canvas, me.getScreenX(), me.getScreenY());
		}
	}

	public void draw(String shapeType) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, 200, 200); // clear the canvas first
		gc.setFill(Color.TAN);

		if (shapeType.equals("Rectangle")) {
			gc.fillRect(0, 0, 200, 200);
		} else if (shapeType.equals("Circle")) { 
			gc.fillOval(0, 0, 200, 200); 
		} else if (shapeType.equals("Ellipse")) {
			gc.fillOval(10, 40, 180, 120);
		}
	}
}
```

![|300](Pasted%20image%2020230725194551.png)

## 2. ContextMenu CSS

ContextMenu 的 CSS 样式类名默认为 context-menu。

详情请参考 modena.css 文件。

ContextMenu 默认使用 DropShadow 特效，下面的 CSS 将 font-size 设置为 8pt，删除默认特效：

```css
.context-menu {
    -fx-font-size: 8pt;
    -fx-effect: null;
}
```
