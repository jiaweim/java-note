# TableView

2023-07-26⭐
@author Jiawei Mao

****
## 概述
TableView 用于显示和编辑表格数据。

TableView 的使用涉及多个类：

- TableView
- TableColumn
- TableRow
- TableCell
- TablePosition
- TableView.TableViewFocusModel
- TableView.TableViewSelectionModel

TableColumn 说明：

- TableColumn 表示表格的 column，TableView 包含多个 TableColumn
- TableColumn 包含多个 TableCell
- TableColumn 使用 cellValueFactory 为 TableCell 生成值
- TableColumn 使用 cellFactory 为 TableCell 渲染数据，默认 cellFactory 能够渲染 text 和 graphic
- 必须为 TableColumn 设置 cellValueFactory

TableRow 说明：

- TableRow 继承 IndexedCell 类，表示 TableView 的行
- 基本不会用到 TableRow，除非需要自定义实现 rows

TableCell 说明：

- TableCell 表示 TableView 中的 cell
- TableCell 高度可定制
- TableCell 可以显示数据和 graphic

TableColumn, TableRow 和 TableCell 都包含一个 tableView 属性，指向所属 TableView。TableColumn 还没加到 TableView 时，该属性为 null。

TablePosition 表示 cell 位置，其 getRow() 和 getColumn() 方法返回 cell 所在 row 和 column indexes。

TableViewFocusModel 是 TableView 的内部静态类，代表 TableView 管理 rows 和 cells 的 focusModel。

TableViewSelectionModel 也是 TableView 的内部静态类，代表 TableView 管理 row 和 cells 的 selectionModel。

与 ListView 和 TreeView 一样，TableView 是虚拟化的。TableView 只创建足够显示可见内容的 cells。滚动内容时，cells 被回收。这有助于减少 scene graph 中 nodes 数。假设 TableView 有 10 列 1000 行，一次只显示 10 行。一次创建 10,000 个 cells 效率太低，TableView 只创建 100 个 cells，滚动内容时，这 100 个 cells 被循环使用显示其它内容。虚拟化是的 TableView 可以用大数据模型 一起是会用，而不会因为查看数据影响性能。

### 示例数据

下面使用 MVC 中的 `Person` 类作为数据类。下面的 `PersonTableUtil` 类为 `TableView` 提供数据。

```java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import mvc.mjw.javafx.Person;

import java.time.LocalDate;

public class PersonTableUtil {

    /* Returns an observable list of persons */
    public static ObservableList<Person> getPersonList() {
        Person p1 = new Person("Ashwin", "Sharan", LocalDate.of(2012, 10, 11));
        Person p2 = new Person("Advik", "Sharan", LocalDate.of(2012, 10, 11));
        Person p3 = new Person("Layne", "Estes", LocalDate.of(2011, 12, 16));
        Person p4 = new Person("Mason", "Boyd", LocalDate.of(2003, 4, 20));
        Person p5 = new Person("Babalu", "Sharan", LocalDate.of(1980, 1, 10));
        return FXCollections.<Person>observableArrayList(p1, p2, p3, p4, p5);
    }

    /* Returns Person Id TableColumn */
    public static TableColumn<Person, Integer> getIdColumn() {
        TableColumn<Person, Integer> personIdCol = new TableColumn<>("Id");
        personIdCol.setCellValueFactory(new PropertyValueFactory<>("personId"));
        return personIdCol;
    }

    /* Returns First Name TableColumn */
    public static TableColumn<Person, String> getFirstNameColumn() {
        TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        return fNameCol;
    }

    /* Returns Last Name TableColumn */
    public static TableColumn<Person, String> getLastNameColumn() {
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        return lastNameCol;
    }

    /* Returns Birth Date TableColumn */
    public static TableColumn<Person, LocalDate> getBirthDateColumn() {
        TableColumn<Person, LocalDate> bDateCol = new TableColumn<>("Birth Date");
        bDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        return bDateCol;
    }
}
```

## 创建 TableView

`TableView` 是一个参数化类，例如，创建 `TableView` 显示 `Person` 数据：

```java
TableView<Person> table = new TableView<>();
```

将该 `TableView` 添加到 `Scene`，由于没有数据，会显示一个占位符。如下所示：

<img src="images/Pasted%20image%2020230726144340.png" width="250" />

也可以在构造时指定数据，参数类型为 `ObservableList`，例如：

```java
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());
```

### 添加 Column

`TableColumn` 类表示 `TableView` 的 `column`：

- `TableColumn` 负责显示和编辑 cell
- `TableColumn` 包含一个 header，可显示标题 text 和 graphic
- 可以为 `TableColumn` 设置 context-menu，右键 `TableColumn` 的 header 区域查看
- 使用 `contextMenu` 属性设置 context-menu

`TableColumn<S, T>` 是泛型类：

- 参数 `S` 为 item 类型，与 TableView 参数类型相同
- 参数 T 是该 column 的数据类型

例如，`TableColumn<Person, Integer>` 表示一个 column，可用来显示 Person 的 ID（Integer）；`TableColumn<Person, String>` 可用来显示 Person 的 firstName（String）。

**示例：** 创建标题为 `First Name` 的 column

```java
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
```

### cellValueFactory

`TableColumn` 需要知道如何从模型获取数据。为了填充 cells，需要为 `TableColumn` 设置 `cellValueFactory` 属性。

如果 `TableView` 显示的对象使用 JavaFX 属性定义，则可以使用 PropertyValueFactory 类作为 `cellValueFactory`，它根据属性名称读取属性值，并填充 column 的所有 cells：

```java
// Use the firstName property of Person object to populate the column cells
PropertyValueFactory<Person, String> fNameCellValueFactory = 
                                            new PropertyValueFactory<>("firstName");
fNameCol.setCellValueFactory(fNameCellValueFactory);
```

需要为 `TableView` 的每个 column 创建 `TableColumn` 对象，并设置对应的 `cellValueFactory`。

最后将创建的所有 `TableColumn` 添加到 `TableView`。`TableView` 将 `TableColumn` 保存在 `ObservableList<TableColumn>` 中，`getColumns()` 返回该 list：

```java
// Add the First Name column to the TableView
table.getColumns().add(fNameCol);
```

**示例：** `TableView` 简单示例

```java
import mvc.mjw.javafx.Person;

@SuppressWarnings("unchecked")
public class SimplestTableView extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a TableView with a list of persons
        TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

        // Add columns to the TableView
        table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                PersonTableUtil.getFirstNameColumn(),
                PersonTableUtil.getLastNameColumn(),
                PersonTableUtil.getBirthDateColumn());

        VBox root = new VBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Simplest TableView");
        stage.show();
    }
}
```

<img src="images/Pasted%20image%2020230801095043.png" width="300" />

### column 嵌套

`TableView` 支持 column 嵌套。例如，将 First 和 Last column 嵌套在 Name column 下。TableColumn 将嵌套 columns 保存在 TableColumn.getColumns() 返回的 list 中。嵌套 column 也需要设置 `cellValueFactory`。

嵌套 columns 只是提供视觉效果，只需要将 topmost columns 添加到 TableView，不需要添加嵌套 columns。

```java
// 创建 TableView
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

// 创建 id column
TableColumn<Person, String> idCol = new TableColumn<>("Id");
idCol.setCellValueFactory(new PropertyValueFactory<>("personId"));

TableColumn<Person, String> fNameCol = new TableColumn<>("First");
fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
TableColumn<Person, String> lNameCol = new TableColumn<>("Last");
lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

// Create Name column and nest First and Last columns in it
TableColumn<Person, String> nameCol = new TableColumn<>("Name");
nameCol.getColumns().addAll(fNameCol, lNameCol);

// Add columns to the TableView
table.getColumns().addAll(idCol, nameCol);
```

<img src="images/Pasted%20image%2020230801095926.png" width="300" />

TableView 提供了查看嵌套 column 的方法：

