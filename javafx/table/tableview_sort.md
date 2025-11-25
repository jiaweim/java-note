# TableView 排序

2025-11-25⭐
@author Jiawei Mao
***
## 简介

`TableView` 内置了 column 数据排序功能：

- 默认允许用户单击 column 标题对数据排序
- 支持通过编程方式排序
- 可以禁用部分或所有 column 的排序

涉及的概念：

- sort-order-list：包含用于排序的 columns

## 用户排序

`TableView` 所有 columns 数据默认可以排序。用户点击 column 标题对数据进行排序：

- 第一次单击按升序排序
- 第二次单击按降序排序
- 第三次点击将该 column 从 sort-order-list 移除

默认启用单个 column 排序，即 `TableView` 只根据单个 column 的数据进行排序。要启用 multi-column 排序，按 Shift 键点击多个 columns 的标题进行排序。

`TableView` 对已排序的 columns 会在标题显示符号：

- 三角形表示排序类型，向上表示升序，向下表示降序
- column 的 sort-order 由点或数字表示：前三个 columns 用点表示，之后用数字表示

sort-order 表示 column 排序的优先级，如下所示：

<img src="images/Pasted%20image%2020230801140922.png" width="400" />

这里对 "Last Name" 降序，其它 columns 升序。按照 sort-order，先根据 "Last Name" column 的数据排序，此时前三个 Last Name 相同，继续使用 "First Name" 进行排序。

## 编程排序

columns 中的数据可以编程排序。`TableView` 和 `TableColumn` 提供了强大的排序 API。

### 1. column 可排序

`TableColumn` 的 `sortable` 属性指定该 column 是否可排序，默认 `true`。设置方式：

```java
// Disable sorting for fNameCol column
fNameCol.setSortable(false);
```

### 2. column 排序类型

`TableColumn` 通过 `sortType` 属性指定排序类型，排序类型由 `TableColumn.SortType` enum 表示：`ASCENDING` 和 `DESCENDING`。

`sortType` 属性默认为 `ASCENDING`，设置方式：

```java
// Set the sort type for fNameCol column to descending
fNameCol.setSortType(TableColumn.SortType.DESCENDING);
```

### 3. 为 column 指定 Comparator

`TableColumn` 使用 `Comparator` 排序，通过 `TableColumn` 的 `comparator` 属性指定 `Comparator`。

`TableColumn` 的默认 `Comparator` 由常量 `TableColumn.DEFAULT_COMPARATOR` 定义。基本规则：

- 检查 `null` `值，null` 值优先，两个 nulls 相等
- 如果要比较的第一个值为 `Comparable` 类型，则调用其 `compareTo()` 方法与第二个对象对比
- 如果以上两个条件都不满足，则调用 `toString()` 将两个对象转换为字符串，然后使用 `Comparator` 对比两个 `String`

大多时候默认 `Comparator` 就够用了。

**示例：** 自定义 comparator，对比 `String` column 数据的第一个字符

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

### 4. column 的 sortNode 属性

`TableColumn` 类包含用一个 `sortNode` 属性，用于在 column 标题栏显示 column 的 sort-type 和 sort-order。

- 当排序类型为升序，`sortNode` 旋转 180°
- 当 column 没有排序，不显示 `sortNode`
- 默认 `sortNode` 为 `null`，`TableColumn` 提供一个三角形作为 sort-node

### 5. 指定 column 的 sort-order

`TableView` 包含多个用于排序的属性。要对 column 排序，需要将其添加到 `TableView` 的 sort-order-list。`TableView` 的 `sortOrder` 属性为包含 `TableColumn` 的 `ObservableList`，column 的 sort-order 与其在该 list 中的顺序一致。

`sortOrder` 应用规则：

- 使用第一个 column 对 rows 进行排序
- 如果第一个 column 中两个 rows 的值相同，使用第二个 column 的数据确定这两个 rows 的顺序
- 以此类推

**示例：** 向 `TableView` 添加两个 columns，并指定 sort-order

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

`TableView` 监听 `sortOrder` 属性，如果被修改，`TableView` 会立刻根据新的 `sortOrder` 排序。添加 column 到 sort-order-list 并不能保证排序会用该 column。首先，该 column 的数据要是能排序的。

`TableView` 同时会监听 `TableColumn` 的 `sortType` 属性，根据 `sortType` 属性值的变化更新 `TableView` 数据顺序。

### 6. 查看 TableView 的 Comparator

`TableView` 包含一个 read-only 属性 `comparator`，它是基于当前 sort-order-list 的 `Comparator` 实例。在代码中基本用不到 comparator 属性。

如前所述，`TableColumn` 也有一个 `comparator` 属性，用于比较 `TableColumn` 中 cells 的顺序。 `TableView` 的 `comparator` 属性结合了 sort-order-list 中所有 `TableColumn` 的 `comparator` 属性。

### 7. 指定 sort-policy

`TableView` 包含一个排序策略用于指定如何排序。它是一个 Callback 对象，`TableView` 作为参数传递给 call() 方法，排序成功返回 true；排序失败返回 false 或 null。

`TableView` 类定义的 `DEFAULT_SORT_POLICY` 常量为默认排序策略。默认排序策略使用 `TableView` 的 `comparator` 属性排序 items。指定 sort-policy 可以完全控制排序算法，sort-policy Callback 的 call() 方法执行 TableView 中 items 的排序。

将 sort-policy 设置为 `null` 将禁用排序：

```java
TableView<Person> table = ...

// Disable sorting for the TableView
table.setSortPolicy(null);
```

有时出于性能考虑，可以临时禁用排序。

假设你有一个有序 `TableView` 包含大量 items，然后你想修改 sort-order-list。但是，每次修改 sort-order-list 都会触发 items 重排，此时，可以先将 sort-policy 设置为 null 禁用排序，完成 sort-order-list 修改后，再恢复 sort-policy 以启用排序。修改 sort-policy 会立刻出发排序：

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

### 8. 手动排序

`TableView` 的 `sort()` 方法使用当前 sort-order-list 排序 items。你可以在为 `TableView` 添加一些 items 后调用 `sort()` 对 items 进行排序。

当 columns 的 sort-type, sort-order 或 sort-policy 发生变化，会自动调用 `sort()` 方法。

### 9. sorting 事件

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

### 10. 禁用 TableView 排序

禁用 `TableView` 排序的方法有多种：

- 设置 `TableColumn` 的 `sortable` 属性只能禁用该 column 的排序功能。如果将 `TableView` 所有 columns 的 `sortable` 属性设置为 false，等价于禁用了 `TableView` 的排序功能
- 将 `TableView` 的 sort-policy 设置为 `null`
- consume 掉 `TableView` 的 `SortEvent`
- 从技术上来讲，可以覆盖 `TableView` 的 `sort()` 方法，提供一个 empty 实现，但不建议这么做

部分或全部禁用 TableView 排序的最佳方法是禁用部分或全部 columns 的排序，即上述的第一种方法。
