# Control

2023-07-17, 14:50
@author Jiawei Mao
****
## 1. 简介

GUI 应用执行三个任务：

- 通过输入设备接收用户输入，如鼠标、键盘等
- 处理输入（根据输入执行操作）
- 显示输出

UI 提供了一种在应用和用户之间信息的方法。用户通过控件（*controls*）或小部件（*widgets*）等图形元素和 GUI 应用程序进行交互。按钮、标签、文本框等都是控件。

## 2. Control 类图

JavaFX 中的每个控件都有一个对应的类。空间类在 `javafx.scene.control` 包中。所有控件类直接或间接继承自 `Control` 类，该类继承自 `Region`，而 `Region` 继承自 `Parent`。因此，从技术上来说， `Control` 也是 `Parent`。因此，前面讨论的 `Parent` 相关性质也适用于 `Control`。

Parent 可以包含 children，control 同样可以由多个 children 组成，只是 Control 没有公开 getChildren() 方法，所以不能添加 child。

Control 的 `getChildrenUnmodifiable()` 返回其内部 children，为 `ObservableList<Node>` 类型。

Control 的部分类图如下：

![](images/Pasted%20image%2020230717144029.png)

## 3. Control 属性

Control 是所有控件的基类。它声明了三个属性：

| 属性        | 类型                          | 说明     |
| ----------- | ----------------------------- | -------- |
| contextMenu | `ObjectProperty<ContextMenu>` | 内容菜单 |
| skin        | `ObjectProperty<Skin<?>>`     | 皮肤     |
| tooltip     | `ObjectProperty<Tooltip>`     | 提示     |

### 3.1. contextMenu

`contextMenu` 指定控件的内容菜单。内容菜单给出一个选择列表，每个选择代表对控件当前状态的一个操作。

部分控件包含默认内容菜单，如 `TextField`，右键单击，会显示包含 `Undo`, `Cut`, `Copy` 和 `Paster` 操作的内容菜单。

JavaFX 目前还不支持自定义 context-menu。包含默认 context-menu 的控件，其 `contextMenu` 依然为 null。设置 `contextMenu` 属性会替换默认 context-menu。

并非所有控件都有 context-menu。例如，Button 不适合使用 context-menu。

### 3.2. skin

控件的外观称为皮肤。皮肤由 `Skin` 接口表示。`Control` 类实现了 `Skinnable` 接口，从而让所有的控件都可以使用皮肤。

Control 的 skin 属性指定 control 的自定义皮肤。开发新的 skin 并不容易。大多数时候使用 CSS 定义控件外观已经足够。

### 3.3. tool tip

当鼠标悬停在控件上，控件显示一段短的信息，称之为工具提示信息（*tool tip*）。该功能由 `Tooltip` 类表示。