```java
TableColumn<S,?> getVisibleLeafColumn(int columnIndex)
ObservableList<TableColumn<S,?>> getVisibleLeafColumns()
int getVisibleLeafIndex(TableColumn<S,?> column)
```

`getVisibleLeafColumn()` 返回指定 index 处的 column，column index 只计算可见的 leaf column，从 0 开始。上图的 "Name" column 就不属于可见的 leaf column，所以不算在 column index 中。 

`getVisibleLeafColumns()` 返回所有可见的 column list。

`getVisibleLeafIndex()` 返回指定 column 的 leafIndex。

### 自定义 placeholder

当 TableView 没有任何可见 leaf column 或内容，它会显示一个占位符（placeholder）。

**示例：** 创建 TableView 并添加 TableColumn

由于没有添加数据，所以 TableView 只显示 column 标题和 placeholder。

```java
TableView<Person> table = new TableView<>();
table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                            PersonTableUtil.getFirstNameColumn(),
                            PersonTableUtil.getLastNameColumn(),
                            PersonTableUtil.getBirthDateColumn());
```

![|300](Pasted%20image%2020230801102259.png)

通过 TableView 的 `placeholder` 属性可以自定义占位符。`placeholder` 属性为 Node 类型。

**示例：** 将 Label 设置为占位符

```java
table.setPlaceholder(new Label("No visible columns and/or data exist."));
```

**示例：** 自定义占位符

不同条件显示不同占位符，将 placeholder 属性与条件语句绑定

```java
table.placeholderProperty().bind(
    new When(new SimpleIntegerProperty(0)
                .isEqualTo(table.getVisibleLeafColumns().size()))
        .then(new When(new SimpleIntegerProperty(0)
                            .isEqualTo(table.getItems().size()))
                .then(new Label("No columns and data exist."))
                .otherwise(new Label("No columns exist.")))
        .otherwise(new When(new SimpleIntegerProperty(0)
                                .isEqualTo(table.getItems().size()))
                .then(new Label("No data exist."))
                .otherwise((Label)null)));
```

### 填充数据

TableView 的 `items` 属性为 `ObservableList<S>` 类型，泛型 S 与 TableView 泛型相同。items 的每个元素对应 TableView 的一个 row。向 `items` 添加一个 item 等价于向 TableView 添加新的 row，从 items 删除一个 item 也会从 TableView 删除相应 row。

```ad-tip
更新 items 中的 item 是否会更新 TableView 中相应的数据，取决于如何设置 TableColumn 的 cellValueFactory。
```

**示例：** 创建 TableView，每个 row 表示一个 Person 对象，添加 2 个 item

```java
TableView<Person> table = new TableView<>();
Person p1 = new Person("John", "Jacobs", null);
Person p2 = new Person("Donna", "Duncan", null);

table.getItems().addAll(p1, p2);
```

如果没有为 TableView 添加 TableColumn，添加 item 无效。TableColumn 的功能包括：

- 定义 column 的 text 和 graphic
- 通过 cellValueFactory 为 column 中的 cells 填充值

通过 TableColumn 类可以完全控制如何填充 column 的所有 cells。

cellValueFactory 属性为 `Callback` 类型，接受 TableColumn.CellDataFeatures 对象，返回 ObservableValue。

`CellDataFeatures` 是 TableColumn 的静态内部类，封装了 cell 所在 TableView, TableColumn 以及对应 row 的 item 值，`getTableView()`, g`etTableColumn()` 和 `getValue()` 返回对应信息。

当 TableView 需要一个 cell 的值，它会调用该 cell 所属 column 的 cellValueFactory 的 call() 方法，call() 返回一个 ObservableValue 对象，并监听其变化。

返回的 `ObservableValue` 可以包含任何类型的对象：

- 如果为 Node 类型，则作为 cell 的 graphic 显示
- 否则调用  item 的 toString() 方法，返回的字符串作为 cell 的 text 显示

**示例：** 使用匿名内部类创建 cellValueFactory，返回 Person 的 firstName 属性，firstName 为 JavaFX property，为 ObservableValue 类型

```java
import static javafx.scene.control.TableColumn.CellDataFeatures;
...
// Create a String column with the header "First Name" for Person object
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");

// Create a cell value factory object
Callback<CellDataFeatures<Person, String>, ObservableValue<String>> fNameCellFactory =
    new Callback<CellDataFeatures<Person, String>, ObservableValue<String>>() {
        @Override
        public ObservableValue<String> call(CellDataFeatures<Person, String> cellData) {
            Person p = cellData.getValue();
            return p.firstNameProperty();
        }
};

// Set the cell value factory
fNameCol.setCellValueFactory(fNameCellFactory);
```

使用 lambda 表达式更方便，上面的代码可以写成：

```java
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
fNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
```

#### JavaFX Property 字段

当 item 中作为 column 数据的字段为 JavaFX property 类型，则可以直接用 `PropertyValueFactory` 作为 cellValueFactory。将 JavaFX 属性字段名称传入 PropertyValueFactory 构造函数即可：

```java
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
```

```ad-tip
使用 JavaFX 属性作为 cell 值有一个很大优势：TableView 会保持属性和 cell 值同步。修改属性值会自动更新 cell 值。
```

#### POJO

TableColumn 还支持 POJO 作为 TableView 的 items。缺点是 model 更新时，cell 值不会自动更新。POJO 对象也可以使用 PropertyValueFactory 创建 cellValueFactory。该类会根据 property name 查找 public getter 和 setter 方法。如果只有 getter 方法，cell 变为 read-only。

对 xxx 属性，PropertyValueFactory 根据 JavaBeans 命名规则查找 getXxx() 和 setXxx() 方法；如果 xxx 是 boolean 类型，则还查找 isXxx() 方法。如果没有找到 getter 和 setter 方法，抛出 RuntimeException。

**示例：** 创建 "Age Category" column

```java
TableColumn<Person, Person.AgeCategory> ageCategoryCol = 
                                                new TableColumn<>("Age Category");
ageCategoryCol.setCellValueFactory(new PropertyValueFactory<>("ageCategory"));
```

该代码表明 item 为 Person 类型，column 为 `Person.AgeCategory` 类型。

将 "ageCategory" 作为属性名传入 `PropertyValueFactory`，`PropertyValueFactory` 首先在 `Person` 类中查找 `ageCategory` 属性；但是 `Person` 没有该属性，所以 `PropertyValueFactory` 将 Person POJO 处理，在 Person 类中查找 getAgeCategory() 和 setAgeCategory() 方法。只找到 getter 方法 getAgeCategory()，所以该 column 为 read-only。

#### 动态计算

column 中 cell 数据除了来自 JavaFX 或 POJO，也可以根据逻辑计算而来。此时需要自定义 cellValueFactory，返回 `ReadOnlyXxxWrapper` 对象封装计算结果。

**示例：** 创建 Age column，显示计算的年龄

```java
TableColumn<Person, String> ageCol = new TableColumn<>("Age");
ageCol.setCellValueFactory(cellData -> {
    Person p = cellData.getValue();
    LocalDate dob = p.getBirthDate();
    String ageInYear = "Unknown";
    if (dob != null) {
        long years = YEARS.between(dob, LocalDate.now());
        if (years == 0) {
            ageInYear = "< 1 year";
        } else if (years == 1) {
            ageInYear = years + " year";
        } else {
            ageInYear = years + " years";
        }
    }
    return new ReadOnlyStringWrapper(ageInYear);
});
```

#### 完整示例

下面分别使用 JavaFX Property, POJO property 以及计算方法创建 cellValueFactory。

