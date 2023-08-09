# 滚动 TableView 窗口

2023-08-03, 20:16
****
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
