# TextFlow

2023-07-11, 13:46
****
## 1. 简介

TextFlow 用于显示富文本。富文本由多个 Text nodes 组成，TextFlow 将这些 Text nodes 的文本合并，以单个文本流显示。

Text child 文本中的换行符 '\n' 表示新的段落。文本在 TextFlow 宽度换行。

`Text` 有 position, size 和 wrap 宽度。将 Text 加到 TextFlow 后，这些属性被忽略。Text nodes 在 TextFlow 中按顺序放置，在需要时自动换行。Text 添加到 TextFlow 后，文本可以多行显示，而单独的 Text 只能单行显示。

下图将 TextFlow 作为 root：

![|250](Pasted%20image%2020230711131854.png)

TextFlow 为显示富文本而设计，但也可以显示其它类型 nodes。如 Button, TextField 等，Text 之外的 nodes 使用 prefSize 显示。

```ad-tip
TextFlow 与 FlowPane 类似，只是对 Text 特殊处理了。
```

## 2. 创建 TextFlow

```java
TextFlow tflow1 = new TextFlow ();
```

创建时指定 children:

```java
Text tx1 = new Text("TextFlow layout pane is cool! ");
Text tx2 = new Text("It supports rich text display.");
TextFlow tflow2 = new TextFlow(tx1, tx2);
```

创建后添加 children:

```java
Text tx1 = new Text("TextFlow layout pane is cool! ");
Text tx2 = new Text("It supports rich text display.");
TextFlow tflow3 = new TextFlow();
tflow3.getChildren().addAll(tx1, tx2);
```

**示例：** TextFlow

添加 3 个 Text 到 TextFlow：

- 第 3 个 Text 以 '\n' 开头，表示新的段落
- TextFlow 的 prefWidth 设置为 300px，lineSpacing 设置为 5px

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class TextFlowTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create three Text nodes
        Text tx1 = new Text("TextFlow layout pane is cool! ");
        tx1.setFill(Color.RED);
        tx1.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Text tx2 = new Text("It supports rich text display.");
        tx2.setFill(Color.BLUE);

        Text tx3 = new Text("\nThis is a new paragraph, which was " +
                "created using the \\n newline character.");

        // Create a TextFlow object with the three Text nodes
        TextFlow root = new TextFlow(tx1, tx2, tx3);

        // Set the preferred width and line spacing 
        root.setPrefWidth(300);
        root.setLineSpacing(5);

        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using TextFlow");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230711132800.png)

TextFlow 可以包含其它类型的 node，不限于 Text。

**示例：** 添加 RadioButton, TextField, Button 到 TextFlow

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class TextFlowEmbeddingNodes extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text tx1 = new Text("I, ");

        RadioButton rb1 = new RadioButton("Mr.");
        RadioButton rb2 = new RadioButton("Ms.");
        rb1.setSelected(true);

        ToggleGroup group = new ToggleGroup();
        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);

        TextField nameFld = new TextField();
        nameFld.setPromptText("Your Name");

        Text tx2 = new Text(", acknowledge the receipt of this letter...\n\n" +
                "Sincerely,\n\n");

        Button submitFormBtn = new Button("Submit Form");

        // Create a TextFlow object with all nodes
        TextFlow root = new TextFlow(tx1, rb1, rb2, nameFld, tx2, submitFormBtn);

        // Set the preferred width and line spacing
        root.setPrefWidth(350);
        root.setLineSpacing(5);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
// Set the textAlignment to CENTER
        root.setTextAlignment(TextAlignment.CENTER);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Creating Forms Using TextFlow");
        stage.show();
    }
}
```

![|400](Pasted%20image%2020230711133350.png)

## 3. TextFlow 属性

| 属性            | 类型                            | 说明                                                                                          |
| --------------- | ------------------------------- | --------------------------------------------------------------------------------------------- |
| `lineSpacing`   | `DoubleProperty`                | 指定行间距，默认 0px                                                                          |
| `tabSize`       | `IntegerProperty`               | tab 的大小（space 个数）                                                                      |
| `textAlignment` | `ObjectProperty<TextAlignment>` | TextFlow 中 content 的对齐方式。TextAlignment enum: LEFT, RIGHT, CENTER 和 JUSTIFY，默认 LEFT |

`lineSpacing` 指定行间距。例如：

```java
TextFlow tflow = new TextFlow();
tflow.setLineSpacing(5); // 5px lineSpacing
```

`textAlignment` 指定 TextFlow 总体 content 对齐方式。默认左对齐。

## 4. 设置 Children 约束

TextFlow 不支持为 children 添加约束，margin 也不行。
