# CardLayout

- [CardLayout](#cardlayout)
  - [简介](#简介)
  - [示例](#示例)

2023-12-22, 10:41
****

## 简介

`CardLayout` 以堆叠的方式放置组件，一次仅有一个卡片可见。

| 构造函数                         | 说明                             |
| -------------------------------- | -------------------------------- |
| `CardLayout()`                   | gap 为 0                         |
| `CardLayout(int hgap, int vgap)` | 指定水平和垂直 gap，gap 放在边缘 |

| 方法                                       | 说明                   |
| ------------------------------------------ | ---------------------- |
| `void first(Container parent)`             | 翻到容器的第一个卡片   |
| `void next(Container parent)`              | 翻到容器的下一个卡片   |
| `void previous(Container parent)`          | 翻到容器的上一个卡片   |
| `void last(Container parent)`              | 翻到容器的最后一个卡片 |
| `void show(Container parent, String name)` | 翻到指定卡片           |

## 示例

```java
public class CardLayoutDemo1 extends JFrame {

    public CardLayoutDemo1() {
        super("CardLayout Test");
        try {
            // 将LookAndFeel设置成Windows样式
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //创建一个具有指定的水平和垂直 gap 的卡片布局
        CardLayout card = new CardLayout(5, 5); 
        // 主要的JPanel，该JPanel的布局管理将被设置成CardLayout
        JPanel mainPane = new JPanel(card);

        // 放按钮的 JPanel
        JPanel p = new JPanel(); // 构造放按钮的JPanel

        JButton button_1 = new JButton("< 上一步");
        JButton button_2 = new JButton("下一步 >");
        // 三个可直接翻转到JPanel组件的按钮
        JButton b_1 = new JButton("1");
        JButton b_2 = new JButton("2");
        JButton b_3 = new JButton("3");
        b_1.setMargin(new Insets(2, 2, 2, 2));
        b_2.setMargin(new Insets(2, 2, 2, 2));
        b_3.setMargin(new Insets(2, 2, 2, 2));
        p.add(button_1);
        p.add(b_1);
        p.add(b_2);
        p.add(b_3);
        p.add(button_2);

        // 要切换的三个 JPanel，设置为不同颜色
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.setBackground(Color.RED);
        p2.setBackground(Color.BLUE);
        p3.setBackground(Color.GREEN);
        p1.add(new JLabel("JPanel-1"));
        p2.add(new JLabel("JPanel-2"));
        p3.add(new JLabel("JPanel-3"));
        mainPane.add(p1, "p1");
        mainPane.add(p2, "p2");
        mainPane.add(p3, "p3");

        //下面是翻转到卡片布局的某个组件的动作事件处理，当单击按钮，就会触发出现下一个组件
        /// 上一步的按钮动作
        button_1.addActionListener(e -> card.previous(mainPane));
        // 下一步的按钮动作
        button_2.addActionListener(e -> card.next(mainPane));
        // 直接翻转到p_1
        b_1.addActionListener(e -> card.show(mainPane, "p1"));
        // 直接翻转到p_2
        b_2.addActionListener(e -> card.show(mainPane, "p2"));
        // 直接翻转到p_3
        b_3.addActionListener(e -> card.show(mainPane, "p3"));

        getContentPane().add(mainPane);
        getContentPane().add(p, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setVisible(true);
    }

    public static void main(String[] args) {
        new CardLayoutDemo1();
    }
}
```

<img src="images/image-20231222104015809.png" width="300"/>



