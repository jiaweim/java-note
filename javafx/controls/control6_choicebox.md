# ChoiceBox

2023-07-24, 08:55
***
## 1. 简介

`ChoiceBox` 用于显示少量元素列表。`ChoiceBox` 为参数化类，可显示任意类型，如果要同时显示多种类型，可使用 raw type。如下所示：

```java
// 可存储任意类型
ChoiceBox seasons = new ChoiceBox();

// 只保存字符串类型
ChoiceBox<String> seasons = new ChoiceBox<String>();
```

创建 ChoiceBox 时指定包含的元素：

```java
// 创建时指定值
ObservableList<String> seasonList = FXCollections.<String>observableArrayList(
                    "Spring", "Summer", "Fall", "Winter");
ChoiceBox<String> seasons = new ChoiceBox<>(seasonList);
```

创建之后，可以使用 `items` 属性（`ObjectProperty<ObservableList<T>>` 类型）继续添加元素：

```java
// 创建后指定值
ChoiceBox<String> seasons = new ChoiceBox<>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");
```

### 1.1. CheckBox 状态

下图是 `CheckBox` 的不同状态。

![|400](Pasted%20image%2020230723200559.png)

- 图 #1 是初始状态，可以用鼠标或键盘打开列表
  - 点击控件内任意地方打开弹窗列表
  - 如果控件持有焦点，按向下箭头也可以打开列表
- 图 #2 是打开列表的效果，可以鼠标点击选择，也可以箭头上下移动，用 Enter 键选择
- 图 #3 是选择后的效果
- 图 #4 是选择后，再次打开列表的效果

### 1.2. ChoiceBox 属性

|属性|类型|说明|
|---|---|---|
|converter|`ObjectProperty<StringConverter<T>>`|converter 的 `toString()` 用于创建对应类型的字符串形式，用于显示|
|items|`ObjectProperty<ObservableList<T>>`|所有选项列表|
|selectionModel|`ObjectProperty<SingleSelectionModel<T>>`|选择模型，用于保存当前被选择的项|
|showing|`ReadOnlyBooleanProperty`|true 表示显示列表项，false表示列表项收缩|
|value|`ObjectProperty<T>`|当前被选择项，如果未选择，为 null|

```ad-tip
除了用鼠标或键盘显示 list 元素，还可以使用 `show()` 和 `hide()` 方法代码设置。
```

### 1.3. 设置值

`value` 属性保存当前选择的元素，类型为 `ObjectProperty<T>`。如果没有选择任何元素，为 `null`。

设置 `value` 值的方法：

```java
// Create a ChoiceBox for String items
ChoiceBox<String> seasons = new ChoiceBox<String>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");

// Get the selected value
String selectedValue = seasons.getValue();

// Set a new value
seasons.setValue("Fall");
```

使用 `setValue()` 设置值：

- 如果值存在于列表中，`ChoiceBox` 选择该值
- 如果值不存在，也可以设置该值，但是控件不显示该值，而显示之前选择的值

### 1.4. 选择模型

`ChoiceBox` 需要记录当前选择的元素及其索引，`ChoiceBox` 使用 selectionModel 属性实现该功能，对应类为 `SingleSelectionModel`。选择模型的提供的功能：

- 选择元素
    - `select(int index)`，根据索引选择元素
    - `select(T item)`，选择指定元素
    - 第一个 `selectFirst()`
    - `selectNext()`：选择下一个元素，如已选择最后一个元素，调用该方法无效
    - `selectPrevious()`：选择上一个 元素，
    - `selectLast()`：选择最后一个 元素
    - 也可以使用 `setValue()` 选择值
- 清除选择 `clearSelection()`
- `selectedItem` 和 `selectedIndex` 属性分别记录当前选择的元素和索引，可以添加 `ChangeListener` 监听它们的改变。当没有选择元素，`selectedItem` 为 `null`，`selectedIndex` 为 -1。

例如，选择第一个元素：

```java
ChoiceBox<String> seasons = new ChoiceBox<>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter", "Fall");

// Select the first item in the list
seasons.getSelectionModel().selectFirst();
```


**示例：** 创建一个包含四季的 `ChoiceBox`

