# GUI 设计

- [GUI 设计](#gui-设计)
  - [GUI Designer 基础](#gui-designer-基础)
    - [GUI Designer 文件](#gui-designer-文件)
    - [绑定类](#绑定类)
    - [GUI Designer 输出选项](#gui-designer-输出选项)
  - [自定义组件面板](#自定义组件面板)

***

## GUI Designer 基础

IntelliJ IDEA 中的 GUI Designer 能够使用 Swing 组件创建 GUI。该工具可以帮助加速最常见任务：创建要在顶级容器（如 `JFrame`）中使用的对话框和控件。

使用 GUI Designer 设计的是 pane，而不是 `JFrame`。

使用 GUI Designer 创建的对话框和组件 group 可以与直接使用 Java 代码创建的组件共存。

GUI Designer 有如下限制：

- GUI Designer 不支持非 Swing 组件
- GUI Designer 不创建 JFrame，也不创建菜单

### GUI Designer 文件

在设计时，GUI 信息存储在扩展名为 `.form` 的特殊文件中，该文件是符合特定 schema 的 XML 文件。form 文件可以与 java 源文件关联。

### 绑定类

IntelliJ IDEA 在创建新的 form 时，会自动创建一个 Java 类。创建的 form 通过 **bind to class** 属性与 Java 类绑定。当添加新的组件到标记表单，会自动在 Java 源文件中插入该组件的字段（JPanel 和 JLabel 例外）。字段名称对应组件的 **field name** 属性。

### GUI Designer 输出选项

IntelliJ IDEA 的 GUI Designer 提供两个输出选项：

- **Runtime classes**，编译项目时生成的运行时类。此时会绕过 Java 源代码的中间步骤。
- **Java Source code**，UI 组件的所有代码都会生成到绑定的类。

## 自定义组件面板
