# Cue Point

2023-07-28, 08:56
****
## 简介

提示点（cue point）是 timeline 上的命名时间点。Animation 使用 `jumpTo(String cuePoint)` 可以跳转到指定 cuePoint。

`Animation.getCuePoints()` 返回 `ObservableMap<String,Duration>`，Map 的 key 是 cutPoint 名称，value 是 cuePoint 在 timeline 上的 duration。

在 timeline 添加 cuePoints 的方法有两种：

- 添加 KeyFrame 时指定名称
- 通过 `getCuePoints()` 添加 name-duration pair

每个 Animation 有两个预定义的 cuePoints: "start" 和 "end"。设置为动画的开始和结束。这两个 cuePoints 不会出现在 `getCuePoints()` 返回的 map 中。

**示例：** 创建名为 "midway" 的 KeyFrame

将命名 `KeyFrame` 添加到 `Timeline`，会自动将名为 "midway" 的 cuePoint 添加到 timeline。使用 `jumpTo("midway")` 可以跳转到该 KeyFrame。

```java
// Create a KeyFrame with name “midway”
KeyValue midKeyValue = ...
KeyFrame midFrame = new KeyFrame(Duration.seconds(5), "midway", midKeyValue);
```

**示例：** 添加 cuePoints

```java
Timeline timeline = ...
timeline.getCuePoints().put("3 seconds", Duration.seconds(3));
timeline.getCuePoints().put("7 seconds", Duration.seconds(7));
```

## 示例

```java
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CuePointTest extends Application {

    Text msg = new Text("JavaFX animation is cool!");
    Pane pane;
    ListView<String> cuePointsListView;
    Timeline timeline;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));

        BorderPane root = new BorderPane();
        root.setPrefSize(600, 150);

        cuePointsListView = new ListView<>();
        cuePointsListView.setPrefSize(100, 150);
        pane = new Pane(msg);

        root.setCenter(pane);
        root.setLeft(cuePointsListView);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Cue Points");
        stage.show();

        this.setupAnimation();
        this.addCuePoints();
    }

    private void setupAnimation() {
        double paneWidth = pane.getWidth();
        double msgWidth = msg.getLayoutBounds().getWidth();

        // Create the initial and final key frames
        KeyValue initKeyValue = new KeyValue(msg.translateXProperty(), paneWidth);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

        // A KeyFrame with a name "midway" that defines a cue point this name
        KeyValue midKeyValue = new KeyValue(msg.translateXProperty(), paneWidth / 2);
        KeyFrame midFrame = new KeyFrame(Duration.seconds(5), "midway", midKeyValue);

        KeyValue endKeyValue = new KeyValue(msg.translateXProperty(), -1.0 * msgWidth);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(10), endKeyValue);

        timeline = new Timeline(initFrame, midFrame, endFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void addCuePoints() {
        // Add two cue points directly to the map
        timeline.getCuePoints().put("3 seconds", Duration.seconds(3));
        timeline.getCuePoints().put("7 seconds", Duration.seconds(7));

        // Add all cue points from the map to the ListView in the order
        // of their durations
        SortedMap<String, Duration> smap = getSortedCuePoints(timeline.getCuePoints());
        cuePointsListView.getItems().addAll(smap.keySet());

        // Add the special "start" and "end" cue points
        cuePointsListView.getItems().add(0, "Start");
        cuePointsListView.getItems().add("End");

        // Jusp to the cue point when the user selects it
        cuePointsListView.getSelectionModel().selectedItemProperty().addListener(
                (prop, oldValue, newValue) -> {
                    timeline.jumpTo(newValue);
                });
    }

    // Sort the cue points based on their durations
    private SortedMap<String, Duration> getSortedCuePoints(
            Map<String, Duration> map) {
        Comparator<String> comparator = (e1, e2) -> map.get(e1).compareTo(map.get(e2));
        SortedMap<String, Duration> smap = new TreeMap<>(comparator);
        smap.putAll(map);
        return smap;
    }
}
```
