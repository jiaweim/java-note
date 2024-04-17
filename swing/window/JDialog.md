# JDialog

2024-01-02⭐
@author Jiawei Mao
***

## 简介

`JDialog` 是类似 `JFrame` 的顶层容器类，是一个标准弹窗组件，常用于向用户显示错误或警告消息。其结构类似于 `JFrame`，即 `JRootPane` 包含 content-pane 和可选的 `JMenuBar`。

为了方便使用，有几个类可以直接实例化对话框：

- `JOptionPane` 简单标准对话框
- `ProgressMonitor` 显示进度的对话框
- `JColorChooser` 和 `JFileChooser` 也是提供标准对话框
- 使用 Printing API 可以显示打印对话框
- 自定义对话框，则直接使用 `JDialog`

每个对话框都依赖于 frame 组件，当 frame 被销毁，对话框随之被销毁。

对话框支持**模态**。模态对话框显示时，阻止用户与其它窗口的交互。`JOptionPane` 创建的 `JDialog` 是模态的，要创建非模态对话框，需要直接使用 `JDialog`。

## 创建 JDialog

`JDialog` 提供了多达 16 个构造函数：

```java
public JDialog()
public JDialog(Dialog owner)
public JDialog(Dialog owner, boolean modal)
public JDialog(Dialog owner, String title)
public JDialog(Dialog owner, String title, boolean modal)
public JDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc)
public JDialog(Frame owner)
...
```

不同构造函数提供不同形式的定制功能。参数说明：

- `owner`，对话框所属容器；
- `modal`，模态，是否为模态；
- `title`，标题

除了手动创建 `JDialog`，`JOptionPane` 提供了标准对话框实现。

## JDialog 属性

`JDialog` 属性和 `JFrame` 基本一样。

|Property Name|Data Type|Access|
|---|---|---|
|accessibleContext|AccessibleContext|Read-only|
|contentPane|Container|Read-write|
|defaultCloseOperation|int|Read-write|
|glassPane|Component|Read-write|
|jMenuBar|JMenuBar|Read-write|
|layeredPane|JLayeredPane|Read-write|
|layout|LayoutManager|Write-only|
|rootPane|JRootPane|Read-only|

- `defaultCloseOperation` 和 `JFrame` 的功能一样，默认为 `HIDE_ON_CLOSE`，这是对话框的合理默认行为。
- `defaultLookAndFeelDecorated` 属性指定新创建的 `JDialog` 的装饰由当前的 laf 提供。

不过这只是一个提示，如果 laf 不支持此功能，设置为 `true` 也没有任何效果。

- `layout` 属性

在此处说明是因为 `JDialog` 覆盖了 `setLayout` 方法，将其调用转到对 `contentPane` 的 `setLayout` 方法的调用。

### owner

`owner` 可以是另一个 `JDialog`, `JFrame` 或 `JWidnow`。

- 指定 `owner`，创建了父子关闭，当 `JDialog` 的 `owner` 被关闭，`JDialog` 也被关闭；
- 当 owner 最小化或最大化，`JDialog` 也随之最小化或最大化；
- `JDialog` 总是在其 `owner` 上方显示；

## JDialog 事件处理

`JDialog` 的事件处理和 `JFrame` 完全一样。

针对对话框，我们常希望按 `Escape` 键可以取消对话框。实现该功能的最简单方式是对 `JRootPane` 中的键盘操作注册 `Escape` 按钮。实现：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EscapeDialog extends JDialog {

    public EscapeDialog() {
        this((Frame) null, false);
    }

    public EscapeDialog(Frame owner) {
        this(owner, false);
    }

    public EscapeDialog(Frame owner, boolean modal) {
        this(owner, null, modal);
    }

    public EscapeDialog(Frame owner, String title) {
        this(owner, title, false);
    }

    public EscapeDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public EscapeDialog(Frame owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    public EscapeDialog(Dialog owner) {
        this(owner, false);
    }

    public EscapeDialog(Dialog owner, boolean modal) {
        this(owner, null, modal);
    }

    public EscapeDialog(Dialog owner, String title) {
        this(owner, title, false);
    }

    public EscapeDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public EscapeDialog(Dialog owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    protected JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action actionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);
        return rootPane;
    }
}
```

## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html