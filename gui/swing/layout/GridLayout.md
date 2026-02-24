# GridLayout

- [GridLayout](#gridlayout)
  - [简介](#简介)
  - [示例](#示例)

2023-12-22, 01:25
****

## 简介

`GridLayout` 将整个布局空间划分为网格。

| 构造函数                   | 说明           |
| -------------------------- | -------------- |
| `GridLayout()`             | 默认 1 行 1 列 |
| `GridLayout(int h, int v)` | 指定行数和列数 |

## 示例

```java
public class GridLayoutDemo {
    public static void main(String[] args) {
        JFrame jf = new JFrame("测试程序");
        jf.setSize(300, 200);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);

        JPanel contentPane = new JPanel();
        jf.setContentPane(contentPane);

        JButton b1 = new JButton("港币");
        JButton b2 = new JButton("人民币");
        JButton b3 = new JButton("美元");
        JButton b4 = new JButton("欧元");
        JButton b5 = new JButton("英镑");
        JButton b6 = new JButton("主板");
        JButton b7 = new JButton("内存");
        JButton b8 = new JButton("硬盘");
        JButton b9 = new JButton("显示器");

        //创建一个 GridLayout，3 行 3 列
        GridLayout gird = new GridLayout(3, 3);
        contentPane.setLayout(gird);

        contentPane.add(b1);
        contentPane.add(b2);
        contentPane.add(b3);
        contentPane.add(b4);
        contentPane.add(b5);
        contentPane.add(b6);
        contentPane.add(b7);
        contentPane.add(b8);
        contentPane.add(b9);

        jf.pack();
    }
}
```



<img src="images/image-20231222012235462.png" width="250"/>