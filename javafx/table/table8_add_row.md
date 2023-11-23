# 添加和删除 row

2023-08-03, 19:49
****
## 1. 简介

在 TableView 中添加和删除行很容易。TableView 中的 row 与 items 的元素一一对应：

- 向 items 添加一个元素，TableView 出现新的 row
- 新 row 的 index 与其在 items 中的 index 一样
- 如果 TableView 是有序的，则添加新 row 后可能需要重新排序，如调用 TableView.sort() 方法

删除 row 的方式也是如此，从  items 删除对应元素即可。

## 2. 示例

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
