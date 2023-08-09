# Map 作为 item

2023-08-01, 13:43
****
## 1. 简介

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
import mjw.study.javafx.mvc.Person;

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

## 2. 显示和隐藏 column

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

## 3. Column 顺序

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