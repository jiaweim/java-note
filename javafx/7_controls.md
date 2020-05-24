# 组件

- [组件](#%e7%bb%84%e4%bb%b6)
  - [概述](#%e6%a6%82%e8%bf%b0)
  - [Control](#control)
    - [contextMenu](#contextmenu)
    - [skin](#skin)
    - [tool tip](#tool-tip)

## 概述

用户通过控件（controls）或小部件（widgets）这些图形元素和 GUI 应用程序进行交互。按钮、标签、文本框等都是控件，每个控件有一个特定的类表示。javafx.scene.control 包中包含丰富的控件类。控件类的继承如下：

![controls](images/2019-06-05-16-41-18.png)

所有控件类直接或间接继承自 `Control` 类，该类继承自 `Region`，而 `Region` 继承自 `Parent`。因此，从技术上来说， `Control` 也是 `Parent`。

## Control

`Control` 类有三个可设置属性值：
| 属性        | 类型                          | 说明     |
| ----------- | ----------------------------- | -------- |
| contextMenu | `ObjectProperty<ContextMenu>` | 内容菜单 |
| skin        | `ObjectProperty<Skin<?>>`     | 皮肤     |
| tooltip     | `ObjectProperty<Tooltip>`     | 提示     |

### contextMenu

用于指定控件的内容菜单。内容菜单给出一个选择列表，每个选择代表对控件当前状态的一个操作。
部分控件具有默认的内容菜单，如 `TextField`，右键单击，会显示包含 `Undo`, `Cut`, `Copy` 和 `Paster`操作的内容菜单。

### skin

控件的外观称为皮肤。皮肤由 `Skin` 接口表示。`Control` 类实现了 `Skinnable` 接口，从而让所有的空间都可以使用皮肤。

### tool tip

当鼠标悬停在控件上，控件显示一段短的信息，称之为工具提示信息。该功能由 `Tooltip` 类表示。
