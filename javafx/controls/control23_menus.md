# 菜单

2023-07-25, 17:06
****
## 1. 简介

菜单以紧凑的形式为用户提供操作列表。也可以使用 button 实现相同功能，使用菜单还是 button 取决于个人偏好。菜单的最大优点就是紧凑。

```ad-tip
还有一种菜单称为上下文菜单（context menu）或弹出菜单（pop-up menu），可以按需显示。
```

如下所示，菜单由几个部分组成：

- menu-bar 菜单最顶层，包含菜单选项，menu-bar 总是可见
- menu 包含 sub-menus 和 menu items
- 例如，File menu 包含 4 个 menu items: New, Open, Save, Exit
- File menu 包含 2 个 separator menu item
- "Save As" submenu 包含 2 个 menu items: Text 和 PDF
- menuitem 是 actionable item

![|350](Pasted%20image%2020230725144333.png)

菜单的使用包含多个步骤：

1. 创建 menu-bar，添加到 layoutPane
2. 创建 menus，添加到 menu-bar
3. 创建 menu-items，添加到 menus
4. 为 menu-items 添加 ActionEvent handler

## 2. Menu Bar

menu-bar 是水平 bar，作为菜单的容器。MenuBar 类表示 menu-bar。创建 MenuBar:

```java
MenuBar menuBar = new MenuBar();
```

MenuBar 一般添加到窗口的顶部，例如，以 BorderPane 作为 root：

```java
// Add the MenuBar to the top region
BorderPane root = new BorderPane();
root.setBottom(menuBar); // 这个是搞笑
```

MenuBar 包含一个 useSystemMenuBar 属性，boolean 类型，默认 false。设置为 true 时，如果平台支持，则使用系统菜单栏。例如，Mac 支持系统菜单栏，在 Mac 上将该属性设置为 true，MenuBar 会使用系统菜单栏显示菜单：

```java
// Let the MenuBar use system menu bar
menuBar.setUseSystemMenuBar(true);
```

如果不添加 menus，MenuBarr 自身不会占用任何控件。MenuBar 的尺寸根据所含的 menus 计算。MenuBar 将所有 menus 保存在 ObservableList 中，getMenus() 返回该 list:

```java
// Add some menus to the MenuBar
Menu fileMenu = new Menu("File");
Menu editMenu = new Menu("Edit");
menuBar.getMenus().addAll(fileMenu, editMenu);
```

## 3. Menus

menu 包含一个可操作项目列表，在需要时显示（点击菜单）。menu 要么添加到 menu-bar，要么作为 submenu 添加到其它 menu。

Menu 类表示 menu。menu 可以包含 text 和 graphic。

- 创建 Menu 后设置 text 和 graphic

```java
// Create a Menu with an empty string text and no graphic
Menu aMenu = new Menu();

// Set the text and graphic to the Menu
aMenu.setText("Text");
aMenu.setGraphic(new ImageView(new Image("image.jpg")));
```

- 创建 Menu 时设置 text 和 graphic

```java
// Create a File Menu
Menu fileMenu1 = new Menu("File");

// Create a File Menu
Menu fileMenu2 = new Menu("File", new ImageView(new Image("file.jpg")));
```

Menu 类继承自 MenuItem，而 MenuItem 继承 Object，不是 Node 类型。所以不能将 Menu 直接添加到 scene graph，而是添加到 MenuBar。

MenuBar.getMenus() 返回 `ObservableList<Menu>`，可用于添加 Menu。

**示例：** 添加 4 个 Menu 到 MenuBar

```java
Menu fileMenu = new Menu("File");
Menu editMenu = new Menu("Edit");
Menu optionsMenu = new Menu("Options");
Menu helpMenu = new Menu("Help");

// Add menus to a menu bar
MenuBar menuBar = new MenuBar();
menuBar.getMenus().addAll(fileMenu, editMenu, optionsMenu, helpMenu);
```

点击 menu，通常显示 menu-items，不执行其它操作。

Menu 类包含如下属性：

- onShowing
- onShown
- onHiding
- onHidden
- showing

`onShowing` event handler 在 menu-items 显示之前被调用。

onShown event handler 在 menu-items 显示之后被调用。

