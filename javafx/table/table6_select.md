# 选择 Cell 和 Row

2023-08-01, 21:59
****
## 1. 简介

TableView 的 `selectionModel` 属性指定选择模型。`selectionModel` 属性为 TableViewSelectionModel 类型，定义为 TableView 的静态内部类。

`selectionModel` 支持 cell-level 和 row-level 选择；支持单选和多选：

- 单选模式，一次只能选一个 cell 或一个 row
- 多选模式，一次能选多个 cells 或多个 rows

row-level 默认单选，设置 row 多选：

```java
TableView<Person> table = ...

// Turn on multiple-selection mode for the TableView
TableViewSelectionModel<Person> tsm = table.getSelectionModel();
tsm.setSelectionMode(SelectionMode.MULTIPLE);
```

cell 的选择可以通过 `selectionModel` 的 `cellSelectionEnabled` 属性启用。当 `cellSelectionEnabled` 为 true，`TableView` 进入 cell 选择模式，此时不能选择整个 row；如果启用多选模式，依然可以选择整个 row，但是在 cell 选择模式下，`TableView` 不将其作为 row 选择，而是看作多个 cell 选择。cell 选择模式默认为 false。

```java
// Enable cell-level selection
tsm.setCellSelectionEnabled(true);
```

## 2. 操作

selectionModel 提供所选 cell 和 row 的信息：

- isSelected(int rowIndex) 如果选择指定 rowIndex 对应的 row，返回 true
- `isSelected(int rowIndex, TableColumn<S,?> column)` 如果选择指定 rowIndex 和 column 的 cell，返回 true

selectionModel 还提供了选择功能：

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

**示例：** 为 etSelectedIndices() 返回的 ObservableList 添加 ListChangeListener

```java
TableView<Person> table = ...
TableViewSelectionModel<Person> tsm = table.getSelectionModel();
ObservableList<Integer> list = tsm.getSelectedIndices();

// Add a ListChangeListener
list.addListener((ListChangeListener.Change<? extends Integer> change) -> {
    System.out.println("Row selection has changed");
});
```

