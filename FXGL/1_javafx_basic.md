# JavaFX 基础

2023-06-01
****
## JavaFX 示例

最简单的 JavaFX 程序如下：

```java
public class FXApp extends Application {  
  
    @Override  
    public void start(Stage stage) throws Exception {  
  
        stage.setScene(new Scene(new Pane(), 800, 600));  
        stage.show();  
    }  
  
    public static void main(String[] args) {  
  
        launch(args);  
    }  
}
```

运行后出现空白窗口：

- 扩展 `Application` 类是 JavaFX 应用的入口
- `Stage` 提供独立于平台窗口接口

下面介绍如何实现用户输入、交互以及输出。示例：

```java
public class BasicApp extends Application {  
  
    public static void main(String[] args) {  
  
        launch(args);  
    }  
  
    @Override  
    public void start(Stage stage) {  
  
        stage.setScene(new Scene(creatContent()));  
        stage.show();  
    }  
  
    private Parent creatContent() {  
  
        TextField input = new TextField();  // 用于接收用户输入
        Text output = new Text();           // 用户输出
  
        Button button = new Button("Press");  // 用户交互
        button.setOnAction(actionEvent -> output.setText(input.getText()));  
  
        VBox root = new VBox(input, button, output);  
        root.setPrefSize(600, 600);  
        return root;  
    }  
}
```

运行效果：

![[Pasted image 20230601132237.png]]

JavaFX 另一个重要功能为 property。JavaFX 将字段包装为 `Property`，便于监听和自动更新。

例如，假设有一个要求用户输入姓名的 `TextField`，如果没有 `Property`，就需要监听 `TextField` 的每个 key event，然后根据用户输入更新 `Text` 对象：

```java
text.setText(textField.getText());
```

使用 `Property`，不需要编写监听器，只需要绑定属性：

```java
text.textProperty().bind(textField.textProperty());
```

## FXGL 示例

下面构建一个简单的 FXGL 程序，作为第一个游戏示例。该游戏需求：

1. 窗口大小 600x600
2. 屏幕上有一个 player，用蓝色矩形表示
3. 可以通过按键 W, S, A, D 来控制 player
4. 用户界面（UI）用单个文本表示，显示 player 移动多少像素
5. player 移动时，UI 文本随之更新

虽然这个游戏过于简陋，但对于理解 FXGL 的基本功能已经足够了。

首先创建入口类 `BasicGameApp`：

```java  
import com.almasb.fxgl.app.GameApplication;  
import com.almasb.fxgl.app.GameSettings;  
  
public class BasicGameApp extends GameApplication {  
  
    @Override  
    protected void initSettings(GameSettings settings) {  
  
    }  
  
    public static void main(String[] args) {  
  
        launch(args);  
    }  
}
```


FXGL app 需要扩展 `GameApplication` 类，并覆盖 `initSettings` 方法。

通过 `initSettings()` 方法和 `GameSettings` 对象，可以修改大量的游戏配置。

### 1. 设置窗口

下面来依次实现游戏需求。首先，要求窗口大小为 600x600，如前所述，用 `initSettings()` 方法可以实现。

```java
@Override  
protected void initSettings(GameSettings settings) {  
  
    settings.setWidth(600);  
    settings.setHeight(600);  
    settings.setTitle("Basic Game App");  
    settings.setVersion("0.1");  
}
```

一旦完成设置，运行期间无法更改。当然，并不是说无法调整窗口大小，这个只是提供初始设置。点击运行，可以看到一个窗口，标题为 "Basic Game App"，大小为 600x600。

### 2. 添加 player

添加一个 player 对象。

```java
protected Entity player;  
  
@Override  
protected void initGame() {  
  
    player = FXGL.entityBuilder()  
            .at(300, 300)  // 设置 Entity 位置，对应矩形左上角坐标
            .view(new Rectangle(25, 25, Color.BLUE))   // 设置 Entity 视图
            .buildAndAttach();  // 创建并添加 Entity
}
```

`Entity` 是 FXGL 定义的一个游戏对象。再次运行，可以在窗口中心看到一个蓝色方框。

### 3. 添加输入

支持使用键盘控制 palyer。在 FXGL 中，所有输入相关的代码都放在 `initInput()` 中。

```java
@Override  
protected void initInput() {  
  
    FXGL.onKey(KeyCode.D, () -> {  
        player.translateX(5); // 向右移动 5 个像素  
    });  
}
```

`FXGL.onKey()` 表示为某个按键事件添加动作。可以看到，FXGL 的大部分功能都可以通过 `FXGL.***` 调用。

其它的动作实现代码基本相同：

