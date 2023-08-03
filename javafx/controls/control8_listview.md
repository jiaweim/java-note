# ListView

2023-08-03, 16:22
add: 8. ListView 过滤
2023-07-24, 14:01
***
## 1. 简介

`ListView` 可用于显示一列元素，支持单选和多选。

`ListView` 存储的元素可以为任意类型，对应控件为 `ListCell` 类。

### 1.1. 创建 ListView

如果想一个 `ListView` 存储多种类型，可以使用非参数化类，创建方式：

```java
// Create a ListView for any type of items
ListView seasons = new ListView();

// Create a ListView for String items
ListView<String> seasons = new ListView<String>();
```

- 构造时指定内容

```java
ObservableList<String> names = FXCollections.observableArrayList(
        "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
ListView<String> listView = new ListView<String>(names);
```

- 在构造后，可以通过 `items` 属性添加和删除元素

```java
ListView<String> seasons = new ListView<>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");
```

### 1.2. preferred size

当 `ListView` 的 `prefSize` 不是你想要的，如果有类似 `ComboBox` 的 `visibleItemCount` 属性设置可见元素个数就好了，然而 ListView 不支持该属性。只能直接设置 prefSize：

```java
// Set preferred width = 100px and height = 120px
seasons.setPrefSize(100, 120);
```

在 `ListView` 中元素较多无法一次显示时，会自动添加水平或垂直滚动条。

### 1.3. placeholder

`ListView` 的 `placeholder` 属性存储一个 `Node` 引用，在没有元素时显示该 `Node`。例如，下面设置一个 `Label` 为占位符：

```java
Label placeHolder = new Label("No seasons available for selection.");
seasons.setPlaceholder(placeHolder);
```

### 1.4. 滚动

`ListView` 提供了一个滚动功能。使用 `scrollTo(int index)` 或 `scrollTo(T item)` 可以滚到列表中指定索引或元素。

如果对应元素不在可见区，会自动转到可见区。调用 `scrollTo()` 方法，或者用户拖动滚动条时，触发 `ScrollToEvent` 事件，调用 `setOnScrollTo()` 方法监听该事件。

`ListView` 的元素使用 `ListCell` 类显示。`ListCell` 是一个 `Labeled` 控件，可以显示 text 和 graphic。`ListView` 的 `cellFactory`可以指定一个 `Callback` 对象，提供自定义的 `ListCell`，而且 `ListCell` 已有多个子类可供选择。

`ListView` 包含的 `ListCell` 数目和当前显示的元素个数相同，而非和包含的元素个数相同，在拖动滚动条时，`ListCell` 可重复使用显示元素。下图是 `ListCell` 相关类图：

![](Pasted%20image%2020230724111628.png)

`Cell` 在多个控件中都是基本组成模块，如 `ListView`, `TreeView`, `TableView`，它们均使用 `Cell` 显示和编辑数据。覆盖 `Cell` 的 `updateItem(T object, booleam empty)` 方法可以设置其填充方式。在需要更新 `Cell` 内容时，该方法被自动调用。

`Cell` 有多个属性：`editable`, `editing`, `empty`, `item` 以及 `selected`。在 `Cell` 为空，即没有和任何数据关联，`empty` 属性为 true。

`IndexedCell` 添加了 `index` 属性，是元素在底层模型的索引。例如 `ListView` 使用 `ObservableList` 作为模型，则 `ObservableList` 的第二个元素对应 `Cell` 的索引为 1。`Cell` 的索引有助于基于索引的自定义，例如，对奇、偶单元格采用不同的颜色。在单元格为空时，`index` 属性为 -1。

## 2. ListView 方向

`ListView` 中的元素可以单列（默认）或单行显示。该性质由 `orientation` 属性设置：

```java
// Arrange list of seasons horizontally
seasons.setOrientation(Orientation.HORIZONTAL);
```

下图显示了两个 `ListView`，一个垂直，一个水平。其中奇数和偶数的行或列背景色不同。这是 `ListView` 的默认样式。

![orientation|400](2020-05-20-13-34-34.png)

## 3. Selection Model

