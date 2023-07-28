# 动画控制

2023-07-27, 22:02
****
## 1. 简介

Animation 提供了许多控制动画相关的属性和方法。

## 2. 播放动画

`Animation` 类包含 4 个播放动画的方法：

```java
play()
playFrom(Duration time)
playFrom(String cuePoint)
playFromStart()
```

`play()` 从当前位置播放动画：

- 如果动画从未开始或停止过，则从头开始播放
- 如果动画处于暂停状态，则从暂停的位置播放

在调用 `play()` 前，可以使用 `jumpTo(Duration time)` 和 `jumpTo(String cuePoint)` 跳转到指定时刻或 cuePoint。

调用 `play()` 是异步的，所以动画可能不会立刻开始。动画运行时调用 play() 无效。

`playFrom()` 从指定时刻或 cuePoint 播放动画。该方法等价于先调用 jumpTo() 跳转，然后调用 play() 播放。

`playFromStart()` 从头开始播放动画（duration=0）。

## 3. 启动延迟

可以使用 `delay` 属性设置动画开始的延迟时间，默认 0 毫秒。

```java
Timeline timeline = ...

// Delay the start of the animation by 2 seconds
timeline.setDelay(Duration.seconds(2));
// Play the animation
timeline.play();
```

## 4. 停止播放

`Timeline.stop()` 停止动画播放，如果动画没有播放，调用该方法无效。

调用 `stop()` 动画可能不会立刻停止，因为该方法也是异步执行。

`stop()` 会重置位置到起始位置，即调用 stop() 之后调用 play() 将从头开始播放。

```java
Timeline timeline = ...
...
timeline.play();
...
timeline.stop();
```

## 5. 暂停播放

Timeline.pause() 暂停动画播放。动画没有播放时调用该方法无效。pause() 也是异步执行。

调用 pause() 暂停后再调用 play()，从暂停位置开始播放。如果想从头开始播放动画，可以使用 playFromStart()。

## 6. 动画状态

动画有三种状态：

- 播放
- 暂停
- 停止

这三种状态由 `Animation.Status` enum 表示：`RUNNING`, `STOPPED`, `PAUSED`。

不能直接修改 Animation 状态，而是通过调用 Animation 的方法来改变。Animation 包含一个 read-only 属性 status，可以查看 Animation 当前状态。

```java
Timeline timeline = ...
...
Animation.Status status = timeline.getStatus();
switch(status) {
    case RUNNING:
        System.out.println("Running");
        break;
    case STOPPED:
        System.out.println("Stopped");
        break;
    case PAUSED:
        System.out.println("Paused");
        break;
}
```

## 7. 动画循环

动画可以循环播放，甚至无效循环播放。cycleCount 属性指定循环周期数，默认为 1：

- 将 cycleCount 设置为 Animation.INDEFINITE 表示无限循环
- cycleCount 需要大于 0
- 在动画运行过程中修改 cycleCount，需要停止动画再重启，修改才有效

```java
Timeline timeline1 = ...
Timeline1.setCycleCount(Timeline.INDEFINITE); // Run the animation forever

Timeline timeline2 = ...
Timeline2.setCycleCount(2); // Run the animation for two cycles
```

## 8. 动画反转

动画默认只沿一个方向。例如，前面的滚动文本动画，在一个周期内从右向左滚动，在下一个循环，再次从右向左进行。

`autoReverse` 属性指定是否正反交替进行，默认 false。如果为 true，那么第一个循环正向进行，第二个循环反向进行，以此类推。

在动画运行时设置无效，需要先关闭动画，再重新启动。

```java
Timeline timeline = ...
timeline.setAutoReverse(true); // Reverse direction on alternating cycles
```

## 9. onFinished

可以在动画自然结束时执行 ActionEvent handler。在动画运行时停止动画或终止 Application 不会执行该 handler。

通过 Animation 的 onFinished 属性指定该 handler。

**示例：** 为 Animation 的 onFinished 属性设置 ActionEvent handler，在 stdout 输出信息

```java
Timeline timeline = ...
timeline.setOnFinished(e -> System.out.print("Animation finished."));
```

```ad-warning
循环数为 `Animation.INDEFINITE` 的 Animation 不会自然结束，设置该 handler 没有作用。
```

## 10. 动画持续时间

Animation 包含两种 durations:

- 单个动画周期的 duration
- 所有动画周期的 duration

这些 durations 不是直接设置，而是通过 Animation 的其它属性间接设置：cycleCount, keyFrames 等。

单个周期的持续时间使用 keyframe 设置。duration 最大的 keyframe 决定了一个周期的 duration。Animation 的 read-only 属性 cycleDuration 即为一个周期的 duration。

 read-only 属性 totalDuration 表示动画总的 duration。等于 `cycleCount * cycleDuration`。如果 cycleCount 为 `Animation.INDEFINITE`，那么 totalDuration 为 `Duration.INDEFINITE`。

注意，动画实际的 duration 还取决于播放速率 `rate` 属性。播放速率可以在动画播放时改变，所以没法计算。

## 11. 动画速度

`Animation` 的 `rate` 属性指定动画的方向和速度：

- rate 的正负表示不同方向：正数表示正向播放，负数为反向播放。
- rate 的绝对值表示速度：1.0 表示正常播放速度，2.0 表示 2 倍速播放，0.5 表示正常播放速度的一半，0.0 表示停止播放。

使用 rate 可以反转正在运行的动画。动画从当前位置反向播放一段时间。

注意，rate 为负数时无法启动动画，即只能在动画启动后才能将 rate 设置为负数。

```java
Timeline timeline = ...
// Play the animation at double the normal rate
Timeline.setRate(2.0);
...
timeline.play();
...
// Invert the rate of the play
timeline.setRate(-1.0 * timeline.getRate());
```

read-only 属性 currentRate 表示动画播放当前的 rate。rate 和 currentRate 属性可能不同：

- rate 属性表示动画播放时的期望速度
- currentRate 表示实际速度

当动画停止或暂停时，currentRate 值为 0。当动画自动反转时，currentRate 在不同方向值不对。例如，如果 rate 为 1.0，正向时 currentRate 为 1.0，反向时 currentRate 为 -1.