onHiding 和 onHidden event handlers 与 onShowing 和 onShown 对应。

onShowing 常用于根据指定条件启用或禁用菜单项。例如，假设有一个 Edit menu，包含 Cut, Copy 和 Paste menu-items。在 onShowing event handler 中，可以根据焦点是否在文本输入控件中来启用或禁用这些 menu-items:

```java
editMenu.setOnAction(e -> {/* Enable/disable menu items here */});
```

```ad-tip
用户不喜欢在使用 GUI 程序时出现意外，为了更好的用户体验，应该禁用 menu-items，而不是让它们不可见。使 menu-items 不可见会改变其它 menu-items 的位置，用户需要适应这种位置变化。
```

showing 属性为 read-only boolean 属性。menu 显示时为 true，隐藏时为 false。

**示例：** Menu

创建 4 个 menus, 1 个 menu-bar，将 menus 添加到 menu-bar，将 menu-bar 添加到 BorderPane 的顶部。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create some menus
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu optionsMenu = new Menu("Options");
        Menu helpMenu = new Menu("Help");

        // Add menus to a menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, optionsMenu, helpMenu);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Menus");
        stage.show();
    }
}
```

![|250](Pasted%20image%2020230725154448.png)

## 4. Menu Items

menu-item 是 menu 中的可操作项。menu-item 关联的功能通过鼠标或键盘来触发。menu-item 可以通过 CSS 设置样式。

MenuItem 类表示 menu-item。MenuItem 不是 Node，而是继承 Object，因此不能直接将 MenuItem 添加到 scene graph，而是添加到 menu。

有多种类型的 menu-item。MenuItem 的类图如下：

![](Pasted%20image%2020230725155105.png)

- MenuItem 用于可操作菜单
- RadioMenuItem 用于互斥选项
- CheckMenuItem 用于切换选项
- Menu，用于 menu-item 时，充当子菜单
- CustomMenuItem，将任意 Node 用作菜单
- SeparatorMenuItem，将分隔符用作 menu-item

### 4.1. MenuItem

MenuItem 表示可操作选项。点击时，注册的 ActionEvent handler 被调用。

**示例：** 创建 Exit MenuItem，添加 ActionEvent handler，点击时退出应用

```java
MenuItem exitItem = new MenuItem("Exit");
exitItem.setOnAction(e -> Platform.exit());
```

Menu.getItems() 返回 `ObservableList<MenuItem>`，为 Menu 包含的所有 MenuItem，可用来添加或删除 MenuItem：

```java
Menu fileMenu = new Menu("File");
fileMenu.getItems().add(exitItem);
```

MenuItem 包含如下属性，适用于其它 menu-item 类型：

- text
- graphic
- disable
- visible
- accelerator
- mnemonicParsing
- onAction
- onMenuValidation
- parentMenu
- parentPopup
- style
- id

text 和 graphic 属性为 menu-item 的 text 和 graphic，类型分别为 String 和 Node。

disable 和 visible 属性都是 boolean 类型，指定 menu-item 是否禁用和可见。

accelerator 属性为 KeyCombination 类型，用于指定 menu-item 的快捷键。

**示例：** 创建 "Rectangle" menu-item，快捷键设置为 Alt+R

```java
MenuItem rectItem = new MenuItem("Rectangle");
KeyCombination kr = new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN);
rectItem.setAccelerator(kr);
```

mnemonicParsing 属性为 boolean 类型。用于启用或禁用文本解析以检测助记符。对 menu-item 默认为 true。menu-item 的 text 中下划线后的字符为助记符。在 Windows 中，按下 Alt 键或显示所有 menu-items 的助记符。助记符一般带下划线显示。

```java
// Create a menu item with x as its mnemonic character
MenuItem exitItem = new MenuItem("E_xit");
```

`onAction` 属性为 ActionEvent handler，menu-item 激活时调用。

**示例：** 点击鼠标或按下助记符可激活

```java
// Close the application when the Exit menu item is activated
exitItem.setOnAction(e -> Platform.exit());
```

onMenuValidation 属性是一个 event handler，当通过快捷键访问 menu-item，或其所属 menu 的 onShowing event handler 被调用时执行。对 menu，当 menu-items 显示时该 handler 被调用。

parentMenu 是 Menu 类型的 read-only 属性。通过该属性和 Menu 的 getItems() 方法，可以在 menu 和 menu-items 之间切换。

parentPopup 是 ContextMenu 类型的 read-only 属性。它是显示 menu-items 的 ContextMenu，对在常规 menu 中显示的 menu-item，该属性为 null。

### 4.2. RadioMenuItem

RadioMenuItem 表示互斥选项。通常将多个 RadioMenuItem 添加到 ToggleGroup，group 中只能选择一个 RadioMenuItem。

RadioMenuItem 被选中时显示一个复选标记。

**示例：** 创建 3  个 RadioMenuItem 并添加到 ToggleGroup

ToggleGroup 中的 RadioMenuItem 默认被选择一个 。

```java
// Create three RadioMenuItems
RadioMenuItem rectItem = new RadioMenuItem("Rectangle");
RadioMenuItem circleItem = new RadioMenuItem("Circle");
RadioMenuItem ellipseItem = new RadioMenuItem("Ellipse");