`SelectionModel` 选择模型用于保存元素的选择状态，`selectionModel` 属性保存当前选择模型，默认为 `MultipleSelectionModel` 类。也可以自定一个选择模型，不过一般用不着。

`ListView` 支持两种选择模式: Single, Multiple，对应单选和多选。

- `ListView` 默认为单选
    - 一次只能选择一个元素，当选择一个元素，上一个被选择的元素自动取消选择
    - 可以用鼠标点击选择
    - 在 `ListView` 持有焦点时，可以用键盘选择
- 对多项选择
    - 按 Shift 键一次选择连续多个
    - 按 Ctrl 键间隔选择
    - 也可以通过上下箭头移动，按 Space 选择

模式选择：

```java
// 多选模式
seasons.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

// 单选模式
seasons.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
```

`MultipleSelectionModel` 类继承自 `SelectionModel`，包含 `selectedIndex` 和 `selectedItem` 属性。

`selectedIndex` 值：

- 没有选择时，`selectedIndex` 为 -1
- 在单选模式，为对应元素索引
- 在多选模式，使用 `getSelectedIndices()` 返回包含所有选择元素索引的 `ObservableList<Integer>`

可以对 `selectedIndex` 属性添加 `ChangeListener`，或者对 `getSelectedIndices()` 返回的 `ObservableList` 添加 `ListChangeListener`，监听索引变化。

`selectedItem` 属性：

- 没有选择时，为 null
- 在单选模式，为当前被选的元素
- 在多选模式，为最后一个选择的元素
- 在多选模式，`getSelectedItems()` 方法返回只读的 `ObservableList<T>`，包含被选的所有元素

为 `selectedItem` 添加 `ChangeListener` 监听单选元素变化。

为 `getSelectedItems()` 返回的 `ObservableList<T>` 添加 `ListChangeListener` 鉴定多选元素变化。

`ListView` 包含选择元素的方法：

- `selectAll()` 选择所有元素
- `selectFirst()` 和 `selectLast()` 分别选择第一个和最后一个元素
- `selectIndices(int index, int... indices)` 根据索引选择多个元素，溢出的索引直接忽略
- `selectRange(int start, int end)` 选择 `[start, end)` 范围内的所有元素
- `clearSelection()` 清除所有选择，`clearSelection(int index)` 清除指定位置的选择

