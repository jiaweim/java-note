# ComboBox

2023-08-01, 09:27
modify: cellFactory
2023-07-24, 11:02
***
## 1. 简介

`ComboBox` 提供列表选项，可以看作 `ChoiceBox` 的升级版，高度可定制，且可编辑。

`ComboBox` 继承自 `ComboBoxBase`，`ComboBoxBase` 为类似 `ComboBox` 的控件提供通用功能，如 `ColorPicker` 和 `DatePicker`。如果需要自定义弹出列表控件，可以从继承 `ComboBoxBase` 开始。

`ComboBox` 的列表可以包含任何类型的对象，`ComboBox` 是一个参数化类。参数类型为列表项的类型。

### 1.1. 创建 ComboBox

如果想在 `ComboBox` 中使用混合类型，可以将参数类型设置为 `<Object>`，如下：

```java
// 可以存储多种类型
ComboBox<Object> seasons = new ComboBox<>();
```

- 创建字符串类型的 `ComboBox`

```java
ComboBox<String> seasons = new ComboBox<>();
```

- 创建 `ComboBox` 时指定列表项

```java
ObservableList<String> seasonList = FXCollections.<String>observableArrayList(
        "Spring", "Summer", "Fall", "Winter");
ComboBox<String> seasons = new ComboBox<>(seasonList);
```

- 创建 `ComboBox` 后添加列表项

```java
ComboBox<String> seasons = new ComboBox<>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");
```

### 1.2. selectionModel

和 `ChoiceBox` 一样，`ComboBox` 需要记录选择元素及其元素，`ComboBox` 使用 `SingleSelectionModel` 类实现该功能，`selectionModel` 属性存储该对象的引用。通过 selectionModel 可以从元素列表中选择指定元素，可以为选择元素添加 `ChangeListener`。

与 `ChoiceBox` 不同，`ComboBox` 是可编辑的。其 `editable` 属性指定是否启用编辑功能，默认不启用。当 `ComboBox` 处于可编辑状态，使用 `TextField` 显示选择元素及输入项。

`ComboBox` 的 editor 属性存储 TextField 的引用，当 ComboBox 不可编辑时 editor 属性为 null。例如：

```java
ComboBox<String> breakfasts = new ComboBox<>();

// Add some items to choose from
breakfasts.getItems().addAll("Apple", "Banana", "Strawberry");

// By making the control editable, let users enter an item
breakfasts.setEditable(true);
```


ComboBox 的 value 属性存储当前选择元素或输入值。在 editable `ComboBox` 中，用户输入的 `String` 值被转换为 `T` 类型。如果输入的不是 String 类型，就需要 `StringConverter<T>` 将 String 值转换为 T 类型。

### 1.3. 提示文本

editable ComboBox 可以设置提示文本。提示文本存储在 promptText 属性，为 StringProperty 类型：

```java
breakfasts.setPromptText("Select/Enter an item"); // Set a prompt text
```

### 1.4. placeholder

ComboBox 的 placeholder 属性存储 Node 引用，当元素 list 为空或 null，在 pop-up area 显示该 Node。

```java
Label placeHolder = new Label("List is empty.\nPlease enter an item");
breakfasts.setPlaceholder(placeHolder);
```

### 1.5. 示例

创建 2 个 ComboBox：seasons 和 breakfast。

- seasons 不能编辑
- breakfasts 可编辑

