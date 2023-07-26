# TabPane 和 Tab

2023-07-26, 10:17
****
## 1. 简介

JavaFX 提供在多个页面显示的控件，如 Accordion和 Pagination。TabPane 和 Tab 可以更好的设置页面信息，Tab 表示一个页面，TabPane 为Tab 容器。

Tab 不是 Control，而是继承 Object。不过 Tab 支持 Control 的一些功能，如可以被禁用，可以使用 CSS 设置样式，也可以有 context-menu 和 tool-tips。

Tab 由 title 和 content 组成：

- title 包含 text、graphic (可选)和关闭按钮（可选）
- content 包含 controls，通常将 controls 放在 layoutPane 中，然后将 layoutPane 放到 content

TabPane 中 Tab 的标题通常可见。所有 Tabs 共享 content area。点击 Tab 标题，显示其 content。在 TabPane 中，一次只能选择一个 Tab。如果所有 tabs 的标题不可见，则会自动显示一个控制按钮，帮助用户选择不可见的 tabs。

TabPane 中 Tabs 可以在 TabPane 的 top, right, bottom, left 四个方向。默认为 top。

![|450](Pasted%20image%2020230725210242.png)

TabPane 可以分为两部分：

- header 区域，显示标题
- content 区域，显示选择 Tab 的 content

header 区域可以分为：

- headers region: header area 的全部区域
- tab header background: 
- control buttons tab: 包含 control buttons 的区域
- tab area: 包含 Label 和一个关闭按钮，Label 显示 text 和 icon

如下图所示：

- tab header background 指 

![|450](Pasted%20image%2020230725210542.png)

## 2. 创建 Tab

- 默认构造函数，空 title

```java
Tab tab1 = new Tab();
```

- 使用 setText() 设置 Tab 的标题 text

```java
tab1.setText("General");
```

- 构造时设置

```java
Tab tab2 = new Tab("General");
```

## 3. 设置 Title 和 Content

Tab 类的如下属性用于设置 title 和 content:

- text
- graphic
- closable
- content

text, graphic 和 closable 属性指定 Tab 标题栏显示的内容。

text 为标题文本，可以通过构造函数或 setText() 方法设置。

graphic 属性将 Node 设为标题的 icon。

**示例：** 创建 Tab，设置 text 和 graphic

```java
// Create an ImageView for graphic
String imagePath = "resources/picture/address_icon.png";
URL imageUrl = getClass().getClassLoader().getResource(imagePath);
Image img = new Image(imageUrl.toExternalForm());
ImageView icon = new ImageView(img);

// Create a Tab with "Address" text
Tab addressTab = new Tab("Address");

// Set the graphic
addressTab.setGraphic(icon);
```

closable 属性为 boolean 类型，指定 tab 是否可以关闭，false 表示无法关闭。

关闭 tab 由 TabPane 的 tab-closing 策略控制。如果 closable 属性为 false，则不管 TabPane 的关闭策略，用户不能关闭 tab。

content 属性指定 tab 的内容。选择 tab 时，tab 的 content 可见。通常将包含控件的 layoutPane 作为 content。

**示例：** 创建 GridPane，添加 controls，然后将 GridPane 设置为 tab 的 content

```java
// Create a GridPane layout pane with some controls
GridPane grid = new GridPane();
grid.addRow(0, new Label("Street:"), streetFld);
grid.addRow(1, new Label("City:"), cityFld);
grid.addRow(2, new Label("State:"), stateFld);
grid.addRow(3, new Label("ZIP:"), zipFld);

Tab addressTab = new Tab("Address");
addressTab.setContent(grid); // Set the content
```

## 4. 创建 TabPane

TabPane 只提供了一个构造函数：

```java
TabPane tabPane = new TabPane();
```

## 5. 添加 Tab 到 TabPane

TabPane.getTabs() 返回 `ObservableList<Tab>`，为 TabPane 包含的所有 Tab。