```java
import mvc.mjw.javafx.Person;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.YEARS;

public class TableViewDataTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        // Create a TableView with data		
        TableView<Person> table =
                new TableView<>(PersonTableUtil.getPersonList());

        // Create an "Age" computed column
        TableColumn<Person, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(cellData -> {
            Person p = cellData.getValue();
            LocalDate dob = p.getBirthDate();
            String ageInYear = "Unknown";

            if (dob != null) {
                long years = YEARS.between(dob, LocalDate.now());
                if (years == 0) {
                    ageInYear = "< 1 year";
                } else if (years == 1) {
                    ageInYear = years + " year";
                } else {
                    ageInYear = years + " years";
                }
            }
            return new ReadOnlyStringWrapper(ageInYear);
        });

        // Create an "Age Cotegory" column
        TableColumn<Person, Person.AgeCategory> ageCategoryCol =
                new TableColumn<>("Age Category");
        ageCategoryCol.setCellValueFactory(
                new PropertyValueFactory<>("ageCategory"));

        // Add columns to the TableView		
        table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                PersonTableUtil.getFirstNameColumn(),
                PersonTableUtil.getLastNameColumn(),
                PersonTableUtil.getBirthDateColumn(),
                ageCol,
                ageCategoryCol);

        HBox root = new HBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Populating TableViews");
        stage.show();
    }
}
```

![|500](Pasted%20image%2020230801125002.png)

TableView 中的 cell 可以显示 text 和 graphic。如果 cellValueFactory 返回 Node 示例，如 ImageView，则 cell 将其作为 graphic 显示。

## Map 作为数据

在 TableView 中一个 row 对应一个 item，item 除了可以映射为 domain 对象，也可以为 Map 类型。

可以自定义 `cellValueFactory` 从 Map 提取数据，也可以使用专门为 Map 设计的 MapValueFactory。

**示例：** TableView 显示 Map 数据

创建 Id column，使用 MapValueFactory 作为 cellValueFactory，`idColumnKey` 作为 key。

```java
TableView<Map> table = new TableView<>();

// Define the column, its cell value factory and add it to the TableView
String idColumnKey = "id";
TableColumn<Map, Integer> idCol = new TableColumn<>("Id");
idCol.setCellValueFactory(new MapValueFactory<>(idColumnKey));
table.getColumns().add(idCol);

// Create and populate a Map an item
Map row1 = new HashMap();
row1.put(idColumnKey, 1);

// Add the Map to the TableView items list
table.getItems().add(row1);
```

```ad-tip
每个 row 对应一个 Map。
```

**示例：** MapValueFactory

```java
import mvc.mjw.javafx.Person;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TableViewMapDataTest extends Application {

    private final String idColumnKey = "id";
    private final String firstNameColumnKey = "firstName";
    private final String lastNameColumnKey = "lastName";
    private final String birthDateColumnKey = "birthDate";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TableView<Map> table = new TableView<>();
        ObservableList<Map<String, Object>> items = this.getMapData();
        table.getItems().addAll(items);
        this.addColumns(table);

        HBox root = new HBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using a Map as items in a TableView");
        stage.show();
    }

    public ObservableList<Map<String, Object>> getMapData() {
        ObservableList<Map<String, Object>> items =
                FXCollections.<Map<String, Object>>observableArrayList();

        // Extract the person data, add the data to a Map, and add the Map to
        // the items list
        ObservableList<Person> persons = PersonTableUtil.getPersonList();
        for (Person p : persons) {
            Map<String, Object> map = new HashMap<>();
            map.put(idColumnKey, p.getPersonId());
            map.put(firstNameColumnKey, p.getFirstName());
            map.put(lastNameColumnKey, p.getLastName());
            map.put(birthDateColumnKey, p.getBirthDate());
            items.add(map);
        }

        return items;
    }

    @SuppressWarnings("unchecked")
    public void addColumns(TableView table) {
        TableColumn<Map, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new MapValueFactory<>(idColumnKey));

        TableColumn<Map, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new MapValueFactory<>(firstNameColumnKey));

        TableColumn<Map, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new MapValueFactory<>(lastNameColumnKey));

        TableColumn<Map, LocalDate> birthDateCol = new TableColumn<>("Birth Date");
        birthDateCol.setCellValueFactory(new MapValueFactory<>(birthDateColumnKey));

        table.getColumns().addAll(idCol, firstNameCol, lastNameCol, birthDateCol);
    }
}
```

![|400](Pasted%20image%2020230801132240.png)

### 显示和隐藏 column

`TableView` 中所有 columns 默认可见。`TableColumn` 类有一个 `visible` 属性，可用于设置 column 的可见性。将 parent column 设置为不可见，那么它包含的所有嵌套 column 将不可见。

```java
TableColumn<Person, String> idCol = new TableColumn<>("Id");

// Make the Id column invisible
idCol.setVisible(false);
...
// Make the Id column visible
idCol.setVisible(true);
```

为了让用户控制 column 的可见性，TableView 提供了 `tableMenuButtonVisible` 属性，设置为 true 时会在表格的标题栏显示一个 menu-button：

```java
// Create a TableView
TableView<Person> table = create the TableView here...

// Make the table menu button visible
table.setTableMenuButtonVisible(true);
```

点击 menu-button 显示所有 leaf-columns 名称。这些 leaf-columns 显示为 radio-menu item 显示，点击可以切换 column 的可见性。如下图所示：

![|400](Pasted%20image%2020230801133100.png)

### Column 顺序

调整 TableView 中 columns 顺序的方式有两种：

- 将 column 拖放到不同为止，默认可用
- 改变 TableView.getColumns() list 中 column 的位置

将 column 拖放到不同为止，该 column 在 `getColumns()` 中的位置随之更改。第二个方式直接更改 column 在 getColumns() 中的位置。

如果想禁用拖放 column，可以将 TableColumn 的 reorderable 属性设置为 false；也可以为 `getColumns()` 返回的 ObservableList 添加 ChangeListener，当 columns 顺序发生变化，将其恢复到原来的顺序。

```java
table.getColumns().forEach(c -> {
    boolean b = ...; // determine whether column is reorderable
    c.setReorderable(b);
});
```

## TableView 数据排序

TableView 内置了 column 数据排序功能，默认允许用户单击 column 标题对数据排序。还支持通过编程方式排序，可以禁用某个或所有 column 的排序。

### 用户操作排序

TableView 所有 columns 数据默认可以排序。用户通过点击 column 标题对数据进行排序：

- 第一次单击按升序排序
- 第二次单击按就降序排序
- 第三次点击将该 column 从 sort-order-list 移除

默认启用单个 column 排序，即 TableView 只根据单个 column 的数据进行排序。要启用 multi-column 排序，按 Shift 键点击多个 columns 的标题进行排序。

TableView 对已排序的 columns 会在标题显示符号：

- 三角形表示排序类型，向上表示升序，向下表示降序
- column 的 sort-order 由点或数字表示：前三个 columns 用点表示，之后用数字表示

sort-order 表示 column 排序的优先级，如下所示：

![|400](Pasted%20image%2020230801140922.png)

这里对 "Last Name" 降序，其它 columns 升序。按照 sort-order，先根据 "Last Name" column 的数据排序，此时前三个 Last Name 相同，继续使用 "First Name" 进行排序。

### 编程排序

columns 中的数据可以编程排序。TableView 和 TableColumn 提供了强大的排序 API。

#### 1. column 可排序

TableColumn 的 sortable 属性指定该 column 是否可排序，默认 true。设置方式：

```java
// Disable sorting for fNameCol column
fNameCol.setSortable(false);
```

#### 2. column 排序类型

TableColumn 通过 sortType 属性指定排序类型，排序类型由 TableColumn.SortType enum 表示：ASCENDING 和 DESCENDING。

sortType 属性默认为 TableColumn.SortType.ASCENDING，设置方式：

```java
// Set the sort type for fNameCol column to descending
fNameCol.setSortType(TableColumn.SortType.DESCENDING);
```

#### 3. 为 column 指定 Comparator

TableColumn 使用 Comparator 排序其数据，通过 TableColumn 的 comparator 属性指定 Comparator。

TableColumn 的默认 Comparator 由常量 TableColumn.DEFAULT_COMPARATOR 定义。基本规则：

