# Button

2023-07-21

****
## 1. 简介

JavaFX 提供了三种类型的 button：

- 用于执行命令的 button
- 用于选择的 button
- 可执行命令和做选择的 button

所有的按钮类都继承自 `ButtonBase` 类，支持 `ActionEvent`，包含标签。激活 button 事件的方式有多种，如鼠标点击、助记符、快捷键或其它组合键。

<img src="images/Pasted%20image%2020230717160226.png" style="zoom: 33%;" />

**命令按钮**（*command button*）：激活时执行命令的按钮称为命令按钮。如 `Button`, `Hyperlink` 和 `MenuButton`。

**选择按钮**（*choice button*）: 向用户展示不同选择的按钮。如 `ToggleButton`, `CheckBox`, `RadioButton`。  

**混合按钮**：同时具有以上两种功能，如 `SplitMenuButton`。

> [!TIP]
>
> 所有 buttons 都是 `Labeled` 控件，因此可以设置 `text` 和 `graphic`。

## 2. Command Button

### Button 组件

`Button` 类表示命令按钮。`Button` 一般包含 text，并注册一个 `ActionEvent` handler。`Button` 的 `mnemonicParsing` 默认为 true。

`Button` 有三种模式：

- normal button: 激活时执行 `ActionEvent`（默认）
- default button: 按 `Enter` 键激活 `ActionEvent`，且 scene 中没有其它 node consume 该 key-press
- cancel button: 按 `Esc` 键激活 `ActionEvent`，且 scene 中没有其它 node consume 该 key-press

default 和 cancel 模式由 `defaultButton` 和 `cancelButton` 属性表示。将对应属性设置为 true，启用对应模式。这 2 个属性默认均为 false。

**示例：** 创建 normal Button，添加 `ActionEvent` handler

当激活 button，如用鼠标点击 button，`newDocument()` 被调用：

```java
Button newBtn = new Button("New");
newBtn.setOnAction(e -> newDocument());
```

**示例：** 创建 default Button，添加 `ActionEvent` handler

激活 button 时，`save()` 方法被调用。按 Enter 键也可以激活 default Button

```java
Button saveBtn = new Button("Save");
saveBtn.setDefaultButton(true);
saveBtn.setOnAction(e -> save());
```

**示例：** 演示 3 种 button

创建 1 个 normal button, 1 个 default button, 1 个 cancel button。各添加 1 个 `ActionEvent` listeners，并设置助记符。

激活按钮的方式有多种：

- 点击按钮
- 使用 Tab 将焦点移到 button，然后按 space 键
- Alt+助记符
- Enter 键激活 Save 按钮
- Esc 键激活 Cancel 按钮

