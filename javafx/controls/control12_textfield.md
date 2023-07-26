# TextField

2023-07-24, 20:09
****
## 1. 简介

TextField 继承 TextInputControl，是文本输入控件。

`TextField` 用于输入单行文本，文本中的换行符和 tab 被移除不显示。对多行文本，使用 `TextArea` 控件。下图包含两个 `TextField`：

![textField|400](2020-05-20-08-31-02.png)

## 2. 创建 TextField

可以用空字符串或指定字符串初始化 `TextField`：

```java
// Create a TextField with an empty string as initial text
TextField nameFld1 = new TextField();

// Create a TextField with "Layne Estes" as an initial text
TextField nameFld2 = new TextField("Layne Estes");
```

## 3. TextField 属性

| 属性           | 功能                                         |
| -------------- | -------------------------------------------- |
| alignment      | 文本对齐方式                                 |
| onAction       | ActionEvent handler, 用于处理输入事件        |
| prefColmnCount | 宽度。默认值 12，宽度 1 可以显示一个大写的 W |
| text           | 存储待显示的文本                             |

`alignment`：指定文本对齐方式

- 如果 node 方向为 `LEFT_TO_RIGHT`，默认为 `CENTER_LEFT`
- 如果 node 方向为 `RIGHT_TO_LEFT`，默认为 `CENTER_RIGHT`

`text`：保存 TextField 的文本内容

- 为 `text` 属性添加 `ChangeListener` 监听文本变化
- `getText()` 和 `setText(String newText)` 用于获取和设置 `TextField` 文本

`onAction`：ActionEvent handler，在 `TextField` 按 Enter 时被调用。例如

```java
TextField nameFld = new TextField();
nameFld.setOnAction(e -> /* Your ActionEvent handler code...*/ );
```

`prefColmnCount`：指定 TextField 宽度，默认 12

- 通过 `setPrefColumnCount()` 设置宽度

## 4. 右键菜单

`TextField` 提供了一个默认的右键菜单，如下图所示：

![|90](Pasted%20image%2020230724200635.png)

目前无法个性化该默认的右键菜单，不过可以重新定义一个右键菜单替换该默认菜单。

例如，下面定义一个右键菜单，显示一个菜单项，表示右键菜单已被禁用。选择该菜单项无任何效果。为菜单项添加 `ActionEvent` handler 才能为其添加功能。

```java
ContextMenu cm = new ContextMenu();
MenuItem dummyItem = new MenuItem("Context menu is disabled");
cm.getItems().add(dummyItem);

TextField nameFld = new TextField();
nameFld.setContextMenu(cm);
```

## 5. 完整实例

下例演示如何使用 `TextField`：

- 添加了两个 textField
- 自定义右键菜单
- 添加 `ActionEvent`
- 使用 `ChangeListener` 监听内容变化

```java
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TextFieldTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a TextFiled with an empty string as its initial text
        TextField firstNameFld = new TextField();
        TextField lastNameFld = new TextField();

        // Both fields should be wide enough to display 15 chars
        firstNameFld.setPrefColumnCount(15);
        lastNameFld.setPrefColumnCount(15);

        // Add a ChangeListener to the text property
        firstNameFld.textProperty().addListener(this::changed);
        lastNameFld.textProperty().addListener(this::changed);

        // Add a dummy custom context menu for the firstname field
        ContextMenu cm = new ContextMenu();
        MenuItem dummyItem = new MenuItem("Context menu is disabled");
        cm.getItems().add(dummyItem);
        firstNameFld.setContextMenu(cm);

        // Set ActionEvent handlers for both fields
        firstNameFld.setOnAction(e -> nameChanged("First Name"));
        lastNameFld.setOnAction(e -> nameChanged("Last Name"));

        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(5);
        root.addRow(0, new Label("First Name:"), firstNameFld);
        root.addRow(1, new Label("Last Name:"), lastNameFld);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using TextField Controls");
        stage.show();
    }

    public void nameChanged(String fieldName) {
        System.out.println("Action event fired on " + fieldName);
    }

    public void changed(ObservableValue<? extends String> prop,
                        String oldValue,
                        String newValue) {
        System.out.println("Old = " + oldValue + ", new = " + newValue);
    }
}
```

![|300](Pasted%20image%2020230724200849.png)

## 6. CSS

`TextField` 的 CSS 样式类为 `text-field`。

- `-fx-alignment` 属性用于指定对其方式
