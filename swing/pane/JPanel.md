# JPanel

2024-03-31
@author Jiawei Mao

## 简介

`JPanel` 既是通用容器，也可以替代 `Canvas`。

## 创建 JPanel

```java
public JPanel()
JPanel panel = new JPanel();

public JPanel(boolean isDoubleBuffered)
JPanel panel = new JPanel(false);

public JPanel(LayoutManager manager)
JPanel panel = new JPanel(new GridLayout(2,2));

public JPanel(LayoutManager manager, boolean isDoubleBuffered)
JPanel panel = new JPanel(new GridLayout(2,2), false);
```

- `JPanel` 的默认 layout 为 `FlowLayout`，通过构造函数可以修改默认值；
- `isDoubleBuffered`  是否开启双缓冲。

## 使用 JPanel

`JPanel` 可以作为**通用容器**，也可以作为**自定义组件的基类**。

作为**通用容器**很简单，只需要创建 `JPanel`，设置 layout，然后使用 `add()` 方法添加组件：

```java
JPanel panel = new JPanel();
JButton okButton = new JButton("OK");
panel.add(okButton);
JButton cancelButton = new JButton("Cancel");
panel.add(cancelButton);
```

如果要创建新组件，则可以扩展 JPane 类，并覆盖 `paintComponent(Graphics g)` 方法。

示例：创建一个简单的组件，在其中绘制一个与组件等比例缩放的椭圆。

```java
import javax.swing.*;
import java.awt.*;

public class OvalPanel extends JPanel {
    Color color;

    public OvalPanel() {
        this(Color.BLACK);
    }

    public OvalPanel(Color color) {
        this.color = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.setColor(color);
        g.drawOval(0, 0, width, height);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Oval Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setLayout(new GridLayout(2, 2));
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
            for (int i = 0; i < 4; i++) {
                OvalPanel panel = new OvalPanel(colors[i]);
                frame.add(panel);
            }
            frame.setSize(300, 200);
            frame.setVisible(true);
        });
    }
}
```

<img src="./images/image-20240331105016912.png" alt="image-20240331105016912" style="zoom:80%;" />

`JPanel` 默认不透明，不像 `JComponent` 默认透明。

## Laf

`JPanel` UIResource 相关的属性如下所示：

| Property String  | Object Type |
| ---------------- | ----------- |
| Panel.background | Color       |
| Panel.border     | Border      |
| Panel.font       | Font        |
| Panel.foreground | Color       |
| PanelUI          | String      |