- 检查 null 值，null 值优先，两个 nulls 相等
- 如果要比较的第一个值为 Comparable 类型，则调用其 `compareTo()` 方法与第二个对象对比
- 如果以上两个条件都不满足，则调用 toString() 将两个对象转换为字符串，然后使用 Comparator 对比两个 String

大多时候默认 Comparator 就够用了。

**示例：** 自定义 comparator，对比 String column 数据的第一个字符

```java
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
...
// Set a custom comparator
fNameCol.setComparator((String n1, String n2) -> {
    if (n1 == null && n2 == null) {
        return 0;
    }
    if (n1 == null) {
        return -1;
    }
    if (n2 == null) {
        return 1;
    }
    String c1 = n1.isEmpty()? n1:String.valueOf(n1.charAt(0));
    String c2 = n2.isEmpty()? n2:String.valueOf(n2.charAt(0));
    return c1.compareTo(c2);
});
```

#### 4. column 的 sortNode 属性

TableColumn 类包含用一个 sortNode 属性，用于在 column 标题栏显示 column 的 sort-type 和 sort-order。

- 当排序类型为升序，sortNode 旋转 180°
- 当 column 没有排序，不显示 sortNode
- 默认 sortNode 为 null，TableColumn 提供一个三角形作为 sort-node

#### 5. 指定 column 的 sort-order

TableView 包含多个用于排序的属性。要对 column 排序，需要将其添加到 `TableView` 的 sort-order-list。TableView 的 sortOrder 属性为包含 TableColumn 的 ObservableList，column 的 sort-order 与其在该 list 中的顺序一致。

sortOrder 应用规则：

- 使用第一个 column 对 rows 进行排序
- 如果第一个 column 中两个 rows 的值相同，使用第二个 column 的数据确定这两个 rows 的顺序
- 以此类推

**示例：** 向 TableView 添加两个 columns，并指定 sort-order

两个 columns 都升序排序，即默认 sort-type。

```java
// Create a TableView with data
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

TableColumn<Person, String> lNameCol = PersonTableUtil.getLastNameColumn();
TableColumn<Person, String> fNameCol = PersonTableUtil.getFirstNameColumn();

// Add columns to the TableView
table.getColumns().addAll(lNameCol, fNameCol );

// Add columns to the sort order to sort by last name followed by first name
table.getSortOrder().addAll(lNameCol, fNameCol);
```

TableView 监听 sortOrder 属性，如果被修改，TableView 会立刻根据新的 sortOrder 排序。添加 column 到 sort-order-list 并不能保证排序会用该 column。首先，该 column 的数据要是能排序的。

TableView 同时会监听 TableColumn 的 sortType 属性，根据 sortType 属性值的变化更新 TableView 数据顺序。

#### 6. 查看 TableView 的 Comparator

TableView 包含一个 read-only 属性 comparator，它是基于当前 sort-order-list 的 Comparator 实例。在代码中基本用不到 comparator 属性。

如前所述，TableColumn 也有一个 comparator 属性，用于比较 TableColumn 中 cells 的顺序。 TableView 的 comparator 属性结合了 sort-order-list 中所有 TableColumn 的 comparator 属性。

#### 7. 指定 sort-policy

TableView 包含一个排序策略用于指定如何排序。它是一个 Callback 对象，TableView 作为参数传递给 call() 方法，排序成功返回 true；排序失败返回 false 或 null。

TableView 类定义的 DEFAULT_SORT_POLICY 常量为默认排序策略。默认排序策略使用 TableView 的 comparator 属性排序 items。指定 sort-policy 可以完全控制排序算法，sort-policy Callback 的 call() 方法执行 TableView 中 items 的排序。

将 sort-policy 设置为 null 将禁用排序：

```java
TableView<Person> table = ...

// Disable sorting for the TableView
table.setSortPolicy(null);
```

有时出于性能考虑，可以临时禁用排序。

假设你有一个有序 TableView 包含大量 items，然后你想修改 sort-order-list。但是，每次修改 sort-order-list 都会触发 items 重排，此时，可以先将 sort-policy 设置为 null 禁用排序，完成 sort-order-list 修改后，再恢复 sort-policy 以启用排序。修改 sort-policy 会立刻出发排序：

```java
TableView<Person> table = ...
...
// Store the current sort policy
Callback<TableView<Person>, Boolean> currentSortPolicy = table.getSortPolicy();

// Disable the sorting
table.setSortPolicy(null)

// Make all changes that might need or trigger sorting
...

// Restore the sort policy that will sort the data once immediately
table.setSortPolicy(currentSortPolicy);
```

#### 8. 手动排序

`TableView` 的 `sort()` 方法使用当前 sort-order-list 排序 items。你可以在为 `TableView` 添加一些 items 后调用 `sort()` 对 items 进行排序。

当 columns 的 sort-type, sort-order 或 sort-policy 发生变化，会自动调用 `sort()` 方法。

#### 9. sorting 事件

`TableView` 在收到排序请求时，执行排序算法前会 fire `SortEvent`。添加 `SortEvent` listener 可以在排序前执行任何操作：

```java
TableView<Person> table = ...
table.setOnSort(e -> {/* Code to handle the sort event */});
```

如果 SortEvent 被 consume，中止排序。通过这种方式可以禁用 TableView 的排序：

```java
// Disable sorting for the TableView
table.setOnSort(e -> e.consume());
```

#### 10. 禁用 TableView 排序

禁用 `TableView` 排序的方法有多种：

- 设置 `TableColumn` 的 `sortable` 属性只能禁用该 column 的排序功能。如果将 `TableView` 所有 columns 的 `sortable` 属性设置为 false，等价于禁用了 `TableView` 的排序功能
- 将 `TableView` 的 sort-policy 设置为 `null`
- consume 掉 `TableView` 的 `SortEvent`
- 从技术上来讲，可以覆盖 `TableView` 的 `sort()` 方法，提供一个 empty 实现，但不建议这么做

部分或全部禁用 TableView 排序的最佳方法是禁用部分或全部 columns 的排序，即上述的第一种方法。

## 自定义 cell 渲染

`TableColumn` 中的 cell 为 `TableCell` 类型，用于显示 cell 中的数据了。`TableCell` 为 `Labeled` 子类，能够显示 text 和 graphic。

`TableColumn` 类包含 `cellFactory` 属性，为 `Callback` 对象。其 `call()` 方法参数为 cell 所属 `TableColumn`，返回 `TableCell` 实例。覆盖 `TableCell` 的 `updateItem()` 方法可以自定义 cell 数据的渲染。

`TableColumn` 提供了默认 `cellFactory` 实现。默认 `cellFactory` 根据数据类型进行显示：

- 如果 cell 数据包含 `Node`，则作为 cell 的 `graphic` 显示
- 否则调用 cell 数据的 `toString()` 方法，将返回的 `String` 作为 cell 的 `text` 显示

### 自定义 text

到目前为止，TableView 示例均使用 Person 对象列表作为数据模型。出生日期 column 的格式为 yyyy-mm-dd，这是 LocalDate 类的 toString() 方法返回的 ISO 默认日期格式。如果希望将日期格式设置为 mm/dd/yyyy 格式，可以通过自定义 "Birth Date" column 的 cellFactory 实现：

```java
TableColumn<Person, LocalDate> birthDateCol = ...;
birthDateCol.setCellFactory(col -> {
    TableCell<Person, LocalDate> cell =
            new TableCell<Person, LocalDate>() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    // Cleanup the cell before populating it
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                        // Format the birth date in mm/dd/yyyy format
                        String formattedDob = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                                .format(item);
                        this.setText(formattedDob);
                    }
                }
            };
    return cell;
});
```

在比如同时显示 birth date 和 age category，如 10/11/2012 (BABY)：

```java
if (!empty) {
    String formattedDob = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(item);
    
    if (this.getTableRow() != null ) {
        // Get the Person item for this cell
        int rowIndex = this.getTableRow().getIndex();
        Person p = this.getTableView().getItems().get(rowIndex);
        String ageCategory = p.getAgeCategory().toString();
        
        // Display birth date and age category together
        this.setText(formattedDob + " (" + ageCategory + ")" );
    }
}
```

