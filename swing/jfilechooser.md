# JFileChooser

2024-01-18⭐
@author Jiawei Mao
***
## 简介

`JFileChooser` 用于选择文件，其视图如下：

<img src="images/2021-11-16-22-29-05.png" alt="|500" style="zoom:67%;" />

`JFileChooser` 类的实现在 `javax.swing.filechooser` 包中，包括

- `FileFilter`，用于过滤在 `FileView` 中显示的文件；
- `FileView`，控制如何列出目录和文件；
- `FileSystemView`，是一个抽象类，是 `JFileChooser` 到文件系统的网关，隐藏根分区、文件类型信息以及隐藏文件等信息。

> [!WARNING]
>
> 不要混淆 `javax.swing.filechooser.FileFilter` 类和 `java.io.FileFilter`。两者虽然功能类似，但是 `java.io.FileFilter` 接口在 Java 1.1 不存在，基于兼容考虑，两者都存在。

## 创建 JFileChooser

`JFileChooser` 有 6 个构造函数。

- 默认构造函数

```java
public JFileChooser()

JFileChooser fileChooser = new JFileChooser();
```

创建指向用户默认目录的 `JFileChooser`，默认目录依赖于系统，在 Windows 系统一般是 "My Documents"，在 Unix 一般为用户 home 目录。

- 指定初始目录，通过 `File` 或 `String` 参数指定初始目录

```java
public JFileChooser(File currentDirectory)

File currentDirectory = new File("."); // starting directory of program
JFileChooser fileChooser = new JFileChooser(currentDirectory);

public JFileChooser(String currentDirectoryPath)

String currentDirectoryPath = "."; // starting directory of program
JFileChooser fileChooser = new JFileChooser(currentDirectoryPath);
```

- 指定 `FileSystemView` 来自定义操作系统顶层目录结构

```java
public JFileChooser(FileSystemView fileSystemView)
JFileChooser fileChooser = new JFileChooser(fileSystemView);

public JFileChooser(File currentDirectory, FileSystemView fsv)
FileSystemView fileSystemView = new SomeFileSystemView(...);
JFileChooser fileChooser = new JFileChooser(currentDirectory, fileSystemView);

public JFileChooser(String currentDirectoryPath, FileSystemView fileSystemView)
JFileChooser fileChooser = new JFileChooser(currentDirectoryPath, fileSystemView);
```
## 使用 JFileChooser

创建 `JFileChooser` 后，可以将其放在任何容器中，因为它是一个 `JComponent`。但是在弹窗之外的地方使用 `JFileChooser` 有点奇怪。

**示例：** 直接将 `JFileChooser` 添加到界面

```java
import javax.swing.*;
import java.awt.*;

public class FileSamplePanel{
    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("JFileChooser Popup");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            final JLabel directoryLabel = new JLabel(" ");
            directoryLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
            frame.add(directoryLabel, BorderLayout.NORTH);

            final JLabel filenameLabel = new JLabel(" ");
            filenameLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
            frame.add(filenameLabel, BorderLayout.SOUTH);

            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setControlButtonsAreShown(false);
            frame.add(fileChooser, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
```

<img src="images/Pasted image 20240118214316.png" style="zoom: 67%;" />

### 添加 ActionListener

`JFileChooser` 支持添加 `ActionListener` 来监听确认或取消操作。双击文件触发确认，按 Escape 触发取消。

通过接收到的 `ActionEvent` 的 action-command 确认事件类型。action-command 有两个值：

- `JFileChooser.APPROVE_SELECTION`
- `JFileChooser.CANCEL_SELECTION`

继续完善上例，使得用户选择文件后，`directoryLabel` 和 `filenameLabel` 的值随之更新。

```java
ActionListener actionListener = actionEvent -> {
    JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
    String command = actionEvent.getActionCommand();
    if (command.equals(JFileChooser.APPROVE_SELECTION)) {
        File selectedFile = theFileChooser.getSelectedFile();
        directoryLabel.setText(selectedFile.getParent());
        filenameLabel.setText(selectedFile.getName());
    } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
        directoryLabel.setText(" ");
        filenameLabel.setText(" ");
    }
};
fileChooser.addActionListener(actionListener);
```
### 在对话框中显示 JFileChooser

