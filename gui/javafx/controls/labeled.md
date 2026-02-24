# Labeled

2025-07-14 update 
2023-07-17⭐
@author Jiawei Mao
****
## 1. 简介

labeled 控件包含一个 read-only 文本和一个可选的 graphic。

`Label`, `Button`, `CheckBox`, `RadioButton` 和 `Hyperlink` 都是 labeled 控件。所有 labeled 控件都直接或间接继承 `Labeled` 类。

`Labeled` 的部分类图如下：

<img src="images/Pasted%20image%2020230717145832.png" width="600" />

## 2. Labeled 属性

`Labeled` 类声明了 `text` 和 `graphic`属性，分别表示文本和图像内容。另外声明了其它几个属性用来处理外观，如对齐、字体、填充和文本换行等。如下表：

| 属性            | 类型                       | 说明              |
| --------------- | ------------------------- | ----------------- |
| `alignment`       | `ObjectProperty<Pos>`          | 控件内容在 content-area 的对齐方式，当 content-area 大于 text+graphic 时效果可检。默认为 `Pos.CENTER_LEFT` |
| `cententDisplay`  | `ObjectProperty<ContentDisplay>` | graphic 相对 text 的位置   |
| `ellipsisString`  | `StringProperty`                | 当 text 太长无法完整显示时而被截断，在末尾显示省略号文本，默认为 "..."。如果指定为空字符串，则截断文本不会显示省略号 |
| `font`            | `ObjectProperty<Font>`         | text 的默认字体   |
| `graphic`         | `ObjectProperty<Node>`         | 控件的图标 (可选)  |
| `graphicTextGap`  | `DoubleProperty`               | text 和 graphic 之间的距离 |
| `labelPadding`    | `ReadOnlyObjectProperty<Insets>` | 控件 content-area 外围 padding，默认 `Insets.EMPTY` |
| `lineSpacing`     | `DoubleProperty`               | 多行显示时的行间距  |
| `mnemonicParsing` | `BooleanProperty`               | 是否解析文本以检测助记符。`true` 时下划线后的第一个字符为助记符，在 Windows **Alt+助记符**为快键键 |
| `textAlignment`   | `ObjectProperty<TextAlignment>`  | text 多行显示时的对齐方式  |
| `textFill`        | `ObjectProperty<Paint>`        | text 颜色 |
| `textOverrun`     | `ObjectProperty<OverrunStyle>`   | text 内容超过控件范围时如何显示  |
| `text`            | `StringProperty`                 | text 内容 |
| `underline`       | `BooleanProperty`                | text 是否加下划线       |
| `wrapText`        | `BooleanProperty`                | 如果一行无法显示文本，是否换行 |

## 3. 设置 text 和 graphic 位置

`contentDisplay` 属性指定 labeled 控件中 graphic 相对 text 的位置。

`ContentDisplay` enum : `TOP`, `RIGHT`, `BOTTOM`, `LEFT`, `CENTER`, `TEXT_ONLY`, `GRAPHIC_ONLY`。下图显示这些值的效果：

- `TEXT_ONLY` 只显示文本
- `GRAPHIC_ONLY` 只显示图像
- `TOP`, graphic 在 text 上面

<img src="images/Pasted%20image%2020230717151318.png" width="600" />

## 4. 助记符和快捷键

`Labeled` 控件支持键盘助记符，又称为快捷键。快捷键向控件发送 `ActionEvent`。一般是 Alt 键和字符结合为助记符。

设置方法：对控件的 `text`，在需要设定为助记符的字符前加下划线（`_`），并且保证 `mnemonicParsing` 属性为 `true` 即可。下一个下划线会被移出，其后字符被设置为空间的助记符。

> [!WARNING]
>
> 有些平台不支持助记符。在 Windows 上，在按下 Alt 之前，控件 text 中的助记符不会加下划线。

**示例：** 对Button，助记符解析默认开启

```java
Button closeBtn = new Button("_Close");
```

按下 Alt 键，所有控件的助记符都会带上下划线，按下对应的助记符，会将焦点设置到该控件，并向其发送 `ActionEvent`。这里，使用Alt+C快捷键，执行 `closeBtn` 绑定的事件。

