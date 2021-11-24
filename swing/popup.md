# Popup

## 简介

使用 `Popup` 和 `PopupFactory` 类，可以在一个组件上弹出**任何组件**，这和 tooltip 不同，tooltip 是只读的、不可选的标签，功能有限。

`Popup` 的声明周期非常短，获得 `Popup` 后，调用 `hide` 方法隐藏，就不应该继续调用它的方法，`PopupFactory` 会缓存起来供以后使用。

## 创建

`Popup` 是一个简单的类，包含两个方法：`hide()` 和 `show()`。

`Popup` 通过 `PupupFactory` 类创建：

```java
PopupFactory factory = PopupFactory.getSharedInstance();
Popup popup = factory.getPopup(owner, contents, x, y);
```

这样 `contents` 组件在弹窗中显示，位于 `owner` 组件上方。

## 实例

下面展示一个按钮组件，点击按钮在界面随机位置弹出第二个按钮。

```java
public class ButtonPopupSample
{
    static final Random random = new Random();

    // 定义监听器
    static class ButtonActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("Selected: " + e.getActionCommand());
        }
    }

    // 定义弹窗监听器
    static class ShowPopupActionListener implements ActionListener
    {
        // owner 组件
        private final Component component;

        public ShowPopupActionListener(Component component)
        {
            this.component = component;
        }

        @Override
        public synchronized void actionPerformed(ActionEvent e)
        {
            JButton button = new JButton("Hello, World!");
            button.addActionListener(new ButtonActionListener());

            PopupFactory factory = PopupFactory.getSharedInstance();
            int x = random.nextInt(200);
            int y = random.nextInt(200);
            Popup popup = factory.getPopup(component, button, x, y);
            popup.show();

            // 3 秒后自动关闭
            Timer timer = new Timer(3000, e1 -> popup.hide());
            timer.start();
        }
    }

    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Popup Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 定义弹窗监听器，在 frame 上显示
            ActionListener actionListener = new ShowPopupActionListener(frame);

            JButton start = new JButton("Pick me for Popup");
            start.addActionListener(actionListener);
            frame.add(start);
            frame.setSize(350, 250);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
```

点击 Pick me for Popup 按钮，在界面上即随机位置出现弹窗按钮，持续 3 秒后自动小时。

![](images/2021-11-19-11-22-23.png)
