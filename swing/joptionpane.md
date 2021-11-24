# JOptionPane

## 简介

`JOptionPane` 是一个特殊的类，用来创建弹窗面板。该面板的目的是向用户显示一条消息，并从该用户获得响应。该组件包含四个区域，如下图所示：

![](images/2021-11-17-13-36-32.png)

下面对这些部分如何配置进行详细讨论。

## 创建

Swing 提供了两种创建 `JOptionPane` 的方式：构造函数和工厂方法。



## Icon

Icon：图标用于指示消息类型。使用 laf 会根据消息类型提供对应图标，

## Message

这部分用于展示信息。

## Input

输入区域允许有用户提供对信息的响应。

输入形式自由，可以使用如 JTextField, JComboBox, `JButton` 等任意组件。

## Button

按钮区域也是用于获取用户反馈信息。在这个区域点击任意按钮，`JOptionPane` 都会关闭。

## 参考

- The Definitive Guide to Java Swing, 3ed
