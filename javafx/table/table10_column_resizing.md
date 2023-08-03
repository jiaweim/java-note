# 调整 TableColumn 大小

2023-08-03, 20:50
****
用户能否调整 TableColumn 尺寸由 TableColumn 的 resizable 属性指定。TableColumn 默认 resizable。

TableView 的 `columnResizePolicy` 属性指定如何调整 column 大小：

- columnResizePolicy 为 Callback 对象
- columnResizePolicy 的 call() 方法参数为 ResizeFeatures 类型，是 TableView 的内部类
- ResizeFeatures 封装了 TableView, 被调整大小的 TableColumn，以及调整的幅度
- 成功调整 column 尺寸，call() 返回 true，否则返回 false

TableView 内置了两种策略：

- `CONSTRAINED_RESIZE_POLICY`
- `UNCONSTRAINED_RESIZE_POLICY`

CONSTRAINED_RESIZE_POLICY 确保所有可见 leaf-column 的宽度加和等于 TableView 的宽度：

- resizing 一个 column 会导致该 column 右侧的所有 columns 被调整
- 增加 column 宽度，最右侧 column 的宽度会缩小到 minWidth
- 如果增加宽度不够，继续将最右第二个 column 的宽度调整到 minWidth
- 当右侧所有 columns 的宽度都减小到 minWidth，不能继续增加该 column 宽度

当减少 column 宽度，其规则类似。

`UNCONSTRAINED_RESIZE_POLICY` 策略（默认选项）：

- 增加 column 宽度，其右边的 columns 向右移动对应的宽度
- 减小 column 宽度，其右边的 columns 向左移动对应的宽度
- 如果当前 column 包含嵌套 columns，则改变的宽度均匀分布到嵌套的 columns 中

设置策略：

```java
TableView<Person> table = ...;

// Set the column resize policy to constrained resize policy
table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
```

也可以自定义 columnResizePolicy。下面是一个模板，你需要编写使用 delta 的逻辑：

```java
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());
table.setColumnResizePolicy(resizeFeatures -> {
    boolean consumedDelta = false; 
    double delta = resizeFeatures.getDelta();
    TableColumn<Person, ?> column = resizeFeatures.getColumn();
    TableView<Person> tableView = resizeFeatures.getTable();
    
    // Adjust the delta here...
    return consumedDelta;
});
```

将 columnResizePolicy 设置为不做任何操作的 Callback，可以禁用 column 尺寸调整功能。如直接让 call() 返回 true，表示它已经完成 resizing：

```java
// Disable column resizing
table.setColumnResizePolicy(resizeFeatures -> true);
```