`JFileChooser` 一般与模态 `JDialog` 一起使用。有三种在 `JDialog` 中显示 `JFileChooser` 的方法，对应不同的按钮名称：

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

执行与上例相同的任务：

```java
JFileChooser fileChooser = new JFileChooser(".");
int status = fileChooser.showOpenDialog(null);
if (status == JFileChooser.APPROVE_OPTION) {
    File selectedFile = fileChooser.getSelectedFile();
    directoryLabel.setText(selectedFile.getParent());
    filenameLabel.setText(selectedFile.getName());
} else if (status == JFileChooser.CANCEL_OPTION) {
    directoryLabel.setText(" ");
    filenameLabel.setText(" ");
}
```

此时检查值，从 String 类型，转换为 `JFileChooser.APPROVE_OPTION` 的 int 类型。
## JFileChooser 属性

理解 JFileChooser 的使用方法后，就可以通过修改属性定制其行为和外观。

| 属性 | 类型 | 权限 |
| ---- | ---- | ---- |
|`acceptAllFileFilter` |`FileFilter` |Read-only|
|`acceptAllFileFilterUsed` |`boolean` |Read-write bound|
|`accessibleContext` |`AccessibleContext` |Read-only|
|`accessory` |`JComponent` |Read-write bound|
|actionListeners |ActionListener[]| Read-only|
|`approveButtonMnemonic` |char|Read-write bound|
|approveButtonText|String|Read-write bound|
|`approveButtonToolTipText` |String |Read-write bound|
|choosableFileFilters|FileFilter[]|Read-only|
|`controlButtonsAreShown` |boolean|Read-write bound|
|currentDirectory|File|Read-write bound|
|`dialogTitle` |`String` |Read-write bound|
|`dialogType` |`int` |Read-write bound|
|`directorySelectionEnabled` |boolean|Read-only|
|dragEnabled|boolean|Read-write|
|`fileFilter` |FileFilter|Read-write bound|
|`fileHidingEnabled` |boolean|Read-write bound|
|`fileSelectionEnabled` |boolean|Read-only|
|`fileSelectionMode` |int|Read-write bound|
|`fileSystemView` |`FileSystemView` |Read-write bound|
|`fileView` |`FileView` |Read-write bound|
|`multiSelectionEnabled` |boolean|Read-write bound|
|`selectedFile` |`File` |Read-write bound|
|`selectedFiles` |`File[]` |Read-write bound|
|UI|FileChooserUI|Read-only|
|UIClassID|String|Read-only|

使用不同的 `showDialog()` 方法，`dialogType` 属性被自动设置为三种 `JOptionPane` 中的一个：

- `OPEN_DIALOG`
- `SAVE_DIALOG`
- `CUSTOM_DIALOG`

如果不使用 `showDialog()`，则需要根据使用的 dialog 类型手动设置该属性值。

`controlButtonsAreShown` 属性可用于隐藏 Open, Save 和 Cancel 按钮。

### 过滤器

`JFileChooser` 支持三种过滤文件和目录的方法。前两种涉及 `FileFilter` 类，最后一种涉及隐藏文件。

`FileFilter` 是一个抽象类，用于确定 `File` 对象是否满足过滤条件。除了过滤机制，`FileFilter` 还提供描述和名称信息。其定义如下：

```java
public abstract class FileFilter {
    protected FileFilter() {}
    public abstract boolean accept(File f);
    public abstract String getDescription();
}
```

**示例：** 根据文件扩展名过滤

```java
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtensionFileFilter extends FileFilter{
    String description;
    String[] extensions;

    public ExtensionFileFilter(String description, String extension) {
        this(description, new String[]{extension});
    }

    public ExtensionFileFilter(String description, String[] extensions) {
        if (description == null) {
            // Since no description, use first extension and # of extensions as description
            this.description = extensions[0] + "{ " + extensions.length + "} ";
        } else {
            this.description = description;
        }
        // Convert array to lowercase
        // Don't alter original entries
        this.extensions = extensions.clone();
        toLower(this.extensions);
    }

    private void toLower(String[] array) {
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = array[i].toLowerCase();
        }
    }

    public String getDescription() {
        return description;
    }

    public boolean accept(File file) {
        if (file.isDirectory()) { //如果是目录，直接接受
            return true;
        } else { // 否则，必须匹配所提供的扩展名数组中的一个
            String path = file.getAbsolutePath().toLowerCase();
            for (String extension : extensions) {
                if ((path.endsWith(extension) &&
                        (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }
} 
```

