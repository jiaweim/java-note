# Text Input 控件

- [Text Input 控件](#text-input-控件)
  - [1. 简介](#1-简介)
  - [2. 定位和移动插入符](#2-定位和移动插入符)
  - [3. 选择文本](#3-选择文本)
  - [4. 修改内容](#4-修改内容)
  - [5. 剪切、复制和粘贴](#5-剪切复制和粘贴)
  - [6. 示例](#6-示例)
  - [7. CSS](#7-css)

2023-07-24, 18:24
****
## 1. 简介

JavaFX 的文本输入控件支持输入单行或多行文本。包括 TextField, PasswordField 和 TextArea。所有文本输入控件继承自 TextInputControl，类图如下：

![](images/Pasted%20image%2020230724152601.png){width="300px"}

`TextInputControl` 定义了所有文本输入控件支持的属性和方法。

**TextInputControl** 声明的属性：

|属性|类型|说明|
|----|---|---|
|anchor|ReadOnlyIntegerProperty|文本选择的锚点。位于选取插入符号的另一端|
|`caretPosition`|`ReadOnlyIntegerProperty`|插入符号在文本中的位置|
|`editable`|`BooleanProperty`|控件可编辑时为 true|
|`font`|`ObjectProperty<Font>`|控件的默认字体|
|`length`|`ReadOnlyIntegerProperty`|控件包含的字符数|
|`promptText`|`StringProperty`|提示文本。控件中没有内容时显示|
|`redoable`|`ReadOnlyBooleanProperty`|是否可以再次执行上一次操作|
|`selectedText`|`ReadOnlyStringProperty`|控件中被选择的文本|
|`selection`|`ReadOnlyObjectProperty<IndexRange>`|被选择文本的索引范围|
|`text`|`StringProperty`|控件的文本|
|`textFormatter`|`ObjectProperty<TextFormatter<?>>`|文本格式化器|
|`undoable`|`ReadOnlyBooleanProperty`|是否可以撤销上一次更改|

## 2. 定位和移动插入符

所有文本输入控件都提供插入符。在持有焦点的输入文本控件中，默认插入符为闪烁的垂直线。

当前插入符位置是下一次键盘输入字符的目标位置：

- 插入符位置从 0 开始，即第一个字符前面
- 位置 1 是第一个字符后，第二个字符前

下图包含 4 个字符的文本输入控件的插入符位置：

![|100](Pasted%20image%2020230724164610.png)

有几个方法以插入符位置为参数。这些方法会自动修正插入符位置，例如，如果控件包含 4 个字符，你希望将插入符移到位置 10，则自动被修正为 4.

- read-only 属性 `caretPosition` 包含当前插入符位置
- `positionCaret(int pos)` 移动插入符到指定位置
- 如果没有选择文本，`backward()` 和 `forward()` 向前和向后移动一个字符串
- 如果有选择文本，则 `backward()` 和 `forward()` 将插入符移到选择的文本的开始和结束位置，并清除选择
- `home()` 和 `end()` 将插入符移到第一个字符串之前和最后一个字符之后，并清除选择
- `nextWord()` 移到下一个单词的开头，并清除选择
- endOfNextWord() 移到下一个单词结束，并清除选择
- `previousWord()` 移到下一个单词的开始，并清除选择

## 3. 选择文本

`TextInputControl` 类通过属性和方法提供了丰富的文本选择 API：

- selectedText 属性包含选中的文本内容，没有选择时其值为空字符串
- selection 属性一个 IndexRange，包含选择内容的索引范围
    - IndexRange 的 getStart() 和 getEnd() 返回选区的开始和结束索引
    - IndexRange 的 getLength() 返回选区长度
    - 如果没有选择，则 IndexRange 的 getStart() 和 getEnd() 相同，等于 `caretPosition`
- anchor 属性指定选择开始时插入符位置，可以从插入符开始向前或向后选择
    - 如果向前选择，anchor 值小于 caretPosition 值
    - 如果向后选择，anchor 值大于 caretPosition

下图显示 anchor 相对 caretPosition 的位置:

- #1 显示一个包含文本 "BLESSINGS" 文本输入控件，caretPosition 为 1
- 用户向前选择 4 个字符，如 #2 所示，此时 `selectedText`="LESS"，anchor=1, caretPosition=5, selection 属性的 IndexRange 为 1 到 5
- 在 #3 中 caretPosition 为 5，用户向后选择 4 个字符，如 #4 所示，此时 `selectedText`="LESS"，anchor=5，caretPosition=1，selection 的 IndexRange 为 1 到 5
- 对 #2 和 #4 ，anchor 和 caretPosition 值不同，selectedText 和 selection 属性值相同

![|500](Pasted%20image%2020230724174535.png)

除了 selection 属性，TextInputControl 还包含许多选择相关方法：

- selectAll()
- deselect()
- selectRange(int anchor, int caretPosition)
- selectHome()
- selectEnd()
- extendSelection(int pos)
- selectBackward()
- selectForward()
- selectPreviousWord()
- selectEndOfNextWord()
- selectNextWord()
- selectPositionCaret(int pos)
- replaceSelection(String replacement)

positionCaret(int pos) 将 caret 移到指定位置并取消选择；selectPositionCaret(int pos) 将 caret 移到指定位置并扩展选择，如果没有选择，则将当前 caret 位置作为 anchor，选择 anchor 和 pos 之间的文本。

`replaceSelection(String replacement)` 将选择的文本替换为指定文本。如果没有选择，则在当前 caret 位置插入指定文本。

## 4. 修改内容

TextInputControl 的 `text` 属性表示控件包含的文本内容：

- 使用 setText(String text) 更改内容
- getText() 返回文本内容
- clear() 清空文本内容
- insertText(int index, String text) 在指定位置插入文本，如果 index 超出有效范围 (0 到 text 长度)，抛出 IndexOutOfBoundsException
- appendText(String text) 将指定文本添加到 text 末尾
- deleteText() 删除指定范围的内容，可以用 IndexRange 指定范围
- deleteNextChar() 和 deletePreviousChar()
    - 如果没有选择，分别删除 caret 位置的下一个和上一个字符
    - 如果有选择，删除选择文本
    - 删除成功返回 true，否则返回 false

read-only 属性 length 返回 text 长度。随着 text 内容修改自动更新。

## 5. 剪切、复制和粘贴

文本输入控件支持通过代码、鼠标和键盘进行剪切、复制和粘贴操作。

- 鼠标和键盘操作与平台相同
- cut(), 将当前选择的文本转移到剪切板，并删除选择的文本
- copy()，将当前选择的文本转移到剪切板，不删除选择的文本
- paste()，将当前选择文本替换为剪切板中的内容，如果没有选择，则在当前 caret 位置插入剪切板的内容

## 6. 示例

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TextControlProperties extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TextField nameFld = new TextField();
        Label anchorLbl = new Label("");
        Label caretLbl = new Label("");
        Label lengthLbl = new Label("");
        Label selectedTextLbl = new Label("");
        Label selectionLbl = new Label("");
        Label textLbl = new Label("");

        // Bind text property of the Labels to the properties of the TextField
        anchorLbl.textProperty().bind(nameFld.anchorProperty().asString());
        caretLbl.textProperty().bind(nameFld.caretPositionProperty().asString());
        lengthLbl.textProperty().bind(nameFld.lengthProperty().asString());
        selectedTextLbl.textProperty().bind(nameFld.selectedTextProperty());
        selectionLbl.textProperty().bind(nameFld.selectionProperty().asString());
        textLbl.textProperty().bind(nameFld.textProperty());

        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(5);
        root.addRow(0, new Label("Name:"), nameFld);
        root.addRow(1, new Label("Anchor Position:"), anchorLbl);
        root.addRow(2, new Label("Caret Postion:"), caretLbl);
        root.addRow(3, new Label("Length:"), lengthLbl);
        root.addRow(4, new Label("Selected Text:"), selectedTextLbl);
        root.addRow(5, new Label("Selection:"), selectionLbl);
        root.addRow(6, new Label("Text:"), textLbl);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Text Input Control Properties");
        stage.show();
    }
}
```

![|350](Pasted%20image%2020230724182053.png)

## 7. CSS

TextInputControl 包含一个 CSS pesudo-class `readonly`，当控件不可编辑时应用。添加了如下样式属性：

- -fx-font
- -fx-text-fill
- -fx-prompt-text-fill
- -fx-highlight-fill
- -fx-highlight-text-fill
- -fx-display-caret

`-fx-font` 从父类继承。`-fx-display-caret` 属性为 true 或 false，表示当控件持有 focus 时是否雄安是 caret，默认 true。
