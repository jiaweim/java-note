# PropertyChangeListener

- [PropertyChangeListener](#propertychangelistener)
  - [简介](#简介)
  - [监听所有属性](#监听所有属性)
  - [监听特定属性](#监听特定属性)
  - [实例展示](#实例展示)

## 简介

当 bean 的绑定属性发生变化时，触发 PropertyChangeEvent。所有的 Swing 组件都是 bean。

通过 getter 和 setter 方法可以访问 JavaBeans 属性。例如，`JComponent` 的 `font` 属性可以通过 `getFont` 和 `setFont` 访问。

部分场景需要 PropertChangeListener，如下：

- 你实现了一个格式化 `JTextField`，需要检测用户何时输入了新值。此时可以在 `JTextField` 上注册一个 PropertyChangeListener 监听属性值变化。
- 你实现了一个对话框，需要知道用户何时点击了对话框中的按钮，或更改了对话框中的选项。

可以在任何符合 JavaBeans 规则的组件的绑定属性上注册属性更改监听器。

注册 PropertyChangeListener 的方法有两种。

> Swing 组件使用 `SwingPropertyChangeSupport` 而不是 `PropertyChangeSupport`，两者的主要差别在，前者保证所有的 listener 只在 EDT 中触发。

## 监听所有属性

使用 `addPropertyChangeListener(PropertyChangeListener)` 注册监听器时，会通知对该对象的每个绑定属性的每一次更改。在 `propertyChange` 方法中，可以使用 `PropertyChangeEvent.getPropertyName` 获得更改属性的名称，如下所示：

```java
KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
focusManager.addPropertyChangeListener(new FocusManagerListener());
...
public FocusManagerListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        if ("focusOwner".equals(propertyName) {
            ...
        } else if ("focusedWindow".equals(propertyName) {
            ...
        }
    }
    ...
}
```

## 监听特定属性

`addPropertyChangeListener(String, PropertyChangeListener)`，其中的 `String` 为属性名称。使用该方法可以只监听单个属性值的更改。例如：

```java
aComponent.addPropertyChangeListener("font", new FontListener());
```

`FontListener` 只有在组件的 `font` 属性值发生更改时收到通知。

下面展示如何在 `JTextField` 上监听 `value` 属性的更改：

```java
//...where initialization occurs:
double amount;
JFormattedTextField amountField;
...
amountField.addPropertyChangeListener("value",
                                      new FormattedTextFieldListener());
...
class FormattedTextFieldListener implements PropertyChangeListener {
    public void propertyChanged(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (source == amountField) {
            amount = ((Number)amountField.getValue()).doubleValue();
            ...
        }
        ...//re-compute payment and update field...
    }
}
```

## 实例展示

```java
/**
 * 创建两个按钮：
 * - 选择任意一个，选择的按钮颜色随机变化；
 * - 第二个按钮监听第一个按钮的颜色变化，b1 颜色改变，b2 随之更换为相同。
 */
public class BoundSample
{
    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Bound");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            final JButton b1 = new JButton("Select Me");
            final JButton b2 = new JButton("No Select Me");
            final Random random = new Random();

            ActionListener actionListener = e -> {
                JButton button = (JButton) e.getSource();
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);
                button.setBackground(new Color(red, green, blue));
            };

            PropertyChangeListener propertyChangeListener = evt -> {
                String propertyName = evt.getPropertyName();
                if (propertyName.equals("background")) {
                    b2.setBackground((Color) evt.getNewValue());
                }
            };

            b1.addActionListener(actionListener);
            b1.addPropertyChangeListener(propertyChangeListener);
            b2.addActionListener(actionListener);

            frame.add(b1, BorderLayout.NORTH);
            frame.add(b2, BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
```