```java
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ComboBoxTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label seasonsLbl = new Label("Season:");
        ComboBox<String> seasons = new ComboBox<>();
        seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");

        Label breakfastsLbl = new Label("Breakfast:");
        ComboBox<String> breakfasts = new ComboBox<>();
        breakfasts.getItems().addAll("Apple", "Banana", "Strawberry");
        breakfasts.setEditable(true);

        // Show the user's selection in a Label
        Label selectionLbl = new Label();
        StringProperty str = new SimpleStringProperty("Your selection: ");
        selectionLbl.textProperty().bind(str.concat("Season=")
                .concat(seasons.valueProperty())
                .concat(", Breakfast=")
                .concat(breakfasts.valueProperty()));

        HBox row1 = new HBox(seasonsLbl, seasons, breakfastsLbl, breakfasts);
        row1.setSpacing(10);
        VBox root = new VBox(row1, selectionLbl);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ComboBox Controls");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230724093806.png)

## 2. 监听所选元素

对不可编辑 ComboBox，为 selectionModel 的 selectedIndex 或 selectedItem 属性添加 ChangeListener 即可监听所选元素。

对 editable ComboBox，依然可以为 selectedItem 属性添加 ChangeListener 监听所选元素。在输入新值时，selectedIndex 属性不变，不适合用来监听。

当 ComboBox 的值发生变化，如通过代码设置、从元素列表选择、输入新的值，可以为值的变化添加 ActionEvent handler:

```java
ComboBox<String> list = new ComboBox<>();
list.setOnAction(e -> System.out.println("Value changed"));
```

## 3. 自定义类型

当 `ComboBox<T>` 的类型 T 不是 String 类型，需要设置 converter 属性。

converter 属性为 `StringConverter<T>` 类型，其 `toString(T object)` 将元素 `object` 转换为字符串，在弹窗列表中显示；其 `fromString(String s)` 将输入的字符串 `s` 转换为 T 类型。

当输入的字符串被成功转换为 T 类型，`value` 属性更新，如果转换失败，value 属性不更新。

**示例：** 在 ComboBox 中使用 converter

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mvc.mjw.javafx.Person;

public class ComboBoxWithConverter extends Application {

    Label userSelectionMsgLbl = new Label("Your selection:");
    Label userSelectionDataLbl = new Label("");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label personLbl = new Label("Select/Enter Person:");
        ComboBox<Person> persons = new ComboBox<>();
        persons.setEditable(true);
        persons.setConverter(new PersonStringConverter());
        persons.getItems().addAll(new Person("John", "Jacobs", null),
                new Person("Donna", "Duncan", null),
                new Person("Layne", "Estes", null),
                new Person("Mason", "Boyd", null));

        // 为 selectionModel 的 selectedItem 和 selectedIndex
        // 属性添加 ChangeListener
        persons.getSelectionModel().selectedItemProperty()
                .addListener(this::personChanged);
        persons.getSelectionModel().selectedIndexProperty()
                .addListener(this::indexChanged);

        // 为 value 变化添加监听器
        persons.setOnAction(e -> valueChanged(persons));

        GridPane root = new GridPane();
        root.addRow(0, personLbl, persons);
        root.addRow(1, userSelectionMsgLbl, userSelectionDataLbl);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using StringConverter in ComboBox");
        stage.show();
    }

    public void valueChanged(ComboBox<Person> list) {
        Person p = list.getValue();
        String name = p.getLastName() + ", " + p.getFirstName();
        userSelectionDataLbl.setText(name);
    }

    // A change listener to track the change in item selection
    public void personChanged(ObservableValue<? extends Person> observable,
            Person oldValue,
            Person newValue) {
        System.out.println("Itemchanged: old = " + oldValue +
                ", new = " + newValue);
    }

    // A change listener to track the change in index selection
    public void indexChanged(ObservableValue<? extends Number> observable,
            Number oldValue,
            Number newValue) {
        System.out.println("Indexchanged: old = " + oldValue + ", new = " + newValue);
    }
}
```

![|400](Pasted%20image%2020230724095147.png)

## 4. 自定义弹窗列表高度

ComboBox 在弹窗列表中默认显示前 10 个元素：

- 当元素超过 10 个，弹窗列表会显示一个滚动条
- 当元素少于 10 个，缩短弹窗列表高度

ComboBox 的 visibleRowCount 属性设置弹窗列表可见的行数：

```java
ComboBox<String> states = new ComboBox<>();
...
// Show five rows in the popup list
states.setVisibleRowCount(5);
```

## 5. Node 作为元素

`ComboBox` 有两个区域：

- button 区域显示选择的元素
- pop-up 区域显示元素列表

两个区域都使用 `ListCell` 显示元素。`ListCell` 为 `Labeled` 子类，类图如下：

![](Pasted%20image%2020230724100319.png)

pop-up 区域为 `ListView`，每个元素以 `ListCell` 显示。

`ComboBox` 的元素可以为任意类型，包括 `Node`。但是不建议直接将 `Node` 添加到 items 列表。将 `Node` 作为 items 时，它们会作为 graphics 添加到 cells。Scene graph 要求一个 node 不能在两个地方同时显示，即一个 node 一次只能在一个容器中。在 item list 中选择一个 node，该 node 从 `ListView` pop-up 中移除，添加到 button 区域。再次点开 pop-up `ListView`，被选择的 node 不显示，因为它已经在 button 区域中。所以为了避免这种情况，不要将 nodes 直接作为 `ComboBox` 的元素。

**示例：** Node 作为元素

将 3 个 HBox 添加到 ComboBox：

- #1 是第一次点开 pop-up 时的视图，三个元素正常显示
- #2 是选择第二个元素后的视图，在 button 区域可以看到正常显示的元素
- #3 是再次点开 pop-up 的视图，此时第二个元素从 ListView 移到 button 区域，在 ListView 中无法正常显示

```java
Label shapeLbl = new Label("Shape:");
ComboBox<HBox> shapes = new ComboBox<>();
shapes.getItems().addAll(new HBox(new Line(0, 10, 20, 10), new Label("Line")),
                         new HBox(new Rectangle(0, 0, 20, 20), new Label("Rectangle")),
                         new HBox(new Circle(20, 20, 10), new Label("Circle")));
```

![|400](Pasted%20image%2020230724102143.png)

如果确实需要自定义列表元素的显示样式，可以通过 CellFactory 实现。

## 6. CellFactory

