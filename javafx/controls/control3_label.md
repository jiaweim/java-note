# Label

2023-07-17, 15:55
****
## 1. 简介

`Label` 类表示标签控件，用于给其它控件提供名称或描述信息。它可以显示 text 和 icon。

`Label` 无法聚焦，即，无法通过 Tab 键将焦点设置到 `Label`，`Label` 组件不会生成任何有用事件。

`Label` 的 `labelFor` 属性非常有用，该属性为 `ObjectProperty<Node>` 类型，为 scene graph 的其它控件设置。`Label` 组件可以有助记符，其助记符解析默认关闭，当按下对应的助记符，焦点移到为 Label 设置的 `labelFor`节点上。

**示例：** 创建一个 `TextField`和`Label`，为 `Label` 设置助记符，启用助记符解析，并将其 `labelFor`属性设置为 `TextField`，当按下 Alt+F 键，焦点将转移到 `TextField`

```java
TextField fNameFld = new TextField();
Label fNameLbl = new Label("_First Name:"); // F is mnemonic
fNameLbl.setLabelFor(fNameFld);
fNameLbl.setMnemonicParsing(true);
```

## 2. 示例

Alt+F 和 Alt+L 在 2 个 TextField 之间切换焦点。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LabelTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TextField fNameFld = new TextField();
        Label fNameLbl = new Label("_First Name:");
        fNameLbl.setLabelFor(fNameFld);
        fNameLbl.setMnemonicParsing(true);

        TextField lNameFld = new TextField();
        Label lNameLbl = new Label("_Last Name:");
        lNameLbl.setLabelFor(lNameFld);
        lNameLbl.setMnemonicParsing(true);

        GridPane root = new GridPane();
        root.addRow(0, fNameLbl, fNameFld);
        root.addRow(1, lNameLbl, lNameFld);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Labels");
        stage.show();
    }
}
```

![|300](Pasted%20image%2020230717155451.png)

