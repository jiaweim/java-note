# BorderLayout

- [BorderLayout](#borderlayout)
  - [简介](#简介)
  - [示例](#示例)

2023-12-22, 01:02
****

## 简介

布局管理器将容器分为 5 个区域，用 5 个常量字段定义：

| 常量                                           | 说明 |
| ---------------------------------------------- | ---- |
| `public static final String NORTH  = "North"`  | 北   |
| `public static final String SOUTH  = "South"`  | 南   |
| `public static final String EAST   = "East"`   | 东   |
| `public static final String WEST   = "West"`   | 西   |
| `public static final String CENTER = "Center"` | 中   |

`BorderLayout` 构造函数：

| 构造函数                     | 说明               |
| ---------------------------- | ------------------ |
| `BorderLayout()`             | 间距为 0           |
| `BorderLayout(int h, int v)` | 指定水平和垂直间距 |

## 示例

```java
public class BorderLayoutDemo {
    public static void main(String[] args) {
        JFrame jf = new JFrame("测试程序");
        jf.setSize(300, 200);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);
        JPanel contentPane = new JPanel();
        jf.setContentPane(contentPane);

        JButton b1 = new JButton("生活");
        JButton b2 = new JButton("工作");
        JButton b3 = new JButton("睡觉");
        JButton b4 = new JButton("购物");
        JButton b5 = new JButton("饮食");

        //创建一个布局管理器对象，将中间容器设置为此布局管理
        BorderLayout lay = new BorderLayout();
        jf.setLayout(lay);

        contentPane.add(b1, "North");
        contentPane.add(b2, "South");
        contentPane.add(b3, "East");
        contentPane.add(b4, "West");
        contentPane.add(b5, "Center");
    }
}
```

<img src="images/image-20231222010204137.png" width="300"/>

`BorderLayout` 的每个区域，除了组件，也可以放中间容器，构建更复杂的布局：

```java
public class BorderLayoutDemo2 {
    public static void main(String[] args) {
        JFrame jf = new JFrame("测试程序");
        jf.setSize(300, 200);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);

        JPanel contentPane = new JPanel();
        jf.setContentPane(contentPane);

        JButton b1 = new JButton("港币");//创建了二十五个普通按钮组件
        JButton b2 = new JButton("人民币");
        JButton b3 = new JButton("美元");
        JButton b4 = new JButton("欧元");
        JButton b5 = new JButton("英镑");
        JButton b6 = new JButton("主板");
        JButton b7 = new JButton("内存");
        JButton b8 = new JButton("硬盘");
        JButton b9 = new JButton("显示器");
        JButton b10 = new JButton("鼠标");
        JButton b11 = new JButton("大米");
        JButton b12 = new JButton("蔬菜");
        JButton b13 = new JButton("稻子");
        JButton b14 = new JButton("猪肉");
        JButton b15 = new JButton("牛肉");
        JButton b16 = new JButton("面包");
        JButton b17 = new JButton("蛋糕");
        JButton b18 = new JButton("巧克力");
        JButton b19 = new JButton("奶酪");
        JButton b20 = new JButton("苹果派");
        JButton b21 = new JButton("笔记本");
        JButton b22 = new JButton("电话");
        JButton b23 = new JButton("办公桌");
        JButton b24 = new JButton("钢笔");
        JButton b25 = new JButton("文件夹");

        jf.setLayout(new BorderLayout());
        //创建了五个中间容器，并且将它们的布局管理器都设置成 BorderLayout
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();
        // layout 只负责管理，组件还是放在容器里
        p1.setLayout(new BorderLayout());
        p2.setLayout(new BorderLayout());
        p3.setLayout(new BorderLayout());
        p4.setLayout(new BorderLayout());
        p5.setLayout(new BorderLayout());
        //将五个中间容器对象分别加入到上层中间容器中，并且按照 BorderLayout 的方式进行布局
        contentPane.add(p1, BorderLayout.NORTH);
        contentPane.add(p2, BorderLayout.SOUTH);
        contentPane.add(p3, BorderLayout.EAST);
        contentPane.add(p4, BorderLayout.WEST);
        contentPane.add(p5, BorderLayout.CENTER);

        p1.add(b1, "North");
        p1.add(b2, "West");
        p1.add(b3, "South");
        p1.add(b4, "East");
        p1.add(b5, "Center");

        p2.add(b6, "North");
        p2.add(b7, "West");
        p2.add(b8, "South");
        p2.add(b9, "East");
        p2.add(b10, "Center");

        p3.add(b11, "North");
        p3.add(b12, "West");
        p3.add(b13, "South");
        p3.add(b14, "East");
        p3.add(b15, "Center");

        p4.add(b16, "North");
        p4.add(b17, "West");
        p4.add(b18, "South");
        p4.add(b19, "East");
        p4.add(b20, "Center");

        p5.add(b21, "North");
        p5.add(b22, "West");
        p5.add(b23, "South");
        p5.add(b24, "East");
        p5.add(b25, "Center");
        jf.pack();
    }
}
```

<img src="images/image-20231222011013847.png" width="550"/>
