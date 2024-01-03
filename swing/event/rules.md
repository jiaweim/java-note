# 事件处理的基本规则

- [事件处理的基本规则](#事件处理的基本规则)
  - [简介](#简介)
  - [设计要素](#设计要素)
  - [事件信息：EventObject](#事件信息eventobject)
  - [低级事件和语义事件](#低级事件和语义事件)
  - [EventAdapter](#eventadapter)
  - [内部类和匿名内部类](#内部类和匿名内部类)
  - [EventHandler](#eventhandler)
  - [参考](#参考)

2023-12-28, 11:21⭐
***

## 简介

本节包含如下内容：

- 实现事件处理程序的注意事项；
- 描述事件的事件对象，特别是 `EventObject`，它是所有 AWT 和 Swing 事件的超类；
- 低级事件和语义事件，建议尽可能选择语义事件
- event-listener 的实现技术

## 设计要素

event-listener 要点：执行要快。

因为所有的绘制操作和事件处理方法都是在 EDT 线程中执行，事件处理方法执行慢会导致程序卡顿，重绘变慢。对十分耗时的事件处理操作，应该创建单独的线程执行。

实现事件 event-listener 的选择很多，没有一个通用的方法。下面会给出一些建议和技巧。例如：

- 将 event-listeners 实现为不同的类。这个架构很容易维护，但是**过多的类会导致性能降低**。
- 在非 public 类中实现 event-listener，private 实现更安全。
- 对简单的 event-listener，使用 `EventHandler` 可以**避免创建新类**。

## 事件信息：EventObject

每个 event-listener 的方法都有一个继承自 `EventObject` 类的参数。

`EventObject` 有一个非常有用的方法：

```java
Object getSource()
```

返回触发事件的对象。

`getSource` 返回 `Object` 类型。`EventObject` 的子类可能有相同功能的其它方法，返回更具体的类型。例如，`ComponentEvent.getComponent` 和 `getSource` 一样返回触发事件的对象，但它返回 `Component` 类型。

事件类包含事件信息。例如，`MouseEvent` 包含事件发生的位置、用户点击了几次鼠标、按了哪些键等。

## 低级事件和语义事件

事件可以分为两类：低级事件和语义事件：

- **低级事件**指窗口系统事件或低级输入，如鼠标和键盘操作事件
- 其它都是语义事件，如单击 button 触发 action-event，按 Enter 导致 text-field 触发 action-event

语义事件可能由用户输入触发，也可能由模型触发。例如，table-model 从数据库接收到新数据时，触发 table-model-event。

应该尽可能监听语义事件，而非低级事件。这样能保证代码的可移植性和稳定。例如，监听 button 的 action-event，而不是 mouse-event，这样当用户用键盘或特定 Laf 的手势激活 button 时，button 能做出正确反应。

当处理类似 combo-box 的复合组件，**必须使用语义事件**，因为没有可靠的方法在所有可能组成复合组件的所有特定于 Laf 的组件上注册 listener。

## EventAdapter

很多 listener 接口包含多个方法。例如，`MouseListener` 包含 5 个方法：`mousePressed`, `mouseReleased`, `mouseEntered`, `mouseExited`, `mouseClicked`。如果直接实现 `MouseListener`，即使只关心 mouse-click 事件，你也要实现所有 5 个方法。其它事件方法可以为空。例如：

```java
public class MyClass implements MouseListener {
    ...
        someObject.addMouseListener(this);
    ...
    // 空方法
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
	
    // 真正需要实现的方法
    public void mouseClicked(MouseEvent e) {
        ...//Event listener implementation goes here...
    }
}
```

这些空方法使代码难以阅读和维护。为了避免实现空方法，Swing 为包含多个方法的 listener 提供了 adapter 类。[Listener API 表格](api.md) 列出了所有 listener 及其 adapter。例如，`MouseAdapter` 类实现了 `MouseListener` 接口。要点：

- adapter 以空方法形式实现了 listener 接口的所有方法。
- adapter 使用：继承 adapter 并实现感兴趣的方法。

下面通过继承 `MouseAdapter` 来实现与上面代码相同的功能：

```java
public class MyClass extends MouseAdapter {
    ... 
        someObject.addMouseListener(this);
    ... 
    public void mouseClicked(MouseEvent e) {
        ...//Event listener implementation goes here...
    }
}
```

## 内部类和匿名内部类

如何想使用 adapter 类，但不想用 public 类继承它，此时可以采用内部类：

```java
public class MyClass extends Applet {
    ...
        someObject.addMouseListener(new MyAdapter());
    ...
    class MyAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            ...// Event listener 具体实现
        }
    }
}
```

> 性能：程序的启动时间和内存占用通常与加载的类的数量成正比。创建的类越多，程序启动所需时间越长，占用的内存也就越多。在设计程序时，应该平衡可维护性和性能。

在创建内部类时不指定名称，就是匿名内部类。匿名内部类使用起来很便捷，不过依然需要权衡便捷性和性能。

使用匿名内部类的示例：

```java
public class MyClass extends Applet {
    ...
        someObject.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ...//Event listener implementation goes here...
            }
        });
    ...
    }
}
```

内部类可以访问其包含类的 `private` 实例变量。只要不将内部类声明为 `static`，就能够访问包含类的实例变量和方法。内部类如果想使用**局部变量**，只需将该变量的副本保存为 `final` 局部变量。

## EventHandler

`EventHandler` 类可用于生成单语句 event-listener，特点：

- 生成的 event-listener 可以持久化
- 不会增加类的数量（性能）

手动创建 `EventHandler` 很困难，还容易出错，最好由 GUI builder 创建。即 `EventHandler` 旨在由 GUI builder 使用，连接 UI-bean (事件源)和 logic-bean (目标)。这种连接可以将逻辑与界面分离。例如，使用 `EventHandler` 连接 `JCheckBox` 和参数为 `boolean` 值的方法，可以提取 `JCheckBox` 的状态并直接传递给方法。

内部类是处理 UI 事件的另一种更通用的方法。`EventHandler` 的功能没有内部类强，但它适合长期持久化方案；此外，在多次实现相同接口的大型应用中，使用 `EventHandler` 可以减少磁盘和内存占用。

**用法一：** 调用目标对象的无参方法

`EventHandler` 最简单的用法是安装一个 listener，调用目标对象的无参方法。

例如，创建 `ActionListener` 调用 `javax.swing.JFrame` 的 `toFont` 方法：

```java
myButton.addActionListener(
    (ActionListener)EventHandler.create(ActionListener.class, frame, "toFront"));
```

点击 `myButton`，`frame.toFront()` 被执行。

相同功能匿名内部类实现：

```java
myButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        frame.toFront();
    }
});
```

**用法二：** 根据 listener 方法的参数，设置目标对象的属性

`EventHandler` 可以从 listener 接口方法的第一个参数提取属性值，并使用它设置目标对象的属性值。例如，下面创建一个 `ActionListener`，它将目标对象 `myButton` 的 `nextFocusableComponent` 属性设置为事件 event 的 `source` 属性：

```java
EventHandler.create(ActionListener.class, myButton, "nextFocusableComponent", "source")
```

相同功能的匿名内部类实现：

```java
new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        myButton.setNextFocusableComponent((Component)e.getSource()); 
    }
}
```

**用法三：** 将事件参数传递给目标对象的方法

`EventHandler` 可以直接将传入的事件对象传递给目标的方法。只需 `EventHandler.create` 的第四个字符串参数为空：

```java
EventHandler.create(ActionListener.class, target, "doActionEvent", "")
```

相同功能的匿名内部类实现：

```java
new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        target.doActionEvent(e);
    }
}
```

**用法四：** 从事件源提取属性，用来设置目标对象属性

`EventHandler`  的最常见用法可能是从事件源提取属性值，并将其设置为目标对象的属性值。例如，下面创建一个 `ActionListener`，它提取事件源的 `text` 属性，并设置为目标对象的 `label` 属性：

```java
EventHandler.create(ActionListener.class, myButton, "label", "source.text")
```

相同功能的匿名内部类实现：

```java
new ActionListener {
    public void actionPerformed(ActionEvent e) {
        myButton.setLabel(((JTextField)e.getSource()).getText()); 
    }
}
```

## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/events/generalrules.html