# Key 事件

2023-06-26, 16:10
****
## 1. 简介

Key 事件是一种输入事件，表示发生了键盘操作，由 `javafx.scene.input.KeyEvent` 类表示。key 事件由持有焦点的节点处理。

Key 事件有三种类型，以常量形式在 `KeyEvent` 类中定义：

| 常量         | 说明                  |
| ------------ | --------------------- |
| `ANY`          | 其它 key 事件的超类型 |
| `KEY_PRESSED`  | 按下键时发生       |
| `KEY_RELEASED` | 松开键时发生       |
| `KEY_TYPED`    | 输入 Unicode 字符时发生    |

```ad-tip
`Circle` 和 `Rectangle` 等 `Shape` 也可以接收 key 事件。节点接收 key 事件的关键是持有焦点，`Shape` 默认不是焦点遍历链的一部分，鼠标点击也不会为它们带来焦点，但是 `Shape` 可以调用 `requestFocus()` 获取焦点。
```

key-pressed 和 key-released 是相对底层的事件，在按下和释放键时触发。

key-typed 是高级事件，输入 Unicode 字符时触发：

- key-pressed 和 key-released 都可以生成 key-typed 事件。例如，在 Windows 上使用 Alt+数字键，不管敲击数字键多少次，松开 Alt 都会触发 key-typed 事件
- 可以通过一系列 key-pressed 和 key-released 生成 key-typed 事件，例如，按 Shift+A 输入字符 A，即两个 key-pressed 生成一个 key-typed 事件
- 并非所有 key-pressed 或 key-released  操作都能生成 key-typed 事件。例如，按下功能键（F1, F2 等）或修饰键（Shift, Ctrl 等）不会输入 Unicode 字符，所以不会触发 key-typed 事件

`KeyEvent` 类包含 3 个变量用于描述 key 事件：code, text 和 character。

这些变量信息可用 `KeyEvent` 的如下方法查询：

| 方法                    | 应用事件                      | 说明                                                                                                 |
| ----------------------- | ----------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| `KeyCode getCode()`     | `KEY_PRESSED`, `KEY_RELEASED` | 枚举类 `KeyCode` 包含键盘上所有的键。对 key-typed 事件总是返回 `KeyCode.UNDEFINED`, 因为 key-typed 事件不一定由一次 keystroke 生成 |
| `String getText()`      | `KEY_PRESSED`, `KEY_RELEASED` | 返回与 key-pressed 和 key-released 事件关联的 `KeyCode` 的 `String` 描述。对 key-typed 事件返回空字符串                              |
| `String getCharacter()` | `KEY_TYPED`                 | 以 `String` 形式返回与 key-typed 事件相关的字符或字符序列。对 key-pressed 和 key-released 事件返回 `KeyEvent.CHAR_UNDEFINED`     |

这里 `getCharacter()` 返回 `String` 而不是 `char`，是因为有些 Unicode 字符用一个 `char` 无法表示；有些设备一次 keystroke 生成多个字符。

`KeyEvent` 类包含 `isAltDown()`, `isControlDown()`, `isMetaDown()`, `isShiftDown()`, `isShortcutDown()` 方法，用于检查发生 key 事件时，是否按下这些修饰键。

## 2. key-pressed 和 key-released 事件

为 `KEY_PRESSED` 和 `KEY_RELEASED` 类型事件添加事件过滤器和处理器即可处理 key-pressed 和 key-released 事件。通常使用这些事件了解按下或释放了哪些键，然后执行某个操作。例如，检测是否按下了 F1 功能键，然后显示焦点节点的帮助窗口。

**示例：** 处理 key-pressed 和 key-released 事件

显示 `Label` 和 `TextField`，运行程序，`TextField` 持有焦点：

- 按下或释放键，控制台输出事件的详细信息。key-released 事件不会在每个 key-pressed 事件后发生
- key-pressed 和 key-released 事件不是一对一的关系。key-pressed 事件后可能没有 key-released 事件，也可能多个 key-pressed 事件对应一个 key-released 事件。当长时间按下一个键可能发生该情况，例如，按下 A 一段时间，然后松开，会键入多个 A，生成多个 key-pressed 事件和一个 key-released 事件
- 按下 F1 键显示帮助窗口。

注意，按 F1 不会为 `TextField` 生成 key-released 事件：在 key-pressed 事件后，显示帮助窗口，帮助窗口持有焦点，主窗口中的 `TextField` 不再持有焦点。因此 key-released 事件被发送到持有焦点的帮助窗口，而不是 `TextField`。

```java
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class KeyPressedReleased extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameTfl = new TextField();

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(nameLbl, nameTfl);

        // 为 TextField 添加 key-pressed 和 key-released 处理器
        nameTfl.setOnKeyPressed(e -> handle(e));
        nameTfl.setOnKeyReleased(e -> handle(e));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Key Pressed and Released Events");
        stage.show();
    }

    public void handle(KeyEvent e) {
        String type = e.getEventType().getName();
        KeyCode keyCode = e.getCode();
        System.out.println(type + ": Key Code=" + keyCode.getName() +
                ", Text=" + e.getText());

        // 按下 F1 键时显示帮助窗口
        if (e.getEventType() == KEY_PRESSED && e.getCode() == KeyCode.F1) {
            displayHelp();
            e.consume();
        }
    }

    public void displayHelp() {
        Text helpText = new Text("Please enter a name.");
        HBox root = new HBox();
        root.setStyle("-fx-background-color: yellow;");
        root.getChildren().add(helpText);

        Scene scene = new Scene(root, 200, 100);
        Stage helpStage = new Stage();
        helpStage.setScene(scene);
        helpStage.setTitle("Help");
        helpStage.show();
    }
}
```

## 3. key-typed 事件

key-typed 事件用于检测特定的 keystrokes。

**示例：** 演示 `TextField` 的 key-typed 事件

```java
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class KeyTyped extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label nameLbl = new Label("Name:");
        TextField nameTfl = new TextField();

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(nameLbl, nameTfl);

        // 为 TextField 设置 key-typed 事件处理器
        nameTfl.setOnKeyTyped(e -> handle(e));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Key Typed Event");
        stage.show();
    }

    public void handle(KeyEvent e) {
        String type = e.getEventType().getName();
        System.out.println(type + ": Character=" +
                e.getCharacter());
    }
}
```