**示例：** 选择模型

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListViewSelectionModel extends Application {

    private ListView<String> seasons;
    private final Label selectedItemsLbl = new Label("[None]");
    private final Label lastSelectedItemLbl = new Label("[None]");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label seasonsLbl = new Label("Select Seasons:");
        seasons = new ListView<>();
        seasons.setPrefSize(120, 120);
        seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");

        // Enable multiple selection
        seasons.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Add a selection change listener
        seasons.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::selectionChanged);

        // Add some buttons to assist in selection
        Button selectAllBtn = new Button("Select All");
        selectAllBtn.setOnAction(e -> seasons.getSelectionModel().selectAll());

        Button clearAllBtn = new Button("Clear All");
        clearAllBtn.setOnAction(
                e -> seasons.getSelectionModel().clearSelection());

        Button selectFirstBtn = new Button("Select First");
        selectFirstBtn.setOnAction(
                e -> seasons.getSelectionModel().selectFirst());

        Button selectLastBtn = new Button("Select Last");
        selectLastBtn.setOnAction(e -> seasons.getSelectionModel().selectLast());

        Button selectNextBtn = new Button("Select Next");
        selectNextBtn.setOnAction(e -> seasons.getSelectionModel().selectNext());

        Button selectPreviousBtn = new Button("Select Previous");
        selectPreviousBtn.setOnAction(
                e -> seasons.getSelectionModel().selectPrevious());

        // Let all buttons expand as needed
        selectAllBtn.setMaxWidth(Double.MAX_VALUE);
        clearAllBtn.setMaxWidth(Double.MAX_VALUE);
        selectFirstBtn.setMaxWidth(Double.MAX_VALUE);
        selectLastBtn.setMaxWidth(Double.MAX_VALUE);
        selectNextBtn.setMaxWidth(Double.MAX_VALUE);
        selectPreviousBtn.setMaxWidth(Double.MAX_VALUE);

        // Display controls in a GridPane
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(5);

        // Add buttons to two VBox objects
        VBox singleSelectionBtns = new VBox(selectFirstBtn, selectNextBtn,
                selectPreviousBtn, selectLastBtn);
        VBox allSelectionBtns = new VBox(selectAllBtn, clearAllBtn);
        root.addColumn(0, seasonsLbl, seasons);
        root.add(singleSelectionBtns, 1, 1, 1, 1);
        root.add(allSelectionBtns, 2, 1, 1, 1);

        // Add controls to display the user selection
        Label selectionLbl = new Label("Your selection:");
        root.add(selectionLbl, 0, 2);
        root.add(selectedItemsLbl, 1, 2, 2, 1);

        Label lastSelectionLbl = new Label("Last selection:");
        root.add(lastSelectionLbl, 0, 3);
        root.add(lastSelectedItemLbl, 1, 3, 2, 1);

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ListView Selection Model");
        stage.show();
    }

    // A change listener to track the change in item selection
    public void selectionChanged(ObservableValue<? extends String> observable,
                                 String oldValue,
                                 String newValue) {
        String lastItem = (newValue == null) ? "[None]" : "[" + newValue + "]";
        lastSelectedItemLbl.setText(lastItem);

        ObservableList<String> selectedItems =
                seasons.getSelectionModel().getSelectedItems();
        String selectedValues =
                (selectedItems.isEmpty()) ? "[None]" : selectedItems.toString();
        this.selectedItemsLbl.setText(selectedValues);
    }
}
```

![|350](Pasted%20image%2020230724113547.png)

## 4. Cell Factory

`ListView` 使用 `ListCell` 显示元素，为 `Labeled` 子类。`ListView` 的 `cellFactory` 用于自定义单元格，其类型为 `ObjectProperty<Callback<ListView<T>, ListCell<T>>>`。即将 ListView 传入 cellFactory，返回 ListCell。

对大型列表，`ListCell` 会被重用，在拖动列表时，`ListCell` 的 `updateItem()` 方法接收新数据的引用。

`ListView` 默认调用元素的 `toString()` 方法，显示该字符串。在自定义 `ListCell` 的 `updateItem()` 方法中，可以根据单元格内容填充 text 和 graphic 到单元格中。

ListView 自定义 cellFactory 的方法与 ComboBox 一样。

**示例：** 使用自定义 cellFactory 显示格式化 `Person`

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import mjw.study.javafx.mvc.Person;

public class ListViewDomainObjects extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ListView<Person> persons = new ListView<>();
        persons.setPrefSize(150, 120);
        persons.getItems().addAll(new Person("John", "Jacobs", null),
                new Person("Donna", "Duncan", null),
                new Person("Layne", "Estes", null),
                new Person("Mason", "Boyd", null));

        // Add a custom cell factory to display formatted names of persons
        persons.setCellFactory(
                new Callback<ListView<Person>, ListCell<Person>>() {
                    @Override
                    public ListCell<Person> call(ListView<Person> listView) {
                        return new ListCell<Person>() {
                            @Override
                            public void updateItem(Person item, boolean empty) {
                                // Must call super
                                super.updateItem(item, empty);

                                int index = this.getIndex();
                                String name = null;

                                // Format name 
                                if (item == null || empty) {
                                    // No action to perform
                                } else {
                                    name = (index + 1) + ". " +
                                            item.getLastName() + ", " +
                                            item.getFirstName();
                                }

                                this.setText(name);
                                setGraphic(null);
                            }
                        };
                    }
                });

        HBox root = new HBox(new Label("Persons:"), persons);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ListView Cell Factory");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230724114407.png)

## 5. Editable ListView

`ListView` 通过两个属性实现了可编辑功能：

- `editable` 设置 true，以允许编辑
- 设置 `cellFactory`，以提供编辑状态的单元格

选择 cell，点击单元格开始编辑。持焦点后点击空格也开始编辑。

如果 `ListView` 为 editable 且包含 editable cell，则可以通过 `edit(int index)` 方法编辑指定索引下的单元格。

```ad-tip
`ListView` 的只读属性 `editingIndex` 表示当前被编辑 cell 的索引，如果没有被编辑的单元格，其值为 -1。
```

JavaFX 预定义了多个 `cellFactory`，从而可以使用 `TextField`, `ChoiceBox`, `ComboBox` 和 `CheckBox` 编辑 `ListCell`，对应 `ListCell` 子类为 `TextFieldListCell`, `ChoiceBoxListCell`, `ComboBoxListCell`, `CheckBoxListCell`。

### 5.1. 使用 TextField 编辑

`TextFieldListCell` :

- 显示状态用 `Label` 显示文本
- 编辑状态用 `TextField`

对非 `String` 类型需要设置 `StringConverter`，以提供字符串和元素类型之间的转换。

`TextFieldListCell` 的 `forListView()` 静态方法返回一个用于编辑字符串的 `cellFactory`。设置 `TextField` 作为单元格编辑器的方法：

```java
ListView<String> breakfasts = new ListView<>();
breakfasts.setEditable(true);

