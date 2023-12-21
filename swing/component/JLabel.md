# JLabel

- [JLabel](#jlabel)
  - [简介](#简介)
  - [创建 JLabel](#创建-jlabel)

2023-12-22, 00:02
****

## 简介

标签用于标识名称、说明性文字。一般来说，标签所显示的文本不会改变，并禁止编辑，不过，也可以通过代码修改标签文字。

## 创建 JLabel

|构造函数|说明|
|---|---|
|`JLabel()`|无图像空文本的 `JLabel`|
|`JLabel(String text)`|指定文本的 `JLabel`|
|`JLabel(String text, int horizontalAlignment)`|指定文本、指定水平对齐的 `JLabel`|
|`JLabel(Icon image)`|指定图像的 `JLabel`|
|`JLabel(Icon image, int horizontalAlignment)`|指定图像、指定水平对齐的 `JLabel`|
|`JLabel(String text, Icon icon, int horizontalAlignment)`|指定图像、指定文本、指定水平对齐的 `JLabel`|

使用示例：

```java
public class JLabelDemo1 {

    public static void main(String[] args) {
        JFrame frame = new JFrame("标签测试");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);

        JLabel label1 = new JLabel("这是一个标签测试程序");
        JLabel label2 = new JLabel("标签不可编辑");
        contentPane.add(label1);
        contentPane.add(label2);
    }
}
```

<img src="images/image-20231222000152858.png" width="300"/>
