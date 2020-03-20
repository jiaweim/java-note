# References
https://openjfx.io/javadoc/11/javafx.controls/javafx/scene/control/ListView.html

# 简介
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

# ListView SelectionModel
ListView 的 selection model 支持两种模式: Single, Multiple，对应单项和多项选择。
由 `SelectionModel` 和 `FocusModel` 类确定 selection 和 focus。

ListView 的默认 `SelectionModel` 实现为 `MultipleSelectionModel`，不过默认选择模式是 `SelectionMode.SINGLE`，要允许一次选择多个，需要按如下设置：

```java
listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
```



# Customizing ListView Visuals
ListView 的外观可以通过实现 `cell factory` 自定义。`cell factory` 生成 `ListCell` 实例，用于表示 ListView 中的条目。

# Editing
首先，编辑时的 UI 和显示的UI一般不同。
