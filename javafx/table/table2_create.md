# 创建 TableView

2023-08-01, 12:52
****
## 1. 简介

`TableView` 是一个参数化类，例如，创建 `TableView` 显示 Person 数据：

```java
TableView<Person> table = new TableView<>();
```

将该 `TableView` 添加到 `Scene`，由于没有数据，会显示一个占位符。如下所示：

![|250](Pasted%20image%2020230726144340.png)

也可以在构造时指定数据，参数类型为 `ObservableList`，例如：

```java
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());
```

## 2. 添加 Column

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

### 2.1. cellValueFactory

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

![|300](Pasted%20image%2020230801095043.png)

### 2.2. column 嵌套

TableView 支持 column 嵌套。例如，将 First 和 Last column 嵌套在 Name column 下。TableColumn 将嵌套 columns 保存在 TableColumn.getColumns() 返回的 list 中。嵌套 column 也需要设置 `cellValueFactory`。

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

![|300](Pasted%20image%2020230801095926.png)

TableView 提供了查看嵌套 column 的方法：

```java
TableColumn<S,?> getVisibleLeafColumn(int columnIndex)
ObservableList<TableColumn<S,?>> getVisibleLeafColumns()
int getVisibleLeafIndex(TableColumn<S,?> column)
```

`getVisibleLeafColumn()` 返回指定 index 处的 column，column index 只计算可见的 leaf column，从 0 开始。上图的 "Name" column 就不属于可见的 leaf column，所以不算在 column index 中。 

`getVisibleLeafColumns()` 返回所有可见的 column list。

`getVisibleLeafIndex()` 返回指定 column 的 leafIndex。

## 3. 自定义 placeholder

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

## 4. 填充数据

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

### 4.1. JavaFX Property 字段

当 item 中作为 column 数据的字段为 JavaFX property 类型，则可以直接用 `PropertyValueFactory` 作为 cellValueFactory。将 JavaFX 属性字段名称传入 PropertyValueFactory 构造函数即可：

```java
TableColumn<Person, String> fNameCol = new TableColumn<>("First Name");
fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
```

```ad-tip
使用 JavaFX 属性作为 cell 值有一个很大优势：TableView 会保持属性和 cell 值同步。修改属性值会自动更新 cell 值。
```

### 4.2. POJO

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

### 4.3. 动态计算

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

### 4.4. 完整示例

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