不管以哪种方式激活 buttons，它们的 `ActionEvent` handler 被调用。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ButtonTest extends Application {

    Label msgLbl = new Label("Press Enter or Esc key to see the message");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // A normal button with N as its mnemonic 
        Button newBtn = new Button("_New");
        newBtn.setOnAction(e -> newDocument());

        // A default button with S as its mnemonic
        Button saveBtn = new Button("_Save");
        saveBtn.setDefaultButton(true);
        saveBtn.setOnAction(e -> save());

        // A cancel button with C as its mnemonic
        Button cancelBtn = new Button("_Cancel");
        cancelBtn.setCancelButton(true);
        cancelBtn.setOnAction(e -> cancel());

        HBox buttonBox = new HBox(newBtn, saveBtn, cancelBtn);
        buttonBox.setSpacing(15);
        VBox root = new VBox(msgLbl, buttonBox);
        root.setSpacing(15);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Command Buttons");
        stage.show();
    }

    public void newDocument() {
        msgLbl.setText("Creating a new document...");
    }

    public void save() {
        msgLbl.setText("Saving...");
    }

    public void cancel() {
        msgLbl.setText("Cancelling...");
    }
}
```

![|300](./images/Pasted%20image%2020230717163319.png)

```ad-note
在一个 Scene 中可以将多个 button 设置为 default 或 cancel button，但只有第一个设置的有效。
```

`Button` 的默认 CSS 类名为 `button`。

Button 类支持两个 CSS pseudo-classes: `default` 和 `cancel`。可以用于自定义 default 和 cancel buttons 的外观。

**示例：** 将 default button 的 text color 设置为 blue，cancel button 的设置为 gray

```css
.button:default {
    -fx-text-fill: blue;
}
.button:cancel {
    -fx-text-fill: gray;
}
```

### 2.2. Hyperlink

`Hyperlink` 看起来和网页中的超链接很像，但网页中的超链接用于导航到另一个网页，而 JavaFX 中的 `Hyperlink` 激活一个 `ActionEvent`。

JavaFX 的 Hyperlink 时一个看起来像超链接的 button。其 mnemonicParsing 默认关闭。Hyperlink 可以持有焦点，且持有焦点时会绘制一个虚线矩形边框。当光标移到 Hyperlink 上，光标变为手形，并且其文本带下划线。

Hyperlink 的 visited 属性为 BooleanProperty 类型。在 Hyperlink 被激活后，该属性自动变为 true。两种状态的 Hyperlink 颜色不同。也可以用 setVisited() 设置 visited 属性值。

**示例：** 创建 text 为 "JDojo" 的 Hyperlink，并添加一个 ActionEvent handler

激活 Hyperlink 时，在 WebView 中自动打开 www.jdojo.com 网页。 

```java
Hyperlink jdojoLink = new Hyperlink("JDojo");
WebView webview = new WebView();
jdojoLink.setOnAction(e -> webview.getEngine().load("http://www.jdojo.com"));
```

**示例：** 在 BorderPane 的 top 添加 3 个 Hyperlink，center 添加 1 个 Webview，点击页面，打开对应网页

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class HyperlinkTest extends Application {

    private WebView webview;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // 在 JAT 创建 WebView
        webview = new WebView();

        // Create some hyperlinks
        Hyperlink jdojoLink = new Hyperlink("Baidu");
        jdojoLink.setOnAction(e -> loadPage("https://www.baidu.com"));

        Hyperlink yahooLink = new Hyperlink("Yahoo!");
        yahooLink.setOnAction(e -> loadPage("http://www.yahoo.com"));

        Hyperlink googleLink = new Hyperlink("Google");
        googleLink.setOnAction(e -> loadPage("http://www.google.com"));

        HBox linkBox = new HBox(jdojoLink, yahooLink, googleLink);
        linkBox.setSpacing(10);
        linkBox.setAlignment(Pos.TOP_RIGHT);

        BorderPane root = new BorderPane();
        root.setTop(linkBox);
        root.setCenter(webview);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Hyperlink Controls");
        stage.show();
    }

    public void loadPage(String url) {
        webview.getEngine().load(url);
    }
}
```

下图是点击 Baidu 显示的页面：

![|400](Pasted%20image%2020230717170036.png)

### 2.3. MenuButton

`MenuButton` 看起来像 button，使用起来像 menu。

激活 `MenuButton` 时，它会弹出一个列表。`getItems()` 返回列表选项，为 `ObservableList<MenuItem>` 类型。

**示例：** 创建 1 个 MenuButton，添加 2 个 MenuItem

每个 MenuItem 绑定一个 ActionEvent handler

```java
// Create two menu items with an ActionEvent handler.
// Assume that the loadPage() method exists
MenuItem jdojo = new MenuItem("JDojo");
jdojo.setOnAction(e -> loadPage("http://www.jdojo.com"));
MenuItem yahoo = new MenuItem("Yahoo");
yahoo.setOnAction(e -> loadPage("http://www.yahoo.com"));

// Create a MenuButton and the two menu items
MenuButton links = new MenuButton("Visit");
links.getItems().addAll(jdojo, yahoo);
```

下图是点击 `MenuButton` 前后的视图：

![|150](Pasted%20image%2020230717171040.png)