### 自定义 graphic

通过 cellFactory 可以在 cell 中显示图形：

- 在 `updateItem()` 方法中创建 `ImageView`
- 使用 `TableCell` 的 `setGraphic()` 设置 `graphic`

### TableCell 子类

JavaFX 提供了多个 TableCell 的子类，以不同方式渲染 cell 数据：

- CheckBoxTableCell
- ChoiceBoxTableCell
- ComboBoxTableCell
- ProgressBarTableCell
- TextFieldTableCell

**示例：** 创建名为 "Baby?" 的 column，然后将 cellFactory 设置为 CheckBoxTableCell

```java
// Create a "Baby?" column
TableColumn<Person, Boolean> babyCol = new TableColumn<>("Baby?");
babyCol.setCellValueFactory(cellData -> {
    Person p = cellData.getValue();
    Boolean v = (p.getAgeCategory() == Person.AgeCategory.BABY);
    return new ReadOnlyBooleanWrapper(v);
});

// Set a cell factory that will use a CheckBox to render the value
babyCol.setCellFactory(CheckBoxTableCell.<Person>forTableColumn(babyCol));
```

### 示例

自定义 cellFactory 完整示例。

```java
import mvc.mjw.javafx.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TableViewCellFactoryTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

        // Create the birth date column
        TableColumn<Person, LocalDate> birthDateCol = PersonTableUtil.getBirthDateColumn();

        // Set a custom cell factory for Birth Date column
        birthDateCol.setCellFactory(col -> {
            TableCell<Person, LocalDate> cell = new TableCell<>() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    // Cleanup the cell before populating it
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        String formattedDob = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                .format(item);
                        this.setText(formattedDob);
                    }
                }
            };
            return cell;
        });

        // Create and configure the baby column
        TableColumn<Person, Boolean> babyCol = new TableColumn<>("Baby?");
        babyCol.setCellValueFactory(
                cellData -> {
                    Person p = cellData.getValue();
                    Boolean v = (p.getAgeCategory() == Person.AgeCategory.BABY);
                    return new ReadOnlyBooleanWrapper(v);
                });

        // Set a custom cell factory for the baby column
        babyCol.setCellFactory(
                CheckBoxTableCell.<Person>forTableColumn(babyCol));

        // Add columns to the table
        table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                PersonTableUtil.getFirstNameColumn(),
                PersonTableUtil.getLastNameColumn(),
                birthDateCol,
                babyCol);

        HBox root = new HBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using a Custom Cell Factory for a TableColumn");
        stage.show();
    }
}
```

<img src="images/Pasted%20image%2020230801155203.png" width="400" />

## 选择 Cell 和 Row

`TableView` 的 `selectionModel` 属性指定选择模型。`selectionModel` 属性为 `TableViewSelectionModel` 类型，定义为 `TableView` 的静态内部类。

`selectionModel` 支持 cell-level 和 row-level 选择；支持单选和多选：

- 单选模式，一次只能选一个 cell 或一个 row
- 多选模式，一次能选多个 cells 或多个 rows

row-level **默认单选**，设置 row 多选：

```java
TableView<Person> table = ...

TableViewSelectionModel<Person> tsm = table.getSelectionModel();
tsm.setSelectionMode(SelectionMode.MULTIPLE);
```

cell 的选择可以通过 `selectionModel` 的 `cellSelectionEnabled` 属性启用。当 `cellSelectionEnabled` 为 `true`，`TableView` 进入 cell 选择模式，此时不能选择整个 row；如果启用多选模式，依然可以选择整个 row，但是在 cell 选择模式下，`TableView` 不将其作为 row 选择，而是看作多个 cell 选择。cell 选择模式默认为 false，启用方法：

```java
tsm.setCellSelectionEnabled(true);
```

### 操作

`selectionModel` 提供所选 cell 和 row 的信息：

- `isSelected(int rowIndex)` 如果选择指定 `rowIndex` 对应的 row，返回 `true`
- `isSelected(int rowIndex, TableColumn<S,?> column)` 如果选择指定 rowIndex 和 column 的 cell，返回 `true`

`selectionModel` 还提供了选择功能：

- selectAll() 选择所有 cells 或 rows
- select() 为重载方法，可以
    - 选择指定 cell
    - 选择指定范围 cells
- 如果没有选择，isEmpty() 返回 true
- `getSelectedCells()` 返回 read-only `ObservableList<TablePosition>`，包含当前选择的 cells
- `getSelectedIndices()` 返回 read-only `ObservableList<Integer>`，返回当前选择 cell 的 indexes，随着选择变化，该 list 会随之变化
    - 如果启用 row 选择，则该 list 包含的是选择 row 的 index
    - 如果启用 cell 选择，则该 list 为所选 cells 所属 row 的 indexes
- `getSelectedItems()` 返回 read-only `ObservableList<S>`，S 为 TableView 的泛型。该 list 包含对应 row 或 cell 被选的所有 items
- `clearAndSelect()` 在选择前清空选择
- `clearSelection()` 用于清除 cell 选择、row 选择整个 TableView 的选择

cell 或 row 选择的变化，往往伴随其它操作。例如，选择 row 时，刷新对应的详细信息视图。对上述方法返回的 ObservableList 添加 ListChangeListener，即可实现这类操作。

**示例：** 为 `getSelectedIndices()` 返回的 `ObservableList` 添加 `ListChangeListener`

```java
TableView<Person> table = ...
TableViewSelectionModel<Person> tsm = table.getSelectionModel();
ObservableList<Integer> list = tsm.getSelectedIndices();

// Add a ListChangeListener
list.addListener((ListChangeListener.Change<? extends Integer> change) -> {
    System.out.println("Row selection has changed");
});
```

## TableView 编辑数据

TableView 中的 cell 可以编辑。可编辑 cell 在编辑和非编辑模式之间切换，在编辑模式，用户可以修改 cell 数据。

要让 cell 可编辑，TableView, TableColumn 和 TableCell 都需要可编辑。三者都有一个 editable 属性，使用 setEditable(true) 可以设置为 true。TableColumn 和 TableCell 默认可编辑。所以要让 TableView 中的 cell 可编辑，只需要使 TableView 可编辑：

```java
TableView<Person> table = ...
table.setEditable(true);
```

TableColumn 支持三类事件：

- onEditStart
- onEditCommit
- onEditCancel

当 TableColumn 中的 cell 进入编辑模式，触发 onEditStart 事件。当用户成功提交编辑结果，如在 TextField 中按 Enter 键，触发 onEditCommit 事件。当用户取消编辑操作，如在 TextField 中按 Esc 键，触发 onEditCancel 事件。

这三类事件由 TableColumn.CellEditEvent 类表示。事件对象封装了 cell 的原始值、新值以及 TableView, TableColumn, TablePosition 等信息。

单纯将 TableView 设置为可编辑还不能编辑 cell 数据。cell 编辑功能通过特定的 TableCell 实现。JavaFX 提供了一些实现，通过设置 column 的 cellFactory 支持对应的编辑功能：

- CheckBoxTableCell
- ChoiceBoxTableCell
- ComboBoxTableCell
- TextFieldTableCell

### CheckBoxTableCell

CheckBoxTableCell 在 cell 中渲染一个 CheckBox：

- 通常用于表示 boolean 类型 column。
- 可以通过 Callback 对象将其它类型的值映射为 boolean 值
- 将 CheckBox 的 selected 属性和底层的 ObservableValue 双向绑定

Person 类没有 boolean 属性，因此需要通过 cellValueFactory 创建 boolean column。如下所示：

```java
TableColumn<Person, Boolean> babyCol = new TableColumn<>("Baby?");
babyCol.setCellValueFactory(cellData -> {
    Person p = cellData.getValue();
    Boolean v = (p.getAgeCategory() == Person.AgeCategory.BABY);
    return new ReadOnlyBooleanWrapper(v);
});
```

