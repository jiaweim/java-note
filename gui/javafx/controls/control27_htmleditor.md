# HTMLEditor

2023-07-26, 11:10
****
## 1. 简介

HTMLEditor 提供富文本编辑功能，将 HTML 作为数据模型。如下所示：

![](Pasted%20image%2020230726102356.png)

HTMLEditor 提供了格式化工具栏，该工具栏不能隐藏，可以使用 CSS 定义样式。工具栏提供：

- 使用系统剪切板进行复制、剪切和粘贴
- text alignment
- indent text
- 项目列表和编号列表
- 设置 foreground 和 background
- 段落、标题、字体和字号
- 粗体、斜体、下划线和删除线
- 水平标尺

HTMLEditor 支持 HTML5。

```ad-note
HTMLEditor 的工具栏功能有限，不能添加部分特性。但是，如果用 HTMLEditor 加载包含这些功能的 HTML 文件，通过工具栏可以编辑。例如，在 HTMLEditor 中不能直接创建表格，但是将包含表格的 HTML 文件加载到 HTMLEditor，可以编辑表格数据。
```

HTMLEditor 没有提供加载和保存 HTML 的功能，需要自己实现这些功能。

## 2. 创建 HTMLEditor

HTMLEditor 类在 javafx.scene.web 包中，只提供了一个构造函数：

```java
HTMLEditor editor = new HTMLEditor();
```

## 3. 使用 HTMLEditor

HTMLEditor 类只有三个方法：

- getHtmlText()
- setHtmlText(String htmlText)
- print(PrinterJob job)

getHTMLText() 以 String 返回 HTML 内容。setHTMLText() 设置  HTML 内容。`print()` 打印 HTML 内容。

**示例：** HTMLEditor

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class HTMLEditorTest extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) {
		HTMLEditor editor = new HTMLEditor();
		editor.setPrefSize(600, 300);
		
		TextArea html = new TextArea();
		html.setPrefSize(600, 300);
		html.setStyle("-fx-font-size:10pt; -fx-font-family: \"Courier New\";");
		
		Button htmlToText = new Button("Convert HTML to Text");
		Button textToHtml = new Button("Convert Text to HTML");
		htmlToText.setOnAction(e -> editor.setHtmlText(html.getText()));
		textToHtml.setOnAction(e -> html.setText(editor.getHtmlText()));

		HBox buttons = new HBox(htmlToText, textToHtml);
		buttons.setSpacing(10);
		
		VBox root = new VBox(editor, buttons, html);
		root.setSpacing(10);
		root.setStyle("-fx-padding: 10;" + 
		              "-fx-border-style: solid inside;" + 
		              "-fx-border-width: 2;" +
		              "-fx-border-insets: 5;" + 
		              "-fx-border-radius: 5;" + 
		              "-fx-border-color: blue;");

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Using an HTMLEditor");
		stage.show();
	}
}
```

![|400](Pasted%20image%2020230726110433.png)

## 4. HTMLEditor CSS

HTMLEditor 的 CSS 样式类名默认为 html-editor。

HTMLEditor 使用 Control 的样式，如 padding, borders, background color 等。

可以设置 toolbar 的每个 buttons 的样式。下面是 toolbar 每个 buttons 的样式类名：

- html-editor-cut
- html-editor-copy
- html-editor-paste
- html-editor-align-left
- html-editor-align-center
- html-editor-align-right
- html-editor-align-justify
- html-editor-outdent
- html-editor-indent
- html-editor-bullets
- html-editor-numbers
- html-editor-bold
- html-editor-italic
- html-editor-underline
- html-editor-strike
- html-editor-hr

**示例：** 为 Cut button 设置 graphic

```css
.html-editor-cut {
    -fx-graphic: url("my_html_editor_cut.jpg");
}
```

如果想为 toolbar 的所有  button 和 ToggleButton 应用样式，可以使用 button 和 toggle-button 样式 类名：

```css
/* Set the background colors for all buttons and toggle buttons */
.html-editor .button, .html-editor .toggle-button {
    -fx-background-color: lightblue;
}
```

HTMLEditor 包含两个 ColorPicker，用于设置 background 和 foreground colors。它们的样式类名分别为 html-editor-background 和 html-editor-foreground。

**示例：** 显示 ColorPicker 的 colorLabels

```css
.html-editor-background {
    -fx-color-label-visible: true;
}
.html-editor-foreground {
    -fx-color-label-visible: true;
}
```