`MenuButton` 声明了 2 个属性：

- `popupSide`，`ObjectProperty<Side>` 类型
- `showing`，`ReadOnlyBooleanProperty` 类型

`popupSide` 指定菜单显示的方向，默认为 `Side.BOTTOM`。如果设置方向的空间不足以显示，JavaFX 会自动调整方向。

`showing` 当弹窗显示时为 true，否则为 false.

**示例：** `MenuButton` 使用

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MenuButtonTest extends Application {

    private WebView webview;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Must create a WebView object from the JavaFX Application Thread
        webview = new WebView();

        MenuItem jdojo = new MenuItem("Baidu");
        jdojo.setOnAction(e -> loadPage("https://www.baidu.com"));

        MenuItem yahoo = new MenuItem("Yahoo");
        yahoo.setOnAction(e -> loadPage("http://www.yahoo.com"));

        MenuItem google = new MenuItem("Google");
        google.setOnAction(e -> loadPage("http://www.google.com"));

        // Add menu items to the MenuButton
        MenuButton links = new MenuButton("Visit");
        links.getItems().addAll(jdojo, yahoo, google);

        BorderPane root = new BorderPane();
        root.setTop(links);
        BorderPane.setAlignment(links, Pos.TOP_RIGHT);
        root.setCenter(webview);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using MenuButton Controls");
        stage.show();
    }

    public void loadPage(String url) {
        webview.getEngine().load(url);
    }
}
```

![|400](Pasted%20image%2020230717171722.png)

## 3. Choice Buttons

JavaFX 提供了多个用于选择的按钮：

- ToggleButton
- CheckBox
- RadioButton

```ad-tip
ChoiceBox, ComboBox 和 ListView 也可以用于选择，后面会单独讨论。
```

3 个控件都是 `Labeled` 控件，它们以不同样式向用户呈现选择。

- 互斥选项：如性别 Male, Female 和 Unknown，选择一个，上一个自动取消选择。ToggleButton 和 RadioButton 用于这种情况
- 两种选择，如 Yes/No 或 On/Off，一般用 ToggleButton 或 CheckBox
- 多选：ToggleButton 和 CheckBox

### 3.1. ToggleButton

`ToggleButton` （切换按钮）有两种状态：`selected` 和 `unselected`。

- `selected` 属性为 `true` 表示处于 `selected` 状态，否则处于 `unselected` 状态
- `ToggleButton` 的 `mnemonicParsing` 默认启用

创建 `ToggleButton` 和 `Button` 一样：

```java
ToggleButton springBtn = new ToggleButton("Spring");
```

`ToggleButton` 用于选择，而非执行命令，所以一般不添加 `ActionEvent` handlers。

如果用 `ToggleButton` 启动或终止 action，则需要为 `selected` 属性添加 `ChangeListener`。

```ad-tip
每次点击 ToggleButton 都激活 ActionEvent handler。第一个选择 ToggleButton，第二次取消选择 ToggleButton。如果选择、然后取消选择 ToggleButton，或激活两次 ActionEvent handler.
```

将 `ToggleButton` 放在 `ToggleGroup` 中，一次只能选择 **0 或 1 个** `ToggleButton`。`ToggleButton` 的 `toggleGroup` 属性指向它所属的 `ToggleGroup`。

**示例：** 创建 4 个 `ToggleButton`，并添加到 `ToggleGroup`

```java
ToggleButton springBtn = new ToggleButton("Spring");
ToggleButton summerBtn = new ToggleButton("Summer");
ToggleButton fallBtn = new ToggleButton("Fall");
ToggleButton winterBtn = new ToggleButton("Winter");

// Create a ToggleGroup
ToggleGroup group = new ToggleGroup();

