# Tooltip

- [Tooltip](#tooltip)
  - [简介](#简介)
  - [Tooltip API](#tooltip-api)
  - [示例](#示例)
  - [参考](#参考)

2023-12-22, 16:26⭐
****

## 简介

为 `JComponent` 设置提示文本很容易，使用 `setToolTipText` 方法指定提示字符串即可。当鼠标悬停在组件上，可以显示提示信息。例如，为三个按钮添加提示，只需三行代码：

```java
b1.setToolTipText("Click this button to disable the middle button.");
b2.setToolTipText("This middle button does not react when you click it.");
b3.setToolTipText("Click this button to enable the middle button.");
```

有些组件包含多个部分，如 `JTabbedPane`，在不同位置显示不同提示文本是有意义的。因此，`JTabbedPane` 可以通过 `addTab` 或 `setToolTipTextAt` 为不同 tab 设置不同提示文本。

有些组件没有设置 tooltip 的 API，此时可以自定义：

- 如果组件支持 renderer，则可以在自定义 renderer 上设置 tooltip
  - 在 table 和 tree 中有自定义 renderer 来设置 tooltip 的示例
- 对 `JComponent` 子类，可以通过覆盖其 `getToolTipText(MouseEvent)` 方法来实现

## Tooltip API

设置 tooltip 的 API 基本都属于 `JComponent`，因此大多数 Swing 组件都支持。`JTabbedPane` 等类则根据需要实现了更多功能。一般来说，这些 API 足以指定和现实 tooltips，我们不需要直接和实现类 `JToolTip` 和 `ToolTipManager` 交互。

下表列出了 `JComponent` 中的文本提示 API。

| 方法 | 说明 |
| ---- | ---- |
| `setToolTipText(String)`   | 如果指定字符串不为空，则为该组件注册 tooltip，并在现实时提供指定文本。如果为空，则关闭该组件的 tooltip |
|`String getToolTipText()`|返回 `setToolTipText` 指定的字符串|
|`String getToolTipText(MouseEvent)`|默认返回与 `String getToolTipText()` 相同的值。`JTabbedPane`, `JTable` 和 `JTree` 多构成组件覆盖该方法，返回与 MouseEvent 位置相关的字符串。例如，tabbed-pane 的每个 tab 返回不同提示文本|
|`Point getToolTipLocation(MouseEvent)`|返回 tool-tip 左上角的位置（对应组件的坐标系）。参数是导致 tool-tip 显示的事件。默认返回 `null`，表示由 Swing 系统选择位置|


## 示例

```java
public class HelloTooltip {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tooltip test");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);

        JButton b1 = new JButton("确定");
        JButton b2 = new JButton("取消");
        b1.setToolTipText("这是确定按钮");
        b2.setToolTipText("这是取消按钮");

        contentPane.add(b1);
        contentPane.add(b2);
        frame.setVisible(true);
    }
}
```

<img src="images/image-20231221232657504.png" width="300"/>

## 参考

- https://docs.oracle.com/javase%2Ftutorial%2F/uiswing/components/tooltip.html