创建好过滤器，将其与 `JFileChooser` 关联。

- 如果只是想多一个选择，而不是替换初始默认选项，可以调用：

```java
public void addChoosableFileFilter(FileFilter filter)
```

这将保持选中默认的 accept-all-files 过滤器。

- 如果希望替换默认过滤器，则调用

```java
public void setFileFilter(FileFilter filter)
```

例如，添加两个过滤器：

```java
FileFilter jpegFilter = new ExtensionFileFilter(null, new String[]{ "JPG", "JPEG"} );
fileChooser.addChoosableFileFilter(jpegFilter);
FileFilter gifFilter = new ExtensionFileFilter("gif", new String[]{ "gif"} );
fileChooser.addChoosableFileFilter(gifFilter);
```

当没有文件过滤器与 JFileChooser 关联，默认使用 `JFileChooser.getAcceptAllFileFilter()`。

```ad-tip
调用 `setFileFilter()` 会导致 accept-all-filter 不可用，调用 `setAcceptAllFileFilterUsed(true)` 可以重新添加回来，另外，调用 `resetChoosableFileFilters()`  可以重置过滤器列表。
```

另外还有一个隐含的过滤器，即 `JFileChooser` 默认不显示系统隐藏文件。将 `fileHidingEnabled` 属性设置为 false 可显示隐藏文件：

```java
aFileChooser.setFileHidingEnabled(false);
```

### 选择模式

`JFileChooser` 支持三种选择模式：

- 只选择文件
- 只选择目录
- 同时选择文件和目录

`fileSelectionMode` 属性设置选择器的模式。可用设置由三个 JFileChooser 常量指定：

- `FILES_ONLY`
- `DIRECTORIES_ONLY`
- `FILES_AND_DIRECTORIES`

默认值为 `FILES_ONLY`。修改模式：

```java
public void setFileSelectionMode(int newMode)
```

另外，可以通过两个 read-only 属性 `fileSelectionEnabled` 和 `directorySelectionEnabled` 确定选择模式。

### 附加面板

`JFileChooser` 支持添加附件组件。该组件可以增强选择器的功能，如预览图像或文档，或播放音频文件。为了响应文件选择的变化，附件组件需要实现 `JFileChooser` 的 `PropertyChangeListener`。当 `JFileChooser` 的 `SELECTED_FILE_CHANGED_PROPERTY` 属性变化，附件随之变化。例如，添加一个图片预览附件：

```java
fileChooser.setAccessory(new LabelAccessory(fileChooser));
```

<img src="images/Pasted image 20240119101404.png" style="zoom:67%;" />

这里使用将图像设置为 `JLabel` 的 icon 实现预览。完整代码：

```java
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class LabelAccessory extends JLabel implements PropertyChangeListener{
    private static final int PREFERRED_WIDTH = 125;
    private static final int PREFERRED_HEIGHT = 100;

    public LabelAccessory(JFileChooser chooser) {
        setVerticalAlignment(JLabel.CENTER);
        setHorizontalAlignment(JLabel.CENTER);
        chooser.addPropertyChangeListener(this);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
    }

    public void propertyChange(PropertyChangeEvent changeEvent) {
        String changeName = changeEvent.getPropertyName();
        if (changeName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            File file = (File) changeEvent.getNewValue();
            if (file != null) {
                ImageIcon icon = new ImageIcon(file.getPath());
                if (icon.getIconWidth() > PREFERRED_WIDTH) {
                    icon = new ImageIcon(icon.getImage().getScaledInstance(
                            PREFERRED_WIDTH, -1, Image.SCALE_DEFAULT));
                    if (icon.getIconHeight() > PREFERRED_HEIGHT) {
                        icon = new ImageIcon(icon.getImage().getScaledInstance(
                                -1, PREFERRED_HEIGHT, Image.SCALE_DEFAULT));
                    }
                }
                setIcon(icon);
            }
        }
    }
}
```

### 使用 FileSystemView



## 返回值
```java
static int APPROVE_OPTION;//选择确认后返回该值
static int CALCEL_OPTION;//选择calcel后返回该值
static int ERROR_OPTION;//发生错误后返回该值。
```