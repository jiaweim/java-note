# Chooser

2023-07-26, 11:43
****
## 1. 简介

JavaFX 提供 `FileChooser` 和 `DirectoryChooser` 类用于显示文件和目录对话框。对话框的样式取决于平台，无法使用 JavaFX 自定义。

它们并不是 JavaFX 控件，不过经常和控件一起使用。

## 2. FileChooser

`FileChooser` 是文件对话框，用于选择打开或保存文件。部分内容，如对话框的标题，初始目录，文件后缀筛选列表等可以在打开对话框之前设置。使用步骤：

1. 创建 `FileChoose` 对象
2. 设置对话框的初识属性
3. 使用 `showXXXDialog()` 方法显示特定类型的文件对话框

### 2.1. 创建 FileChooser

```java
FileChooser fileChooser = new FileChooser();
```

### 2.2. 对话框属性设置

可设置属性有：

- 标题（Title）
- 初始目录（initialDirectory）
- 初始文件名（initialFileName）
- 后缀过滤器（Extension filters）

**title**

FileChooser 的 title 属性为字符串，表示对话框标题：

```java
fileDialog.setTitle("Open Resume");
```

**initialDirectory**

FileChooser 的 initialDirectory 属性为 File 类型，表示打开文件对话框的初识目录：

```java
fileDialog.setInitialDirectory(new File("C:\\"));
```

**initialFileName**

FileChooser 的 `initialFileName` 属性为 String 类型，设置文件的初始名称，一般用在文件保存对话框。

```java
fileDialog.setInitialFileName("untitled.htm");
```

#### 2.2.1. 过滤器

文件对话框过滤器以下拉框的形式显示，一次有一个过滤器处于激活状态，对话框只显示满足过滤器的文件：

- 文件扩展过滤器以 ExtensionFilter 类表示，它是 FileChooser 的静态内部类
- FileChooser 的 `getExtensionFilters()` 返回 `ObservableList<FileChooser.ExtensionFilter>`，包含 FileChooser 的所有文件扩展过滤器

扩展过滤器包含两个属性：

- 描述信息
- `*.<extension>` 格式的文件扩展列表

添加文件后缀过滤器：

```java
// Add three extension filters
fileDialog.getExtensionFilters().addAll(
    new ExtensionFilter("HTML Files", "*.htm", "*.html"),
    new ExtensionFilter("Text Files", "*.txt"),
    new ExtensionFilter("All Files", "*.*"));
```

默认激活第一个过滤器。通过如下方法设置激活的过滤器：

```java
fileDialog.setSelectedExtensionFilter(fileDialog.getExtensionFilters().get(1));
```

selectedExtensionFilter 属性包含用户选择的过滤器，`getSelectedExtensionFilter()` 返回选择的过滤器。

### 2.3. 显示对话框

`FileChooser` 支持三种对话框：

- 打开单个文件的对话框: `showOpenDialog(Window ownerWindow)`
- 打开多个文件的对话框: `showOpenMultipleDialog(Window ownerWindow)`
- 保存文件的对话框: `showSaveDialog(Window ownerWindow)`

三个方法在关闭对话框后返回。

`ownerWindow` 可以设置为 null；设置 ownerWindow 后，显示文件对话框时阻止对 ownerWindow 的输入。

`showOpenDialog()` 和 `showSaveDialog()` 方法返回 `File` 对象，没有选择文件时返回 null。

`showOpenMultipleDialog()` 则返回 `List<File>`，即所有选择的文件，没有选择文件时返回 null。

```java
// Show a file open dialog to select multiple files
List<File> files = fileDialog.showOpenMultipleDialog(primaryStage);
if (files != null) {
    for(File f : files) {
        System.out.println("Selected file :" + f);
    }
} else {
    System.out.println("No files were selected.");
}
```

FileChooser 的 selectedExtensionFilter 属性包含关闭文件对话框后选择的扩展过滤器：

```java
import static javafx.stage.FileChooser.ExtensionFilter;
...
// Print the selected extension filter description
ExtensionFilter filter = fileDialog.getSelectedExtensionFilter();
if (filter != null) {
    System.out.println("Selected Filter: " + filter.getDescription());
} else {
    System.out.println("No extension filter selected.");
}
```

### 2.4. FileChooser 示例

open 和 save 文件对话框示例：

- 显示包含 3 个 Buttons 和 1 个 HTMLEditor 的窗口
- 使用 Open button 打开 HTML 文件
- 使用 Save button 保存 HTMLEditor 内容到文件
- 在 Save 对话框中选择已有文件，会覆盖其内容

```java
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static javafx.stage.FileChooser.ExtensionFilter;

public class FileChooserTest extends Application {

    private Stage primaryStage;
    private HTMLEditor resumeEditor;
    private final FileChooser fileDialog = new FileChooser();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage; // Used in file dialogs later
        resumeEditor = new HTMLEditor();
        resumeEditor.setPrefSize(600, 300);

        // Filter only HTML files
        fileDialog.getExtensionFilters()
                .add(new ExtensionFilter("HTML Files", "*.htm", "*.html"));

        Button openBtn = new Button("Open");
        Button saveBtn = new Button("Save");
        Button closeBtn = new Button("Close");
        openBtn.setOnAction(e -> openFile());
        saveBtn.setOnAction(e -> saveFile());
        closeBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(20, openBtn, saveBtn, closeBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox root = new VBox(resumeEditor, buttons);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Editing Resume in HTML Format");
        stage.show();
    }

    private void openFile() {
        fileDialog.setTitle("Open Resume");
        File file = fileDialog.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            // Read the file and populate the HTMLEditor		
            byte[] resume = Files.readAllBytes(file.toPath());
            resumeEditor.setHtmlText(new String(resume));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        fileDialog.setTitle("Save Resume");
        fileDialog.setInitialFileName("untitled.htm");
        File file = fileDialog.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            // Write the HTML contents to the file. Overwrite the existing file.
            String html = resumeEditor.getHtmlText();
            Files.write(file.toPath(), html.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

![](Pasted%20image%2020230726113803.png)

## 3. DirectoryChooser

DirectoryChooser 实现目录对话框功能，包含 2 个属性：

- title
- initialDirectory

title 属性为 String 类型，为目录对话框的标题。

initialDirectory 属性为 File 类型，指定目录对话框的初始目录。

DirectoryChooser 的 `showDialog(Window ownerWindow)` 方法打开目录对话框：

- 对话框打开后，可以选择一个目录，或关闭对话框
- 返回 File，即选择的目录，没有选择目录返回 null

该方法阻塞知道对话框关闭。如果指定 `ownerWindow`，在显示对话框时阻塞所有对 ownerWindow 的输入。ownerWindow 可以为 null。

**示例：** 创建，设置和显示目录对话框

```java
DirectoryChooser dirDialog = new DirectoryChooser();

// Configure the properties
dirDialog.setTitle("Select Destination Directory");
dirDialog.setInitialDirectory(new File("c:\\"));

// Show the directory dialog
File dir = dirDialog.showDialog(null);
if (dir != null) {
    System.out.println("Selected directory: " + dir);
} else {
    System.out.println("No directory was selected.");
}
```

