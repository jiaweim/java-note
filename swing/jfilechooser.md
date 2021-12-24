# JFileChooser

- [JFileChooser](#jfilechooser)
  - [简介](#简介)
  - [构造函数](#构造函数)
  - [在对话框中显示](#在对话框中显示)
  - [属性](#属性)
  - [过滤器](#过滤器)
    - [文件过滤](#文件过滤)
  - [设置选择模式](#设置选择模式)
  - [返回值](#返回值)

2021-11-16, 22:23
***

## 简介

JFileChooser 用于选择文件，其视图如下：

![](images/2021-11-16-22-29-05.png)

`JFileChooser` 类的实现在 `javax.swing.filechooser` 包中，包括

- `FileFilter`，用于过滤在 `FileView` 中显示的文件；
- `FileSystemView`，是一个抽象类，是 `JFileChooser` 到文件系统的网关，隐藏根分区、文件类型信息以及隐藏文件等信息。

> 不要混淆 `javax.swing.filechooser.FileFilter` 类和 `java.io.FileFilter`。两者虽然功能类似，但是 `java.io.FileFilter` 接口在 Java 1.1 不存在，基于兼容考虑，两者都存在。

## 构造函数

`JFileChooser` 有 6 个构造函数。

```java
public JFileChooser()
```

创建指向用户默认目录的 `JFileChooser`，该默认目录依赖于系统，在 Windows 系统一般是 "My Documents"，在 Unix 一般为用户 home 目录。

```java
public JFileChooser(File currentDirectory)
```

指定起始目录。

```java
public JFileChooser(File currentDirectory, FileSystemView fsv)
```

指定起始目录和 `FileSystemView`。

```java
JFileChooser(String currentDirectoryPath) // 指向某个路径
```

## 在对话框中显示

有三种在 `JDialog` 中显示 `JFileChooser` 的方法：

```java
public int showDialog(Component parentComponent, String approvalButtonText)
public int showOpenDialog(Component parentComponent)
public int showSaveDialog(Component parentComponent)
```

调用上面的方法会将 `JFileChooser` 放到模态 `JDialog` 中，并在父组件中心显示。父组件设置为 `null` 则在屏幕中间显示。

该方法在用户选择或取消操作后才返回。根据选择的按钮不同返回不同值：

- `APPROVE_OPTION`
- `CANCEL_OPTION`
- `ERROR_OPTION`

## 属性

使用不同的 `showDialog()` 方法，`dialogType` 属性会自动设置为三种 `JOptionPane` 中的一个：`OPEN_DIALOG`, `SAVE_DIALOG`, `CUSTOM_DIALOG`。

不过使用 `showDialog()`，则需要根据使用的 dialog 类型设置该属性值。

`controlButtonsAreShown` 属性可用于隐藏 Open, Save 和 Cancel 按钮。

## 过滤器

`JFileChooser` 支持三种过滤文件和目录的方法。前两种涉及 `FileFilter` 类，最后一种涉及隐藏文件。

### 文件过滤

```java
FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG&GIF Images","jpg","gif");
dlg.setFileFilter(filter);
```

使用 `addChoosableFileFilter(FileFilter filter)` 添加过滤器，默认的过滤器依然保留。当没有过滤器时，默认使用 `JFileChooser.getAcceptAllFileFilter()`。

使用 `setFileFilter(FileFilter filter)` 设置过滤器，并作为初始过滤器使用，此时 accept-all-file filter 不可用，可以调用 `setAcceptAllFileFilterUsed(true)` 将其放回来。

使用 `resetChoosableFileFilters()` 重置过滤器。

## 设置选择模式

```java
dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
dlg.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
```


## 返回值
```java
static int APPROVE_OPTION;//选择确认后返回该值
static int CALCEL_OPTION;//选择calcel后返回该值
static int ERROR_OPTION;//发生错误后返回该值。
```