CheckBoxTableCell 的 forTableColumn() 静态方法通过  column 创建 cellFactory:

```java
// Set a CheckBoxTableCell to display the value
babyCol.setCellFactory(CheckBoxTableCell.<Person>forTableColumn(babyCol));
```

CheckBoxTableCell 不会触发 cell-editing 事件。CheckBox 的 selected 属性与 cell 的数据 ObservableValue 绑定，如果对 cell-editing 事件感兴趣，可以为 ObservableValue 添加 ChangeListener。

### ChoiceBoxTableCell

ChoiceBoxTableCell 在 cell 中渲染一个 ChoiceBox：

- ChoiceBoxTableCell 在非编辑模式使用 Label 显示 cell 数据
- 在编辑模式使用 ChoiceBox

Person 没有 gender 属性。假设需要添加一个 "Gender" column，可以通过 ChoiceBox 进行编辑。

**示例：** 创建 TableColumn 并设置 cellValueFactory，这里将所有 cell 值设置为空字符串

```java
// Gender is a String, editable, ComboBox column
TableColumn<Person, String> genderCol = new TableColumn<>("Gender");

// Use an appropriate cell value factory.
// For now, set all cells to an empty string
genderCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(""));
```

ChoiceBoxTableCell.forTableColumn() 创建使用 ChoiceBox 编辑数据的 cellFactory，创建时指定选项：

```java
// Set a cell factory, so it can be edited using a ChoiceBox
genderCol.setCellFactory(
    ChoiceBoxBoxTableCell.<Person, String>forTableColumn("Male", "Female")
);
```

在 ChoiceBox 进行选择，所选值同步到底层模型。支持设置 onEditCommit event handler ：

```java
// Add an onEditCommit handler
genderCol.setOnEditCommit(e -> {
    int row = e.getTablePosition().getRow();
    Person person = e.getRowValue();
    System.out.println("Gender changed (" + person.getFirstName() 
            + " " + person.getLastName() + ")" + " at row " + (row + 1) 
            + ". New value = " + e.getNewValue());
});
```

单击选择的 cell 或双击未选择的 cell 进入编辑模式。focus 移到其它 cell、或从 ChoiceBox 选择一个 item 进入非编辑模式，使用 Label 显示当前值。

### ComboBoxTableCell

ComboBoxTableCell 在 cell 中渲染一个 ComboBox，与 ChoiceBoxTableCell 用法基本一样。

### TextFieldTableCell

TextFieldTableCell 在 cell 中渲染一个 TextField：

- 编辑模式使用 TextField 
- 显示模式使用 Label

单击选择的 cell、或双击未选择的 cell 进入编辑模式，使用 TextField 显示 cell 数据。进入编辑模式后，需要再次点击 TextField，从而在文本框中显示插入符，以便进行修改。简而言之，编辑一个 cell 至少需要 3 次点击，当需要编辑大量数据时非常麻烦。

对正在编辑的 cell：

- 按 Esc 取消编辑，cell 切换到显示模式，cell 数据也恢复到编辑前
- 如果 TableColumn 基于 Writable ObservableValue，则按 Enter 提交数据到底层数据模型

如果正在使用 TextFieldTableCell 编辑一个 cell，切换到另一个 cell 取消编辑。这可能不是用户期望的，但是解决起来不容易。例如，可以创建一个 TableCell 子类，添加 focusChangeListener，当 TextField 失去 focus 时自动提交数据。

使用 static TextFieldTableCell.forTableColumn() 创建 TextField 对应的 cellFactory。例如：

```java
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
fNameCol.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
```

那么，如何用 TextField 编辑非 String 类型数据，如日期。日期在模型中可以表示为 LocalDate 实例。在 TextField 中以格式化字符串显示，用户编辑后以 LocalDate 提交到模型。TextFieldTableCell 通过 StringConverter 支持这类转换。

**示例：** 使用 StringConverter 为 Birth Date column 设置 cellFactory

```java
TableColumn<Person, LocalDate> birthDateCol = new TableColumn<>("Birth Date");
LocalDateStringConverter converter = new LocalDateStringConverter();
birthDateCol.setCellFactory(
            TextFieldTableCell.<Person, LocalDate>forTableColumn(converter));
```

### 示例

使用不同类型的控件编辑 cell 数据。

TableView 包含 Id, First Name, Last Name, Birth Date, Baby 和 Gender columns：

- Id column 不可编辑 
- First Name, Last Name 和 Birth Date columns 使用 TextFieldTableCell
- Baby column 不可编辑，动态计算而来，用 CheckBoxTableCell 显示结果
- Gender column 用 ComboBoxTableCell

```java
import mvc.mjw.javafx.Person;

import java.time.LocalDate;

public class TableViewEditing extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

        // Make the TableView editable
        table.setEditable(true);

        // Add columns with appropriate editing features
        addIdColumn(table);
        addFirstNameColumn(table);
        addLastNameColumn(table);
        addBirthDateColumn(table);
        addBabyColumn(table);
        addGenderColumn(table);

        HBox root = new HBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Editing Data in a TableView");
        stage.show();
    }

    public void addIdColumn(TableView<Person> table) {
        // Id column is non-editable
        table.getColumns().add(PersonTableUtil.getIdColumn());
    }

    public void addFirstNameColumn(TableView<Person> table) {
        // First Name is a String, editable column
        TableColumn<Person, String> fNameCol =
                PersonTableUtil.getFirstNameColumn();

        // Use a TextFieldTableCell, so it can be edited
        fNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        table.getColumns().add(fNameCol);
    }

    public void addLastNameColumn(TableView<Person> table) {
        // Last Name is a String, editable column
        TableColumn<Person, String> lNameCol = PersonTableUtil.getLastNameColumn();

        // Use a TextFieldTableCell, so it can be edited
        lNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        table.getColumns().add(lNameCol);
    }

    public void addBirthDateColumn(TableView<Person> table) {
        // Birth Date is a LocalDate, editable column
        TableColumn<Person, LocalDate> birthDateCol = PersonTableUtil.getBirthDateColumn();

        // Use a TextFieldTableCell, so it can be edited
        LocalDateStringConverter converter = new LocalDateStringConverter();
        birthDateCol.setCellFactory(TextFieldTableCell.forTableColumn(converter));

        table.getColumns().add(birthDateCol);
    }

    public void addBabyColumn(TableView<Person> table) {
        // Baby? is a Boolean, non-editable column
        TableColumn<Person, Boolean> babyCol = new TableColumn<>("Baby?");
        babyCol.setEditable(false);

        // Set a cell value factory
        babyCol.setCellValueFactory(cellData -> {
            Person p = cellData.getValue();
            Boolean v = (p.getAgeCategory() == Person.AgeCategory.BABY);
            return new ReadOnlyBooleanWrapper(v);
        });

        // Use a CheckBoxTableCell to display the boolean value
        babyCol.setCellFactory(
                CheckBoxTableCell.<Person>forTableColumn(babyCol));

        table.getColumns().add(babyCol);
    }

    public void addGenderColumn(TableView<Person> table) {
        // Gender is a String, editable, ComboBox column
        TableColumn<Person, String> genderCol = new TableColumn<>("Gender");
        genderCol.setMinWidth(80);

        // By default, all cells are have null values
        genderCol.setCellValueFactory(
                cellData -> new ReadOnlyStringWrapper(null));

        // Set a ComboBoxTableCell, so we can selects a value from a list
        genderCol.setCellFactory(
                ComboBoxTableCell.<Person, String>forTableColumn("Male", "Female"));

        // Add an event handler to handle the edit commit event.
        // It displays the selected value on the standard output
        genderCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();
            Person person = e.getRowValue();
            System.out.println("Gender changed for " +
                    person.getFirstName() + " " + person.getLastName() +
                    " at row " + (row + 1) + " to " + e.getNewValue());
        });

        table.getColumns().add(genderCol);
    }
}
```