// Select the Rantangle option by default
rectItem.setSelected(true);

// Add them to a ToggleGroup to make them mutually exclusive
ToggleGroup shapeGroup = new ToggleGroup();
shapeGroup.getToggles().addAll(rectItem, circleItem, ellipseItem);

// Add RadioMenuItems to a File Menu
Menu fileMenu = new Menu("File");
fileMenu.getItems().addAll(rectItem, circleItem, ellipseItem);
```

![|200](Pasted%20image%2020230725162557.png)

为 RadioMenuItem 添加 ActionEvent handler 用于在被选后执行操作。

**示例：** 为每个 RadioMenuItem 添加一个 ActionEvent handler

```java
rectItem.setOnAction(e -> draw());
circleItem.setOnAction(e -> draw());
ellipseItem.setOnAction(e -> draw());
```

### 4.3. CheckMenuItem

CheckMenuItem 表示具有两种选项的 boolean  menu-item。

使用 ActionEvent handler 监听 CheckMenuItem 状态变化：

```java
CheckMenuItem strokeItem = new CheckMenuItem("Draw Stroke");
strokeItem.setOnAction( e -> drawStroke());
```

选中的 CheckMenuItem 会显示一个复选标记。

### 4.4. SubMenu

Menu 类继承自 MenuItem，所以也可以用作 menu-item，用来创建子菜单。

**示例：** SubMenu

- 创建 MenuBar
- 添加 File menu
- File menu 中添加 "New" 和 "Open" menu-items
- File menu 中添加 "Save As" submenu
- "Save As" submenu 中添加 Text 和 PDF menu-items

```java
MenuBar menuBar = new MenuBar();
Menu fileMenu = new Menu("File");
menuBar.getMenus().addAll(fileMenu);

MenuItem newItem = new MenuItem("New");
MenuItem openItem = new MenuItem("Open");
Menu saveAsSubMenu = new Menu("Save As");

// Add menu items to the File menu
fileMenu.getItems().addAll(newItem, openItem, saveAsSubMenu);

MenuItem textItem = new MenuItem("Text");
MenuItem pdfItem = new MenuItem("PDF");
saveAsSubMenu.getItems().addAll(textItem, pdfItem);
```

![|120](Pasted%20image%2020230725163344.png)

通常不会为 submenu 添加 ActionEvent handler，而是为 onShowing 属性设置 event handler，用于在子菜单显示前启用或禁用 menu-items.

### 4.5. CustomMenuItem

CustomMenuItem 是一个简单但功能强大的 menu-item 类型，可用于设计各种不同的 menu-items。CustomMenuItem 支持使用任何 Node 类型，如 Slider, TextField 或 HBox 用作 menu-item。

CustomMenuItem 包含 2 个属性：

- content
- hideOnClick

content 属性为 Node 类型，指定用作 menu-item 的 Node。

点击 menu-item 时，所有可见的 menu 隐藏，仅 menu-bar 的顶级菜单保持可见。当使用 CustomMenuItem，你可能不希望点击 menu-item 后隐藏 menu，因为用户可能需要继续与 menu-item 进行交互，比如，输入或选择一些数据。hideOnClick 属性为 boolean 类型，用于设置该行为。hideOnClick 属性默认为 true，表示单击 CustomMenuItem 时隐藏所有 menus。

CustomMenuItem 提供了多个构造函数。默认构造函数的 content 属性为 null，hideOnClick 属性为 true:

```java
// Create a Slider control
Slider slider = new Slider(1, 10, 1);

