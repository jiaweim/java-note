# Transition

## 简介

使用 Timeline 需要设置 keyframes，使用起来相对麻烦。例如，创建 keyframes 并设置 Timeline 让 node 沿着圆移动就不容易实现。JavaFX 提供了许多类，可以使用预定义属性对 node 实现动画，这些类就是 transition。

所有 transition 都继承 Transition 类，而 Transition 又继承自 Animation，所以 Animation 的属性很方法在 Transition 也能用。

使用 Transition 只需要设置 node、duration 和 end-value。

Animation 的 interpolator 属性指定插值器。默认为 `Interpolator.EASE_BOTH`。

## FadeTransition

FadeTransition 类在指定时间内逐渐增加或减少 node `opacity` 实现淡入或淡出效果。该类定义了如下属性来指定动画：

- duration：指定一个动画周期的持续时间
- node：该 node 的 `opacity` 属性被修改
- fromValue：`opacity` 的初始值，如果不指定，使用 node 当前 `opacity`
- toValue：`opacity` 的端点值，动画期间，node 的 `opacity` 在 `fromValue` 和 `toValue` 之间变化
- byValue：指定 opacity 端点值的另一种方式，此时 endValue=fromValue+byValue

如果同时指定 toValue 和 byValue，则使用 toValue。

例如，假设你想在动画中将 node 的 opacity 设置在 1.0 到 0.5 之间。那么可以设置 fromValue 为 1.0，toValue 为 0.5；也可以设置 fromValue 为 1.0，byValue 为 -0.50.

opacity 有效值在 0.0 到 1.0 之间，超出该范围 FadeTransition 会自动截断。

**示例：** 为 Rectangle 实现淡出效果，在 2 秒内 opacity 从 1.0 到 0.2

```java
Rectangle rect = new Rectangle(200, 50, Color.RED);
FadeTransition fadeInOut = new FadeTransition(Duration.seconds(2), rect);
fadeInOut.setFromValue(1.0);
fadeInOut.setToValue(.20);
fadeInOut.play();
```

**示例：** 为 Rectangle 实现淡出和淡入动画的无效循环

```java
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FadeTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(200, 50, Color.RED);
        HBox root = new HBox(rect);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Fade-in and Fade-out");
        stage.show();

        // Set up a fade-in and fade-out animation for the rectangle
        FadeTransition fadeInOut = new FadeTransition(Duration.seconds(2), rect);
        fadeInOut.setFromValue(1.0);
        fadeInOut.setToValue(.20);
        fadeInOut.setCycleCount(FadeTransition.INDEFINITE);
        fadeInOut.setAutoReverse(true);
        fadeInOut.play();
    }
}
```

![](images/ani2.gif)

## FillTransition

`FillTransition` 通过在指定范围和 duration 逐步改变 `Shape` 的 `fill` 属性实现填充过度效果。

该类定义了如下属性来定义动画：

- duration：指定一个动画周期的持续时间
- shape：`fill` 属性被更改的 Shape
- fromValue：初始 `fill` 值，默认为 Shape 的当前 `fill`
- toValue：末端 `fill` 值

在一个动画周期，Shape 的 `fill` 属性从 fromValue 到 toValue。

Shape 的 fill 属性为 Paint 类型，而 fromValue 和 toValue 为 Color 类型，即 FillTransition 只支持 Color 渐变。

**示例：** Rectangle 的 fill 属性在 2 秒内从 blueviolet 变为 blue

```java
FillTransition fillTransition = new FillTransition(Duration.seconds(2), rect);
fillTransition.setFromValue(Color.BLUEVIOLET);
fillTransition.setToValue(Color.AZURE);
fillTransition.play();
```

**示例：** 上个示例的完整实现

```java
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FillTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle rect = new Rectangle(200, 50, Color.RED);
        HBox root = new HBox(rect);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Fill Transition");
        stage.show();

        // Set up a fill transition for the rectangle
        FillTransition fillTransition = new FillTransition(Duration.seconds(2), rect);
        fillTransition.setFromValue(Color.BLUEVIOLET);
        fillTransition.setToValue(Color.AZURE);
        fillTransition.setCycleCount(FillTransition.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
    }
}
```

![](images/ani3.gif)

## StrokeTransition

StrokeTransition 实现 `stroke` 属性的动画，与 FillTransition 的使用基本一致。

StrokeTransition 定义的属性与 FillTransition 一样，即：

- duration：指定一个动画周期的持续时间
- shape：`stroke` 属性被更改的 Shape
- fromValue：初始 `stroke` 值，默认为 Shape 的当前 `stroke`
- toValue：末端 `stroke` 值

**示例：** Rectangle 的 stroke 动画，stroke 从 red 到 blue，2 秒一个周期

```java
Rectangle rect = new Rectangle(200, 50, Color.WHITE);
StrokeTransition strokeTransition = 
                    new StrokeTransition(Duration.seconds(2), rect);
strokeTransition.setFromValue(Color.RED);
strokeTransition.setToValue(Color.BLUE);
strokeTransition.setCycleCount(StrokeTransition.INDEFINITE);
strokeTransition.setAutoReverse(true);
strokeTransition.play();
```

## TranslateTransition

TranslateTransition  类对 Node 的translateX, translateY 和 translateZ 属性实现动画。该类定义了如下属性：

- duration：指定一个动画周期的持续时间
- node
- fromX
- fromY
- fromZ
- toX
- toY
- toZ
- byX
- byY
- byZ

(fromX, fromY, fromZ) 指定 node 初始位置，默认为 (translateX, translateY, translateZ)。