**示例：** 添加 2 个 Tab 到 TabPane

```java
Tab generalTab = new Tab("General");
Tab addressTab = new Tab("Address");
...
TabPane tabPane = new TabPane();

// Add the two Tabs to the TabPane
tabPane.getTabs().addAll(generalTab, addressTab);
```

从 TabPane 删除 Tab:

```java
// Remove the Address tab
tabPane.getTabs().remove(addressTab);
```

Tab 的 read-only  tabPane 属性指向包含该 Tab 的 TabPane 的 引用。Tab 还未添加到 TabPane 时，其 tabPane 属性为 null。getTabPane() 返回 TabPane。

## 6. 示例

创建 2 个 Tab 类：

- GeneralTab 包含输入 Person 信息的页面
- AddressTab 包含输入 address 信息的页面

```java
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class GeneralTab extends Tab {

    TextField firstNameFld = new TextField();
    TextField lastNameFld = new TextField();
    DatePicker dob = new DatePicker();

    public GeneralTab(String text, Node graphic) {
        this.setText(text);
        this.setGraphic(graphic);
        init();
    }

    public void init() {
        dob.setPrefWidth(200);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("First Name:"), firstNameFld);
        grid.addRow(1, new Label("Last Name:"), lastNameFld);
        grid.addRow(2, new Label("DOB:"), dob);
        this.setContent(grid);
    }
}
```

