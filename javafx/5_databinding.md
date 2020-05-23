# Data Binding

- [Data Binding](#data-binding)
  - [什么是属性 (Property)](#%e4%bb%80%e4%b9%88%e6%98%af%e5%b1%9e%e6%80%a7-property)
  - [什么是 Binding](#%e4%bb%80%e4%b9%88%e6%98%af-binding)
  - [JavaFX 的 Properties 支持](#javafx-%e7%9a%84-properties-%e6%94%af%e6%8c%81)

## 什么是属性 (Property)

Java 类包含两种成员：字段和方法。字段表示对象的状态，一般声明为 private。public 存取方法，或者是 getters 和 setters，用于修改 private 字段。对全部或部分 private 字段有对应的 public getters 和 setters的类称为 Java bean。

property 即用于描述对象的字段。除了最简单的属性，还有 indexed, bound, constrained properties。indexed 属性为数组对象，通过索引访问。bound 属性在值改变时，会发送通知到所有的监听器。constrained 属性为特殊类型的 bound 属性，监听器可以否定特定的更改。

## 什么是 Binding

binding 有很多种，这儿特指 data binding。在GUI程序中，data binding 常用于数据模型和 UI 组件之间的数据同步。
JavaBeans 支持数据绑定。通过 PropertyChangeSupport, PropertyChangeListener 实现。

## JavaFX 的 Properties 支持

所有的 JavaFX properties 都是 obsevable，即可以监听值的变化。在JavaFX中，属性由特定的类表示，如 IntegerProperty, DoubleProperty, StringProperty等，这些类都是抽象类，对应的实现有两种，如 SimpleDoubleProperty 和 ReadOnlyDoubleWrapper，其他类都有对应的实现。
