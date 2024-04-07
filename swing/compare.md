# AWT/Swing 对比

2024-04-07 ⭐
***

| 特性          | AWT            | Swing          | SWT/JFace                    |
| ------------- | -------------- | -------------- | ---------------------------- |
| 组件类型      | 少（最少子集） | 多（最大子集） | 丰富                         |
| 组件特性      | 少（最少子集） | 多（可定制）   | 丰富（平台+模拟）            |
| 响应速度      | 快             | 快             | 快                           |
| 内存消耗      | 少             | 多             | 少                           |
| 扩展性        | 无扩展性       | 强             | 不可扩展                     |
| Look And Feel | 不支持         | 出色支持       | 不支持                       |
| 成熟稳定性    | 好             | 好             | Windows 平台高，其它平台不行 |
| 总体性能      | 好             | 一般           | Windows 平台高，其它平台不行 |
| API 模型支持  | 无             | MVC            | MVC                          |
| GUI 库来源    | JRE标准工具集  | JRE 标准工具集 | 程序捆绑                     |
| 启动速度      | 快             | 慢             | 快                           |
| 可视化编程    | 可用           | 支持           | 支持                         |

Swing 为每个 AWT 组件提供了替代品，Swing 组件支持原始 AWT 组件的所有功能，并支持更多功能。

!!! note
    尽管 Swing 组件完全替代了 AWT 组件，但仍然需要理解几个基本的 AWT 概念，如 layout-manager, event-handing, drawing 等。此外，Swing 的所有功能都是建立在 AWT 库之上。

Swing 组件名词基本在 AWT 前面加了一个 `J`，如 Swing 的 `JButton` 替代 AWT 的 `Button`。有个例外，Swing 的 `JComboBox` 替代 `AWT` 的 Choice 组件。

|AWT 组件|对应的 Swing 组件|
|---|---|
|`Button`|`JButton`|
|`Canvas` |`JPanel`|
|`Checkbox`|`JCheckBox`|
|`Checkbox` in `CheckboxGroup`| `JRadioButton` in `ButtonGroup`|
|`Choice` |`JComboBox`|
|`Component` |`JComponent`|
|`Container` |`JPanel`|
|`Label` |`JLabel`|
|`List` |`JList`|
|`Menu` |`JMenu`|
|`MenuBar`| `JMenuBar`|
|`MenuItem`| `JMenuItem`|
|`Panel` |`JPanel`|
|`PopupMenu`| `JPopupMenu`|
|`Scrollbar` |`JScrollBar`|
|`ScrollPane` |`JScrollPane`|
|`TextArea` |`JTextArea`|
|`TextField` |`JTextField`|

除了替换基本组件，Swing 还替换了高级窗口对象。

|AWT 窗口|对应的 Swing 窗口|
|---|---|
|Applet|JApplet|
|Dialog |JDialog|
|FileDialog |JFileChooser|
|Frame |JFrame|
|Window |JWindow|

!!! warning
    `JApplet` 自 Java 9 弃用。

AWT 组件依赖于用户的操作系统来为 Java 程序提供实际的组件，而 Swing 组件都是从 Java runtime 控制：

- AWT 称为重量级方法，或对等方法（本地组件和 AWT 组件一一对应）；
- Swing 组件基本都是轻量级。

Swing 还提供了许多 AWT 没有的组件，如：

- JPasswordField
- JEditorPane 和 JTextPane
- JSpinner
- JToggleButton
- JSlider
- JProgressBar
- JFormattedTextField
- JTable
- JTree
- JToolTip
- JToolBar
- JRadioButtonMenuItem
- JSeparator
- JDesktopPane 和 JInternalFrame
- JOptionPane
- JColorChooser
- JSplitPane
- JTabbedPane