![|400](Pasted%20image%2020230803165336.png)

### 自定义控件编辑 cell 数据

前面介绍了使用不同控件在 TableView 的 cell 中编辑数据。我们也可以继承 TableCell 使用任何控件编辑 cell。例如，在日期 column 中使用 DatePicker 选择日期。

自定义 TableCell 需要重写下面 4 个方法：

- startEdit()
- commitEdit()
- cancelEdit()
- updateItem()

startEdit() 从显示模式切换到编辑模式被调用。通常用 cell 数据设置自定义控件，在 cell 的 graphic 属性中显示自定义控件。

commitEdit() 当用户按 Enter 键提交数据时被调用。通常不要重写该方法，因为如果 TableColumn 是基于 Writable ObservableValue，修改的数据会自动提交到数据模型。

cancelEdit() 当用户取消编辑时被调用。cell 切换到显示模式，数据恢复到编辑前状态，需要重写该方法，恢复数据到编辑前状态。

updateItem() 用 cell 需要重新渲染时被调用。根据编辑模式不同，需要设置 cell 的 text 和 graphic 属性。

**示例：** 实现 DatePickerTableCell

```java
@SuppressWarnings("unchecked")
public class DatePickerTableCell<S, T> extends TableCell<S, java.time.LocalDate> {

    private DatePicker datePicker;
    private StringConverter converter = null;
    private boolean datePickerEditable = true;

    public DatePickerTableCell() {
        this.converter = new LocalDateStringConverter();
    }

    public DatePickerTableCell(boolean datePickerEditable) {
        this.converter = new LocalDateStringConverter();
        this.datePickerEditable = datePickerEditable;
    }

    public DatePickerTableCell(StringConverter<java.time.LocalDate> converter) {
        this.converter = converter;
    }

    public DatePickerTableCell(StringConverter<java.time.LocalDate> converter,
                               boolean datePickerEditable) {
        this.converter = converter;
        this.datePickerEditable = datePickerEditable;
    }

    @Override
    public void startEdit() {
        // Make sure the cell is editable
        if (!isEditable() ||
                !getTableView().isEditable() ||
                !getTableColumn().isEditable()) {
            return;
        }

        // Let the ancestor do the plumbing job
        super.startEdit();

        // Create a DatePicker, if needed, and set it as the graphic for the cell
        if (datePicker == null) {
            this.createDatePicker();
        }

        this.setGraphic(datePicker);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        this.setText(converter.toString(this.getItem()));
        this.setGraphic(null);
    }

    @Override
    public void updateItem(java.time.LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        // Take actions based on whether the cell is being edited or not
        if (empty) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            if (this.isEditing()) {
                if (datePicker != null) {
                    datePicker.setValue(item);
                }
                this.setText(null);
                this.setGraphic(datePicker);
            } else {
                this.setText(converter.toString(item));
                this.setGraphic(null);
            }
        }
    }

    private void createDatePicker() {
        datePicker = new DatePicker();
        datePicker.setConverter(converter);

        // Set the current value in the cell to the DatePicker
        datePicker.setValue((java.time.LocalDate) this.getItem());

        // Configure the DatePicker properties
        datePicker.setPrefWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.setEditable(this.datePickerEditable);

        // Commit the new value when the user selects or enters a date
        datePicker.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue prop,
                                Object oldValue,
                                Object newValue) {
                if (DatePickerTableCell.this.isEditing()) {
                    DatePickerTableCell.this.commitEdit((java.time.LocalDate) newValue);
                }
            }
        });
    }

    public static <S> Callback<TableColumn<S, java.time.LocalDate>,
            TableCell<S, java.time.LocalDate>> forTableColumn() {
        return forTableColumn(true);
    }

    public static <S> Callback<TableColumn<S, java.time.LocalDate>, TableCell<S, java.time.LocalDate>>
    forTableColumn(boolean datePickerEditable) {
        return (col -> new DatePickerTableCell<>(datePickerEditable));
    }

    public static <S> Callback<TableColumn<S, java.time.LocalDate>, TableCell<S, java.time.LocalDate>> forTableColumn(StringConverter<java.time.LocalDate> converter) {
        return forTableColumn(converter, true);
    }

    public static <S> Callback<TableColumn<S, java.time.LocalDate>, TableCell<S, java.time.LocalDate>> forTableColumn(StringConverter<java.time.LocalDate> converter, boolean datePickerEditable) {
        return (col -> new DatePickerTableCell<>(converter, datePickerEditable));
    }
}
```

DatePickerTableCell 特点：

- 通过 StringConverter 实现 LocalDate 和 String 的转换。`LocalDateStringConverter` 在介绍 [DatePicker](../controls/control10_datepicker.md) 中有实现。
- 通过 datePickerEditable 字段指定 DatePicker 是否可编辑
- 第一次调用 startEdit() 时创建 DatePicker 控件
- 为 DatePicker 的 value 属性添加 ChangeListener，如果 DatePickerTableCell 处于编辑状态，提交新的 Date

**示例：** 使用 DatePickerTableCell

```java
import mvc.mjw.javafx.Person;

import java.time.LocalDate;

public class CustomTableCellTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

        // Make sure teh TableView is editable
        table.setEditable(true);

        // Set up teh Birth Date column to use DatePickerTableCell
        TableColumn<Person, LocalDate> birthDateCol = PersonTableUtil.getBirthDateColumn();
        StringConverter converter = new LocalDateStringConverter("MMMM dd, yyyy");
        birthDateCol.setCellFactory(
                DatePickerTableCell.<Person>forTableColumn(converter, false));

        table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                PersonTableUtil.getFirstNameColumn(),
                PersonTableUtil.getLastNameColumn(),
                birthDateCol);

        HBox root = new HBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using a Custom TableCell");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230803174652.png)

## 添加和删除 row

在 TableView 中添加和删除行很容易。TableView 中的 row 与 items 的元素一一对应：

- 向 items 添加一个元素，TableView 出现新的 row
- 新 row 的 index 与其在 items 中的 index 一样
- 如果 TableView 是有序的，则添加新 row 后可能需要重新排序，如调用 TableView.sort() 方法

删除 row 的方式也是如此，从  items 删除对应元素即可。

### 示例

演示在 TableView 添加和删除 row。界面如下：

![|300](Pasted%20image%2020230803194007.png)

包含三部分：

- "Add" 根据三个字段的值创建 Person，并添加到 TableView
- "Restore Rows" 将 TableView 的数据恢复到初始状态，"Delete Selected Rows" 删除选择的 rows
- 最下面为 TableView，启用了多行选择，按 Ctrl 或 Shift 多选

```java
import mvc.mjw.javafx.Person;

import static javafx.scene.control.TableView.TableViewSelectionModel;

public class TableViewAddDeleteRows extends Application {

    // Fields to add Person details
    private final TextField fNameField = new TextField();
    private final TextField lNameField = new TextField();
    private final DatePicker dobField = new DatePicker();

    // The TableView
    TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        // Turn on multi-row selection for the TableView
        TableViewSelectionModel<Person> tsm = table.getSelectionModel();
        tsm.setSelectionMode(SelectionMode.MULTIPLE);

