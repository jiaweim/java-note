# ActionListener

2023-12-28, 14:31⭐
***

## 简介

action-listener 是最容易、最常见的 event-handler，用于定义用户某些操作应该执行的内容。

用户操作触发 action-event。例如，点击 button，选择 menu-item，在 text-field 中按回车，都会将 `actionPerformed` 消息传递给所有注册的 action-listeners。

定义 action-listener 的步骤：

1. 定义 event-handler 类，必须实现 `ActionListener` 接口或扩展实现 `ActionListener` 接口的类。例如

```java
public class MyClass implements ActionListener { 
```

2. 将 event-handler 注册为组件的 listener。例如

```java
someComponent.addActionListener(instanceOfMyClass);
```

3. 实现 listener 接口的方法。例如

```java
public void actionPerformed(ActionEvent e) { 
    ...//code that reacts to the action... 
}
```

通常，检测用户何时点击按钮或等效的键盘操作：

- 首先要定义一个实现 `ActionListener` 接口的类
- 然后使用 `addActionListener` 方法将 `ActionListener` 实例注册为按钮的 action-listener
- 当点击按钮，按钮触发 action-event，导致调用 action-listener 的 `actionPerformed` 方法，该方法只有一个 `ActionEvent` 类型参数，提供事件和事件源信息。

具体示例可参考 [Beeper](_event.md)。

## ActionListener API

**ActionListener 接口** 

`ActionListener` 接口只有一个方法，因此没有匹配的 adaper 类。

- 用户操作后被调用的方法

```java
actionPerformed(actionEvent)
```

**ActionEvent 类**

- 获取与操作关联的字符串。大多数触发 action-event 的组件都有 `setActionCommand` 方法，用于设置该字符串

```java
String getActionCommand()
```

- 返回表示 action-event 发生时用户按下的 modifier-keys 的整数

```java
int getModifiers()
```

可以使用 `ActionEvent` 定义的常量 `SHIFT_MASK`, `CTRL_MASK`, `META_MASK`, `ALT_MASK` 确定按下的键。例如，如果用户选择 menu-item 时按下 shift 键，那么下面的表达式非零

```java
actionEvent.getModifiers() & ActionEvent.SHIFT_MASK
```

- 返回触发事件的对象，即事件源

```java
Object getSource()
```