// Set a TextField as the editor
Callback<ListView<String>, ListCell<String>> cellFactory = 
                                TextFieldListCell.forListView();
breakfasts.setCellFactory(cellFactory);
```

对非字符串类型，如 `Person`，需要设置 converter：

```java
ListView<Person> persons = new ListView<>();
persons.setEditable(true);

// Set a TextField as the editor.
// Need to use a StringConverter for Person objects.
StringConverter<Person> converter = new PersonStringConverter();
Callback<ListView<Person>, ListCell<Person>> cellFactory = 
                                TextFieldListCell.forListView(converter);
persons.setCellFactory(cellFactory);
```

**示例：** `TextField` 类型的 editable `ListView`

```java
import mjw.study.javafx.mvc.Person;

public class ListViewEditing extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ListView<String> breakfasts = getBreakfastListView();
        ListView<Person> persons = getPersonListView();

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(10);
        root.add(new Label("Double click an item to edit."), 0, 0, 2, 1);
        root.addRow(1, new Label("Persons:"), new Label("Breakfasts:"));
        root.addRow(2, persons, breakfasts);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ListView Cell Factory");
        stage.show();
    }

    public ListView<Person> getPersonListView() {
        ListView<Person> persons = new ListView<>();
        persons.setPrefSize(200, 120);
        persons.setEditable(true);
        persons.getItems().addAll(new Person("John", "Jacobs", null),
                new Person("Donna", "Duncan", null),
                new Person("Layne", "Estes", null),
                new Person("Mason", "Boyd", null));

        // Set a TextField cell factory to edit the Person items. Also use a
        // StringConverter to convert a String to a Person and vice-versa
        StringConverter<Person> converter = new PersonStringConverter();
        Callback<ListView<Person>, ListCell<Person>> cellFactory =
                TextFieldListCell.forListView(converter);
        persons.setCellFactory(cellFactory);

        return persons;
    }

    public ListView<String> getBreakfastListView() {
        ListView<String> breakfasts = new ListView<>();
        breakfasts.setPrefSize(200, 120);
        breakfasts.setEditable(true);
        breakfasts.getItems().addAll("Apple", "Banana", "Donut", "Hash Brown");

        // Set a TextField cell factory to edit the String items
        Callback<ListView<String>, ListCell<String>> cellFactory =
                TextFieldListCell.forListView();
        breakfasts.setCellFactory(cellFactory);

        return breakfasts;
    }
}
```

![|400](Pasted%20image%2020230724115555.png)

### 5.2. 使用 ChoiceBox 或 ComboBox 编辑

ChoiceBoxListCell 特点：

- 使用 Label 显示元素
- 使用 ChoiceBox 编辑元素

对非 String 类型，需要设置 StringConverter 进行转换。

`ChoiceBoxListCell.forListView()` 创建对应的 cellFactory。

为 ListView 设置 ChoiceBoxListCell：

```java
ListView<String> breakfasts = new ListView<>();
...
breakfasts.setEditable(true);

// Set a cell factory to use a ChoiceBox for editing
ObservableList<String> items = FXCollections.<String>observableArrayList(
                        "Apple", "Banana", "Donut", "Hash Brown");