// Add all ToggleButtons to the ToggleGroup
springBtn.setToggleGroup(group);
summerBtn.setToggleGroup(group);
fallBtn.setToggleGroup(group);
winterBtn.setToggleGroup(group);
```

每个 `ToggleGroup` 维护了一个 `ObservableList<Toggle>`，`ToggleButton` 实现了泛型接口 `Toggle`。`ToggleGroup` 的 `getToggles()` 返回包含 `Toggle` list，可以通过该 list 添加 `Toggle`，所以上面的代码可以修改为：

```java
ToggleButton springBtn = new ToggleButton("Spring");
ToggleButton summerBtn = new ToggleButton("Summer");
ToggleButton fallBtn = new ToggleButton("Fall");
ToggleButton winterBtn = new ToggleButton("Winter");

// Create a ToggleGroup
ToggleGroup group = new ToggleGroup();

// Add all ToggleButtons to the ToggleGroup
group.getToggles().addAll(springBtn, summerBtn, fallBtn, winterBtn);
```

`ToggleGroup` 的 `selectedToggle` 属性指向 group 中被选择的 `Toggle`。`getSelectedToggle()` 返回被选择的 `Toggle`，如果没有选择 `Toggle`，返回 `null`。为 `selectedToggle` 属性添加 `ChangeListener` 可以监听选择的 `Toggle`。

**示例：** 添加 4 个 `ToggleButton` 到 `ToggleGroup`

在 `ToggleGroup` 中最多只能选择一个 `ToggleButton`。监听 `ToggleGroup` 的 `selectedToggle` 属性，在 `Label` 中显示选择的 ``ToggleButton``。

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ToggleButtonTest extends Application {

    Label userSelectionMsg = new Label("Your selection: None");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create four ToggleButtons
        ToggleButton springBtn = new ToggleButton("Spring");
        ToggleButton summerBtn = new ToggleButton("Summer");
        ToggleButton fallBtn = new ToggleButton("Fall");
        ToggleButton winterBtn = new ToggleButton("Winter");

        // Add all ToggleButtons to a ToggleGroup
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(springBtn, summerBtn, fallBtn, winterBtn);

        // Track the selection changes and display the currently selected season
        group.selectedToggleProperty().addListener(this::changed);

        Label msg = new Label("Select the season you like:");

        // Add ToggleButtons to an HBox
        HBox buttonBox = new HBox(springBtn, summerBtn, fallBtn, winterBtn);
        buttonBox.setSpacing(10);

        VBox root = new VBox(userSelectionMsg, msg, buttonBox);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ToggleButtons in a Group");
        stage.show();
    }

    // A change listener to track the selection in the group
    public void changed(ObservableValue<? extends Toggle> observable,
                        Toggle oldBtn,
                        Toggle newBtn) {
        String selectedLabel = "None";
        if (newBtn != null) {
            selectedLabel = ((Labeled) newBtn).getText();
        }

        userSelectionMsg.setText("Your selection: " + selectedLabel);
    }
}
```

![|350](Pasted%20image%2020230717175428.png)

### 3.2. RadioButton

`RadioButton` （单选按钮）继承 `ToggleButton`，和 `ToggleButton` 一样有两种状态：`selected` 和 `unselected`。

- `selected` property 指示当前状态
- mnemonic 默认启用

选择、取消选择 `RadioButton` 均触发 ActionEvent。

`RadioButton` 和 `ToggleButton` 的主要差别：

- group 中 toggleButton 可以一个不选，RadioButton 必须选择一个
- 点击选择的 toggleButton，会取消去选，而点击选择的 RadioButton 不会取消选择

```ad-tip
RadioButton 用于必须选择一个的场景；ToggleButton 用于选择一个或不选。
```

