# JOptionPane

## 简介

`JOptionPane` 提供了标准对话框实现。

`JOptionPane` 提供了 4 个静态方法展示单条信息：

- `showMessageDialog`，显示一条信息，并等到用户单击 OK
- `showConfirmDialog`，显示信息并获得确认结果（如 Ok/Cancel）
- `showOptionDialog`，显示消息并从一组选项中获取用户选择
- `showInputDialog`，显示消息并获取一行用户输入

典型的对话框包括：

- icon
- 消息
- 一个或多个选项按钮

**输入对话框**还有一个用于用户输入的附加组件，可以是 text-field 或 combo-box 等。

### icon

对话框的具体布局和标准消息类型的 icon 取决于 Laf。左侧的 icon 取决于消息类型：

- `ERROR_MESSAGE`
- `INFORMATION_MESSAGE`
- `WARNING_MESSAGE`
- `QUESTION_MESSAGE`
- `PLAIN_MESSAGE`

其中 `PLAIN_MESSAGE` 没有 icon。每种对话框类型还提供了自定义 icon 的方法。

### message

每个



**示例：** 创建并显示标准对话框

```java
JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be green.");
```

![](images/2023-12-28-22-13-17.png)