breakfasts.setCellFactory(ChoiceBoxListCell.forListView(items));
```

**示例：** 使用 ChoiceBox 编辑元素

双击元素开始编辑。在编辑模式，cell 变成 ChoiceBox。

```java
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListViewChoiceBoxEditing extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ListView<String> breakfasts = new ListView<>();
        breakfasts.setPrefSize(200, 120);
        breakfasts.setEditable(true);
        breakfasts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Let the user select a maximum of four breakfast items
        breakfasts.getItems().addAll("[Double click to select]",
                "[Double click to select]",
                "[Double click to select]",
                "[Double click to select]");

        // The breakfast items to select from
        ObservableList<String> items = FXCollections.<String>observableArrayList(
                "Apple", "Banana", "Donut", "Hash Brown");

        // Set a ChoiceBox cell factory for editing
        breakfasts.setCellFactory(ChoiceBoxListCell.forListView(items));

        VBox root = new VBox(new Label("Double click an item to select."),
                new Label("Breakfasts:"),
                breakfasts);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ListView Cell Factory");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230724121812.png)

### 5.3. 使用 CheckBox 编辑

CheckBoxListCell 采用 CheckBox 编辑 ListView 的 cell，不支持 CheckBox 的 `indeterminate` 状态。

CheckBoxListCell 的值采用 `ObservableValue<Boolean>` 类型，在 ListView 内部将 `ObservableValue<Boolean>` 替换为 CheckBox。

例如，创建一个 map，key 为元素名称，值为 ObservableValue 类型：

```java
Map<String, ObservableValue<Boolean>> map = new HashMap<>();
map.put("Apple", new SimpleBooleanProperty(false));
map.put("Banana", new SimpleBooleanProperty(false));
map.put("Donut", new SimpleBooleanProperty(false));
map.put("Hash Brown", new SimpleBooleanProperty(false));
```

创建 editable ListView：

```java
ListView<String> breakfasts = new ListView<>();
breakfasts.setEditable(true);

// Add all keys from the map as items to the ListView
breakfasts.getItems().addAll(map.keySet());
```

创建 Callback，CheckBoxListCell 会自动调用 call() 方法：

```java
Callback<String, ObservableValue<Boolean>> itemToBoolean = 
                                            (String item) -> map.get(item);
```

创建并设置 celllFactory，对非String 对象，还需要设置 StringConverter：

```java
// Set the cell factory
breakfasts.setCellFactory(CheckBoxListCell.forListView(itemToBoolean));
```

用户对 CheckBox 的操作会自动更新到 map 中的 ObservableValue。要查看 ListView 中的元素是否被选择，需要监听对应的 ObservableValue 对象。

**示例：** CheckBox 编辑 ListView

```java
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

public class ListViewCheckBoxEditing extends Application {

    Map<String, ObservableValue<Boolean>> map = new HashMap<>();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Populate the map with ListView items as its keys and 
        // their selected state as the value 
        map.put("Apple", new SimpleBooleanProperty(false));
        map.put("Banana", new SimpleBooleanProperty(false));
        map.put("Donut", new SimpleBooleanProperty(false));
        map.put("Hash Brown", new SimpleBooleanProperty(false));

        ListView<String> breakfasts = new ListView<>();
        breakfasts.setPrefSize(200, 120);
        breakfasts.setEditable(true);
        breakfasts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Add all keys from the map as items to the ListView		
        breakfasts.getItems().addAll(map.keySet());

        // Create a Callback object
        Callback<String, ObservableValue<Boolean>> itemToBoolean = 
                                                    (String item) -> map.get(item);

        // Set the cell factory
        breakfasts.setCellFactory(CheckBoxListCell.forListView(itemToBoolean));

        Button printBtn = new Button("Print Selection");
        printBtn.setOnAction(e -> printSelection());

        VBox root = new VBox(new Label("Breakfasts:"), breakfasts, printBtn);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ListView Cell Factory");
        stage.show();
    }

    public void printSelection() {
        System.out.println("Selected items: ");
        for (String key : map.keySet()) {
            ObservableValue<Boolean> value = map.get(key);
            if (value.getValue()) {
                System.out.println(key);
            }
        }

        System.out.println();
    }
}
```