- 默认选择第一个元素
- 为 selectedIndex 和 selectedItem 属性添加 `ChangeListener`
- 将 Label 的 text 属性与 ChoiceBox 的 `value` 属性绑定，显示当前选项

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChoiceBoxTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label seasonLbl = new Label("Select a Season:");
        ChoiceBox<String> seasons = new ChoiceBox<>();
        seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");

        // Select the first season from the list
        seasons.getSelectionModel().selectFirst();

        // Add ChangeListeners to track change in selected index and item. Only 
        // one listener is necessary if you want to track change in selection
        seasons.getSelectionModel().selectedItemProperty()
                .addListener(this::itemChanged);
        seasons.getSelectionModel().selectedIndexProperty()
                .addListener(this::indexChanged);

        Label selectionMsgLbl = new Label("Your selection:");
        Label selectedValueLbl = new Label("None");

        // Bind the value property to the text property of the Label
        selectedValueLbl.textProperty().bind(seasons.valueProperty());

        // Display controls in a GridPane
        GridPane root = new GridPane();
        root.setVgap(10);
        root.setHgap(10);
        root.addRow(0, seasonLbl, seasons);
        root.addRow(1, selectionMsgLbl, selectedValueLbl);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using ChoiceBox Controls");
        stage.show();
    }

    // A change listener to track the change in selected item
    public void itemChanged(ObservableValue<? extends String> observable,
                            String oldValue,
                            String newValue) {
        System.out.println("Itemchanged: old = " + oldValue +
                ", new = " + newValue);
    }

    // A change listener to track the change in selected index
    public void indexChanged(ObservableValue<? extends Number> observable,
                             Number oldValue,
                             Number newValue) {
        System.out.println("Indexchanged: old = " + oldValue + ", new = " + newValue);
    }
}
```

![|350](Pasted%20image%2020230723210356.png)

## 2. StringConverter

`ChoiceBox` 支持任何对象类型。`CheckBox` 调用对象的 `toString()` 方法，在弹出列表中显示返回的 String 值。

**示例：** 创建 `ChoiceBox`，添加 4 个 `Person` 对象

注意，`ChoiceBox` 显示的是 `Person` 的 `toString(`) 字符串。

```java
import com.jdojo.mvc.model.Person;
import javafx.scene.control.ChoiceBox;
...
ChoiceBox<Person> persons = new ChoiceBox<>();
persons.getItems().addAll(new Person("John", "Jacobs", null),
                          new Person("Donna", "Duncan", null),
                          new Person("Layne", "Estes", null),
                          new Person("Mason", "Boyd", null));
```

![|400](Pasted%20image%2020230723215316.png)

ChoiceBox 包含一个 converter 属性，该属性为 `StringConverter<T>` 类型的 ObjectProperty，用于对象类型 T 和 String 之间的转换。该类为抽象类：

```java
public abstract class StringConverter<T> {
    public abstract String toString(T object);
    public abstract T fromString(String string);
}
```

`toString(T object)` 将对象 T 转换为 String；`fromString(String string)` 将 String 转换为 T 对象。

ChoiceBox 的 converter 属性默认为 null。设置 converter 属性后，ChoiceBox 不再使用对象的 toString() 方法，而是用 converter.toString(T object) 获得对象的字符串表示。

在 ChoiceBox 中不需要实现 converter.fromString() 方法。ComboBox 中会用到。

```java
import javafx.util.StringConverter;
import mjw.study.javafx.mvc.Person;

public class PersonStringConverter extends StringConverter<Person> {

    @Override
    public String toString(Person p) {
        return p == null ? null : p.getLastName() + ", " + p.getFirstName();
    }

    @Override
    public Person fromString(String string) {
        Person p = null;
        if (string == null) {
            return p;
        }

        int commaIndex = string.indexOf(",");
        if (commaIndex == -1) {
            // Treat the string as first name
            p = new Person(string, null, null);
        } else {
            // Ignoring string bounds check for brevity
            String firstName = string.substring(commaIndex + 2);
            String lastName = string.substring(0, commaIndex);
            p = new Person(firstName, lastName, null);
        }

        return p;
    }
}
```

使用该 converter 显示 Person:

```java
import mjw.study.javafx.mvc.Person;
import javafx.scene.control.ChoiceBox;
...
ChoiceBox<Person> persons = new ChoiceBox<>();

// Set a converter to convert a Person object to a String object
persons.setConverter(new PersonStringConverter());

// Add five person objects to the ChoiceBox
persons.getItems().addAll(new Person("John", "Jacobs", null),
                        new Person("Donna", "Duncan", null),
                        new Person("Layne", "Estes", null),
                        new Person("Mason", "Boyd", null));
```

## 3. Null

ChoiceBox 支持 null 质。如下：

```java
ChoiceBox<String> seasons = new ChoiceBox<>();
seasons.getItems().addAll(null, "Spring", "Summer", "Fall", "Winter");
```

null 显示为空白：

![|100](Pasted%20image%2020230724082812.png)

使用 converter 可以将 null 显示为其它字符串，如显示为 "[None]"：

```java
ChoiceBox<String> seasons = new ChoiceBox<>();
seasons.getItems().addAll(null, "Spring", "Summer", "Fall", "Winter");

// Use a converter to convert null to "[None]"
seasons.setConverter(new StringConverter<String>() {
    @Override
    public String toString(String string) {
        return (string == null) ? "[None]" : string;
    }
    @Override
    public String fromString(String string) {
        return string;
    }
});
```

## 4. Separators

`Separator` 类用于在 ChoiceBox 中添加分割线。`Separator` 在 ChoiceBox 中不可选择。例如：

```java
ChoiceBox breakfasts = new ChoiceBox();
breakfasts.getItems().addAll("Apple", "Banana", "Strawberry",
                            new Separator(),
                            "Apple Pie", "Donut", "Hash Brown");
```

![|120](Pasted%20image%2020230724084110.png)

## 5. CSS

ChoiceBox 的 style class name 默认为 `choice-box`。

ChoiceBox 支持 `showing` CSS pseudo-class，当 `showing` 属性为 true 时应用。

ChoiceBox 有两个子结构：

- open-button
- arrow

两者均为 StackPane 类型。

ChoiceBox 用 Label 显示选择的元素。选项列表用 ContextMenu 显示：

- ContextMenu 的 ID 为 `choice-box-popup-menu`
- 每个元素以 menuItem 显示，ID 为 `choice-box-menu-item`

```css
/* Set the text color and font size for the selected item in the control */
.choice-box .label {
    -fx-text-fill: blue;
    -fx-font-size: 8pt;
}

/* Set the text color and text font size for choices in the popup list */
#choice-box-menu-item * {
    -fx-text-fill: blue;
    -fx-font-size: 8pt;
}

/* Set background color of the arrow */
.choice-box .arrow {
    -fx-background-color: blue;
}

/* Set the background color for the open-button area */
.choice-box .open-button {
    -fx-background-color: yellow;
}

/* Change the background color of the popup */
#choice-box-popup-menu {
    -fx-background-color: yellow;
}
```
