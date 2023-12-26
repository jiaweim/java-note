# Swing 并发

- [Swing 并发](#swing-并发)
  - [鼠标事件判断](#鼠标事件判断)
  - [Timer](#timer)

2021-11-18, 12:52
***

## 鼠标事件判断

`MouseInputListener` 接口包含 7 个方法，包括 `MouseListener` 的 5 个：

```java
public interface MouseListener extends EventListener {

    public void mouseClicked(MouseEvent e);

    public void mousePressed(MouseEvent e);

    public void mouseReleased(MouseEvent e);

    public void mouseEntered(MouseEvent e);

    public void mouseExited(MouseEvent e);
}
```

和 `MouseMotionListener` 的 2 个：

```java
public interface MouseMotionListener extends EventListener {

    public void mouseDragged(MouseEvent e);

    public void mouseMoved(MouseEvent e);
}
```

如果要确定鼠标的哪个键被按下，可以检查 `MouseEvent` 的 `modifiers` 属性，并将其与 `InputEvent` 类的不同掩码常量对比。例如，在 `mousePressed()` 方法中检查是否鼠标中间按钮被按下：

```java
public void mousePressed(MouseEvent mouseEvent) {
    int modifiers = mouseEvent.getModifiers();
    if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
        System.out.println("Middle button pressed.");
    }
}
```

不过 `SwingUtilities` 中有更简单的方法：

```java
SwingUtilities.isLeftMouseButton(MouseEvent mouseEvent)
SwingUtilities.isMiddleMouseButton(MouseEvent mouseEvent)
SwingUtilities.isRightMouseButton(MouseEvent mouseEvent)
```

目前推荐使用 `public int getModifiersEx()` 方法获得鼠标事件的修饰掩码。

对原来的修饰符进行了扩展，扩展修饰符以 `_DOWN_MASK` 后缀结尾。扩展修饰符用于支持模态键，如按下 ALT, CTRL, META 后按鼠标。

例如，假设用户按顺序按下 button1，button 2，然后按同样的顺序释放，则生成如下事件序列：

```txt
MOUSE_PRESSED:   BUTTON1_DOWN_MASK
MOUSE_PRESSED:   BUTTON1_DOWN_MASK | BUTTON2_DOWN_MASK
MOUSE_RELEASED:  BUTTON2_DOWN_MASK
MOUSE_CLICKED:   BUTTON2_DOWN_MASK
MOUSE_RELEASED:
MOUSE_CLICKED:
```

这里不建议使用 `==` 比较该方法返回的值，因为将来可能会添加新的修饰符。推荐方式是，如检查按下 SHIFT+BUTTON1，释放 CTRL：

```java
int onmask = SHIFT_DOWN_MASK | BUTTON1_DOWN_MASK;
int offmask = CTRL_DOWN_MASK;
if ((event.getModifiersEx() & (onmask | offmask)) == onmask) {
    ...
}
```

下面依然以 `Button` 为例演示这两种用法：

```java
public class ButtonSample
{
    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JButton button = new JButton("Select Me");
            // Attach listeners
            button.addActionListener(e -> System.out.println("I was selected."));
            MouseListener mouseListener = new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    int modifiers = e.getModifiersEx();
                    if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) {
                        System.out.println("Left button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK) {
                        System.out.println("Middle button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
                        System.out.println("Right button pressed.");
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Left button released.");
                    }
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        System.out.println("Middle button released.");
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        System.out.println("Right button released.");
                    }
                }
            };
            button.addMouseListener(mouseListener);
            frame.add(button, BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
```

## Timer

Java 提供了两种执行定时任务的方法：`java.util.Timer` 和 `javax.swing.Timer`。两个类都使用计时器线程提供类似的功能。

例如，下面使用 `java.util.Timer` 实现每 3 秒修改按钮颜色：

```java
java.util.Timer clown = new java.util.Timer();
clown.schedule(new TimerTask()
{
    public void run()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                button.setForeground(getRandomColor());
            }
        });
    }
}, 0, 3000); // delay, period
```

`java.util.Timer` 可以调度多个 `TimerTask`，每个 `TimerTask` 具有不同的时间间隔。也可以随时取消 `TimerTask`。`java.util.Timer` 的主要问题是不再 EDT 上执行。由于 UI 很少需要能够一次处理数百个任务的高精度计时器，因此不如使用 `javax.swing.Timer`。

`javax.swing.Timer` 和 Swing 集成更好。一个 `Timer` 支持多个任务，都具有相同的重复周期。下面使用 `javax.swing.Timer` 重写上面的例子：

```java
javax.swing.Timer clown = new javax.swing.Timer(3000,
        new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                button.setForeground(getRandomColor());
            }
        });
clown.start();
```

Swing timer 在 EDT 上执行。

Timer 选择：

- 需要更新UI 时使用 `javax.swing.Timer`；
- 周期执行后台操作时使用 `java.util.Timer`。