![|350](Pasted%20image%2020230724130913.png)

## 6. Editable ListView 事件处理

editable ListView 触发三类事件：

- 开始编辑时触发 `editStart` 事件
- 提交编辑结果时触发 `editCommit` 事件
- 取消编辑时触发 `editcancel` 事件

ListView 的静态内部类 `ListView.EditEvent<T>` 用于表示 edit 相关事件：

- getIndex() 返回被编辑元素的 index
- getNewValue() 返回新的输入值
- getSource() 返回触发事件的 ListView

ListView 的 onEditStart, onEditCommit 和 onEditCancel 属性用于设置相关 event handler。

例如，为 ListView 添加 editStart event handler，该 handler 输出被编辑元素的 index 和新的值：

```java
ListView<String> breakfasts = new ListView<>();
...
breakfasts.setEditable(true);
breakfasts.setCellFactory(TextFieldListCell.forListView());

// Add an editStart event handler to the ListView
breakfasts.setOnEditStart(e ->
System.out.println("Edit Start: Index=" + e.getIndex() +
                    ", item = " + e.getNewValue()));
```

**示例：** 处理 edit 相关事件

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ListViewEditEvents extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        ListView<String> breakfasts = new ListView<>();
        breakfasts.setPrefSize(200, 120);
        breakfasts.getItems().addAll("Apple", "Banana", "Donut", "Hash Brown");
        breakfasts.setEditable(true);
        breakfasts.setCellFactory(TextFieldListCell.forListView());

        // Add Edit-related event handlers
        breakfasts.setOnEditStart(this::editStart);
        breakfasts.setOnEditCommit(this::editCommit);
        breakfasts.setOnEditCancel(this::editCancel);

        HBox root = new HBox(new Label("Breakfast:"), breakfasts);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ListView Edit Events");
        stage.show();
    }

    public void editStart(ListView.EditEvent<String> e) {
        System.out.println("Edit Start: Index=" + e.getIndex() +
                ", Item=" + e.getNewValue());
    }

    public void editCommit(ListView.EditEvent<String> e) {
        System.out.println("Edit Commit: Index=" + e.getIndex() +
                ", Item=" + e.getNewValue());
    }

    public void editCancel(ListView.EditEvent<String> e) {
        System.out.println("Edit Cancel: Index=" + e.getIndex() +
                ", Item=" + e.getNewValue());
    }
}
```

## 7. CSS

ListView 的默认 CSS 样式类名为 list-view；ListCell 的为 list-cell。

ListView 有两个 CSS pseudo-classes: horizontal 和 vertical。

`-fx-orientation` CSS 属性指定 ListView 的方向，可以指定为 horizontal 或 vertical。

ListCell 提供了如下 CSS pseudo-classes:

- empty，cell 为空时应用
- filled，cell 不为空时应用
- selected，cell 被选时应用
- odd，索引为奇数
- even，索引为偶数

例如，将奇数 cell 显示为褐色，偶数 cell 显示为浅灰色：

```css
.list-view .list-cell:even {
    -fx-background-color: tan;
}

.list-view .list-cell:odd {
    -fx-background-color: lightgray;
}
```

在 modena.css 文件中，`ListCell` 的默认背景颜色为 `-fx-control-inner-background`，这是一个 CSS 派生颜色：

- 对奇数 cell，默认颜色为 `derive(-fx-control-innerbackground,-5%)`

为了让所有单元格的背景颜色保持一致，需要覆盖奇数单元格的背景颜色：

```css
.list-view .list-cell:odd {
    -fx-background-color: -fx-control-inner-background;
}
```

这只是设置了 ListView 在正常状态下的背景颜色。ListView 还有 focused, selected, empty 和 filled 等状态。因此还需要为这些状态指定背景色。详情可参考 modena.css 文集爱你。

`ListCell` 支持 `-fx-cell-size` CSS 属性，表示垂直 `ListView` 中 cell 的高度；或水平 `ListView` 中 cell 的宽度。

cell 类型可以为 `ListCell`, `TextFieldListCell`, `ChoiceBoxListCell`, `ComboBoxListCell` 或 `CheckBoxListCell`。默认 CSS 样式类名为 text-field-list-cell, choice-box-list-cell, combo-box-list-cell 和 check-box-list-cell。

下面的 CSS 样式将以 TextField 编辑的 editable ListView 的 TextField 背景设置为黄色：

```css
.list-view .text-field-list-cell .text-field {
    -fx-background-color: yellow;
}
```

## 8. ListView 过滤

下面演示如何实现 `ListView` 过滤。在应用中管理两个 lists，一个包含数据模型的所有 items，一个包含过滤后感兴趣的 items。最终界面如下：

![|230](Pasted%20image%2020230803155148.png)

其中显示的 domain 对象定义为 `Player`：

```java
static class Player {