**示例：** 在 ToggleGroup 中使用 RadioButton

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RadioButtonTest extends Application {

    Label userSelectionMsg = new Label("Your selection: None");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create four RadioButtons
        RadioButton springBtn = new RadioButton("Spring");
        RadioButton summerBtn = new RadioButton("Summer");
        RadioButton fallBtn = new RadioButton("Fall");
        RadioButton winterBtn = new RadioButton("Winter");

        // Add all RadioButtons to a ToggleGroup
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(springBtn, summerBtn, fallBtn, winterBtn);

        // Track the selection changes and display the currently selected season
        group.selectedToggleProperty().addListener(this::changed);

        // Select the default season as Summer
        summerBtn.setSelected(true);

        Label msg = new Label("Select the season you like the most:");

        // Add RadioButtons to an HBox
        HBox buttonBox = new HBox(springBtn, summerBtn, fallBtn, winterBtn);
        buttonBox.setSpacing(10);

        VBox root = new VBox(userSelectionMsg, msg, buttonBox);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using RadioButtons in a Group");
        stage.show();
    }

    // A change listener to track the selection in the group
    public void changed(ObservableValue<? extends Toggle> observable,
                        Toggle oldBtn,
                        Toggle newBtn) {
        String selectedLabel = "None";
        if (newBtn != null) {
            selectedLabel = ((Labeled) newBtn).getText();
        }
        userSelectionMsg.setText("Your selection: " + selectedLabel);
    }
}
```

![|350](Pasted%20image%2020230721132525.png)

### 3.3. CheckBox

CheckBox 有三种状态：checked, unchecked, undefined。undefined 也称为 indeterminate。

- CheckBox 支持三种选择：true/false/unknown。
- CheckBox 一般包含 text 作为 label，没有 graphic (虽然可以设置)。
- 点击 CheckBox，在三种状态之间切换

示例：CheckBox 的三种状态

![|350](Pasted%20image%2020230721133122.png)

CheckBox 默认只启用两个状态：checked 和 unchecked。通过 `allowIndeterminate` property 开启第三种状态，默认为 false。

```java
// Create a CheckBox that supports checked and unchecked states only
CheckBox hungryCbx = new CheckBox("Hungry");

// Create a CheckBox and configure it to support three states
CheckBox agreeCbx = new CheckBox("Hungry");
agreeCbx.setAllowIndeterminate(true);
```

所以，`CheckBox` 的三种状态通过 selected 和 indeterminate 两个属性控制，规则如下：

|indeterminate|selected|**State**|
|----|----|----|
|false|true|Checked|
|false|false|Unchecked|
|true |true/false|Undefined|

监听 CheckBox 的状态变化，需要同时为两个属性添加 ChangeListener。点击 CheckBox 触发 ActionEvent，也可以使用 ActionEvent 检测 state 变化。

示例：使用两个 ChangeListeners 检测 CheckBox 的 state 变化。

```java
// Create a CheckBox to support three states
CheckBox agreeCbx = new CheckBox("I agree");
agreeCbx.setAllowIndeterminate(true);

// Add a ChangeListener to the selected and indeterminate properties
agreeCbx.selectedProperty().addListener(this::changed);
agreeCbx.indeterminateProperty().addListener(this::changed);
...
// A change listener to track the selection in the group
public void changed(ObservableValue<? extends Boolean> observable,
                    Boolean oldValue,
                    Boolean newValue) {
    String state = null;
    if (agreeCbx.isIndeterminate()) {
        state = "Undefined";
    } else if (agreeCbx.isSelected()) {
        state = "Checked";
    } else {
        state = "Unchecked";
    }
    System.out.println(state);
}
```

**示例：** CheckBox 的使用

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckBoxTest extends Application {

    Label userSelectionMsg = new Label("Do you agree? No");
    CheckBox agreeCbx;
	
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a CheckBox to support only two states
        CheckBox hungryCbx = new CheckBox("Hungry");

        // Create a CheckBox to support three states
        agreeCbx = new CheckBox("I agree");
        agreeCbx.setAllowIndeterminate(true);

        // Track the state change for the "I agree" CheckBox
        // Text for the Label userSelectionMsg will be updated
        agreeCbx.selectedProperty().addListener(this::changed);
        agreeCbx.indeterminateProperty().addListener(this::changed);

        VBox root = new VBox(userSelectionMsg, hungryCbx, agreeCbx);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root, 200, 130);
        stage.setScene(scene);
        stage.setTitle("Using CheckBoxes");
        stage.show();
    }

    // A change listener to track the state change in agreeCbx
    public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean oldValue,
                        Boolean newValue) {
        String msg;
        if (agreeCbx.isIndeterminate()) {
            msg = "Not sure";
        } else if (agreeCbx.isSelected()) {
            msg = "Yes";
        } else {
            msg = "No";
        }
        this.userSelectionMsg.setText("Do you agree? " + msg);
    }
}
```

