# GridBagLayout

- [GridBagLayout](#gridbaglayout)
  - [简介](#简介)
  - [示例](#示例)

2023-12-22, 01:31
****

## 简介

`GridBagLayout` 是自由度较高的布局管理器，通过网格划分，一个组件可以占据一个或多个网格。

组件在网格中的位置由 4 个参数控制：

- `gridX` 和 `gridY` 定义组件左上角行和列的位置；
- `gridwidth` 和 `gridheight` 定义组件占据的行数和列数。

`fill` 指定组件不能填满单元格时的行为：

- `GridBagConstraints.NONE`，每个方向都不填充，即保持原状；
- `GridBagConstraints.HORIZONTAL`，水平方向填充；
- `GridBagConstraints.VERTICAL`，垂直方向填充；
- `GridBagConstraints.BOTH`，两个方向都填充。

`anchor` 指定组件大于分配区域时的行为：

- `GridBagConstraints.CENTER`，居中缩小；
- `GridBagConstraints.NORTH`，顶部缩小；
- `GridBagConstraints.NORTHEASY`，左上角缩小；
- `GridBagConstraints`，右侧缩小。

## 示例

```java
public class GridBagLayoutDemo extends JPanel {

    JFrame loginframe;
	
    //用来添加控件到容器中
    public void add(Component c, GridBagConstraints constraints, int x, int y, int w, int h) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        add(c, constraints);
    }

    GridBagLayoutDemo() {
        loginframe = new JFrame("信息管理系统");
        loginframe.setSize(300, 150);
        loginframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //创建网格组布局方式对象
        GridBagLayout lay = new GridBagLayout();
        setLayout(lay);

        loginframe.add(this, BorderLayout.WEST);

        Toolkit kit = Toolkit.getDefaultToolkit();//设置顶层容器框架为居中
        Dimension screenSize = kit.getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;
        loginframe.setLocation(x, y);

        JButton ok = new JButton("确认");
        JButton cancel = new JButton("放弃");
        JLabel title = new JLabel("布局管理器测试窗口");
        JLabel name = new JLabel("用户名");
        JLabel password = new JLabel("密 码");

        final JTextField nameinput = new JTextField(15);
        final JTextField passwordinput = new JTextField(15);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 3;
        constraints.weighty = 4;

        add(title, constraints, 0, 0, 4, 1); //使用网格组布局添加控件
        add(name, constraints, 0, 1, 1, 1);
        add(password, constraints, 0, 2, 1, 1);
        add(nameinput, constraints, 2, 1, 1, 1);
        add(passwordinput, constraints, 2, 2, 1, 1);
        add(ok, constraints, 0, 3, 1, 1);
        add(cancel, constraints, 2, 3, 1, 1);

        loginframe.setResizable(false);
        loginframe.setVisible(true);
    }

    public static void main(String[] args) {
        new GridBagLayoutDemo();
    }
}
```

<img src="images/image-20231222013006860.png" width="300"/>