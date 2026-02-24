# Interpolator

2023-07-31, 16:50
****
## 1. 简介

Interpolator 抽象类表示插值器（interpolator），插值器负责在动画过程中计算中间 keyframes 的 keyvalue。

自定义插值器也不难，继承 `Interpolator` 类并实现 `curve()` 方法。传入 `curve()` 的参数为当前 interval 经过的时间，该时间被归一化到 0.0 到 1.0 之间。0.5 表示该 interval 过了一般。`curve()` 返回值表示动画属性变化的百分比。

例如，下面为线性插值器实现：

```java
public static final Interpolator LINEAR = new Interpolator() {
    @Override
    protected double curve(double t) {
        return t;
    }

    @Override
    public String toString() {
        return "Interpolator.LINEAR";
    }
};
```

线性插值器动画属性变化比例与时间变化比例相同。

自定义插值器后，在基于 Timeline 的动画中可以使用插值器构造 keyframes；在基于 Transition 的动画中，可以将其作为 Transition 类的 `interpolator` 属性使用。

动画 API 调用 `Interpolator.interpolate()`：

- 如果动画属性为 `Number` 类型，返回

```java
startValue + (endValue - startValue) * curve(timeFraction)
```

- 如果动画属性为 `Interpolatable` 类型，则将差值工作委托给 Interpolatable 的 interpolate()
- 否则，插值器默认为离散的，当 timeFraction 为 1.0 时返回 1.0，其它时候返回 0

JavaFX 提供了动画中常用的标准插值器。以静态方法的时候在 Interpolator 类中提供：

- Linear interpolator
- Discrete interpolator
- Ease-in interpolator
- Ease-out interpolator
- Ease-both interpolator
- Spline interpolator
- Tangent interpolator

## 2. Linear

`Interpolator.LINEAR` 常量定义线性插值器。

线性插值器动画属性与时间成线性关系。即动画属性变化比例与 interval 持续时间中度过的时间比例相同。

## 3. Discrete

`Interpolator.DISCRETE` 常量定义离散插值器。

离散插值器从一个 keyframe 调到下一个 keyframe，没有中间 keyframe：

- timeFrame 为 1.0 时，curve() 返回 1.0
- 其它情况 curve() 返回 0.0

即动画属性值在整个 duration 保持为初始值，在最后才调到端点值。

**示例：** 所有 keyFrames 都采用离散插值器

```java
import javafx.animation.Interpolator;
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

public class HoppingText extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text msg = new Text("Hopping text!");
        msg.setTextOrigin(VPos.TOP);
        msg.setFont(Font.font(24));
		
        Pane root = new Pane(msg);
        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Hopping Text");
        stage.show();

        // Setup a Timeline animation
        double start = scene.getWidth();
        double end = -1.0 * msg.getLayoutBounds().getWidth();

        KeyFrame[] frame = new KeyFrame[11];
        for (int i = 0; i <= 10; i++) {
            double pos = start - (start - end) * i / 10.0;

            // Set 2.0 seconds as the cycle duration
            double duration = i / 5.0;

            // Use a discrete interpolator
            KeyValue keyValue = new KeyValue(msg.translateXProperty(),
                    pos,
                    Interpolator.DISCRETE);
            frame[i] = new KeyFrame(Duration.seconds(duration), keyValue);
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }
}
```

![|500](images/ani10.gif)

## 4. Ease-In

`Interpolator.EASE_IN` 实现一个缓入的效果，它在前 20% 的时间缓慢启动动画，然后加速。

## 5. Ease-Out

`Interpolator.EASE_OUT` 实现一个缓出的效果。在前 80% 的时间常速播放动画，后 20% 时间减缓速度。

## 6. Ease-Both

`Interpolator.EASE_BOTH` 实现缓入缓出的动画效果。前 20% 和后 20% 时间动画速度较慢。

## 7. Spline

静态方法 `Interpolator.SPLINE(double x1, double y1, double x2, double y2)` 返回一个样条插值器。

`SPLINE` 使用三次样条形状计算动画在任意时间点的速度，参数 `(x1, y1)` 和 `(x2, y2)` 定义三次样条线的控制点，默认分别为 (0, 0) 和 (1, 1)。

三次样条线上点的斜率定义动画的加速度。接近水平方向表示减速，接近垂直方向表示加速。例如：

- `SPLINE` 参数 `(0, 0, 1, 1)` 定义恒定速度的插值器
- `SPLINE` 参数 `(0.5, 0, 0.5, 1)` 前半段加速，后半段减速

详情可参考： https://www.w3.org/TR/SMIL/smil-animation.html#animationNS-OverviewSpline

## 8. Tangent

`Interpolator.TANGENT` 定义切线插值器，定义动画在指定 keyFrame 前后的行为。

其它插值器都是在两个 keyframes 之间插值，而 `TANGENT` 在一个 keyFrame 前后插值。

动画曲线根据切线定义，在 keyframe 之前指定时间内称为 in-tangent，在 keyframe 之后指定时间内称为 out-tangent。

该插值器只用于 Timeline 动画中。`TANGENT` 定义了重载方法：

```java
Interpolator TANGENT(Duration t1, double v1, Duration t2, double v2)
Interpolator TANGENT(Duration t, double v)
```

在第一个版本，参数 t1 和 t2 指定 keyframe 前后的 duration。参数 v1 和 v2 为 in-tangent 和 out-tangent。

在第二个版本，keyframe 前后的参数相同。
