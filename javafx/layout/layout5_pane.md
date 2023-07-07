# Pane

2023-07-07, 14:57
****
## 1. 简介

`Pane` 是 `Region` 的子类，它公开了 `Parent` 的 `getChildren()` 方法。所以 `Pane` 及其子类可以添加子节点。

![|300](Pasted%20image%2020230707143803.png)
`Pane` 为绝对位置布局，其特征为：

- 所有子节点默认位置在 (0, 0)，需要显式设置子节点位置。
- 将 resizable 子节点的尺寸设置为 preferred sizes

一般只在放置图表或图片时使用，其他情况均推荐使用布局管理器。

Pane 包含三个 size：

| |width|height|
|---|---|---|
|minimum|left + right insets|top + bottom insets|
|preferred|以 preferred width 显示所有子节点所需宽度|以 preferred heigth 显示所有子节点所需高度|
|maximum|`Double.MAX_VALUE`|`Double.MAX_VALUE`|

## 2. 示例

创建 `Pane`，添加 2 个 `Button`。

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PaneTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");
        okBtn.relocate(10, 10);
        cancelBtn.relocate(60, 10);

        Pane root = new Pane();
        root.getChildren().addAll(okBtn, cancelBtn);
        root.setStyle("-fx-border-style: solid inside;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: red;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Panes");
        stage.show();
    }
}
```

![|120](Pasted%20image%2020230707145114.png)

可以设置 `Pane` 的 preferred size:

```java
Pane root = new Pane();
root.setPrefSize(300, 200); // 300px wide and 200px tall
```

还可以让 Pane 根据子节点的 preferred size 重新计算自身的 preferred size:

```java
Pane root = new Pane();

// Set the preferred size to 300px wide and 200px tall
root.setPrefSize(300, 200);

/* Do some processing... */

// Set the default preferred size
root.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
```

```ad-tip
`Pane` 不会裁剪其内容，因此子节点可能在其边界外显示。
```

