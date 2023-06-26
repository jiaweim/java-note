# 窗口事件

2023-06-26, 17:27
****
## 1. 简介

在显示、隐藏和关闭窗口时发生窗口事件。窗口事件由 `javafx.stage.WindowEvent` 类定义。

`WindowEvent` 中定义的窗口事件类型常量。

| 常量                 | 说明                     |
| -------------------- | ------------------------ |
| `ANY`                  | 其它窗口事件类型的超类型 |
| `WINDOW_SHOWING`       | 窗口显示前触发           |
| `WINDOW_SHOWN`         | 窗口显示后触发           |
| `WINDOW_HIDING`        | 窗口隐藏前触发           |
| `WINDOW_HIDDEN`        | 窗口隐藏后触发           |
| `WINDOW_CLOSE_REQUEST` | 外部请求关闭窗口时触发   |

window-showing 和 window-shown 事件在窗口显示前后触发。为 window-showing 添加耗时的事件处理器会延迟向用户显示窗口，降低用户体验。该事件适合用来初始化一些 window-level 变量。例如，在 window-shown 事件中将焦点设置为第一个可编辑字段，向用户显示需要关注任务的 alert 等。

window-hiding 和 window-hidden 是 window-showing 和 window-shown 的对应部分。

window-close-request 在外部请求关闭窗口时发生。上下文菜单的 Close 菜单、窗口标题栏的关闭按钮、Alt+F4 快捷键都属于关闭窗口的外部请求。使用编程方式关闭窗口，如 `Stage.close()` 或 `Platform.exit()` 不属于外部请求。

## 2. 示例

演示如何使用所有窗口事件。

在 `Stage` 中添加一个 `CheckBox` 和两个 `Button`：

- 未勾选 `CheckBox` 时，外部请求被消耗，阻止关闭窗口。
- "Close" 按钮关闭窗口
- "Hiden" 按钮隐藏主窗口，打开一个新窗口

![|350](images/2023-06-26-17-21-59.png)

```java
import javafx.application.Application;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST;

public class WindowEventApp extends Application {

    private CheckBox canCloseCbx = new CheckBox("Can Close Window");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> stage.close());

        Button hideBtn = new Button("Hide");
        hideBtn.setOnAction(e -> {
            showDialog(stage);
            stage.hide();
        });

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(canCloseCbx, closeBtn, hideBtn);

        // Add window event handlers to the stage
        stage.setOnShowing(e -> handle(e));
        stage.setOnShown(e -> handle(e));
        stage.setOnHiding(e -> handle(e));
        stage.setOnHidden(e -> handle(e));
        stage.setOnCloseRequest(e -> handle(e));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Window Events");
        stage.show();
    }

    public void handle(WindowEvent e) {
        // 如果没有勾选 CheckBox，消耗事件，从而无法关闭窗口
        EventType<WindowEvent> type = e.getEventType();
        if (type == WINDOW_CLOSE_REQUEST && !canCloseCbx.isSelected()) {
            e.consume();
        }

        System.out.println(type + ": Consumed=" + e.isConsumed());
    }

    public void showDialog(Stage mainWindow) {
        Stage popup = new Stage();

        Button closeBtn = new Button("Click to Show Main Window");
        closeBtn.setOnAction(e -> {
            popup.close();
            mainWindow.show();
        });

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.getChildren().addAll(closeBtn);

        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.setTitle("Popup");
        popup.show();
    }
}
```

