# GroupLayout

- [GroupLayout](#grouplayout)
  - [简介](#简介)
  - [示例](#示例)

2023-12-22, 11:29
****

## 简介

把多个组件按区域划分到不同的 Group，再根据各个 Group 相对于水平轴和垂直轴的排列来管理。

## 示例

```java
public class GroupLayoutDemo1 extends JFrame {
    public GroupLayoutDemo1() throws HeadlessException {
        //创建一个中间容器，并且创建一个GroupLayout布局管理器对象
        Container c = getContentPane();
        GroupLayout layout = new GroupLayout(c);

        //创建两个普通按钮组件、文本框组件
        JButton b1 = new JButton("按钮 1");
        JButton b2 = new JButton("按钮 2");
        JTextField text = new JTextField("文本");

        //创建一个hsg组，将两个按钮一个一个的添加到组里面
        GroupLayout.SequentialGroup hsg = layout.createSequentialGroup();
        hsg.addComponent(b1);
        hsg.addComponent(b2);

        //创建一个hpg组，将文本框组件和上面的那个组添加到其中，并且居中排列
        GroupLayout.ParallelGroup hpg =
                layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        hpg.addComponent(text).addGroup(hsg);
        layout.setHorizontalGroup(hpg); //沿水平线来确定hpg组中两个按钮组件的位置

        //创建一个vpg组，按照水平线来排列两个按钮组件的位置
        GroupLayout.ParallelGroup vpg = layout.createParallelGroup();
        vpg.addComponent(b1);
        vpg.addComponent(b2);

        // 将文本框组件和前面的容纳两个按钮组件的组同时添加到vsg组中
        GroupLayout.SequentialGroup vsg = layout.createSequentialGroup();
        vsg.addComponent(text).addGroup(vpg);
        //沿垂直线来确定vsg中vpg和文本框组件的位置
        layout.setVerticalGroup(vsg);

        setLayout(layout);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args) {
        new GroupLayoutDemo1();
    }
}
```

<img src="images/image-20231222112908331.png" width="140"/>