        // Add columns to the TableView
        table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                PersonTableUtil.getFirstNameColumn(),
                PersonTableUtil.getLastNameColumn(),
                PersonTableUtil.getBirthDateColumn());

        GridPane newDataPane = this.getNewPersonDataPane();

        Button restoreBtn = new Button("Restore Rows");
        restoreBtn.setOnAction(e -> restoreRows());

        Button deleteBtn = new Button("Delete Selected Rows");
        deleteBtn.setOnAction(e -> deleteSelectedRows());

        VBox root = new VBox(newDataPane, new HBox(restoreBtn, deleteBtn), table);
        root.setSpacing(5);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Adding/Deleting Rows in a TableViews");
        stage.show();
    }

    public GridPane getNewPersonDataPane() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.addRow(0, new Label("First Name:"), fNameField);
        pane.addRow(1, new Label("Last Name:"), lNameField);
        pane.addRow(2, new Label("Birth Date:"), dobField);

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> addPerson());

        // Add the "Add" button
        pane.add(addBtn, 2, 0);

        return pane;
    }

    public void deleteSelectedRows() {
        TableViewSelectionModel<Person> tsm = table.getSelectionModel();
        if (tsm.isEmpty()) {
            System.out.println("Please select a row to delete.");
            return;
        }

        // Get all selected row indices in an array
        ObservableList<Integer> list = tsm.getSelectedIndices();
        Integer[] selectedIndices = new Integer[list.size()];
        selectedIndices = list.toArray(selectedIndices);

        // Sort the array
        Arrays.sort(selectedIndices);

        // Delete rows (last to first)
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            tsm.clearSelection(selectedIndices[i].intValue());
            table.getItems().remove(selectedIndices[i].intValue());
        }
    }

    public void restoreRows() {
        table.getItems().clear();
        table.getItems().addAll(PersonTableUtil.getPersonList());
    }

    public Person getPerson() {
        return new Person(fNameField.getText(),
                lNameField.getText(),
                dobField.getValue());
    }

    public void addPerson() {
        Person p = getPerson();
        table.getItems().add(p);
        clearFields();
    }

    public void clearFields() {
        fNameField.setText(null);
        lNameField.setText(null);
        dobField.setValue(null);
    }
}
```

代码中的大部分逻辑都很简单。

`deleteSelectedRows()` 删除选择的 rows。从 items 中删除 item 时，假设删除第一个 item，则第二个 item 上移变为第一个 item，并被选择。所有从前向后删除会删错。为了避免该情况：

- 从 items 删除 item 时，清除选择的 row
- 按 index 降序进行删除。

## 滚动 TableView 窗口

当 rows 或 columns 超出可用空间，TableView 自动提供水平和垂直滚动条。用户可以使用滚动条滚动到特定的 row 或 column。

但是，有时候需要对滚动的编程支持。例如，追加 一行到 TableView 末尾，你可能希望滚动视图到 用户看到新添加的这一行。

TableView 包含 4 个滚动方法：

- scrollTo(int rowIndex)
- scrollTo(S item)
- `scrollToColumn(TableColumn<S,?> column)`
- scrollToColumnIndex(int columnIndex)

scrollTo() 通过 index 或 item 滚动到特定 row；scrollToColumn() 和 scrollToColumnIndex() 滚动到特定 column。

调用上面的方法，TableView 会 fires `ScrollToEvent`。ScrollToEvent 类包含一个 getScrollTarget() 方法，根据滚动类型返回 row index 或 column reference：

```java
TableView<Person> table = ...
// Add a ScrollToEvent for row scrolling
table.setOnScrollTo(e -> {
    int rowIndex = e.getScrollTarget();
    System.out.println("Scrolled to row " + rowIndex);
});

// Add a ScrollToEvent for column scrolling
table.setOnScrollToColumn(e -> {
    TableColumn<Person, ?> column = e.getScrollTarget();
    System.out.println("Scrolled to column " + column.getText());
});
```

用户拖动滚动条不会触发 ScrollToEvent。

## 调整 TableColumn 大小

用户能否调整 TableColumn 尺寸由 TableColumn 的 resizable 属性指定。TableColumn 默认 resizable。

TableView 的 `columnResizePolicy` 属性指定如何调整 column 大小：

- columnResizePolicy 为 Callback 对象
- columnResizePolicy 的 call() 方法参数为 ResizeFeatures 类型，是 TableView 的内部类
- ResizeFeatures 封装了 TableView, 被调整大小的 TableColumn，以及调整的幅度
- 成功调整 column 尺寸，call() 返回 true，否则返回 false

TableView 内置了两种策略：

- `CONSTRAINED_RESIZE_POLICY`
- `UNCONSTRAINED_RESIZE_POLICY`

CONSTRAINED_RESIZE_POLICY 确保所有可见 leaf-column 的宽度加和等于 TableView 的宽度：

- resizing 一个 column 会导致该 column 右侧的所有 columns 被调整
- 增加 column 宽度，最右侧 column 的宽度会缩小到 minWidth
- 如果增加宽度不够，继续将最右第二个 column 的宽度调整到 minWidth
- 当右侧所有 columns 的宽度都减小到 minWidth，不能继续增加该 column 宽度

当减少 column 宽度，其规则类似。

`UNCONSTRAINED_RESIZE_POLICY` 策略（默认选项）：

- 增加 column 宽度，其右边的 columns 向右移动对应的宽度
- 减小 column 宽度，其右边的 columns 向左移动对应的宽度
- 如果当前 column 包含嵌套 columns，则改变的宽度均匀分布到嵌套的 columns 中

设置策略：

```java
TableView<Person> table = ...;

// Set the column resize policy to constrained resize policy
table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
```

也可以自定义 columnResizePolicy。下面是一个模板，你需要编写使用 delta 的逻辑：

```java
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());
table.setColumnResizePolicy(resizeFeatures -> {
    boolean consumedDelta = false; 
    double delta = resizeFeatures.getDelta();
    TableColumn<Person, ?> column = resizeFeatures.getColumn();
    TableView<Person> tableView = resizeFeatures.getTable();
    
    // Adjust the delta here...
    return consumedDelta;
});
```

将 columnResizePolicy 设置为不做任何操作的 Callback，可以禁用 column 尺寸调整功能。如直接让 call() 返回 true，表示它已经完成 resizing：

```java
// Disable column resizing
table.setColumnResizePolicy(resizeFeatures -> true);
```

## TableView CSS

`TableView` 的 column-header, cells, placeholder 等都可以使用 CSS 定义样式。将 CSS 应用于 `TableView` 比较复杂。

- TableView 的 CSS 样式类名默认为 table-view
- 单元格的 CSS 样式类名默认为 table-cell
- row 的 CSS 样式类名默认为 table-row-cell
- column 标题的 CSS 样式类名默认为 column-header

```css
/* Set the font for the cells */
.table-row-cell {
    -fx-font-size: 10pt;
    -fx-font-family: Arial;
}
/* Set the font size and text color for column headers */
.table-view .column-header .label{
    -fx-font-size: 10pt;
    -fx-text-fill: blue;
}
```

TableView 支持的 CSS pseudo-classes:

- cell-selection：启用 cell-level selection 时应用
- row-selection：启用 row-level selection 时应用
- constrained-resize：当 column-resize-policy 为 CONSTRAINED_RESIZE_POLICY 时应用

TableView 默认隔行高亮显示。下面删除隔行高亮功能，将所有 row 的背景设置白色：

```css
.table-row-cell {
    -fx-background-color: white;
}

.table-row-cell .table-cell {
    -fx-border-width: 0.25px;
    -fx-border-color: transparent gray gray transparent;
}
```

对多余的空间，TableView 显示空 rows 来填充。下面移除这些空 rows (其实只是让这些 rows 不可见)：

```css
.table-row-cell:empty {
    -fx-background-color: transparent;
}
.table-row-cell:empty .table-cell {
    -fx-border-width: 0px;
}
```

TableView 包含多个子结构：

- column-resize-line
- column-overlay
- placeholder
- column-header-background

column-resize-line 为 Region 类型，当用户 resize column 时显示。

column-overlay 也是 Region 类型，在移动的 column 上面显示一个涂层。

placeholder 为 StackPane 类型，当 TableView 不包含 column 或 data 时显示。例如：

```css
/* Make the text in the placeholder red and bold */
.table-view .placeholder .label {
    -fx-text-fill: red;
    -fx-font-weight: bold;
}
```

column-header-background 也是 StackPane 类型，它是  column header 下面的区域。column-header-background 包含多个子结构：

- filler，Region 类型，标题区域 TableView 右边 edge 和 最右边 column 中间的区域
- show-hide-columns-button，StackPane 类型，显示菜单按钮的区域
