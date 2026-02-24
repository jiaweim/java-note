# JRootPane

2024-03-31
@author Jiawei Mao
***

## 简介

每个顶层容器都依赖于一个称为 root-pane 的中间容器。root-pane 负责管理 content-pane、菜单栏，以及其它几个容器。使用 Swing 组件通常不需要了解 root-pane。但是，如果需要拦截鼠标事件或绘制多个组件，就需要熟悉 root-pane。

下面是 root-pane 提供给 `JFrame` 以及其它顶层容器的组件列表：

<img src="images/ui-rootPane.gif" width="360" />

`JRootPane` 包含两部分：

- `JLayeredPane`
- glass-pane (`Component`)

**glass-pane**

glass-pane 在最前面，可以是任意 `Component`，通常不可见，即默认隐藏。glass-pane 确保类似 tooltip 组件出现在其它 Swing 组件前面。如果使 glass-pane 可见，它就像一块玻璃覆盖在 `JLayeredPane` 上面。它完全透明，除非实现 glass-pane 的 `paintComponent` 方法。通常用于拦截 root-pane 的输入事件。

**layered-pane**

glass-pane 下面是 `JLayeredPane`。`JLayeredPane` 的顶部为可选的菜单栏 `JMenuBar`，和 content-pane (`Container`)。可视化组件通常放在 content-pane 中。

**content-pane**

root-pane 的可见组件的容器，不包括菜单栏。

**menu-bar**

root-pane 的菜单。如果一个容器由菜单栏，通过使用容器的 `setJMenuBar` 将菜单栏放在适当的位置。

!!! note
    `JLayeredPane` 只是一个 Swing 容器，它可以包含任何组件，并具有特殊的分层特点。`JRootPane` 中使用的默认 `JLayeredPane` 仅包含一个可选的 `JMenuBar` 和一个 `Container`。content-pane 有自己的 layout，默认为 `BorderLayout`。

## 创建 JRootPane

尽管 `JRootPane` 提供了一个无参构造函数，但通常不用自己创建 `JRootPane`，而是由实现 `RootPaneContainer` 接口的类创建 `JRootPane`。然后通过 `RootPaneContainer.getRootPane()` 获得 root-pane。

<img src="./images/image-20240331210138640.png" alt="image-20240331210138640" style="zoom:50%;" />

## JRootPane 属性

如下表所示，`JRootPane` 有 11 个属性。当为顶级容器获取或设置这些属性时，大多情况容器只是将请求传递给它的 `JRootPane`。

| 属性                    | 类型              | 权限             |
| ----------------------- | ----------------- | ---------------- |
| `accessibleContext`       | `AccessibleContext` | Read-only        |
| `contentPane`             | `Container`         | Read-write       |
| `defaultButton`           | `JButton`           | Read-write bound |
| `glassPane`               | `Component`         | Read-write       |
| `jMenuBar`                | `JMenuBar`          | Read-write       |
| `layeredPane`             | `JLayeredPane`      | Read-write       |
| `optimizedDrawingEnabled` | `boolean`           | Read-only        |
| `UI`                      | `RootPaneUI`        | Read-write       |
| `UIClassID`               | `String`            | Read-only        |
| `validateRoot`            | `boolean`           | Read-only        |
| `windowDecorationStyle`   | `int`               | Read-write bound |

`JRootPane` 的 `glassPane` 必须透明。因为 glass-pane 占据了 `JLayeredPane` 表层的整个区域，如果 glass-pane 不透明，那么下面的 menu-bar 和 content-pane 都看不见。而且，由于 glass-pane 和 content-pane bounds 相同，`optimizedDrawingEnabled` 根据 glass-pane 的可见性返回值。

`windowDecorationStyle` 属性描述包含 `JRootPane` 的窗口的装饰（边框、标题、关闭窗口按钮），它可以设置为以下 `JRootPane` 常量：

- COLOR_CHOOSER_DIALOG
- ERROR_DIALOG
- FILE_CHOOSER_DIALOG
- FRAME
- INFORMATION_DIALOG
- NONE
- PLAIN_DIALOG
- QUESTION_DIALOG
- WARNING_DIALOG

`windowDecorationStyle` 属性设置效果取决于当前 Laf。该设置只是一个 hint。

`windowDecorationStyle` 默认为 `NONE`。如果不是 `NONE`，且 `JDialog` 或 `JFrame` 的 `setUndecorated(false)` 被调用，当前 Laf 的 `getSupportsWindowDecorations()` 返回 `true`，则有当前 Laf，而非窗口管理器提供窗口装饰。

