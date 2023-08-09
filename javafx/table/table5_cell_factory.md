# 自定义 cell 渲染

2023-08-01, 15:59
****
## 1. 简介

TableColumn 中的 cell 为 TableCell 类型，用于显示 cell 中的数据了。TableCell 为 Labeled 子类，能够显示 text 和 graphic。

TableColumn 类包含 cellFactory 属性，为 Callback 对象。其 `call()` 方法参数为 cell 所属 TableColumn，返回 TableCell 实例。覆盖 TableCell 的 updateItem() 方法可以自定义 cell 数据的渲染。

TableColumn 提供了默认 cellFactory 实现。默认 cellFactory 根据数据类型进行显示：

- 如果 cell 数据包含 Node，则作为 cell 的 graphic 显示
- 否则调用 cell 数据的 toString() 方法，将返回的 String 作为 cell 的 text 显示

## 2. 自定义 text

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

## 3. 自定义 graphic

通过 cellFactory 可以在 cell 中显示图形：

- 在 `updateItem()` 方法中创建 `ImageView`
- 使用 `TableCell` 的 `setGraphic()` 设置 `graphic`

## 4. TableCell 子类

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

## 5. 示例

自定义 cellFactory 完整示例。

```java
import mjw.study.javafx.mvc.Person;

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

![|400](Pasted%20image%2020230801155203.png)

