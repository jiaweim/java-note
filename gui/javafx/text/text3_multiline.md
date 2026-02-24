# 多行文本显示

2023-07-26, 15:23
****
`Text` 可以显示多行文本：

- 碰到换行符 '\n' 自行换行
- `Text` 包含一个 `wrappingWidth` 属性，默认为 0.0。该值为像素值，而非字符数。当该值大于0，则每行文本在指定像素宽度换行

`lineSpacing` 属性指定两行之间的距离，默认为 0.0px。

`textAlignment` 属性指定文本水平对齐方式。最宽的文本定义外框的宽度，该属性对单行文本无效。

`TextAlignment` enum 包含如下几个选项.

- `LEFT`, 左对齐，默认值
- `RIGHT`, 右对齐
- `CENTER`, 居中
- `JUSTIFY`，两端对齐

**示例：** 多个 Text

创建 3 个多行 Text，每个 Text 的文本相同，包含三个换行符

- 第一个 Text 左对齐，lineSpacing=5px
- 第二个 Text 右对齐，lineSpacing 采用默认 0px
- 第三个 Text 使用 wrappingWidth=100px，故在 100px 宽度自动换行

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MultilineText extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        String text = "Strange fits of passion have I known: \n" +
                "And I will dare to tell, \n" +
                "But in the lover's ear alone, \n" +
                "What once to me befell.";

        Text t1 = new Text(text);
        t1.setLineSpacing(5);

        Text t2 = new Text(text);
        t2.setTextAlignment(TextAlignment.RIGHT);

        Text t3 = new Text(text);
        t3.setWrappingWidth(100);

        HBox root = new HBox(t1, t2, t3);
        root.setSpacing(20);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Multiline Text Nodes");
        stage.show();
    }
}
```

![](Pasted%20image%2020230726152313.png)