对 Metal Laf，`getSupportsWindowDecorations()` 返回 `true`，其它系统提供的 laf 返回 `false`。下面是 Metal Laf 提供的 Frame 窗口的装饰：

```java
import javax.swing.*;
import java.awt.*;

public class AdornSample {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Adornment Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            frame.setSize(300, 100);
            frame.setVisible(true);
        });
    }
}
```

<img src="./images/image-20240331214705533.png" alt="image-20240331214705533" style="zoom:80%;" />

## 自定义 JRootPane Laf

`JRootPane` 包含 12 个 `UIResource` 相关属性。这些设置大多与配置窗口装饰样式时的默认 border 有关。

|Property String|Object Type|
|---|---|
|RootPane.actionMap|ActionMap|
|RootPane.ancestorInputMap|InputMap|
|RootPane.colorChooserDialogBorder|Border|
|RootPane.defaultButtonWindowKeyBindings|Object[ ]|
|RootPane.errorDialogBorder|Border|
|RootPane.fileChooserDialogBorder|Border|
|RootPane.frameBorder| Border|
|RootPane.informationDialogBorder| Border|
|RootPane.plainDialogBorder |Border|
|RootPane.questionDialogBorder |Border|
|RootPane.warningDialogBorder |Border|
|RootPaneUI |String|

## RootPaneContainer 接口

RootPaneContainer 接口定义

## Glass Pane

如果需要捕获事件，或在已经包含组件的区域上绘制，就可以采用 glass-pane。例如:

- 通过 glass-pane 拦截事件来停用多组件区域的鼠标事件
- 使用 glass-pane 在组件上面显示图像

例如，下面包含一个复选框，设置 glass-pane 是否可见，glass-pane 可见时，它会阻止所有输入事件到达 content-pane 的组件，并在鼠标按下的地方画一个红点：

![](images/2023-12-28-16-54-20.png)

操作：

1. 点击 Button1

按钮的外观会随之变化，显示它被点击。

2. 勾选 check-box，再点击 Button 1

由于 glass-pane 拦截了所有鼠标事件，因此按钮不会被单击。当你释放鼠标，glass-pane 上会画出一个红色圆圈。

3. 再次单击 check-box，隐藏 glass-pane

当 glass-pane 检测到 check-box 上的事件，它会将事件转发给 check-box。

下面是显示和隐藏 glass-pane 的代码。这个程序创建了自己的 glass-pane，但是，如果 glass-pane 不执行任何绘制，则可以使用 `getGlassPane` 返回的默认 glass-pane。

```java
myGlassPane = new MyGlassPane(...);
changeButton.addItemListener(myGlassPane);
frame.setGlassPane(myGlassPane);
...
class MyGlassPane extends JComponent
                  implements ItemListener {
    ...
    //React to change button clicks.
    public void itemStateChanged(ItemEvent e) {
        setVisible(e.getStateChange() == ItemEvent.SELECTED);
    }
...
}
```

下面实现 glass-pane 鼠标事件处理。如果 check-box 上发生鼠标事件，则 glass-pane 会重新调度该事件，使 check-box 能收到：

```java
...//In the implementation of the glass pane's mouse listener:
public void mouseMoved(MouseEvent e) {
    redispatchMouseEvent(e, false);
}

.../* The mouseDragged, mouseClicked, mouseEntered,
    * mouseExited, and mousePressed methods have the same
    * implementation as mouseMoved. */...

public void mouseReleased(MouseEvent e) {
    redispatchMouseEvent(e, true);
}

private void redispatchMouseEvent(MouseEvent e,
                                  boolean repaint) {
    Point glassPanePoint = e.getPoint();
    Container container = contentPane;
    Point containerPoint = SwingUtilities.convertPoint(
                                    glassPane,
                                    glassPanePoint,
                                    contentPane);

    if (containerPoint.y < 0) { //we're not in the content pane
        //Could have special code to handle mouse events over
        //the menu bar or non-system window decorations, such as
        //the ones provided by the Java look and feel.
    } else {
        //The mouse event is probably over the content pane.
        //Find out exactly which component it's over.
        Component component =
            SwingUtilities.getDeepestComponentAt(
                                    container,
                                    containerPoint.x,
                                    containerPoint.y);

        if ((component != null)
            && (component.equals(liveButton))) {
            //Forward events over the check box.
            Point componentPoint = SwingUtilities.convertPoint(
                                        glassPane,
                                        glassPanePoint,
                                        component);
            component.dispatchEvent(new MouseEvent(component,
                                                 e.getID(),
                                                 e.getWhen(),
                                                 e.getModifiers(),
                                                 componentPoint.x,
                                                 componentPoint.y,
                                                 e.getClickCount(),
                                                 e.isPopupTrigger()));
        }
    }

    //Update the glass pane if requested.
    if (repaint) {
        glassPane.setPoint(glassPanePoint);
        glassPane.repaint();
    }
}
```