![|300](Pasted%20image%2020230721134818.png)

CheckBox 的默认 style class name 为 `check-box`。

CheckBox 支持三个 CSS pseudo-classes: selected, determinate, indeterminate

- `selected` 用于 `selected` property 为 true 的情况
- `determinate` 用于 `indeterminate` property 为 false 的情况
- `indeterminate` 用于 indeterminate property 为 true 的情况

CheckBox CSS 包含两个子结构：box 和 mark。

box 和 mark 都是 StackPane。mark 用于设置勾选 CheckBox 的形状，可以自定义为不同形状。

下面的 CSS 将 box 显示为棕褐色，tick mark 显示为红色：

```css
.check-box .box {
    -fx-background-color: tan;
}

.check-box:selected .mark {
    -fx-background-color: red;
}
```

## 4. SplitMenuButton

`SplitMenuButton` 不完全是 command button 或 choice button，它结合了 pop-up menu 和 command button 的特性。

SplitMenuButton 可以分为两个区域：action area 和 menu-open area。

- 点击 action area 触发 ActionEvent，执行命令
- 点击 menu-open area 弹出菜单，根据选择执行操作

SplitMenuButton 的 Mnemonic 解析默认开启。‘

下图显示 SplitMenuButton 的两种状态：

- 左图为折叠状态
- 右图显示菜单项

显示文本的左侧为 action area，右侧为 menu-open area。

**示例：** 创建 SplitMenuButton

```java
// Create an empty SplitMenuItem
SplitMenuButton splitBtn = new SplitMenuButton();
splitBtn.setText("Home"); // Set the text as "Home"

// Create MenuItems
MenuItem jdojo = new MenuItem("JDojo");
MenuItem yahoo = new MenuItem("Yahoo");
MenuItem google = new MenuItem("Google");

// Add menu items to the MenuButton
splitBtn.getItems().addAll(jdojo, yahoo, google);
```

为点击 SplitMenuButton 的 action area 添加 ActionEvent handler：

```java
// Add ActionEvent handler when "Home" is clicked
splitBtn.setOnAction(e -> /* Take some action here */);
```

**示例：** SplitMenuButton 的使用

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SplitMenuButtonTest extends Application {

    private WebView webview;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Must create a WebView object from the JavaFX Application Thread
        webview = new WebView();

        MenuItem jdojo = new MenuItem("JDojo");
        jdojo.setOnAction(e -> loadPage("http://www.jdojo.com"));

        MenuItem yahoo = new MenuItem("Yahoo");
        yahoo.setOnAction(e -> loadPage("http://www.yahoo.com"));

        MenuItem google = new MenuItem("Google");
        google.setOnAction(e -> loadPage("http://www.google.com"));

        // Create a SplitMenuButton
        SplitMenuButton splitBtn = new SplitMenuButton();
        splitBtn.setText("Home");

        // Add menu items to the SplitMenuButton
        splitBtn.getItems().addAll(jdojo, yahoo, google);

        // Add ActionEvent handler when "Home" is clicked
        splitBtn.setOnAction(e -> loadPage("http://www.jdojo.com"));

        BorderPane root = new BorderPane();
        root.setTop(splitBtn);
        BorderPane.setAlignment(splitBtn, Pos.TOP_RIGHT);
        root.setCenter(webview);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using SplitMenuButton Controls");
        stage.show();
    }

    public void loadPage(String url) {
        webview.getEngine().load(url);
    }
}
```

![|400](Pasted%20image%2020230721142313.png)
