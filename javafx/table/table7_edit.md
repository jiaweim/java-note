# TableView 编辑数据

## 简介

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

## CheckBoxTableCell

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

## ChoiceBoxTableCell

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

## ComboBoxTableCell