下面是 MyGlassPane 中自定义绘制内容：

```java
protected void paintComponent(Graphics g) {
    if (point != null) {
        g.setColor(Color.red);
        g.fillOval(point.x - 10, point.y - 10, 20, 20);
    }
}
```

完整代码：

```java
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

public class GlassPaneDemo {

    static private MyGlassPane myGlassPane;

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JCheckBox changeButton = new JCheckBox("Glass pane \"visible\"");
        changeButton.setSelected(false);

        //Set up the content pane, where the "main GUI" lives.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(changeButton);
        contentPane.add(new JButton("Button 1"));
        contentPane.add(new JButton("Button 2"));

        //Set up the menu bar, which appears above the content pane.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.add(new JMenuItem("Do nothing"));
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // 设置 glass-pane，它在 menu-bar 和 content-pane 上方，item-listener
        myGlassPane = new MyGlassPane(changeButton, menuBar, frame.getContentPane());
        changeButton.addItemListener(myGlassPane);
        frame.setGlassPane(myGlassPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GlassPaneDemo::createAndShowGUI);
    }
}

// 自定义 glass-pane，以实现自定义绘制
class MyGlassPane extends JComponent implements ItemListener {

    Point point;

    /**
     * @param aButton     check-box
     * @param menuBar     menu-bar
     * @param contentPane content-pane
     */
    public MyGlassPane(JCheckBox aButton, JMenuBar menuBar, Container contentPane) {
        CBListener listener = new CBListener(aButton, menuBar, this, contentPane);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    // 响应点击 check-box
    public void itemStateChanged(ItemEvent e) {
        setVisible(e.getStateChange() == ItemEvent.SELECTED);
    }

    // 在 glass-pane 上画个圆
    protected void paintComponent(Graphics g) {
        if (point != null) {
            g.setColor(Color.red);
            g.fillOval(point.x - 10, point.y - 10, 20, 20);
        }
    }

    // 画圆的位置
    public void setPoint(Point p) {
        point = p;
    }
}

// 监听所有 check-box 可能感兴趣的事件，并分配给 check-box
class CBListener extends MouseInputAdapter {

    Toolkit toolkit;
    JCheckBox liveButton;
    JMenuBar menuBar;
    MyGlassPane glassPane;
    Container contentPane;

    public CBListener(JCheckBox liveButton, JMenuBar menuBar,
            MyGlassPane glassPane, Container contentPane) {
        toolkit = Toolkit.getDefaultToolkit();
        this.liveButton = liveButton;
        this.menuBar = menuBar;
        this.glassPane = glassPane;
        this.contentPane = contentPane;
    }

    public void mouseMoved(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseDragged(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseClicked(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseEntered(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseExited(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mousePressed(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseReleased(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }

    //A basic implementation of redispatching events.
    private void redispatchMouseEvent(MouseEvent e, boolean repaint) {
        Point glassPanePoint = e.getPoint();
        Container container = contentPane;
        Point containerPoint = SwingUtilities.convertPoint(
                glassPane,
                glassPanePoint,
                contentPane);
        if (containerPoint.y < 0) { // 不在 content-pane 里面
            if (containerPoint.y + menuBar.getHeight() >= 0) {
                // 鼠标在菜单栏
            } else {
                // 鼠标在非系统窗口装饰，如 Java Laf提供的装饰
            }
        } else {
            // 鼠标可能在 content-pane 上面，找到它下面的组件
            Component component = SwingUtilities.getDeepestComponentAt(
                    container,
                    containerPoint.x,
                    containerPoint.y);

            if ((component != null) && (component.equals(liveButton))) {
                // 转给 check box
                Point componentPoint = SwingUtilities.convertPoint(
                        glassPane,
                        glassPanePoint,
                        component);
                component.dispatchEvent(new MouseEvent(component,
                        e.getID(),
                        e.getWhen(),
                        e.getModifiers(),
                        componentPoint.x,
                        componentPoint.y,
                        e.getClickCount(),
                        e.isPopupTrigger()));
            }
        }

        //Update the glass pane if requested.
        if (repaint) {
            glassPane.setPoint(glassPanePoint);
            glassPane.repaint();
        }
    }
}
```

## Layered Pane

layered-pane 具有深度，其包含的组件可以互相重叠。



## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/components/rootpane.html