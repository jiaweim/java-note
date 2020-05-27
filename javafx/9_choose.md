# Chooser

- [Chooser](#chooser)
  - [概述](#概述)
  - [FileChooser](#filechooser)
    - [创建 FileChooser](#创建-filechooser)
    - [对话框属性设置](#对话框属性设置)
      - [标题](#标题)
      - [初始目录](#初始目录)
      - [初始文件名](#初始文件名)
      - [添加过滤器](#添加过滤器)
    - [显示对话框](#显示对话框)

## 概述

JavaFX 提供 `FileChooser` 和 `DirectoryChooser` 类用于显示文件和目录选择对话框。对应的对话框的样式取决于平台，无法使用 JavaFX 自定义。

这两个并不是 JavaFX 组件，不过它们经常和组件一起使用。

## FileChooser

`FileChooser` 文件对话框，用于选择打开或保存文件。部分内容，如对话框的标题，初始目录，文件后缀筛选列表等都可以预先配置。使用基本步骤：

1. 创建 `FileChoose` 对象
2. 设置属性
3. 使用 `showXXXDialog()` 方法显示特定文件对话框

### 创建 FileChooser

```java
FileChooser fileChooser = new FileChooser();
```

### 对话框属性设置

可设置属性有：

- 标题（Title）
- 初始目录（initialDirectory）
- 初始文件名（initialFileName）
- 后缀过滤器（Extension filters）

#### 标题

标题就是一个字符串，设置方法：

```java
fileDialog.setTitle("Open Resume");
```

#### 初始目录

初始目录是 `File` 对象，设置方法：

```java
fileDialog.setInitialDirectory(new File("C:\\"));
```

#### 初始文件名

`initialFileName` 属性用于设置文件的初始名称，一般用在文件保存对话框。

初始文件名为字符串对象，设置方法：

```java
fileDialog.setInitialFileName("untitled.htm");
```

#### 添加过滤器

文件对话框过滤器以下拉框的形式显示，一次有一个过滤器处于激活状态，对话框只显示满足过滤器的文件。添加文件后缀过滤器：

```java
// Add three extension filters
fileDialog.getExtensionFilters().addAll(
    new ExtensionFilter("HTML Files", "*.htm", "*.html"),
    new ExtensionFilter("Text Files", "*.txt"),
    new ExtensionFilter("All Files", "*.*"));
```

默认第一个过滤器为激活状态。通过如下方法设置激活的过滤器：

```java
fileDialog.setSelectedExtensionFilter(fileDialog.getExtensionFilters().get(1));
```

`getSelectedExtensionFilter()`
返回选择的过滤器。

### 显示对话框

`FileChooser` 可以显示三种对话框：

- 打开单个文件的对话框（showOenDialog(Window ownerWindow)）
- 打开多个文件的对话框（showOpenMultipleDialog(Window ownerWindow)）
- 保存文件的对话框（showSaveDialog(Window ownerWindow)）

三个方法在关闭对话框后返回。
`ownerWindow` 可以为 null，如果是设置了，则在显示文件对话框时，该窗口被阻挡。

`showOpenDialog()` 和 `showSaveDialog()` 方法返回 `File` 对象或者 null 如果没有选择任何文件。  
`showOpenMultipleDialog()` 则返回 `List<File>`，即所有选择的文件，如果没有选择任何文件返回 null.
