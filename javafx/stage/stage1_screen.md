# Screen

2023-08-09, 16:46
modify: 样式
2023-07-04, 20:44
****
## 1. 简介

`javafx.stage.Screen` 类用于获得屏幕的详细信息，如 DPI，尺寸、设置等。如果有多个屏幕，则一个称为主屏幕（primary），其他的为副屏幕。

## 2. 获得主屏幕

```java
Screen primaryScreen = Screen.getPrimary();
```

## 3. 获得所有屏幕

```java
ObservableList<Screen> screenList = Screen.getScreens();
```

## 4. 屏幕分辨率

```java
Screen primaryScreen = Screen.getPrimary();
double dpi = primaryScreen.getDpi();
```

## 5. 屏幕大小

```java
// 获得屏幕大小
Rectangle2D getBounds()

// 获得屏幕可视区域大小
Rectangle2D getVisualBounds()
```

**可视区域**排除了系统本地窗口使用的区域（如任务栏和菜单栏）之后，屏幕剩余的区域，所以一般来说，可视区域小于整个屏幕区域。

如果一个桌面跨越多个屏幕，则副屏的 bounds 是相对主屏幕定义的。例如，如果一个桌面跨越两个屏幕，主屏幕左上角坐标为 (0, 0)，宽度为 1600，则副屏幕左上角的坐标为 (1600, 0)。

## 6. 示例

!!! note
    虽然 `Screen` 类的API没有明说，但是 `Screen` 只能在 JavaFX 程序中使用。即在 JavaFX launcher 启动后才能使用，但是不需要在 JAT 线程中，在 `init()` 方法中也能使用。

```java{.line-numbers}
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScreenDetailsApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        ObservableList<Screen> screenList = Screen.getScreens();
        System.out.println("Screens Count: " + screenList.size());

        // Print the details of all screens
        for (Screen screen : screenList) {
            print(screen);
        }

        Platform.exit();
    }

    public void print(Screen s) {
        System.out.println("DPI: " + s.getDpi());

        System.out.print("Screen Bounds: ");
        Rectangle2D bounds = s.getBounds();
        print(bounds);

        System.out.print("Screen Visual Bounds: ");
        Rectangle2D visualBounds = s.getVisualBounds();
        print(visualBounds);
        System.out.println("-----------------------");
    }

    public void print(Rectangle2D r) {
        System.out.format("minX=%.2f, minY=%.2f, width=%.2f, height=%.2f%n",
                r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }
}
```

```
Screens Count: 1
DPI: 93.0
Screen Bounds: minX=0.00, minY=0.00, width=2560.00, height=1440.00
Screen Visual Bounds: minX=0.00, minY=0.00, width=2560.00, height=1392.00
-----------------------
```