```java
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AddressTab extends Tab {

    TextField streetFld = new TextField();
    TextField cityFld = new TextField();
    TextField stateFld = new TextField();
    TextField zipFld = new TextField();

    public AddressTab(String text, Node graphic) {
        this.setText(text);
        this.setGraphic(graphic);
        init();
    }

    public void init() {
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Street:"), streetFld);
        grid.addRow(1, new Label("City:"), cityFld);
        grid.addRow(2, new Label("State:"), stateFld);
        grid.addRow(3, new Label("ZIP:"), zipFld);
        this.setContent(grid);
    }
}
```

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TabTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ImageView privacyIcon = getImage("privacy_icon.png");
        GeneralTab generalTab = new GeneralTab("General", privacyIcon);

        ImageView addressIcon = getImage("address_icon.png");
        AddressTab addressTab = new AddressTab("Address", addressIcon);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(generalTab, addressTab);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using TabPane and Tab Controls");
        stage.show();
    }

    public ImageView getImage(String fileName) {
        ImageView imgView = null;
        try {
            // TODO: book text
            String imagePath = getClass().getResource("/picture/" + fileName).toExternalForm();
            Image img = new Image(imagePath);
            imgView = new ImageView(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgView;
    }
}
```

![|300](Pasted%20image%2020230725213418.png)

## 7. Tab 选择

TabPane 只支持单选，当用户或通过代码选择一个 tab，则之前选择的 tab 被取消选择。

Tab 的 read-only selected 属性为 boolean 类型。当 tab 被选中，该属性为 true。

Tab 的 onSelectionChanged 属性保存 tab 选择事件，可以监听其变化：

```java
Tab generalTab = ...
generalTab.setOnSelectionChanged(e -> {
    if (generalTab.isSelected()) {
        System.out.println("General tab has been selected.");
    } else {
        System.out.println("General tab has been unselected.");
    }
});
```

TabPane 会记录被选的 tab 及其索引。TabPane 使用 selectionModel 属性实现该功能，该属性为 SingleSelectionModel 类型。也可以自定义 selectionModel，不过几乎用不着。

selectionModel 与选择相关的功能：

- 支持通过 index 选择 tab，第一个 tab 的索引为 0
- 支持选择第一个、上一个、下一个和最后一个 tab
- 支持清除选择，**注意：** 这个功能可用，但基本不会用，TabPane 通常应该选择一个 tab
- selectedIndex 和 selectedItem 属性记录所选 tab及其索引。可以向这些属性添加 ChangeListener 以处理 TabPane 中 tab 选择更改事件

TabPane 默认选择第一个 tab。下面选择最后一个 tab:

```java
tabPane.getSelectionModel().selectLast();
```

- selectionModel 的 selectNext() 方法选择下一个 tab，在最后一个 tab 调用该方法无效
- selectPrevious() 和 selectLast() 选择上一个和下一个 tab
- select(int index) 和 select(T item) 选择指定 tab

**示例：** 添加 2 个 tab 到 TabPane

- 为 2 个 tabs 添加 selection-changed event handler
- 为 selectionModel 属性的 selectedItem 属性添加 ChangeListener

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TabSelection extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        GeneralTab generalTab = new GeneralTab("General", null);
        AddressTab addressTab = new AddressTab("Address", null);

        // Add selection a change listener to Tabs
        generalTab.setOnSelectionChanged(e -> tabSelectedChanged(e));
        addressTab.setOnSelectionChanged(e -> tabSelectedChanged(e));

        TabPane tabPane = new TabPane();

        // Add a ChangeListsner to the selection model
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener(this::selectionChanged);

        tabPane.getTabs().addAll(generalTab, addressTab);

        HBox root = new HBox(tabPane);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("TabPane Selection Model");
        stage.show();
    }

    public void selectionChanged(ObservableValue<? extends Tab> prop,
                                 Tab oldTab,
                                 Tab newTab) {
        String oldTabText = oldTab == null ? "None" : oldTab.getText();
        String newTabText = newTab == null ? "None" : newTab.getText();
        System.out.println("Selection changed in TabPane: old = " +
                oldTabText + ", new = " + newTabText);
    }

    public void tabSelectedChanged(Event e) {
        Tab tab = (Tab) e.getSource();
        System.out.println("Selection changed event for " + tab.getText() +
                " tab, selected = " + tab.isSelected());
    }
}
```

## 8. 关闭 Tab

有时用户需要能够关闭 tab，例如 Web 浏览器都支持打开和关闭 tabs。根据需要添加 Tab 需要一些代码，但是关闭 Tab是 TabPane 的内置功能。

用户可以点击 tab 标题栏中的关闭按钮来关闭 tab。tab 关闭功能由两个属性控制：

- Tab 类的 closable 属性
- TabPane 类的 tabClosingPolicy 属性

Tab 类的 closable 属性指定 tab 是否可以关闭，如果为 false，则不论 TabPane 的 tabClosingPolicy 取值，tab 都不能关闭。默认 false。

TabPane 的 tabClosingPolicy 属性指定关闭 Tab 的方式。类型为 enum `TabPane.TabClosingPolicy`：

- ALL_TABS：所有 tab 都有关闭按钮
- SELECTED_TAB：选中的 tab 才有关闭按钮，默认策略
- UNAVAILABLE：所有 tab 都没有关闭按钮 ，不论其 `closable` 属性取值

关闭 tab 的方式有两种：

- 用户点击关闭按钮
- 以编程方式

二者效果相同，均将 Tab 从 TabPane 删除。这里讨论的是用户点击关闭按钮。

用户关闭 tab 的操作可以被否决。为 tab 的 `TAB_CLOSE_REQUEST_EVENT` 添加 event handler 即可。当用户点击关闭按钮，该 event handler 被调用。当添加的 event handler consume 该 event，即可取消关闭操作。例如：

```java
Tab myTab = new Tab("My Tab");
myTab.setOnCloseRequest(e -> {
    if (SOME_CONDITION_IS_TRUE) {
        // Cancel the close request
        e.consume();
    }
});
```

tab 被用户关闭后，会触发 closedEvent。使用 Tab 的 onClosed 属性设置 closedEvent handler。该 event handler 一般用来释放 tab 持有的资源：

```java
myTab.setOnClosed(e -> {/* Release tab resources here */});
```

**示例：** 关闭 tab 

- 显示 2 个 tabs
- CheckBox 用于否决关闭 tab 操作，除非勾选 CheckBox，否则无法关闭 tab

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import static javafx.scene.control.TabPane.TabClosingPolicy;

public class TabClosingTest extends Application {

    GeneralTab generalTab = new GeneralTab("General", null);
    AddressTab addressTab = new AddressTab("Address", null);
    TabPane tabPane = new TabPane();

    CheckBox allowClosingTabsFlag = new CheckBox("Are Tabs closable?");
    Button restoreTabsBtn = new Button("Restore Tabs");
    ChoiceBox<TabPane.TabClosingPolicy> tabClosingPolicyChoices = new ChoiceBox<>();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Add Tabs to the TabPane
        tabPane.getTabs().addAll(generalTab, addressTab);

        // Set a tab close request event handler for tabs
        generalTab.setOnCloseRequest(this::tabClosingRequested);
        addressTab.setOnCloseRequest(this::tabClosingRequested);

        // Set a closed event handler for the tabs
        generalTab.setOnClosed(e -> tabClosed(e));
        addressTab.setOnClosed(e -> tabClosed(e));

        // Set an action event handler for the restore button
        restoreTabsBtn.setOnAction(e -> restoreTabs());

        // Add choices to the choice box
        tabClosingPolicyChoices.getItems()
                .addAll(TabClosingPolicy.ALL_TABS,
                        TabClosingPolicy.SELECTED_TAB,
                        TabClosingPolicy.UNAVAILABLE);

        // Set the default value for the tab closing policy
        tabClosingPolicyChoices.setValue(tabPane.getTabClosingPolicy());


        // Bind the tabClosingPolicy of the tabPane to the value property of the 
        // of the ChoiceBoxx
        tabPane.tabClosingPolicyProperty().bind(
                tabClosingPolicyChoices.valueProperty());

        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 10;");
        grid.addRow(0, allowClosingTabsFlag, restoreTabsBtn);
        grid.addRow(1, new Label("Tab Closing Policy:"),
                tabClosingPolicyChoices);
        root.setTop(grid);
        root.setCenter(tabPane);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Closing Tabs");
        stage.show();
    }

    public void tabClosingRequested(Event e) {
        if (!allowClosingTabsFlag.isSelected()) {
            e.consume(); // Closing tabs is not allowed
        }
    }

    public void tabClosed(Event e) {
        Tab tab = (Tab) e.getSource();
        String text = tab.getText();
        System.out.println(text + " tab has been closed.");
    }

    public void restoreTabs() {
        ObservableList<Tab> list = tabPane.getTabs();
        if (!list.contains(generalTab)) {
            list.add(0, generalTab);
        }

        if (!list.contains(addressTab)) {
            list.add(1, addressTab);
        }
    }
}
```

![|300](Pasted%20image%2020230726093147.png)

## 9. Tab 位置

TabPane 中 Tab 可以在 top, right, bottom, left 四个方位，由 TabPane 的 side 属性设置。Side enum:

- TOP
- RIGHT
- BOTTOM
- LEFT

side 属性默认为 `Side.TOP`。

**示例：** 创建 TabPane，将 side 属性设置为 `Side.LEFT`

```java
TabPane tabPane = new TabPane();
tabPane.setSide(Side.LEFT);
```

```ad-note
tabs 的实际位置还受 TabPane 的 orientation 影响。例如，如果 side 属性为 Side.LEFT，而 TabPane 的 orientation 为 RIGHT_TO_LEFT，则 tabs 实际在右边。
```

TabPane 还有一个 boolean 类型的 rotateGraphic 属性。该属性与 side 属性相关：

-  如果 side 属性为 Side.TOP 或 Side.BOTTOM，所有 tabs 标题栏的 graphics 处于垂直位置
- 如果 side 属性为 Side.LEFT 或 Side.RIGHT，标题栏的 text 默认会旋转 90°，而 graphics 保持垂直
- rotateGraphic 属性指定 grpahic 是否随着 text 一起旋转，默认 false

设置方式：

```java
// Rotate the graphic with the text for left and right sides
tabPane.setRotateGraphic(true);
```

下图是不同样式：

![|450](Pasted%20image%2020230726094907.png)

## 10. Tab 尺寸

TabPane 的 layout 可以分为两部分：

- header area
- content area

header area 显示 tabs 的标题。content area 显示选择 tab 的内容。

content area 的尺寸根据所有 tabs 的 content 自动计算。TabPane 包含如下属性用于设置标题栏尺寸：

- tabMinHeight
- tabMaxHeight
- tabMinWidth
- tabMaxWidth

width 和 heigth 的默认尺寸：

- 最小值默认为 0
- 最大值默认为 Double.MAX_VALUE
- 默认尺寸根据 tab 标题的尺寸计算

如果希望所有 tabs 尺寸固定，将 width 和 heigth 的 minimum 和 maximum 值设置为相同值。固定尺寸后，标题栏过长 text 会 被截断。

**示例：** 设置所有 tabs 100px 宽 30px 高

```java
TabPane tabPane = new TabPane();
tabPane.setTabMinHeight(30);
tabPane.setTabMaxHeight(30);
tabPane.setTabMinWidth(100);
tabPane.setTabMaxWidth(100);
```

## 11. Recessed and Floating

TabPane 有凹陷（recessed）和浮动（floating）两种模式。

- 默认为 recessed 模式。
- floating 模式移除了 header area 的 background color，为 content area 添加了 border。

两种模式的样式：

![](Pasted%20image%2020230726100131.png)

floating 模式通过样式类指定。TabPane 定义了一个 `STYLE_CLASS_FLOATING` 常量，将该样式添加到 TabPane 的样式类即切换为 floating 模式：

```java
TabPane tabPane = new TabPane();

