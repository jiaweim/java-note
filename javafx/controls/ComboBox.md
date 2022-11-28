# ComboBox

- [ComboBox](#combobox)
  - [简介](#简介)
  - [创建 ComboBox](#创建-combobox)
  - [参考](#参考)

***

## 简介

`ComboBox` 让用户从选项列表中选择，可以将 `ComboBox` 看作 `ChoiceBox` 的升级版，高度可定制，且可编辑。

`ComboBox` 继承自 `ComboBoxBase` 类，`ComboBoxBase` 为所有类似 `ComboBox` 的控件提供了通用功能，如 `ColorPicker` 和 `DatePicker`。如果需要自定义弹出列表控件，可以从继承 `ComboBoxBase` 开始。

`ComboBox` 的列表可以包含任何类型的对象，`ComboBox` 是一个参数化类。参数类型为列表项的类型。

## 创建 ComboBox

如果想在 `ComboBox` 中使用混合类型，可以将参数类型设置为 `<Object>`，如下：

```java
// 可以存储多种类型
ComboBox<Object> seasons = new ComboBox<>();
```

- 创建字符串类型的 `ComboBox`

```java
ComboBox<String> seasons = new ComboBox<>();
```

- 创建 `ComboBox` 时指定列表项

```java
ObservableList<String> seasonList = FXCollections.<String>observableArrayList(
        "Spring", "Summer", "Fall", "Winter");
ComboBox<String> seasons = new ComboBox<>(seasonList);
```

- 创建 `ComboBox` 添加列表项

```java
ComboBox<String> seasons = new ComboBox<>();
seasons.getItems().addAll("Spring", "Summer", "Fall", "Winter");
```

`ComboBox` 需要跟踪所选项以及该项在列表中的索引，`ComboBox` 使用 `SingleSelectionModel` 类实现该功能，`selectionModel` 存储该对象的引用。

与 `ChoiceBox` 不同，`ComboBox` 是可编辑的。其 `editable` 属性指定是否启用编辑功能，默认不启用。当 `ComboBox` 处于可编辑状态，使用 `TextField` 显示所选项及输入项。`ComboBox` 的 

## 参考

- Learn JavaFX 17 Building User Experience and Interfaces with Java, 2ed, Kishori Sharan & Peter Späth