```java
@Override  
protected void initInput() {  
  
    FXGL.onKey(KeyCode.D, () -> {  
        player.translateX(5); // 向右 5 个像素  
    });  
    FXGL.onKey(KeyCode.A, () -> {  
        player.translateX(-5);  
    });  
    FXGL.onKey(KeyCode.W, () -> {  
        player.translateY(-5); // 向上 5 像素   
	});  
    FXGL.onKey(KeyCode.S, () -> {  
        player.translateY(5);  
    });  
}
```

### 4. 添加 UI

在 `initUI()` 方法中实现 UI 功能。

```java
@Override  
protected void initUI() {  
  
    Text textPixels = new Text();  
    textPixels.setTranslateX(50);  
    textPixels.setTranslateY(100);  
  
    FXGL.getGameScene().addUINode(textPixels);  
}
```

对 `Entity` ，FXGL 自动将其添加到 `Scene`，而 UI 对象需要我们手动添加。通过 `FXGL.getGameScene().addUINode()` 来实现。

### 5. 更新 UI

在 FXGL 中，游戏变量可以在任何部分访问和修改。先创建一个变量：

```java
@Override  
protected void initGameVars(Map<String, Object> vars) {  
  
    vars.put("pixelsMoved", 0);  
}
```

这里创建了变量 `pixelsMoved`，初始值为 0。FXGL根据初始值自动推断类型。下一步，当移动 player 时，更新变量，添加到 `initInput()` 中：

```java
@Override  
protected void initInput() {  
  
    FXGL.onKey(KeyCode.D, () -> {  
        player.translateX(5); // 向右 5 个像素  
        FXGL.inc("pixelsMoved", +5);  
    });  
    FXGL.onKey(KeyCode.A, () -> {  
        player.translateX(-5);  
        FXGL.inc("pixelsMoved", +5);  
  
    });  
    FXGL.onKey(KeyCode.W, () -> {  
        player.translateY(-5); // 向上 5 像素  
        FXGL.inc("pixelsMoved", +5);  
    });  
    FXGL.onKey(KeyCode.S, () -> {  
        player.translateY(5);  
        FXGL.inc("pixelsMoved", +5);  
    });  
}
```

在 `initUI()` 中，将创建的 `textPixels` 与 `pixelsMoved` 绑定：

```java
@Override  
protected void initUI() {  
  
    Text textPixels = new Text();  
    textPixels.setTranslateX(50);  
    textPixels.setTranslateY(100);  
  
    FXGL.getGameScene().addUINode(textPixels);  
    textPixels.textProperty().bind(FXGL.getWorldProperties()  
            .intProperty("pixelsMoved").asString());  
}
```

### 完整代码

```java  
import com.almasb.fxgl.app.GameApplication;  
import com.almasb.fxgl.app.GameSettings;  
import com.almasb.fxgl.dsl.FXGL;  
import com.almasb.fxgl.entity.Entity;  
import javafx.scene.input.KeyCode;  
import javafx.scene.paint.Color;  
import javafx.scene.shape.Rectangle;  
import javafx.scene.text.Text;  
  
import java.util.Map;  
  
public class BasicGameApp extends GameApplication {  
  
    @Override  
    protected void initSettings(GameSettings settings) {  
  
        settings.setWidth(600);  
        settings.setHeight(600);  
        settings.setTitle("Basic Game App");  
        settings.setVersion("0.1");  
    }  
  
    protected Entity player;  
  
    @Override  
    protected void initGame() {  
  
        player = FXGL.entityBuilder().at(300, 300).view(new Rectangle(25, 25, Color.BLUE)).buildAndAttach();  
    }  
  
    @Override  
    protected void initInput() {  
  
        FXGL.onKey(KeyCode.D, () -> {  
            player.translateX(5); // 向右 5 个像素  
            FXGL.inc("pixelsMoved", +5);  
        });  
        FXGL.onKey(KeyCode.A, () -> {  
            player.translateX(-5);  
            FXGL.inc("pixelsMoved", +5);  
  
        });  
        FXGL.onKey(KeyCode.W, () -> {  
            player.translateY(-5); // 向上 5 像素  
            FXGL.inc("pixelsMoved", +5);  
        });  
        FXGL.onKey(KeyCode.S, () -> {  
            player.translateY(5);  
            FXGL.inc("pixelsMoved", +5);  
        });  
    }  
  
    @Override  
    protected void initUI() {  
  
        Text textPixels = new Text();  
        textPixels.setTranslateX(50);  
        textPixels.setTranslateY(100);  
  
        FXGL.getGameScene().addUINode(textPixels);  
        textPixels.textProperty().bind(FXGL.getWorldProperties()  
                .intProperty("pixelsMoved").asString());  
    }  
  
    @Override  
    protected void initGameVars(Map<String, Object> vars) {  
  
        vars.put("pixelsMoved", 0);  
    }  
  
    public static void main(String[] args) {  
  
        launch(args);  
    }  
}
```
