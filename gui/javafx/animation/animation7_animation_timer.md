# AnimationTimer

2023-08-10, 21:47
@author Jiawei Mao
****
## 1. 简介

`AnimationTimer` 是创建动画最简单的工具。它是一个简单的计时器，在动画的每一帧，其 `handle` 方法都会被调用。

`AnimationTimer` 是抽象类，所以比如扩展该类，覆盖 `handle` 方法。AnimationTimer 的 start() 方法启动计时器，stop() 方法停止计时器。

## 2. 示例

```java{.line-numbers}
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AnimationTimerEx extends Application {

    private double opacity = 1;
    private Label lbl;

    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    private void initUI(Stage stage) {
        var root = new StackPane();

        lbl = new Label("JavaFX");
        lbl.setFont(Font.font(48));
        root.getChildren().add(lbl);

        AnimationTimer timer = new MyTimer();
        timer.start();

        var scene = new Scene(root, 300, 250);

        stage.setTitle("AnimationTimer");
        stage.setScene(scene);
        stage.show();
    }

    private class MyTimer extends AnimationTimer {

        @Override
        public void handle(long now) {
            doHandle();
        }

        private void doHandle() {
            opacity -= 0.01;
            lbl.opacityProperty().set(opacity);

            if (opacity <= 0) {
                stop();
                System.out.println("Animation stopped");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

该示例使用 `AnimationTimer` 在 node 上实现淡出效果。

- 上面动画改变该 `Label` 的 `opacity` 属性

```java
lbl = new Label("JavaFX");
lbl.setFont(Font.font(48));
root.getChildren().add(lbl);
```

- 创建 `AnimationTimer`，调用 `start()` 启动

```java
AnimationTimer timer = new MyTimer();
timer.start();
```

- `MyTimer` 继承 `AnimationTimer`，覆盖其 `handle()` 方法

```java
private class MyTimer extends AnimationTimer {

    @Override
    public void handle(long now) {
        doHandle();
    }

    private void doHandle() {
        opacity -= 0.01;
        lbl.opacityProperty().set(opacity);

        if (opacity <= 0) {
            stop();
            System.out.println("Animation stopped");
        }
    }
}
```

在 `doHandle()` 方法中，降低 `opacity` 值，并更新 `Label` 的 `opacityProperty` 属性。当 opacity 达到最小值（0），timer 调用 `stop()` 停止。
