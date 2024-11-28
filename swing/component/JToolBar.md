# JToolBar

## 简介

工具栏（`JToolBar` ）用于快速访问常用命令，这些命令通常隐藏在分层菜单中。`JToolBar` 是一个专门用于容纳组件的 Swing 容器。

### 构造函数

```java
public JToolBar()
JToolBar jToolBar = new JToolBar();

public JToolBar(int orientation)
JToolBar jToolBar = new JToolBar(JToolBar.VERTICAL);

public JToolBar(String name)
JToolBar jToolBar = new JToolBar("Window Title");

public JToolBar(String name,int orientation)
JToolBar jToolBar = new JToolBar("Window Title", ToolBar.VERTICAL);
```

`JToolBar` 默认为水平方向，通过常量 `HORIZONTAL` 和 `VERTICAL` 设置工具栏方向。

另外，工具栏默认 floatable。因此在创建指定方向的工具栏后，用户可以拖动工具栏改变方向。

### 添加组件

可以将任何组件添加到 `JToolBar`。出于美观考虑，水平工具栏内组件的高度应大致相同；垂直工具栏内组件的宽度应大致相同。

`JToolBar` 类只定义了一个添加组件的方法，其它方法如 `add(Component)` 均从 `Container` 继承。此外，还可以向工具栏添加分隔符：

```java
public JButton add(Action action);
public void addSeparator();
public void addSeparator(Dimension size);
```

使用 `JToolBar.add(Action)` 添加 `Action` 时，将 `Action` 封装到 `JButton` 中，这与 `JMenu` 和 `JPopupMenu` 不同，向它们添加 `Action` 是用 `JMenuItem` 封装。

使用从 `Container` 继承的方法删除组件：

```java
public void remove(Component component);
```

### JToolBar 属性

**paintBorder**

默认绘制 `JToolBar` 的边框。

**orientation**

`orientation` 属性只能设置为 `JToolBar` 的常量 `HORIZONTAL` 或 `VERTICAL`。

更改 `orientation` 属性会导致 `JToolBar` 的 layout。如果直接使用 `setLayout()` 更改 layout，再次修改 `orientation` 会导致 layout 再次改变，因此不建议手动修改 `JToolBar` 的 layout。

> [!NOTE]
>
> `JToolBar` 内部使用 `BoxLayout` 执行布局。

**floatable**

工具栏默认是可浮动的。即用户可以将工具栏拖到其它边、主窗口上方（单独的 window）或原程序窗口外部。如果原始窗口的 layout 是 `BorderLayout`，则工具栏可以放在没有组件的边（不能放在 `BorderLayout` 中心）。

**rollover**

`rollover` 属性定义用户将鼠标移到工具栏内不同组件上时的外观。该行为可能涉及颜色或边框差异。默认当鼠标指针悬停在工具栏按钮上才绘制按钮的边框，默认为 `false`。

### JToolBar 事件处理

`JToolBar` 没有特定事件：

- 将 listener 添加到 `JToolBar` 包含的组件监听特定操作
- `JToolBar` 是一个 `Container`，所以可以添加容器相关事件

### 自定义 JToolBar 的 laf

每个 Swing laf 提供自己的 `JToolBar` 外观和一组默认的 `UIResource` 值。

## 示例



## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/components/toolbar.html