JavaFX 在 `javafx.scene.input` 包中提供了如下 4 个类用于为所有控件设置助记符：

- `Mnemonic`, 助记符
- `KeyCombination`, 快捷键组合，抽象类
- `KeyCharacterCombination`
- `KeyCodeCombination`

`KeyCharacterCombination` 和 `KeyCodeCombination` 都是 `KeyCombination` 的子类：

- `KeyCharacterCombination` 使用字符创建按键组合
- `KeyCodeCombination` 使用 key code 创建按键组合，因为键盘上的按键并非都是字符，该方法更通用

一般为 `Node` 创建 `Mnemonic`，然后将 `Mnemonic` 添加到 `Scene`。当 `Scene` 收到对应的 unconsumed key events，它会发送 `ActionEvent` 到目标 `Node`。

**示例：** 使用 `Mnemonic` 实现上面相同的功能

```java
Button closeBtn = new Button("Close");

// 为 Alt+C 创建 KeyCombination
KeyCombination kc = new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN);

// 为 closeBtn 创建 Mnemonic
Mnemonic mnemonic = new Mnemonic(closeBtn, kc);
Scene scene = create a scene...;
scene.addMnemonic(mnemonic); // 将 mnemonic 添加到 scene
```

也可以使用 `KeyCharacterCombination` 创建：

```java
KeyCombination kc = new KeyCharacterCombination("C", KeyCombination.ALT_DOWN);
```



`Scene` 也支持快键键，用于执行特定的 `Runnable` 任务。该快捷键和助记符有所不同：

- 助记符和某个控件绑定，按下对应的键会向控件发送 `ActionEvent`
- `Scene` 快捷键不与控件绑定，而是与特定的任务绑定

`Scene` 类包含一个 `ObservableMap<KeyCombination, Runnable>`，可以通过 `getAccelerators()`获得。

**示例：** 为 `Scene` 创建快捷键

功能：按下快捷键关闭窗口。在 Windows 上为 Ctrl+X，在 Mac 上为 Meta+X。即 `KeyCombination.SHORTCUT_DOWN` 在 Windows 中为 `Ctrl`，在Mac 中为 `Meta`。

```java
Scene scene = create a scene object...;
...
KeyCombination kc = new KeyCodeCombination(KeyCode.X,
                                           KeyCombination.SHORTCUT_DOWN);
Runnable task = () -> scene.getWindow().hide();
scene.getAccelerators().put(kc, task);
```

**示例：** 助记符和快捷键

- Alt+1 点击 Button 1
- Alt+2 点击 Button 2
- Ctrl+X 关闭程序

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MnemonicTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        Label msg = new Label("Press Ctrl + X on Windows \nand " +
                "\nMeta + X on Mac to close the window");
        Label lbl = new Label("Press Alt + 1 or Alt + 2");

        // Use Alt + 1 as the mnemonic for Button 1
        Button btn1 = new Button("Button _1");
        btn1.setOnAction(e -> lbl.setText("Button 1 clicked!"));

        // Use Alt + 2 as the mnemonic key for Button 2
        Button btn2 = new Button("Button 2");
        btn2.setOnAction(e -> lbl.setText("Button 2 clicked!"));
        KeyCombination kc = new KeyCodeCombination(KeyCode.DIGIT2,
                KeyCombination.ALT_DOWN);
        Mnemonic mnemonic = new Mnemonic(btn2, kc);
        scene.addMnemonic(mnemonic);

        // Add an accelarator key to the scene
        KeyCombination kc4 = new KeyCodeCombination(KeyCode.X,
                KeyCombination.SHORTCUT_DOWN);
        Runnable task = () -> scene.getWindow().hide();
        scene.getAccelerators().put(kc4, task);

        // Add all children to the VBox
        root.getChildren().addAll(msg, lbl, btn1, btn2);

        stage.setScene(scene);
        stage.setTitle("Using Mnemonics and Accelerators");
        stage.show();
    }
}
```

<img src="images/Pasted%20image%2020230717153620.png" width="300" />
