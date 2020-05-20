# ListView

- [ListView](#listview)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [SelectionModel](#selectionmodel)
  - [Customizing ListView Visuals](#customizing-listview-visuals)
  - [Editing](#editing)

2020-05-18, 17:57
***

## 简介

ListView 可用于显示一列 items，user 可以从中选择一个或多个进行交互。

填充 ListView
在构造时添加：

```java
ObservableList<String> names = FXCollections.observableArrayList(
        "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
ListView<String> listView = new ListView<String>(names);
```

ListView 受益于 `ObservableList`，会根据 List 内容自动调整显示内容。
构造后添加：

```java
listView.setItems(names);
```

## SelectionModel

`SelectionModel` 选择模型用于保存元素的选择状态，而 `selectionModel` 属性保存选择的模型。

`ListView` 的 selection model 支持两种模式: Single, Multiple，对应单项和多项选择。

- `ListView` 默认为单选，即一次只能选一个元素。
- 对多项选择
  - 按 Shift 键一次选择连续多个
  - 按 Ctrl 键间隔选择
  - 也可以通过上下箭头移动，按 Space 选择

模式设置：

```java
// Use multiple selection mode
seasons.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
// Set it back to single selection mode, which is the default for a ListView
seasons.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
```





## Customizing ListView Visuals

ListView 的外观可以通过实现 `cell factory` 自定义。`cell factory` 生成 `ListCell` 实例，用于表示 ListView 中的条目。

## Editing

首先，编辑时的 UI 和显示的UI一般不同。
