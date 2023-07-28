# 创建 TableView

## 简介

TableView 是一个参数化类，例如，创建 TableView 显示 Person 数据：

```java
TableView<Person> table = new TableView<>();
```

将该 TableView 添加到 scene 中，由于没有数据，会显示一个占位符。如下所示：

![|250](Pasted%20image%2020230726144340.png)

也可以在构造时指定数据，参数类型为 `ObservableList`，例如：

```java
TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());
```

## 添加 Column

TableColumn 类表示 TableView 的 column。

- TableColumn 负责显示和编辑 cell 数据
- TableColumn 包含一个 header，可显示标题 text, graphic
- 可以为 TableColumn 设置 context-menu，右键 TableColumn 的 header 区域可以看到
- 使用 contextMenu 属性设置 context-menu

`TableColumn<S, T>` 是泛型类，
