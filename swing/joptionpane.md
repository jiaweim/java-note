# JOptionPane

## 简介

`JOptionPane` 是一个特殊的类，用来创建弹窗面板。该面板的目的是向用户显示一条消息，并从该用户获得响应。该组件包含四个区域，如下图所示：

![](images/2021-11-17-13-36-32.png)

下面对这些部分如何配置进行详细讨论。

## 创建

Swing 提供了两种创建 `JOptionPane` 的方式：构造函数和工厂方法。

`JOptionPane` 有 7 个构造函数，其中包含所有配置参数的构造函数如下：

```java
public JOptionPane(Object message, int messageType, int optionType,
                       Icon icon, Object[] options, Object initialValue)
```

`message` 参数为 `Object` 类型，而不是 `String`，所以这部分理论可以显示任何内容，不限于文本。

`messageType` 指定信息类型。如果不提供自定义图标，laf 会根据 `messageType` 在图标区域展示不同的图标。`JOptionPane` 支持 5 种 `messageType`：

- ERROR_MESSAGE
- INFORMATION_MESSAGE
- QUESTION_MESSAGE
- WARNING_MESSAGE
- PLAIN_MESSAGE

`optionType` 用于确定按钮区域的按钮配置，包含 4 个可选项；

- `DEFAULT_OPTION`，单个 OK 按钮；
- `OK_CANCEL_OPTION`，OK 和 Cancel 两个按钮；
- `YES_NO_CANCEL_OPTION`，Yes, No, Cancel 三个按钮；
- `YES_NO_OPTION`，Yes，No 两个按钮。

`options` 是 `Object` 数组，用于为 `JOptionPane` 的按钮区域构建一组按钮对象。如果按参数为 `null`，则根据 `optionType` 参数确定按钮。否则，`options` 的工作方式与 `message` 参数类似，但不支持递归：

- 如果 `options` 数组元素为 `Component`，则将其放在按钮区域；
- 如果 `options` 数组为 `Icon`，则将 `Icon` 放到 `JButton` 中，然后将 `JButton` 放到按钮区域；
- 如果 `options` 数组为 `Object`，则调用其 `toString()` 方法转换为字符串，将字符串放到 `JButton`，然后将按钮放到按钮区域。

使用字符串或 `Icon` 显示文本或图标，如果两者都想要，则传入定制好的 `Component` 数组。

如果指定了 `options` 参数，`initialValue` 参数指定默认选择的按钮。如果 `initialValue` 为 `null`，选择第一个按钮。

## Icon

Icon：图标用于指示消息类型。使用 laf 会根据消息类型提供对应图标，

## Message

`message` 参数为 `Object` 类型，而不是 `String`，所以这部分理论可以显示任何内容，不限于文本。

`messageType` 指定信息类型。如果不提供自定义图标，laf 会根据 `messageType` 在图标区域展示不同的图标。`JOptionPane` 支持 5 种 `messageType`：

- ERROR_MESSAGE
- INFORMATION_MESSAGE
- QUESTION_MESSAGE
- WARNING_MESSAGE
- PLAIN_MESSAGE

## Input

输入区域允许有用户提供对信息的响应。

输入形式自由，可以使用如 JTextField, JComboBox, `JButton` 等任意组件。

## Button

按钮区域也是用于获取用户反馈信息。在这个区域点击任意按钮，`JOptionPane` 都会关闭。

## 参考

- The Definitive Guide to Java Swing, 3ed
