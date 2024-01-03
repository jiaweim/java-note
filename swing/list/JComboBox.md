# JComboBox

- [JComboBox](#jcombobox)
  - [简介](#简介)
    - [创建 JComboBox](#创建-jcombobox)
    - [属性](#属性)
    - [JComboBox cell 渲染](#jcombobox-cell-渲染)
    - [选择 JComboBox 元素](#选择-jcombobox-元素)
    - [事件处理](#事件处理)
  - [不可编辑 combo-box](#不可编辑-combo-box)
  - [combo-box 事件处理](#combo-box-事件处理)
  - [自定义 Renderer](#自定义-renderer)
  - [参考](#参考)


## 简介

`JComboBox` 为用户提供选项，有两种形式：

- 不可编辑（默认），包含按钮、下拉列表和 label
- 可编辑，包含按钮、下拉列表和 text-field，用户可以在 text-field 中输入值，或点击按钮弹出下拉列表

combo-box 只需要少量屏幕空间，就能够提供大量选择。也有其它组件可以提供选择：

- radio-button，适合少量选择，空间有限或选项太多，combo-box 更合适
- list，当选项很多（>20）或需要多选

`JComboBox` 实现包含四部分：

- 用于保存 `JComboBox` 数据的数据模型，由 `ListModel` 接口定义
- 用于绘制 `JComboBox` 元素的 renderer，由 `ListCellRenderer` 接口定义
- 编辑器，用于输入不在预定义数据模型中的选项，由 `ComboBoxEditor` 接口定义
- keystroke 管理器，用于支持通过键盘输入选择 `JComboBox` 元素，由 `KeySelectionManager` 接口定义

`JComboBox` 的许多功能与 `JList` 共享。

调用 `setEditable` 设置为可编辑。编辑功能仅影响所选项目，不会改变列表。

`getSelectedItem` 返回当前选项，如果 combo-box 是可编辑的，则返回选项可能是编辑过的；

可编辑和不可编辑的 combo-box 差异较大，因此下面分开描述。


`JComboBox` 是一个组合组件，允许用户从一个下拉列表中从一组预定义选项进行选择。

`JComboBox` 实现由 4 部分组成：

- 数据模型 `ComboBoxModel`；
- cell 渲染 `ListCellRenderer`；
- 字段编辑 `ComboBoxEditor`；
- 使用键盘选择的模型 `KeySelectionManager`。

### 创建 JComboBox

### 属性

`maximumRowCount`， 弹窗中可见元素个数。

### JComboBox cell 渲染

`JComboBox` cell 渲染使用 `ListCellRenderer` 实现，和 `JList` 相同。

### 选择 JComboBox 元素

`JComboBox` 至少支持三种不同和选择相关的事件。

### 事件处理

JComboBox 的事件分为两种：

- 取得用户选择的项目，使用 `ItemListener`；
- 自定输入完毕后回车，使用 `ActionListener`。



## 不可编辑 combo-box

下面创建一个下图所示的应用：

![](images/2023-12-27-20-55-58.png)


源码：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ComboBoxDemo extends JPanel implements ActionListener {

    JLabel picture;

    public ComboBoxDemo() {
        super(new BorderLayout());
        String[] petStrings = {"Bird", "Cat", "Dog", "Rabbit", "Pig"};

        // 创建 combo-box，选择 index 4 的 Pig
        JComboBox<String> petList = new JComboBox<>(petStrings);
        petList.setSelectedIndex(4);
        petList.addActionListener(this);

        // 配置图片
        picture = new JLabel();
        picture.setFont(picture.getFont().deriveFont(Font.ITALIC));
        picture.setHorizontalAlignment(JLabel.CENTER);
        updateLabel(petStrings[petList.getSelectedIndex()]);
        picture.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 将 JLabel 的首选大小硬编码为最宽图像的宽度和最高图像的高度+边框
        // 真实程序应该根据需要计算
        picture.setPreferredSize(new Dimension(177, 122 + 10));

        add(petList, BorderLayout.PAGE_START);
        add(picture, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    protected void updateLabel(String name) {
        ImageIcon imageIcon = createImageIcon("images/" + name + ".gif");
        picture.setIcon(imageIcon);
        picture.setToolTipText("A drawing of a " + name.toLowerCase());
        if (imageIcon != null) {
            picture.setText(null);
        } else {
            picture.setText("Image not found");
        }
    }

    protected static ImageIcon createImageIcon(String path) {
        URL imgURL = ComboBoxDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> cb = (JComboBox) e.getSource();
        String petName = (String) cb.getSelectedItem();
        updateLabel(petName);
    }

    protected static void createAndShowGUI() {
        JFrame frame = new JFrame("ComboBoxDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JComponent contentPane = new ComboBoxDemo();
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ComboBoxDemo::createAndShowGUI);
    }
}
```

此 combo-box 包含 `String[]` 数组，也可以直接使用 icon 数组。但对 String 以外的类型，需要定义在 combo-box 如何显示。对可编辑 combo-box，还需要自定义编辑器。

上面的代码还为 combo-box 注册了一个 action-listener。

不管使用哪个构造函数创建 combo-box，其内部都使用 `ComboBoxModel` 来管理数据。通过实现 `ComboBoxModel` 接口，可以自定义模型。

在自定义 ComboBoxModel 模型时要小心，combo-box 中修改数据的操作，如 `insertItemAt`，只有实现 MutableComboBoxModel （ComboBoxModel 的子接口）接口时才有效。

另外，即使对不可编辑的 combo-box，确保自定义模型在 combo-box 的数据或状态发生变化时能触发 list-data-event。即使是数据永不可变，combo-box-model 也应该在选择发生变化时触发 `CONTENTS_CHANGED` 事件。实现这些操作的最简单方法是继承 `AbstractListModel`。

## combo-box 事件处理

ComboBoxDemo 示例中注册和实现 action-listener 的代码如下：

```java
public class ComboBoxDemo ... implements ActionListener {
    . . .
        petList.addActionListener(this) {
    . . .
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        String petName = (String)cb.getSelectedItem();
        updateLabel(petName);
    }
    . . .
}
```

action-listener 从 combo-box 获取最新选项，从而获取图片文件名称，然后更新 JLabel 显示的图像。

当用户从 combo-box 选择一个选项，combo-box 会触发 action-event。

## 自定义 Renderer

combo-box 使用 renderer 显示其元素：

- 对不可编辑 combo-box，使用 renderer 显示选择的元素
- 对可编辑 combo-box，使用 editor 显示选择的元素

combo-box 的 renderer 必须实现 `ListCellRenderer` 接口，editor 则需要实现 `ComboBoxEditor` 接口。

默认 renderer 知道如何渲染字符串和 icon。对其它类型，

## 参考

- https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html