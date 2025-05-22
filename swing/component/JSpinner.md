# JSpinner

v1: 2024-01-16⭐😎
***
## 简介

`JSpinner` 是复合组件，包含三部分：

- 2个箭头按钮
- 1个 editor

editor 可以是任何 `JComponent`，默认实现为包含 `JFormattedTextField` 的 `JPanel`。使用 `JSpinner` 涉及多个类：

- `JSpinner`，主要类
- `SpinnerModel`，数据模型
- `JSpinner.DefaultEditor`，编辑器，用于数据的显示和编辑
### 创建 JSpinner

`JSpinner` 提供了两个构造函数：

```java
public JSpinner()
JSpinner spinner = new JSpinner();

public JSpinner(SpinnerModel model)
SpinnerModel model = new SpinnerListModel(args);
JSpinner spinner = new JSpinner(model);
```

其中 `SpinnerModel` 有三个子类：`SpinnerDateModel`, `SpinnerListModel` 和 `SpinnerNumberModel`，默认为 `SpinnerNumberModel`。

虽然组件的展示和编辑组件为 `JFormattedTextField`，但具体编辑功能通过 `JSpinner` 的内部类 `DateEditor`, `ListEditor` 或 `NumberEditor` 完成。
### JSpinner 属性
| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| accessibleContext|AccessibleContext|Read-only |
| changeListeners|ChangeListener[]|Read-only |
| editor|JComponent|Read-write bound|
| model|SpinnerModel|Read-write bound |
| `nextValue` |Object|Read-only |
| `previousValue` | Object | Read-only |
| UI | SpinnerUI|Read-write |
| UIClassID|String|Read-only |
| `value` |Object|Read-write |
`value` 属性可用于修改组件当前值；`nextValue` 和 `previousValue` 属性用于从不同方向查看 model。
### 使用 ChangeListener 监听 JSpinner 事件

`JSpinner` 只支持一种类型的 listener：`ChangeListener`。当调用相关组件的 `commitEdit()` 方法时，listener 会收到通知。

**示例：** 演示 `JSpinner` 的 `ChangeListener`

```java
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.util.Locale;

public class SpinnerSample
{
    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("JSpinner Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.FRENCH);
            ChangeListener listener = e -> System.out.println("Source: " + e.getSource());

            String[] days = symbols.getWeekdays();

            SpinnerModel model1 = new SpinnerListModel(days);
            JSpinner spinner1 = new JSpinner(model1);
            spinner1.addChangeListener(listener);

            JLabel label1 = new JLabel("French Days/List");
            JPanel panel1 = new JPanel(new BorderLayout());
            panel1.add(label1, BorderLayout.WEST);
            panel1.add(spinner1, BorderLayout.CENTER);
            frame.add(panel1, BorderLayout.NORTH);

            SpinnerModel model2 = new SpinnerDateModel();
            JSpinner spinner2 = new JSpinner(model2);
            spinner2.addChangeListener(listener);

            JLabel label2 = new JLabel("Dates/Date");
            JPanel panel2 = new JPanel(new BorderLayout());
            panel2.add(label2, BorderLayout.WEST);
            panel2.add(spinner2, BorderLayout.CENTER);
            frame.add(panel2, BorderLayout.CENTER);

            SpinnerModel model3 = new SpinnerNumberModel();
            JSpinner spinner3 = new JSpinner(model3);
            spinner3.addChangeListener(listener);

            JLabel label3 = new JLabel("Numbers");
            JPanel panel3 = new JPanel(new BorderLayout());
            panel3.add(label3, BorderLayout.WEST);
            panel3.add(spinner3, BorderLayout.CENTER);

            frame.add(panel3, BorderLayout.SOUTH);
            frame.setSize(200, 90);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
```
### 自定义 JSpinner Laf

## SpinnerModel

`SpinnerModel` 是 `JSpinner` 的数据模型，其定义如下：

```java
public interface SpinnerModel
{
    // 属性
    Object getValue();
    void setValue(Object value);
    Object getNextValue();
    Object getPreviousValue();

    // listener
    void addChangeListener(ChangeListener l);
    void removeChangeListener(ChangeListener l);
}
```

`JSpinner` 中的相关方法重定向到 `SpinnerModel` 的这 6 个方法。相关实现：

![[images/Pasted image 20240116192443.png|600]]
### AbstractSpinnerModel

`AbstractSpinnerModel` 提供了 `SpinnerModel` 的基础实现，包含 listener 的管理和通知。子类需要实现余下 4 个与值相关的方法。
### SpinnerDateModel

