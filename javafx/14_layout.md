# Layout

- [Layout](#layout)
  - [概述](#%e6%a6%82%e8%bf%b0)
  - [布局类](#%e5%b8%83%e5%b1%80%e7%b1%bb)
  - [添加子节点](#%e6%b7%bb%e5%8a%a0%e5%ad%90%e8%8a%82%e7%82%b9)
  - [工具类](#%e5%b7%a5%e5%85%b7%e7%b1%bb)
  - [Insets](#insets)
  - [Group](#group)
  - [创建 Group](#%e5%88%9b%e5%bb%ba-group)
  - [设置子节点位置](#%e8%ae%be%e7%bd%ae%e5%ad%90%e8%8a%82%e7%82%b9%e4%bd%8d%e7%bd%ae)
  - [Group CSS](#group-css)
  - [Region](#region)
  - [background fills](#background-fills)
  - [Border](#border)
  - [Pane](#pane)
  - [HBox](#hbox)
  - [FlowPane](#flowpane)
  - [GridPane](#gridpane)
  - [TextFlow](#textflow)
  - [TabPane](#tabpane)
    - [设置 Tab 大小](#%e8%ae%be%e7%bd%ae-tab-%e5%a4%a7%e5%b0%8f)

## 概述

在 SceneGraph 中，有两种布局方式：静态布局和动态布局。

- 静态布局，以绝对位置放置 Node.
- 动态布局，即下面要讲的布局管理器.

两种方式各有优缺点，静态布局可以让你完全控制所有 `Node` 的位置，动态布局更为复杂，需要做更多的事情，但是可以灵活的放置各个控件。

布局管理器，或布局窗格（layout pane），可以包含其他 `Node`，并且对子节点的位置进行控制：

- 计算子节点在容器中的位置(x, y坐标)
- 计算子节点的大小(width and height)

对 3D node,布局窗格还需要计算节点的 z 坐标和 depth。

每个Node有三个尺寸值：preferred size, minimum size, maximum size.

大部分容器尽量设置子节点尺寸为其 preferred size。不过实际尺寸受多种因素影响：

- 窗口大小
- 容器的布局策略
- 节点的缩放策略

## 布局类

JavaFX 包含如下所示的布局类：

![](2019-06-05-16-54-32.png)

说明：

- `Region` 的子类为容器类，它们可以通过 CSS 自定义风格。
- 所有的容器类继承自 `Parent`，但并非所有继承 `Parent` 的类都是容器类，例如 `Button` 继承-自 `Parent`，但并非容器类。
- 所有的节点必须通过容器添加到 `SceneGraph`。
- 容器通过布局策略布局其子节点，如果不希望容器管理子节点，可以设置 `managed` 属性为 false.
- 一个节点只能在一个容器中，如果一个节点已经在一个容器中，当将它添加到另一个容器，就从前一个容器移除。
- 要创建复杂的布局，则需要不同的布局管理器组合使用。

|布局类|说明|
|---|---|
|Group|将多个子节点分为一组，方便批量操作，如特效和转换操作|
|Pane|绝对布局|
|HBox|单行排列|
|VBox|单列排列|
|FlowPane|以列或行自动放置子节点，如果单行(列)放不下，自动换行|
|BorderPane|以上、下、左、右、中五个区域放置组件|
|StackPane|以堆栈的形式放置组件|
|TilePane|以均匀的网格放置组件|
|GridPane|以可变的网格放置组件|
|AnchorPane|将子组件固定在容器的某个边，或者使得子节点跟着 AnchorPane 调整大小|
|TextFlow|用于布局包含多个 `Text` 节点的富文本内容|

`Parent` 类用于获得子节点的方法：

|方法|说明|
|---|---|
|`protected ObservableList<Node> getChildren()`|包含所有子节点，可以通过该 `List` 添加子节点，是最常用的方法。注意该方法是 `protected`，如果 `Parent`不是容器，可以保持为 `protected`，如`Button`, `TextField`等。而`Group`和`Pane` 则需要将其调整为 `public`|
`public ObservableList<Node> getChildrenUnmodifiable()`|该方法一般在以下情况使用：1.获得子节点信息; 2.哪些是容器，哪些是组件|
`protected <E extends Node> List<E> getManagedChildren()`|在使用自定义容器时使用|

## 添加子节点

添加节点，主要有两种方法：
|方法|说明|
|---|---|
|getChildren().add()|添加一个子节点|
|getChildren().addAll()|添加多个子节点|

那些在构造时添加节点的构造函数，也是通过调用以上那个方法实现。

```java
// Create two buttons
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");

// Create an HBox with two buttons as its children
HBox hBox1 = new HBox(okBtn, cancelBtn);

// Create an HBox with two buttons with 20px horizontal spacing between them
double hSpacing = 20;
HBox hBox2 = new HBox(hSpacing, okBtn, cancelBtn);

// Create an empty HBox, and afterwards, add two buttons to it
HBox hBox3 = new HBox();
hBox3.getChildren().addAll(okBtn, cancelBtn);
```

## 工具类

下面的工具类单独使用没有任何用，一般用于设置节点的布局属性。

- `HPos` 包括三个常量：`LEFT`, `CENTER`, `RIGHT`，用于定义水平位置和对齐。
- `VPos` 包括四个常量：`TOP`, `CENTER`, `BASELINE`, `BOTTOM`，用于定义垂直位置和对齐。

`Pos` 定义水平和垂直位置和对齐。包括 `VPos` 和 `HPos`的所有可能组合。包括：`BASELINE_CENTER`, `BASELINE_LEFT`, `BASELINE_RIGHT`, `BOTTOM_CENTER`, `BOTTOM_LEFT`, `BOTTOM_RIGHT`, `CENTER`, `CENTER_LEFT`, `CENTER_RIGHT`, `TOP_CENTER`, `TOP_LEFT`, `TOP_RIGHT`。

- getHpos(), 返回 HPos，获得水平定位和对齐策略
- getVpos(), 返回 VPos, 获得垂直定位和对齐策略

`HorizontalDirection` 包含两个常量：`LEFT`和 `RIGHT`，分别表示左侧和右侧两个方向。
`VerticalDirection` 包含两个常量：`UP` 和 `DOWN`，分别表示上、下两个方向。
`Orientation` 包含两个常量：`HORIZONTAL` 和 `VERTICAL`，分别表示水平、垂直方向。
`Side` 包含四个常量：`TOP`, `RIGHT`, `BOTTOM`, `LEFT`，表示矩形的四个边。

有时，容器包含的空间多余或少于子节点的 preferred size需要的空间。在容器缩放时，`Priority`用于指定子节点的缩放优先级。`Priority`包含三个值：`ALWAYS`, `NEVER`, `SOMETIMES`：

- `ALWAYS`, 随着空余空间的缩放而缩放
- `NEVER`, 不缩放
- `SOMETIMES`, 当没有其他节点为 `ALWAYS`，或为 `ALWAYS`优先级的节点无法消耗所有变化的空间。

## Insets

`Insets` 类表示两个矩形四个方向边的距离。如下图所示：

![](2019-06-05-16-58-36.png)

在上图中，两个矩形可能交叉，此时，offsets 可能为负值。offsets 是相对值，所以要正确解析 offsets，需要知道相对的是哪个矩形。

该类包含两个构造函数:
|构造函数|说明|
|---|---|
|`Insets(double topRightBottomLeft)`|四个offsets 值完全相同|
|`Insets(double top, double right, double bottom, double left)`|分别指定 offset值|

`Insets.EMPTY()` 定义offsets 均为 0 的 Insets 对象。
	
在 JavaFX 中，在以下情况会用到 `Insets` ：

- Border insets
- Background insets
- Outsets
- Insets

前两者表示 layout bounds 和 inner edge of the border 或 innder edge of the background 的距离。

边框线或图片可能落在 `Region` 的 layout 边框之外。`Outsets` 表示

## Group

Group 类具有容器类的诸多特性：具有其自身的布局策略、坐标系，是 Parent 的子类。但是，它不对子节点定位，只提供 preferred size。所以应该将它看作节点集合，而不是容器。它只是为了方便对多个节点同时进行操作，因为对 Group 进行的转换、特效及属性，均会应用到其子节点。

Group 特征说明：
- `Group` 将子节点保存在 `ObservableList` 中， 按照节点添加的顺序依次渲染节点
- `Group` 采用所有子节点边框的加和作为其大小，不是 resizable
- 对 `Group` 施加的转换、特效和属性修改会作用于其子节点，但是转换为特效不在 Group layout 边框内；但是如果单独给各个子节点添加转换、特效等，就会包含在 Group layout 边框内。
- `Group` 会将其 resizable 的子节点设置为 preferred size，将 `autoSizeChildren` 为 false则取消该功能，即子节点的 preferred size 改变后， 它们不会随着改变大小。
- 不定位子节点。所有的子节点默认都放在 (0,0) 位置，通过子节点的 `layoutX`和 `layoutY` 属性设置子节点在 Group 中的位置，如果不设置，子节点会互相重叠。

## 创建 Group

容器的创建方式大同小异，Group 有 3 种构造函数：

```java
// 1. 创建一个空的 Group
Group emptyGroup = new Group();

// 2. 创建 Group 时同时添加子节点
Button smallBtn = new Button("Small button");
Button bigBtn = new Button("This is a big button");
Group root = new Group();

// 3. 通过集合添加节点 
List<Node> initailList = new ArrayList<>();
initailList.add(smallBtn);
initailList.add(bigBtn);

Group group2 = new Group(initailList);
```

## 设置子节点位置

```java
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");

okBtn.relocate(10, 10);
cancelBtn.layoutXProperty().bind(okBtn.layoutXProperty().add(okBtn.widthProperty().add(10)));
cancelBtn.layoutYProperty().bind(okBtn.layoutYProperty());
```

两种设置位置的方式：

- okBtn 是设置绝对位置
- calcelBtn 是相对 okBtn 进行设置。

## Group CSS

Group 自身没有多少 CSS 选项，从 Node 继承的 CSS 都可用。
Group 没有外观设置，如 padding, backgrounds, borders 等。

## Region

Regon 是包含背景和边框的Parent.
Region 是所有 JavaFX Node UI Controls 和容器的基类。它是 resizable 的可用 CSS 个性化的 Parent node。它可以有多个背景和边框，该类是为了尽可能的支持 CSS3 中的背景和边框而设计。

Region 按照如下顺序渲染：
|Order|Item|说明|
|---|---|---|
|1|background filles, BackgroundFills|背景填充|
|2|background images, BackgroundImages|背景图片填充|
|3|border strokes, BorderStrokes|边框线条|
|4|border images, BorderImages|边框图片|
|5|content|内容|

说明

- background filles, background images 和 border stroke, border images 都是可选的。
- 内容在背景和边框之上绘制。如果存在 BorderImage，则实际上不绘制 BorderStrokes，不过在计算内容区域的时候会考虑其大小。这样设置的目的，在于当 ImageStroke 加载失败时，载入 BorderStroke。

Region 默认为矩形，不过 BackgroundFill 半径可能会使矩形变为圆角。这不仅会影响 Region 的外观，还使圆角外的区域被Region 排除。Region 可以通过 `shape` 属性指定任意的形状，然后所有的 BackgroundFills, BackgroundImages, BorderStrokes 会应用到该形状，BorderImages 则被忽略。

每个 Region 都有其边框，由 (0, 0, width, height) 指定。Region 可能在其边框之外绘制，内容区域为子节点的 layout 占据的区域。该区域默认为 Region 的 layout 边框，但是可以被 border 属性或 padding 修改。padding 可以为负值，这样Region 的内容区域就超过其 layout 边框。

虽然 Region 的 layout 边框不受边框和背景影响，但是内容区域受影响。如下图所示：

![](2019-06-05-17-02-13.png)

|内容|功能|
|---|---|
|insets|内边距，layout 边框的边和内容区域边之间的距离，受 padding 和 Border 影响|
|content area|内容区域，放置子节点|
|padding|边距，content area 四周可选的空白区域，如果其宽度为0，则 padding edge和 content area edge 重合|
|margin|边沿，Border 外的区域，margin 和 padding 很相似，将节点添加到容器后，HBox, VBox 支持该属性，Region 不直接支持|

如果 layout 边框为 (x=0, y=0, width=200, height=100)，insets 为 (top=10, right=20, bottom=30, left=40)，则内容区域为 (x=40, y=10, width=140, height=60)。

Region 默认继承其父类 Parent 的 layout，即它会根据子节点的 preferred size 调整子节点大小，但不会调整它们的位置。如果应用程序需要具体的布局行为，那么它应该使用 Region 的子类：StackPane, HBox, VBox, TilePane, FlowPane, BorderPane, GridPane, AnchorPane.

要自定义 layout，则扩展 Region 的子类需要覆盖 `computePrefWidth`, `computePrefHeight`, `layoutChildren` 方法。`layoutChildren` 由 scene graph 自动调用，Region 子类不应该直接调用该方法。

Region 子类应该通过 `layoutX`, `layoutY` 设置子节点位置，`translateX`, `translateY` 保留作为调整和动画功能。

## background fills

Region 背景，可以包含 Fill、图形或两者兼而有之。
Fill 包括：颜色、四个角的半径，四边的 insets。Fills 根据添加顺序进行修饰。

- Fill color 定义了背景填充颜色。
- 半径定义了四个角，设置为 0 则为常规矩形。
- insets 定义了 Region layout 边和 fill 边的距离，说明 fill 只填充 content area。inset 可以为负值，使得填充区域大于 layout 边框。

|CSS属性|功能|
|---|---|
|-fx-background-color	|背景色|
|-fx-background-radius	|边角半径|
|-fx-background-insets	|内边距|
|-fx-background-image	|背景图片的 CSS URL|
|-fx-background-repeat	|图片重复填充方式|
|-fx-background-position|图片在 Region 中的填充方式|
|-fx-background-size	|图片相对 Region 的大小|

-fx-background-color 包含由逗号分隔的多个颜色值，颜色值的个数，对应需要渲染的背景框的个数。

```css
-fx-background-image: URL('your_image_url_goes_here');
-fx-background-repeat: space;
-fx-background-position: center;
-fx-background-size: cover;
```

## Border

Region 的边框包含 strokes, images or both。
stoke 包含5个属性：color, style, width, Radii for four corners, Insets on four sides.
style: solid, dashed, etc. inside, outside, centered.

insets 定义 stroke 和 layout 边界的距离。

![](2019-06-05-17-34-11.png)

|CSS属性|说明|实例|
|---|---|---|
|-fx-border-color|边框颜色|red;|
|-fx-border-style|边框线条样式|solid inside;|
|-rx-border-width|边框宽度|10;|
|-fx-border-radius|半径|5;|
|-fx-border-insets|内边距|0;|

## Pane

所有Layout pane 的基类，子节点为 public, 方便扩展类添加、移除子节点。
Pane 为绝对布局，除了将子节点设置为 preferred size，它不干其他事，所以需要手动设置节点位置。一般只在放置图表或图片时使用，其他情况均推荐使用布局管理器。所以，Pane 只干了两件事：

- 所有子节点位置默认在 (0, 0)
- 将子节点大小设置为 perferred size
Pane 包含三个size:

| |width|height|
|---|---|---|
|minimum|left + right insets|top + bottom insets|
|preferred|子节点在当前位置以 preferred width 显示所需要的宽度|子节点在当前位置以preferred heigth 显示所需的高度|
|maximum|Double.MAX_VALUE|Double.MAX_VALUE|

```java
Pane root = new Pane(); // 创建 Pane
Rectangle rect = new Rectangle(25, 25, 50, 50);
Line line = new Line(90, 40, 230, 40);
Circle circle = new Circle(130, 130, 30);
root.getChildren().addAll(rect, line, circle); // 添加子节点
```

## HBox

默认采用子节点的 prefWidth, preHeight。当 Parent 不是 resizable（如 Group），HBox 采用最高子节点的高度。
|属性|类型|说明|
|---|---|---|
|alignment|ObjectProperty<Pos>|子节点相对 HBox 的内容区域的对齐方式。如果垂直对齐方式为 BASELINE，fillHeight 被忽略，默认为 Pos.TOP_LEFT|
|fillHeight|BooleaProperty|resizable 子节点是否 resize 以填充 HBox 的高度，默认为true，alignment=BASELINE 该属性被忽略|
|spacing|DoubleProperty|水平相邻子节点之间的空隙|

## FlowPane

FlowPane(Orientation orientation, double hgap, double vgap)
创建FlowPane，横向排列，指定组件间水平和垂直距离

## GridPane

`GridPane` 是最强大的布局管理器之一。

属性：
|属性|说明|取值|
|---|---|---|
|alignment||		|
|gridLinesVisible|网格线可见性|true, false|
|nodeOrientation|网格编号顺序|LEFT_TO_RIGHT, RIGHT_TO_LEFT|

方法：
|方法|说明|
|---|---|
|new GridPane()|创建GridPane|
|static void setColumnIndex(Node child, Integer value)|设置 colum index|
|static void setRowIndex(Node child, Integer value)|设置 row index|
|static void setCOlumnSpan(Node child, Integer value)||	
|static void setConstraints(Node child, int columnIndex, int rowIndex)|设置位置|
|void add(Node child, int columnIndex, int rowIndex)|添加控件到指定位置|
|void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)|添加控件，并指定位置|
|void addRow(int rowIndex, Node... children)|添加一行控件，每个控件占一个网格|
|void addColumn(int columnIndex, Node... children)|添加一列控件，每个占一个网格|

设置node占有的水平和垂直网格数

node占有的网格数，可以添加node时设置后，也可以之后修改：

- void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)
- static void setColumnSpan(Node child, Integer value)
- static void setConstraints(Node child, int columnIndex, int rowIndex, int columnspan, int rowspan)

修改 node 占用网格数的方法：

- setRowSpan(Node child, Integer value)
- setColumnSpan(Node child, Integer value)

## TextFlow

TextFlow 用于显示富文本。
多个 Text 节点添加到 TextFlow 中，TextFlow 将这些 Text 合并显示。`Text` 有其位置，大小，换行宽度等，添加到 TextFlow后，这些属性均被忽略。

虽然TextFlow 是为显示文本而设计，不过其他 Node，如 Buttons, TextFields 等都可添加其中，大小以 preferred size 显示。

可以将 TextFlow 看做 FlowPane  的修改版，只是对 Text 特殊对待。

## TabPane

`TabPane`包含两部分：标题和内容。标题包含多个组成部分，如下图所示：

![](2019-06-05-17-40-06.png)

说明：

- "Headers region" 是标题全部区域
- Tabheaderbackground为标题背景
- Controlbuttons则是在分栏太多无法显示时，用于选择特定tab
- tab区域包含一个`Label`和一个closebutton，Label用于显示tab的标题和图标，closebutton为关闭tab的按钮

### 设置 Tab 大小

`TabPane` 将其区域分为两部分：

- 标题区
- 内容区

标题区显示 tab 的标题，内容区显示当前选择的 tab 的内容。

内容区的大小根据其内容自动计算大小，`TabPane` 包含如下属性用于设置标题区的尺寸：

- `tabMinHeight`
- `tabMaxHeight`
- `tabMinWidth`
- `tabMaxWidth`

默认宽度和高度的最小值为0，最大值为 `Double.MAX_VALUE`。而默认尺寸则是根据内容自动计算。如果你希望所有 tab 具有相同大小的标题区，可以将高度和宽度的最小值和最大值设置为相同值。如下所示：

```java
TabPane tabPane = new TabPane();
tabPane.setTabMinHeight(30);
tabPane.setTabMaxHeight(30);
tabPane.setTabMinWidth(100);
tabPane.setTabMaxWidth(100);
```
