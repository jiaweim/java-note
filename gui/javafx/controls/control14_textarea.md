# TextArea

2023-07-24, 21:09
****
## 1. 简介

`TextArea` 是一个文本输入控件。`TextArea` 继承 [TextInputControl](control11_text_input.md)，用于输入多行文本。

- 对单行文本，请使用 [TextField](control12_textfield.md)
- 对富文本，请使用 HTMLEditor

与 TextField 不同，TextArea 保留文本中的 newline 和 tab 字符。newline 开始一个新的段落，如下所示：

![|400](Pasted%20image%2020230724203619.png)

创建 TextArea 默认为空文本，也可以指定初始文本：

```java
// Create a TextArea with an empty string as its initial text
TextArea resume1 = new TextArea();

// Create a TextArea an initial text
TextArea resume2 = new TextArea("Years of Experience: 19");
```

TextArea 的 text 属性存储实际文本。为 text 添加 ChangeListener 可监听 TextArea 的内容变化。

大多时候使用 setText(String newText) 设置 TextArea 的文本，使用 getText() 查询 TextArea 包含的文本。

TextArea 添加了如下属性：

- prefColumnCount：指定 TextArea 宽度，默认 32，即可以显示 32 个大写 W
- prefRowCount：指定 TextArea 高度，默认 10
- scrollLeft
- scrollTop
- wrapText

设置 TextArea 宽度：

```java
// Set the preferred column count to 80
resume1.setPrefColumnCount(80);
```

设置 TextArea 高度：

```java
// Set the preferred row count to 20
resume.setPrefColumnCount(20);
```

当 text 超过行数或列数，TextArea 自动显示水平和垂直滚动条。

和 TextField 一样，TextArea 提供了默认的 contextMenu。

scrollLeft 和 scrollTop 属性是 text 相对左侧和顶部滚动的像素数。下面将其设置为 30px：

```java
// Scroll the resume text by 30px to the top and 30 px to the left
resume.setScrollTop(30);
resume.setScrollLeft(30);
```

TextArea 默认在换行符处换行，开始新的段落。当 text 超过 TextArea 宽度，TextArea 默认不会换行。`wrapText` 属性指定该换行行为，默认为 false。设置方法：

```java
// Wrap the text if needed
resume.setWrapText(true);
```

TextArea 的 getParagraphs() 方法返回所有段落的 unmodifiable list。list 的每个元素为一个段落，以 CharSequence 类表示。返回的段落不包含换行符。

**示例：** 输出 TextArea 的段落信息

```java
ObservableList<CharSequence> list = resume.getParagraphs();
int size = list.size();
System.out.println("Paragraph Count:" + size);

for(int i = 0; i < size; i++) {
    CharSequence cs = list.get(i);
    System.out.println("Paragraph #" + (i + 1) + ", Characters=" + cs.length());
    System.out.println(cs);
}
```

**示例：** TextArea 的使用

```java
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextAreaTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TextField title = new TextField("Luci");
        title.setPromptText("Your poem title goes here");

        TextArea poem = new TextArea();
        poem.setPromptText("Your poem goes here");
        poem.setPrefColumnCount(20);
        poem.setPrefRowCount(10);
        poem.appendText("I told her this: her laughter light\n" +
                "Is ringing in my ears:\n" +
                "And when I think upon that night\n" +
                "My eyes are dim with tears.");

        Button printBtn = new Button("Print Poem Details");
        printBtn.setOnAction(e -> print(poem));

        VBox root = new VBox(new Label("Title:"), title,
                new Label("Poem:"), poem, printBtn);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using TextArea Controls");
        stage.show();
    }

    public void print(TextArea poem) {
        System.out.println("Poem Length: " + poem.getLength());
        System.out.println("Poem Text:\n" + poem.getText());
        System.out.println();

        ObservableList<CharSequence> list = poem.getParagraphs();
        int size = list.size();
        System.out.println("Paragraph Count:" + size);
        for (int i = 0; i < size; i++) {
            CharSequence cs = list.get(i);
            System.out.println("Paragraph #" + (i + 1) +
                    ", Characters=" + cs.length());
            System.out.println(cs);
        }
    }
}
```

![|350](Pasted%20image%2020230724210209.png)

## 2. CSS

TextArea 的 CSS 样式类名默认为 text-area。没有添加其它 CSS 属性。

TextArea 包含两个子结构：scroll-pane 和 content，分别为 ScrollPane 和 Region 类型。

- 当文本超出高度或宽度，scroll-pane 是显示的 ScrollPane
- content 是显示 text 的区域

下面将水平和垂直滚动条设置为 always，即始终显示滚动条。为 content 区域设置 10px padding：

```css
.text-area > .scroll-pane {
    -fx-hbar-policy: always;
    -fx-vbar-policy: always;
}

.text-area .content {
    -fx-padding: 10;
}
```

```ad-warning
为 scroll-pane 子结构设置的 scrollbar 策略可能被忽略。
```
