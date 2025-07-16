# Spinner

2025-07-16⭐
@author Jiawei Mao

***

## 简介

`Spinner` 是 JavaFX 8u40 引入的控件，可以将其看作单行 `TextField`，用户从有序序列中选择数值或对象。`Spinner` 一般提供上下箭头用于选择值，从键盘的上下箭头也可以循环选择值。

`Spinner` 支持直接输入值，虽然 `ComboBox` 也提供类似功能，但是 `Spinner` 不需要下拉列表。

`Spinner` 支持各种类型的值，包括 `int`, `float`, `double` 或者其它类型的 `List`。值的迭代由 `SpinnerValueFactory` 定义。JavaFX 内置了许多 `SpinnerValueFactory` 类型，如：

- `IntegerSpinnerValueFactory`
- `DoubleSpinnerValueFactory`
- `ListSpinnerValueFactory`

`Spinner` 有一个 `TextField` 类型的 child-node，用于显示及编辑当前值。不过 `Spinner` 默认不可编辑，通过 `editable` 属性设置可否编辑。

**示例**：选择整数的 `Spinner`

```java
Spinner<Integer> spinner = new Spinner<>();
spinner.setValueFactory(new SpinnerValueFactory.
IntegerSpinnerValueFactory(5, 10));
spinner.valueProperty().addListener((o, oldValue, newValue) -> {
        log("value changed: '" + oldValue + "' -> '" + newValue + "'");
});
```

## Spinner 属性

| 属性           | 类型                                     | 说明                           |
| -------------- | ---------------------------------------- | ------------------------------ |
| `editable`     | `BooleanProperty`                        | 用户是否可以输入文本           |
| `editor`       | `ReadOnlyObjectProperty<TextField>`      | `Spinner` 使用的编辑控件       |
| `promptText`   | `StringProperty`                         | 当没有用户输入时显示的提示文本 |
| `valueFactory` | `ObjectProperty<SpinnerValueFactory<T>>` | 生成值                         |
| `value`        | `ReadOnlyObjectProperty<T>`              | 用户选择的值                   |
