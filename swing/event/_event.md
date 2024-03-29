# Swing 事件处理

- [Swing 事件处理](#swing-事件处理)
  - [简介](#简介)
  - [一个更复杂的例子](#一个更复杂的例子)
  - [参考](#参考)

2023-12-27, 21:52⭐
***

## 简介

从一个简单的事件处理示例：点击按钮，发出哔哔声。

```java
public class Beeper extends JPanel implements ActionListener {

    JButton button;

    public Beeper() {
        super(new BorderLayout());
        button = new JButton("Click Me");
        button.setPreferredSize(new Dimension(200, 80));
        add(button, BorderLayout.CENTER);
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Toolkit.getDefaultToolkit().beep();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Beeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Beeper contentPane = new Beeper();
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(Beeper::createAndShowGUI);
    }
}
```

下面是实现 button 事件处理的关键部分：

```java
public class Beeper ... implements ActionListener {
    ...
        button.addActionListener(this);
    ...
    public void actionPerformed(ActionEvent e) {
        ...//Make a beep sound...
    }
}
```

`Beeper` 类实现 `ActionListener` 接口，表明它可以作为 action-event 的 listener，当它接收到这类事件，会自动执行 `actionPerformed` 方法。

上例将 `Beeper` 注册为 button 触发的 action-event 的 listener，因此每次点击 button，都会调用 `Beeper` 的 `actionPerformed` 方法。

## 一个更复杂的例子

上例看到的事件模型强大而灵活。任意多的事件监听器可以监听来自任意多的事件源的任意类型事件。事件源和 listener 可以一一对应，也可以一对多，多对一。

<img src="images/2020-01-07-14-31-04.png" width="500"/>

即可以为特定事件源的特定类型事件注册多个 listeners，也可以一个 listener 监听多个对象的事件。

- 每种类型的事件由特定对象表示，该对象提供事件的信息和事件源
- 事件源通常是组件或模型，不过其它类型的对象也可以是事件源

下面演示如何在多个对象上注册 event-listener，如何将相同事件发送到多个 listeners。该示例包含两个事件源（`JButton`）和两个 event-listeners：

- `MultiListener` 同时监听两个按钮的事件：接收事件后，将事件的 action-command（被设置为 button 的文本）添加到上面 text-area
- `Eavesdropper` 只监听一个按钮的事件：接收到事件后，将事件的 action-command 添加的底部的 text-area

<img src="images/2023-12-27-21-52-38.png" width="500"/>

事件处理的代码：

```java
public class MultiListener ... implements ActionListener {
    ...
    //where initialization occurs:
        button1.addActionListener(this);
        button2.addActionListener(this);

        button2.addActionListener(new Eavesdropper(bottomTextArea));
    }

    public void actionPerformed(ActionEvent e) {
        topTextArea.append(e.getActionCommand() + newline);
    }
}

class Eavesdropper implements ActionListener {
    ...
    public void actionPerformed(ActionEvent e) {
        myTextArea.append(e.getActionCommand() + newline);
    }
}
```

`MultiListener` 和 `Eavesdropper` 都实现了 `ActionListener` 接口，并通过 `JButton.addActionListener` 注册。两者 `actionPerformed` 的实现也类似，即将事件的 action-command 添加到 text-area。完整代码：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiListener extends JPanel implements ActionListener {

    JTextArea topTextArea;
    JTextArea bottomTextArea;
    JButton button1, button2;
    final static String newline = "\n";

    public MultiListener() {
        super(new GridBagLayout());
        GridBagLayout gridbag = (GridBagLayout) getLayout();
        GridBagConstraints c = new GridBagConstraints();

        JLabel l = null;

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        l = new JLabel("What MultiListener hears:");
        gridbag.setConstraints(l, c);
        add(l);

        c.weighty = 1.0;
        topTextArea = new JTextArea();
        topTextArea.setEditable(false);
        JScrollPane topScrollPane = new JScrollPane(topTextArea);
        Dimension preferredSize = new Dimension(200, 75);
        topScrollPane.setPreferredSize(preferredSize);
        gridbag.setConstraints(topScrollPane, c);
        add(topScrollPane);

        c.weightx = 0.0;
        c.weighty = 0.0;
        l = new JLabel("What Eavesdropper hears:");
        gridbag.setConstraints(l, c);
        add(l);

        c.weighty = 1.0;
        bottomTextArea = new JTextArea();
        bottomTextArea.setEditable(false);
        JScrollPane bottomScrollPane = new JScrollPane(bottomTextArea);
        bottomScrollPane.setPreferredSize(preferredSize);
        gridbag.setConstraints(bottomScrollPane, c);
        add(bottomScrollPane);

        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.insets = new Insets(10, 10, 0, 10);
        button1 = new JButton("Blah blah blah");
        gridbag.setConstraints(button1, c);
        add(button1);

        c.gridwidth = GridBagConstraints.REMAINDER;
        button2 = new JButton("You don't say!");
        gridbag.setConstraints(button2, c);
        add(button2);

        button1.addActionListener(this);
        button2.addActionListener(this);

        button2.addActionListener(new Eavesdropper(bottomTextArea));

        setPreferredSize(new Dimension(450, 450));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        1, 1, 2, 2, Color.black),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    public void actionPerformed(ActionEvent e) {
        topTextArea.append(e.getActionCommand() + newline);
        topTextArea.setCaretPosition(topTextArea.getDocument().getLength());
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MultiListener");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new MultiListener();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

class Eavesdropper implements ActionListener {
    JTextArea myTextArea;

    public Eavesdropper(JTextArea ta) {
        myTextArea = ta;
    }

    public void actionPerformed(ActionEvent e) {
        myTextArea.append(e.getActionCommand()
                + MultiListener.newline);
        myTextArea.setCaretPosition(myTextArea.getDocument().getLength());
    }
}
```


## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/events/intro.html