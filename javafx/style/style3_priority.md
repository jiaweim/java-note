# 样式优先级

2023-06-19, 18:48
****
## 1. 简介

JavaFX 程序中，`Node` 可视化属性可能有多个来源。指定样式的优先级如下，依次降低：

- 内联样式（最高优先级）
- `Parent` 样式表
- `Scene` 样式表
- 使用 JavaFX API 代码指定
- 默认样式（最低优先级）

`Parent` 样式表的优先级比 `Scene` 样式表高，因此可以为 `Scene` graph 的不同分支指定不同样式。

```ad-warning
在样式表和代码中为相同 `Node` 设置属性。按照优先级，会使用样式表中的样式，此时使用代码设置无效。这一点非常容易出错。
```

## 2. 示例

- 样式表

```css
.button {  
	-fx-font-size: 24px;  
	-fx-font-weight: bold;  
}
```

- 演示程序

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StylesPriorities extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        Button cancelBtn = new Button("Cancel");

        // 使用内联样式修改字体
        yesBtn.setStyle("-fx-font-size: 16px");
        // 使用 API 修改字体
        yesBtn.setFont(new Font(10));

        // 使用 API 修改 No 按钮字体
        noBtn.setFont(new Font(8));

        HBox root = new HBox();
        root.setSpacing(10);
        root.getChildren().addAll(yesBtn, noBtn, cancelBtn);

        Scene scene = new Scene(root);

        // 为 Scene 添加样式表
        var url = getClass().getResource("/css/stylespriorities.css").toExternalForm();
        scene.getStylesheets().addAll(url);

        stage.setScene(scene);
        stage.setTitle("Styles Priorities");
        stage.show();
    }
}
```

![|250](images/Pasted%20image%2020230619183256.png)

yesBtn 的字体大小有四个来源：

- 内联样式（16px）
- 为 `Scene` 设置的样式表（24px）
- JavaFX API（10px）
- 默认样式

其中内联样式优先级最高，因此 Yes 按钮的字体大小为 16px.

```ad-tip
`Node` 的默认字体大小由系统字体大小决定。
```

No 按钮的字体大小有三个来源：

- `Scene` 样式表（24px）
- JavaFX API（10px）
- 默认字体

按照优先级，`Scene` 样式表最高，因此 No 按钮字体大小为 24px。

Cancel 按钮的字体大小有两个来源：

- `Scene` 样式表（24px）
- 默认字体

按照优先级其字体大小为 24px.

所有按钮的字体都加粗，因为 `Scene` 样式表中使用了 `-fx-font-weight: bold;`，该属性值没有被其它样式来源覆盖。

你可能会疑惑：

- 设置 `Scene` 样式表后，如何让 Cancel 按钮使用默认字体大小？
- 对按钮，如何在 `HBox` 中使用一种字体，在 `VBox` 中使用另一种字体？

这些都能通过在样式表中定义合适的 selector 实现。

