# TableView 概述

2023-07-26, 14:39
****
## 1. 简介

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

## 2. 示例数据

下面使用 MVC 中的 Person 类作为数据类。下面的 PersonTableUtil 类为 TableView 提供数据。

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

