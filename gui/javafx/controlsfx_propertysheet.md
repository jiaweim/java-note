# PropertySheet

- [PropertySheet](#propertysheet)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [基本类介绍](#%e5%9f%ba%e6%9c%ac%e7%b1%bb%e4%bb%8b%e7%bb%8d)
    - [Mode](#mode)
    - [Item](#item)
    - [PropertyEditor](#propertyeditor)

2020-05-20, 20:00
***

## 简介

`PropertySheet` 提供编辑属性列表的功能控件，长用于编辑和可视化大量属性。

下面是一个 `PropertySheet` 的视图：

![PropertySheet](images/2020-05-20-20-07-20.png)

在该 `PropertySheet` 中包含两列：

- 左侧列为属性名称，以标签显式
- 右侧列为 `PropertyEditor`，方便用户编辑属性

在上图中可以看到 `CheckEditor`, `ChoiceEditor`, `TextEditor`, `FontEditor` 以及 `Editors` 中定义其它编辑器类。

创建 `PropertySheet` 很简单：实例化 `PropertySheet` 类，然后传入 `PropertySheet.Item` 实例列表，每个 `Item` 表示一个属性。

## 基本类介绍

### Mode

`Mode` 用于指定 `PropertySheet` 的样式

- `NAME`, 按照属性在列表中的顺序显示
- `CATEGORY`, 根据属性的 `getCategory()` 对属性分类显示

### Item

`Item` 表示 `PropertySheet` 的单个属性。接口如下：

```java
public static interface Item {

    public Class<?> getType();

    public String getCategory();

    public String getName();

    public String getDescription();

    public Object getValue();

    public void setValue(Object value);

    public Optional<ObservableValue<? extends Object>> getObservableValue();

    default public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.empty();
    }

    default public boolean isEditable() {
        return true;
    }
}
```

| 方法          | 返回类型 | 说明               |
| ------------- | -------- | ------------------ |
| `getType()`   | Class    | 属性类型                                |
| `getCategory` | String   | 属性分类，当 `Mode` 设置为 `CATEGORY` 时，相同分类的属性控件分组在一起 |
|`getName()`|String|属性显示名称|
|`getDescription()`|String|属性详细信息，在界面上以 tooltip 形式呈现|
|`getValue`|Object|属性当前值|
|`setValue(Object value)`|void|设置属性值|
|`getObservableValue()`|`Optional<ObservableValue<? extends Object>>`|返回底层的 `ObservableValue`，设置该值，方便监听值|
|`getPropertyEditorClass()`|`Optional<Class<? extends PropertyEditor<?>>>`|用于编辑属性的 `PropertyEditor` 实现类，该类必须包含单个 `PropertySheet.Item` 参数的构造函数，默认为 `Optional.empty()`|
|`isEditable()`|boolean|是否允许编辑该属性，或者是只读，默认为 true|

### PropertyEditor

`PropertyEditor` 为 `PropertySheet` 用于编辑参数的编辑器接口。

```java
public interface PropertyEditor<T> {

    /**
     * 返回编辑该属性的编辑器
     */
    public Node getEditor();

    /**
     * 编辑器中的当前值，可能不是属性的当前值
     */
    public T getValue();

    /**
     * 设置编辑器显示的值，该值可能和属性值不同
     */
    public void setValue(T value);
}
```