`ComboBox` 包含一个 `cellFactory` 属性：

```java
public ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;
```

`Callback` 接口在 `javafx.util` 包中：

```java
public interface Callback<P,R> {
    public R call(P param);
}
```

`ComboBox` 的 `cellFactory` 属性定义的 Callback，参数类型为 `ListView<T>`，返回类型为 `ListCell<T>`。在 `call()` 方法中需要创建 `ListCell<T>` 并覆盖 `ListCell` 的 `updateItem(T item, boolean empty)` 方法。

```ad-summary
`cellFactory` 的使用要点是在 `Callback` 的 `updateItem()` 方法中设置 `ListCell` 的 `text` 和 `graphic`（可选）。记得调用 super 方法。
```

**示例：** cellFactory

`StringShapeCell` 实现：

- 继承 `ListCell<String>`
- 实现 `updateItem()` 方法
- 在 `updateItem()` 中定义 cell 内容

```java
import javafx.scene.control.ListCell;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class StringShapeCell extends ListCell<String> {

    @Override
    public void updateItem(String item, boolean empty) {
        // Need to call the super first
        super.updateItem(item, empty);

        // Set the text and graphic for the cell
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item);
            Shape shape = this.getShape(item);
            setGraphic(shape);
        }
    }

    public Shape getShape(String shapeType) {
        Shape shape = null;
        switch (shapeType.toLowerCase()) {
            case "line":
                shape = new Line(0, 10, 20, 10);
                break;
            case "rectangle":
                shape = new Rectangle(0, 0, 20, 20);
                break;
            case "circle":
                shape = new Circle(20, 20, 10);
                break;
            default:
                shape = null;
        }
        return shape;
    }
}
```

- Callback 实现

```java
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ShapeCellFactory implements Callback<ListView<String>, ListCell<String>> {

    @Override
    public ListCell<String> call(ListView<String> listview) {
        return new StringShapeCell();
    }
}
```

设置 cellFactory:

```java
// Set the cellFactory property
shapes.setCellFactory(new ShapeCellFactory());
```

- buttonCell

`cellFactory` 解决了在 pop-up 区域的显示问题，还需要设置在 button 区域的显示和编辑问题。即设置 buttonCell 属性：

```java
// Set the buttonCell property
shapes.setButtonCell(new StringShapeCell());
```

完整示例：

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ComboBoxCellFactory extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label shapeLbl = new Label("Shape:");
        ComboBox<String> shapes = new ComboBox<>();
        shapes.getItems().addAll("Line", "Rectangle", "Circle");

        // Set the cellFactory property
        shapes.setCellFactory(new ShapeCellFactory());

        // Set the buttonCell property
        shapes.setButtonCell(new StringShapeCell());

        HBox root = new HBox(shapeLbl, shapes);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using CellFactory in ComboBox");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230724104723.png)

在 `ComboBox` 中使用自定义 `cellFactory` 和 `buttonCell` 可以完整控制 pop-up 列表和选择元素。

`Cell` 为 `Labeled` 控件，在 `updateItem()` 中可以自定义 `text` 和 `graphic`。

`ComboBoxBase` 提供了 4 个属性，在 `ComboBox` 也可以使用：

- `onShowing`
- `onShown`
- `onHiding`
- `onHidden`

这些属性均为 `ObjectProperty<EventHandler<Event>>` 类型，可以添加 event handler，分别在弹窗显示时前、显示后、隐藏前、隐藏后被调用。例如，添加 `onShowing` event handler 可用于定义 pop-up 的样式。

## 7. CSS

ComboBox 的 CSS style class name 默认为 combo-box。ComboBox 包含多个 CSS 子结构，如下图所示：

![|400](Pasted%20image%2020230724105401.png)

这些子结构的  CSS 名称分别为：

- arrow-button
- list-cell
- text-input
- combo-box-popup

`arrow-button` 包含一个名为 `arrow` 的子结构。arrow-button 和 arrow 都是 StackPane 类型。

list-cell 区域用于表示不可编辑 ComboBox 中显示选择元素的 ListCell 。

text-input 区域表示 editable ComboBox 中显示选择或输入的元素的 TextField。

combo-box-popup 为 Popup 控件，在点击 arrow-button 时弹出元素列表。combo-box-popup 也包含两个子结构：

- list-view，ListView 控件，显示元素列表
- list-cell，表示 ListView 中的单元格

CSS 示例：

```css
/* The ListCell that shows the selected item in a non-editable ComboBox */
.combo-box .list-cell {
    -fx-background-color: yellow;
}

/* The TextField that shows the selected item in an editable ComboBox */
.combo-box .text-input {
    -fx-background-color: yellow;
}

/* Style the arrow button area */
.combo-box .arrow-button {
    -fx-background-color: lightgray;
}

/* Set the text color in the popup list for ComboBox to blue */
.combo-box-popup .list-view .list-cell {
    -fx-text-fill: blue;
}
```

