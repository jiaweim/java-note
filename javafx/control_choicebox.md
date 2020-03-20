# 概述
`ChoiceBox` 用于显示少数项列表。`ChoiceBox`为参数化类，可用于显示任意类型，如果要同时显示多种类型，可使用 raw type。

当选择项不多时，用 ChoiceBox 显示。

ChoiceBox 属性
|属性|类型|说明|
|---|---|---|
|converter|ObjectProperty<StringConverter<T>>|convert 的 toString()被调用，用于创建对应类型显示的字符串|
|items|ObjectProperty<ObservableList<T>>|待显示条目列表|
|selectionModel|ObjectProperty<SingleSelectionModel<T>>|选择模型，用于确定当前被选择的项|
|showing|ReadOnlyBooleanProperty|true 表示显示列表项，false表示列表项收缩|
|value|ObjectProperty<T>|当前被选择项，如果未选择，为 null|

## setValue()
通过 `setValue()` 可以设置值，即使该值不在已定义的列表中，不过此时该值不被显示。

创建方法：
```java
// 可存储任意类型a
ChoiceBox seasons = new ChoiceBox();

// 只保存字符串类型
ChoiceBox<String> seasons = new ChoiceBox<String>();

// 创建时指定值
ObservableList<String> seasonList = FXCollections.<String>observableArrayList("Spring", "Summer", "Fall", "Winter");
ChoiceBox<String> seasons = new ChoiceBox<>(seasonList);

// 创建后指定值
ChoiceBox<String> seasons = new ChoiceBox<>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");
```