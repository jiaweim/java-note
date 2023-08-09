# Timeline 动画

2023-07-27, 17:54
****
Timeline 动画可以实现 Node 任何属性的动画。Timeline 的使用包含如下步骤：

- 创建 keyframes
- 使用 keyframes 创建 Timeline
- 设置 animation 属性
- 调用 play() 运行动画

Timeline 将 KeyFrame 保存在 `ObservableList<KeyFrame>`，`getKeyFrames()` 返回该 list。

Timeline 提供了如下构造函数：

```java
Timeline()
Timeline(double targetFramerate)
Timeline(double targetFramerate, KeyFrame... keyFrames)
Timeline(KeyFrame... keyFrames)
```

keyframes 添加到 Timeline 的顺序不重要。Timeline 会根据 keyframes 的 time offset 自动排序。

**示例：** 

```java
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ScrollingText extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text msg = new Text("JavaFX animation is cool!");
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));

        Pane root = new Pane(msg);
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        /* Set up a Timeline animation */
        // 获得 scene width 和 text width
        double sceneWidth = scene.getWidth();
        double msgWidth = msg.getLayoutBounds().getWidth();

        // Create the initial and final key frames
        // 0 秒 Text 在 scene width 未知，所以不可见
        KeyValue initKeyValue =
                new KeyValue(msg.translateXProperty(), sceneWidth);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

        // 3 秒 Text 在 scene 左侧 text width 未知，所以也不可见
        KeyValue endKeyValue =
                new KeyValue(msg.translateXProperty(), -1.0 * msgWidth);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(3), endKeyValue);

        // 使用上面 2 个 keyframes 创建 Timeline
        Timeline timeline = new Timeline(initFrame, endFrame);

        // 动画默认只执行一次，即 Text 从右到左，然后停止，这里将其设置为永远
        timeline.setCycleCount(Timeline.INDEFINITE);

        // 开始动画
        timeline.play();
    }
}
```

![|450](images/ani1.gif)

这个例子有个缺陷，文本的初始位置固定为 scene 初始初始宽度，当 scene 宽度改变，Text 的还是原来的初始位置。可以在 scene 宽度改变时，更新初始 keyframe 的位置：

```java
scene.widthProperty().addListener( (prop, oldValue , newValue) -> {
    KeyValue kv = new KeyValue(msg.translateXProperty(), scene.getWidth());
    KeyFrame kf = new KeyFrame(Duration.ZERO, kv);
    timeline.stop();
    
    timeline.getKeyFrames().clear();
    timeline.getKeyFrames().addAll(kf, endFrame);
    timeline.play();
});
```

只用一个 keyframe 也可以创建 Timeline，该 keyframe 作为最后一个 keyframe，Timeline 使用 target 属性的当前值和时间 0s 自动生成一个初始 keyframe。将上面示例的：

```java
Timeline timeline = new Timeline(initFrame, endFrame);
```

替换为：

```java
Timeline timeline = new Timeline(endFrame);
```

Timeline 使用 Text 的 translateX 属性的当前值创建初始 keyframe。