`SpinnerDateModel` 提供日期选择模型。该类有两个构造函数，默认的支持选择所有日期，另一个可以限制日期范围。

```java
public SpinnerDateModel()

SpinnerModel model = new SpinnerDateModel();
JSpinner spinner = new JSpinner(model);

public SpinnerDateModel(Date value, Comparable start, Comparable end, int calendarField)

Calendar cal = Calendar.getInstance();
Date now = cal.getTime();
cal.add(Calendar.YEAR, -50);

Date startDate = cal.getTime();
cal.add(Calendar.YEAR, 100);

Date endDate = cal.getTime();
SpinnerModel model = new SpinnerDateModel(now, startDate, endDate, Calendar.YEAR);

JSpinner spinner = new JSpinner(model);
```

如果不指定参数，则没有起始和结束日期。最后一个参数为 `Calendar` 类常量，指定 `JSpinner` 调节的字段：

- Calendar.AM_PM
- Calendar.DAY_OF_MONTH
- Calendar.DAY_OF_WEEK
- Calendar.DAY_OF_WEEK_IN_MONTH
- Calendar.DAY_OF_YEAR
- Calendar.ERA
- Calendar.HOUR
- Calendar.HOUR- F_DAY
- Calendar.MILLISECOND
- Calendar.MINUTE
- Calendar.MONTH
- Calendar.SECOND
- Calendar.WEEK_OF_MONTH
- Calendar.WEEK_OF_YEAR
- Calendar.YEAR

下表是 `SpinnerModel` 的 3 个属性和 `SpinnerDateModel` 特有的 4 个属性：

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| calendarField | int | Read-write |
| date | Date | Read-only |
| end | Comparable | Read-write |
| nextValue | Object | Read-only |
| previousValue | Object | Read-only |
| start | Comparable | Read-write |
| value | Object | Read-only |
如果在构造函数中指定日期范围，则在边界处 `previousValue` 和 `nextValue` 可能为 `null`。

### SpinnerListModel

`SpinnerListModel` 支持从 List 值中进行选择，它提供了 3 个构造函数：

```java
public SpinnerListModel()
SpinnerModel model = new SpinnerListModel();
JSpinner spinner = new JSpinner(model);

public SpinnerListModel(List<?> values)
List<String> list = args;
SpinnerModel model = new SpinnerListModel(list);
JSpinner spinner = new JSpinner(model);

public SpinnerListModel(Object[] values)
SpinnerModel model = new SpinnerListModel(args);
JSpinner spinner = new JSpinner(model);
```

说明：

- 无参构造函数，模型只包含一个空字符串
- `List` 版本保留对 `List` 引用，`JSpinner` 不会复制 `List`，更改 `List`，模型元素随之更改
- 数组版本提供初始元素，但无法更改
- `List` 和数组版本默认选择第一个元素

`SpinnerListModel` 只是接口上增加了 list 属性：

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| `list` | `List<?>` | Read-write |
| `nextValue` | `Object` | Read-only |
| `previousValue` | `Object` | Read-only |
| `value` | `Object` | Read-write |
### SpinnerNumberModel

`SpinnerNumberModel` 提供从开区间或闭区间选择数字的功能。数字可以是 `Number` 的任意子类，包括 `Integer`, `Double` 等。

`SpinnerNumberModel` 有 4 个构造函数，前 3 个都是第 4 个简单形式：

```java
public SpinnerNumberModel()
SpinnerModel model = new SpinnerNumberModel();
JSpinner spinner = new JSpinner(model);

public SpinnerNumberModel(double value, double minimum, double maximum, double stepSize)
SpinnerModel model = new SpinnerNumberModel(50, 0, 100, .25);
JSpinner spinner = new JSpinner(model);

public SpinnerNumberModel(int value, int minimum, int maximum, int stepSize)
SpinnerModel model = new SpinnerNumberModel(50, 0, 100, 1);
JSpinner spinner = new JSpinner(model);

public SpinnerNumberModel(Number value, Comparable minimum, Comparable maximum, Number stepSize)
Number value = new Integer(50);
Number min = new Integer(0);
Number max = new Integer(100);
Number step = new Integer(1);
SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
JSpinner spinner = new JSpinner(model);
```

如果 `minimum` 或 `maximum` 为 `null`，表示为开区间。无参构造函数的初始值为 0，`stepSize` 为 1。