// Create a custom menu item and set its content and hideOnClick properties
CustomMenuItem cmi1 = new CustomMenuItem();
cmi1.setContent(slider);
cmi1.setHideOnClick(false);

// Create a custom menu item with a Slider content and
// set the hideOnClick property to false
CustomMenuItem cmi2 = new CustomMenuItem(slider);
cmi1.setHideOnClick(false);

// Create a custom menu item with a Slider content and false hideOnClick
CustomMenuItem cmi2 = new CustomMenuItem(slider, false);
```

**示例：** CustomMenuItem

```java
CheckMenuItem strokeItem = new CheckMenuItem("Draw Stroke");
strokeItem.setSelected(true);

Slider strokeWidthSlider = new Slider(1, 10, 1);
strokeWidthSlider.setShowTickLabels(true);
strokeWidthSlider.setShowTickMarks(true);
strokeWidthSlider.setMajorTickUnit(2);
CustomMenuItem strokeWidthItem = new CustomMenuItem(strokeWidthSlider, false);

Menu optionsMenu = new Menu("Options");
optionsMenu.getItems().addAll(strokeItem, strokeWidthItem);

MenuBar menuBar = new MenuBar();
menuBar.getMenus().add(optionsMenu);
```

![|150](Pasted%20image%2020230725164928.png)

### 4.6. SeparatorMenuItem

SeparatorMenuItem 继承自 CustomMenuItem，`content` 属性为 `Separator`，hideOnClick 属性为 false。用于 menu-items 分组：

```java
// Create a separator menu item
SeparatorMenuItem smi = SeparatorMenuItem();
```

## 5. 示例

菜单理解起来容易，创建起来麻烦。必须分别创建菜单的每一部分，添加 listeners，然后组合起来。下面使用菜单创建一个绘图应用：

- 使用所有的 menu-item 类型
- root Pane 为 BorderPane
- top 区域为 menu
- center 区域为 Canvas，用于绘图
- 使用 File menu 下的 menu-items 绘制不同形状
- 点击 "Clear" 清除 Canvas
- 点击 "Exit" 退出应用
- 使用 "Options" menu 设置 stroke
- "Options" 下用 Slider 实现了一个 CustomMenuItem，调整 slider，Canvas 中的 strokeWidth 随之变化
- "Draw Stroke" 为 CheckMenuItem 类型，设置是否使用 stroke

```java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MenuItemTest extends Application {

    // A canvas to draw shapes
    Canvas canvas = new Canvas(200, 200);

    // Create three RadioMenuItems for shapes
    RadioMenuItem rectItem = new RadioMenuItem("_Rectangle");
    RadioMenuItem circleItem = new RadioMenuItem("_Circle");
    RadioMenuItem ellipseItem = new RadioMenuItem("_Ellipse");

    // A menu item to draw stroke
    CheckMenuItem strokeItem = new CheckMenuItem("Draw _Stroke");

    // To adjust the stroke width
    Slider strokeWidthSlider = new Slider(1, 10, 1);
    CustomMenuItem strokeWidthItem = new CustomMenuItem(strokeWidthSlider, false);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Menu fileMenu = getFileMenu();
        Menu optionsMenu = getOptionsMenu();

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, optionsMenu);

        // Draw the default shape, which is a Rectangle
        this.draw();

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(canvas);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Different Types of Menu Items");
        stage.show();
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 200, 200); // First clear the canvas

        // Set drawing parameters
        gc.setFill(Color.TAN);
        gc.setStroke(Color.RED);
        gc.setLineWidth(strokeWidthSlider.getValue());

        String shapeType = getSelectedShape();
        switch (shapeType) {
            case "Rectangle":
                gc.fillRect(0, 0, 200, 200);
                if (strokeItem.isSelected()) {
                    gc.strokeRect(0, 0, 200, 200);
                }
                break;
            case "Circle":
                gc.fillOval(10, 10, 180, 180);
                if (strokeItem.isSelected()) {
                    gc.strokeOval(10, 10, 180, 180);
                }
                break;
            case "Ellipse":
                gc.fillOval(10, 10, 180, 150);
                if (strokeItem.isSelected()) {
                    gc.strokeOval(10, 10, 180, 150);
                }
                break;
            default:
                clear(); // Do not know the shape type
        }
    }

    public void clear() {
        canvas.getGraphicsContext2D().clearRect(0, 0, 200, 200);
        this.rectItem.setSelected(false);
        this.circleItem.setSelected(false);
        this.ellipseItem.setSelected(false);
    }

    public Menu getFileMenu() {
        Menu fileMenu = new Menu("_File");

        // Make Rectangle the default option
        rectItem.setSelected(true);

        // Set Key Combinations for shapes
        KeyCombination kr =
                new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN);
        KeyCombination kc =
                new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN);
        KeyCombination ke =
                new KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN);
        rectItem.setAccelerator(kr);
        circleItem.setAccelerator(kc);
        ellipseItem.setAccelerator(ke);

        // Add ActionEvent handler to all shape radio menu items
        rectItem.setOnAction(e -> draw());
        circleItem.setOnAction(e -> draw());
        ellipseItem.setOnAction(e -> draw());

        // Add RadioMenuItems to a ToggleGroup to make them mutually exclusive
        ToggleGroup shapeGroup = new ToggleGroup();
        shapeGroup.getToggles().addAll(rectItem, circleItem, ellipseItem);

        MenuItem clearItem = new MenuItem("Cle_ar");
        clearItem.setOnAction(e -> clear());

        MenuItem exitItem = new MenuItem("E_xit");
        exitItem.setOnAction(e -> Platform.exit());

        // Add menu items to the File menu
        fileMenu.getItems().addAll(rectItem,
                circleItem, ellipseItem,
                new SeparatorMenuItem(),
                clearItem,
                new SeparatorMenuItem(),
                exitItem);
        return fileMenu;
    }

    public Menu getOptionsMenu() {
        // Draw stroke by default		
        strokeItem.setSelected(true);

        // Redraw the shape when draw stroke option toggles
        strokeItem.setOnAction(e -> syncStroke());

        // Configure the slider
        strokeWidthSlider.setShowTickLabels(true);
        strokeWidthSlider.setShowTickMarks(true);
        strokeWidthSlider.setMajorTickUnit(2);
        strokeWidthSlider.setSnapToPixel(true);
        strokeWidthSlider.valueProperty().addListener(this::strokeWidthChanged);

        Menu optionsMenu = new Menu("_Options");
        optionsMenu.getItems().addAll(strokeItem, this.strokeWidthItem);

        return optionsMenu;
    }

    public void strokeWidthChanged(ObservableValue<? extends Number> prop,
                                   Number oldValue,
                                   Number newValue) {
        draw();
    }

    public String getSelectedShape() {
        if (rectItem.isSelected()) {
            return "Rectangle";
        } else if (circleItem.isSelected()) {
            return "Circle";
        } else if (ellipseItem.isSelected()) {
            return "Ellipse";
        } else {
            return "";
        }
    }

    public void syncStroke() {
        // Enable/disable the slider
        strokeWidthSlider.setDisable(!strokeItem.isSelected());
        draw();
    }
}
```

![|300](Pasted%20image%2020230725170216.png)

## 6. CSS

下表是 menu 相关的 CSS 样式类名：

|Menu Component|Style Class Name|
|---|---|
|MenuBar|menu-bar|
|Menu|menu|
|MenuItem|menu-item|
|RadioMenuItem|radio-menu-item|
|CheckMenuItem|check-menu-item|
|CustomMenuItem|custom-menu-item|
|SeparatorMenuItem|separator-menu-item|

MenuBar 支持 `-fx-use-system-menu-bar` 属性，默认 false。

MenuBar 包含一个 menu 子结构，包含 menu-bar 中的所有 menus。

Menu 支持 showing CSS pseudo-class，menu 显示时应用。

RadioMenuItem 和 CheckMenuItem 支持 `selected` CSS pseudo-class，menu-item 被选时应用。

