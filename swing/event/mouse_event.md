# 鼠标事件处理

2024-04-07
@author Jiawei Mao
***

## 识别按钮类型

`SwingUtilities` 类提供了许多辅助方法，例如，通过 `MouseEvent` 判断是按下了鼠标左键还是右键。

`MouseInputListener` 继承 `MouseListener` 和 `MouseMotionListener`：

```java
public interface MouseInputListener extends MouseListener, MouseMotionListener {}

public interface MouseListener extends EventListener {
    public void mouseClicked(MouseEvent e);
    public void mousePressed(MouseEvent e);
    public void mouseReleased(MouseEvent e);
    public void mouseEntered(MouseEvent e);
    public void mouseExited(MouseEvent e);
}

public interface MouseMotionListener extends EventListener {
    public void mouseDragged(MouseEvent e);
    public void mouseMoved(MouseEvent e);
}

public interface EventListener {}
```

因此，`MouseInputListener` 包含 7 个方法。

如果需要确定事件发生时鼠标的哪个键被按下或释放，可以检查 `MouseEvent` 的 modifiers 属性，并将其与 `InputEvent` 类的 mask 常量进行比较。

例如，在 `mousePressed` 事件中检查是否按下了鼠标中键，可以在 mouse-listener 的 `mousePressed()` 中使用如下代码：

```java
public void mousePressed(MouseEvent mouseEvent) {
    int modifiers = mouseEvent.getModifiers();
    if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
        System.out.println("Middle button pressed.");
    }
}
```

虽然这种使用方式没问题，但是 `SwingUtilities` 有三个方法使这个过程更简单：

```java
SwingUtilities.isLeftMouseButton(MouseEvent mouseEvent)
SwingUtilities.isMiddleMouseButton(MouseEvent mouseEvent)
SwingUtilities.isRightMouseButton(MouseEvent mouseEvent)
```

不再需要手动比较 mask 值：

```java
if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
    System.out.println("Middle button released."); 
}
```

**示例：** 在 `mousePressed` 中使用 mask 方式，在 `mouseReleased` 中使用 `SwingUtilities`

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonSample {

    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JButton button = new JButton("Select Me");

            ActionListener actionListener = e -> System.out.println("I was selected.");
            MouseListener mouseListener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int modifiers = e.getModifiers();
                    if ((modifiers & InputEvent.BUTTON1_MASK)
                            == InputEvent.BUTTON1_MASK) {
                        System.out.println("Left button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON2_MASK)
                            == InputEvent.BUTTON2_MASK) {
                        System.out.println("Middle button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON3_MASK)
                            == InputEvent.BUTTON3_MASK) {
                        System.out.println("Right button pressed.");
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Left button released.");
                    }
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        System.out.println("Middle button released.");
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        System.out.println("Right button released.");
                    }
                    System.out.println();
                }
            };
            button.addActionListener(actionListener);
            button.addMouseListener(mouseListener);

            frame.add(button, BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
```