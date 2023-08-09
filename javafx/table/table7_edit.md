# TableView 编辑数据

2023-08-03, 17:48
****
## 1. 简介

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

## 2. CheckBoxTableCell

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

## 3. ChoiceBoxTableCell

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

## 4. ComboBoxTableCell

ComboBoxTableCell 在 cell 中渲染一个 ComboBox，与 ChoiceBoxTableCell 用法基本一样。

## 5. TextFieldTableCell

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

## 6. 示例

使用不同类型的控件编辑 cell 数据。

TableView 包含 Id, First Name, Last Name, Birth Date, Baby 和 Gender columns：

- Id column 不可编辑 
- First Name, Last Name 和 Birth Date columns 使用 TextFieldTableCell
- Baby column 不可编辑，动态计算而来，用 CheckBoxTableCell 显示结果
- Gender column 用 ComboBoxTableCell

```java
import mjw.study.javafx.mvc.Person;

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

## 7. 自定义控件编辑 cell 数据

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
import mjw.study.javafx.mvc.Person;

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