`SpinnerNumberModel` 的属性如下表所示：

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| maximum | Comparable | Read-write |
| minimum | Comparable | Read-write |
| nextValue | Object | Read-only |
| number | Number | Read-only |
| previousValue | Object | Read-only |
| stepSize | Number | Read-write |
| value | Object | Read-write |
### 自定义模型

虽说 `JSpinner` 提供的模型基本够用，但总有需要自定义的地方。

**示例：** 定制 `SpinnerListModel`，使得到末端不停止，而是循环到另一端

```java
public class RolloverSpinnerListModel extends SpinnerListModel
{
    public RolloverSpinnerListModel(List<?> values) {
        super(values);
    }

    public RolloverSpinnerListModel(Object[] values) {
        super(values);
    }

    @Override
    public Object getNextValue() {
        Object nextValue = super.getNextValue();
        if (nextValue == null) {
            nextValue = getList().get(0);
        }
        return nextValue;
    }

    @Override
    public Object getPreviousValue() {
        Object previousValue = super.getPreviousValue();
        if (previousValue == null) {
            List<?> list = getList();
            previousValue = list.get(list.size() - 1);
        }
        return previousValue;
    }
}
```
## JSpinner Editor

JSpinner Editor 负责显示和编辑选择的值。
### JSpinner.DefaultEditor

使用 `JSpinner.setEditor()` 可以将任何 `JComponent` 设置为 `JSpinner` 的编辑器。不过大多时候我们使用内置编辑器。类图如下：

![[images/Pasted image 20240116201141.png|400]]

`DefaultEditor` 提供了基于 `JFormattedTextField` 的简单编辑器，只有一个构造函数：

```java
public JSpinner.DefaultEditor(JSpinner spinner)

JSpinner spinner = new JSpinner();
JComponent editor = JSpinner.DefaultEditor(spinner);
spinner.setEditor(editor);
```

`DefaultEditor` 有两个属性：

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| `spinner` | `JSpinner` | Read-only |
| `textField` | `JFormattedTextField` | Read-only |
对 `DefaultEditor`，常通过修改 `JFormattedTextField` 自定义显示。
### JSpinner.DateEditor

`DateEditor` 通过 `java.text.SimpleDateFormat` 格式化日期。

```java
public JSpinner.DateEditor(JSpinner spinner)

SpinnerModel model = new SpinnerDateModel();
JSpinner spinner = new JSpinner(model);
JComponent editor = JSpinner.DateEditor(spinner);
spinner.setEditor(editor);

public JSpinner.DateEditor(JSpinner spinner, String dateFormatPattern)

SpinnerModel model = new SpinnerDateModel();
JSpinner spinner = new JSpinner(model);
JComponent editor = JSpinner.DateEditor(spinner, "MMMM yyyy");
spinner.setEditor(editor);
```

默认格式为 "M/d/yy hLmm a"，即 "12/25/04 13:34 PM" 样式。

该编辑器也有两个属性：

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| `format` | `SimpleDateFormat` | read-only |
| `model` | `SpinnerDateModel` | read-only |
### JSpinner.ListEditor

`ListEditor` 为 `SpinnerListModel` 提供提前输入功能，不额外提供格式化功能。由于模型的所有元素已知，因此该编辑器会尝试将用户已输入的字符与已有元素匹配。

`ListEditor` 只有一个构造函数，一般用不着。

```java
public JSpinner.ListEditor(JSpinner spinner)
```

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| `model` | `SpinnerListModel` | Read-only |
### JSpinner.NumberEditor

`NumberEditor` 的工作方式类似于 `DateEditor`，允许通过格式化字符串定义显示格式。`NumberEditor` 使用 `java.text.DecimalFormat` 格式化数字。`NumberEditor` 提供了 2 个构造函数：

```java
public JSpinner.NumberEditor(JSpinner spinner)
SpinnerModel model = new SpinnerNumberModel(50, 0, 100, .25);
JSpinner spinner = new JSpinner(model);
JComponent editor = JSpinner.NumberEditor(spinner);
spinner.setEditor(editor);

public JSpinner.NumberEditor(JSpinner spinner, String decimalFormatPattern)
SpinnerModel model = new SpinnerNumberModel(50, 0, 100, .25);
JSpinner spinner = new JSpinner(model);
JComponent editor = JSpinner.NumberEditor(spinner, "#,##0.###");
spinner.setEditor(editor);
```

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
| format | DecimalFormat | Read-only |
| model | SpinnerNumberModel | Read-only |