    private final String team;
    private final String playerName;

    public Player(String team, String playerName) {
        this.team = team;
        this.playerName = playerName;
    }

    public String getTeam() {
        return team;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String toString() {return playerName + " (" + team + ")";}
}
```

`Player` 包含两个字段：`team` 和 `playerName`。提供了一个 toString() 方法，便于 ListView 显示 Player。

定义 `ListView` items:

```java
Player[] players = {new Player("BOS", "David Ortiz"),
                    new Player("BOS", "Jackie Bradley Jr."),
                    new Player("BOS", "Xander Bogarts"),
                    new Player("BOS", "Mookie Betts"),
                    new Player("HOU", "Jose Altuve"),
                    new Player("HOU", "Will Harris"),
                    new Player("WSH", "Max Scherzer"),
                    new Player("WSH", "Bryce Harper"),
                    new Player("WSH", "Daniel Murphy"),
                    new Player("WSH", "Wilson Ramos")};
```

下面定义两个 list：

- playersProperty 存储所有 items
- viewablePlayersProperty 存储过滤后显示的 items

```java
ReadOnlyObjectProperty<ObservableList<Player>> playersProperty =
                new SimpleObjectProperty<>(FXCollections.observableArrayList());

ReadOnlyObjectProperty<FilteredList<Player>> viewablePlayersProperty =
                new SimpleObjectProperty<>(new FilteredList<>(playersProperty.get()));
```

定义一个 Predicate，用于指定过滤规则：

```java
ObjectProperty<Predicate<? super Player>> filterProperty = 
                viewablePlayersProperty.get().predicateProperty();
```

UI root 为 VBox，包含 HBox of ToggleButtons 和 ListView：

```java
VBox vbox = new VBox();  
vbox.setPadding(new Insets(10));  
vbox.setSpacing(4);  
  
HBox hbox = new HBox();  
hbox.setSpacing(2);  
  
ToggleGroup filterTG = new ToggleGroup();
```

- 设置 handler

为每个 `ToggleButton` 添加一个  handler，在每个 `ToggleButton` 的 `userData` 字段中存储 `Predicate`。`toggleHandler` 使用该 `Predicate` 设置 `filter` 属性。其中 "Show All" 比较特殊，没有执行任何过滤，单独设置 `Predicate`：

```java
@SuppressWarnings("unchecked")
EventHandler<ActionEvent> toggleHandler = (event) -> {
    ToggleButton tb = (ToggleButton) event.getSource();
    Predicate<Player> filter = (Predicate<Player>) tb.getUserData();
    filterProperty.set(filter);
};

ToggleButton tbShowAll = new ToggleButton("Show All");
tbShowAll.setSelected(true);
tbShowAll.setToggleGroup(filterTG);
tbShowAll.setOnAction(toggleHandler);
tbShowAll.setUserData((Predicate<Player>) (Player p) -> true);
```

ToggleButton 根据 Player 的 `team` 创建：

1. 从 players 提取 unique teams
2. 对每个 team 创建 ToggleButton
3. 为每个 ToggleButton 设置 Predicate 作为 filter
4. 收集 ToggleButton，添加到 HBox

```java
List<ToggleButton> tbs = Arrays.stream(players)
        .map(Player::getTeam)
        .distinct()
        .map((team) -> {
            ToggleButton tb = new ToggleButton(team);
            tb.setToggleGroup(filterTG);
            tb.setOnAction(toggleHandler);
            tb.setUserData((Predicate<Player>) (Player p) -> team.equals(p.getTeam()));
            return tb;
        }).toList();

hbox.getChildren().add(tbShowAll);
hbox.getChildren().addAll(tbs);
```

创建 ListView，并将其与 viewablePlayersProperty 绑定。这样 ListView 就能根据 filter 变化更新内容：

```java
ListView<Player> lv = new ListView<>();  
lv.itemsProperty().bind(viewablePlayersProperty);
```

最后创建 Scene，显示 Stage。在 onShown 中加载数据：

```java
vbox.getChildren().addAll(hbox, lv);

Scene scene = new Scene(vbox);

primaryStage.setScene(scene);
primaryStage.setOnShown((evt) -> {
    playersProperty.get().addAll(players);
});
```

完整代码：

```java
public class FilterListApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Test data
        Player[] players = {new Player("BOS", "David Ortiz"),
                new Player("BOS", "Jackie Bradley Jr."),
                new Player("BOS", "Xander Bogarts"),
                new Player("BOS", "Mookie Betts"),
                new Player("HOU", "Jose Altuve"),
                new Player("HOU", "Will Harris"),
                new Player("WSH", "Max Scherzer"),
                new Player("WSH", "Bryce Harper"),
                new Player("WSH", "Daniel Murphy"),
                new Player("WSH", "Wilson Ramos")};