// Turn on the floating mode
tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
...
// Turn off the floating mode
tabPane.getStyleClass().remove(TabPane.STYLE_CLASS_FLOATING);
```

## 12. Tab 和 TabPane CSS

TabPane 的 CSS 样式类名默认为 tab-pane，Tab 的为 tab。

可以使用 tab 样式类直接定义 Tab 的样式，也可以使用 TabPane 的子结构，一般使用 TabPane 的子结构定义样式。

TabPane 支持 4 个 CSS pseudo-classes，对应 side 属性的四个值：

- top
- right
- bottom
- left

使用如下 CSS 属性设置 TabPane 标题栏的最小和最大尺寸：

- -fx-tab-min-width
- -fx-tab-max-width
- -fx-tab-min-height
- -fx-tab-max-height

header area 的子结构为 tab-header-area，tab-header-area 子结构又包含如下子结构

- headers-region
- tab-header-background
- control-buttons-tab
    - 包含 tab-down-button 子结构
        - 包含 arrow 子结构
- tab
    - tab-label
    - tab-close-button

tab-content-area 表示 TabPane 的 content area。

**示例：** 移除 header area 的 background color（模拟 floating 模式）

```css
.tab-pane > .tab-header-area > .tab-header-background {
    -fx-background-color: null;
}
```

**示例：** 将选中 tab 的 text 设置为粗体

```css
.tab-pane > .tab-header-area > .headers-region > .tab:selected 
> .tab-container > .tab-label {
    -fx-font-weight: bold;
}
```

**示例：** 将 Tabs 的 background 设置为 blue，标题文本设置为 10pt white

```css
.tab-pane > .tab-header-area > .headers-region > .tab {
    -fx-background-color: blue;
}

.tab-pane > .tab-header-area > .headers-region > .tab > .tab-container
> .tab-label {
    -fx-text-fill: white;
    -fx-font-size: 10pt;
}
```

**示例：** 将 floating 模式的 border color 设置为  blue
TabPane 的 floating 样式类用于设置 floating 模式。

```css
.tab-pane.floating > .tab-content-area {
    -fx-border-color: blue;
}
```
