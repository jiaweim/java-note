# FlowLayout

- [FlowLayout](#flowlayout)
  - [简介](#简介)
  - [示例](#示例)

2023-12-22, 01:16⭐

****

## 简介

`FlowLayout` 规则：

- 按照组件加入的先后顺序从左到右排列
- 一行排满了，再换下一行，继续从左到右排列
- 每行组件都是居中排列

`FlowLayout` 一般用于按钮的布局。

如果有些组件看不到，可以调用 `pack` 方法自动调整 `JFrame` 大小。

| 构造函数                       | 说明                              |
| ------------------------------ | --------------------------------- |
| `FlowLayout()`                 | 居中对齐，默认水平和垂直 gap 为 5 pixels |
| `FlowLayout(int align)`        | 指定对齐方式，默认 gap            |
| `FlowLayout(int align, int hgap, int vgap)` | 指定对齐方式和 gap         |

对齐方式：

|对齐方式|说明|
|---|---|
|`FlowLayout.LEFT`|每行组件左对齐|
|`FlowLayout.RIGHT`|每行组件右对齐|
|`FlowLayout.CENTER`|每行组件居中|
|`FlowLayout.LEADING`|每行组件与容器前沿对齐。如，对 left-to-right 方向为左对齐|
|`FlowLayout.TRAILING`|每行组件与容器末尾对齐。如，对 left-to-right 方向为右对齐|

`alignOnBaseline` 属性说明：该属性设置组件垂直方向是否沿 base-line 对齐，`false` 表示垂直居中，默认 false。

## 示例

```java
public class FlowLayoutDemo {
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

        //将中间容器的布局管理器设置为FlowLayout
        contentPane.setLayout(new FlowLayout());

        contentPane.add(b1);
        contentPane.add(b2);
        contentPane.add(b3);
        contentPane.add(b4);
        contentPane.add(b5);
        jf.pack();
    }
}
```



<img src="images/image-20231222011607732.png" width="360"/>