        // Set up the model which is two lists of Players and a filter criteria
        ReadOnlyObjectProperty<ObservableList<Player>> playersProperty =
                new SimpleObjectProperty<>(FXCollections.observableArrayList());

        ReadOnlyObjectProperty<FilteredList<Player>> viewablePlayersProperty =
                new SimpleObjectProperty<>(new FilteredList<>(playersProperty.get()));

        ObjectProperty<Predicate<? super Player>> filterProperty =
                viewablePlayersProperty.get().predicateProperty();


        // Build the UI
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(4);

        HBox hbox = new HBox();
        hbox.setSpacing(2);

        ToggleGroup filterTG = new ToggleGroup();

        // The toggleHandler action wills set the filter based on the TB selected
        @SuppressWarnings("unchecked")
        EventHandler<ActionEvent> toggleHandler = (event) -> {
            ToggleButton tb = (ToggleButton) event.getSource();
            Predicate<Player> filter = (Predicate<Player>) tb.getUserData();
            filterProperty.set(filter);
        };

        ToggleButton tbShowAll = new ToggleButton("Show All");
        tbShowAll.setSelected(true);
        tbShowAll.setToggleGroup(filterTG);
        tbShowAll.setOnAction(toggleHandler);
        tbShowAll.setUserData((Predicate<Player>) (Player p) -> true);

        // Create a distinct list of teams from the Player objects, then create ToggleButtons
        List<ToggleButton> tbs = Arrays.stream(players)
                .map(Player::getTeam)
                .distinct()
                .map((team) -> {
                    ToggleButton tb = new ToggleButton(team);
                    tb.setToggleGroup(filterTG);
                    tb.setOnAction(toggleHandler);
                    tb.setUserData((Predicate<Player>) (Player p) -> team.equals(p.getTeam()));
                    return tb;
                }).toList();

        hbox.getChildren().add(tbShowAll);
        hbox.getChildren().addAll(tbs);

        //
        // Create a ListView bound to the viewablePlayers property
        //
        ListView<Player> lv = new ListView<>();
        lv.itemsProperty().bind(viewablePlayersProperty);

        vbox.getChildren().addAll(hbox, lv);

        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.setOnShown((evt) -> {
            playersProperty.get().addAll(players);
        });

        primaryStage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }

    static class Player {

        private final String team;
        private final String playerName;

        public Player(String team, String playerName) {
            this.team = team;
            this.playerName = playerName;
        }

        public String getTeam() {
            return team;
        }

        public String getPlayerName() {
            return playerName;
        }

        @Override
        public String toString() {return playerName + " (" + team + ")";}
    }
}
```
