# Animation 概述

2023-08-10, 21:09
modify: 样式
2023-07-27, 16:38
@author Jiawei Mao
****
## 1. 简介

在现实世界中，动画是通过快速连续地显示图形而产生的一种运动。在 JavaFX 中，动画通过 node 属性随时间变化而定义，如果改变的属性影响 node 位置，就会产生动画效果。

动画涉及以下概念：

- 时间轴（Timeline）
- 关键帧（Key frame）
- 关键值（Key value）
- 插值器（Interpolator）

动画在一段时间内执行，*timeline* 表示动画的时间轴。*key frame* 表示 `Node` 在特定时刻的状态。 *keyframe* 具有关联的 *keyvalue*，*keyvalue*  表示 node 的某个属性值以及要使用的插入器（interpolator）。

假设需要将 scene 中的一个圆在 10 秒内从左边移到右边。如下图所示：

- 粗的水平线表示时间轴
- 实线圆表示时间轴上特定时刻的 keyframe
- 实线圆上方为 keyframe 对应的 keyvalue，即 `translateX` 值

@import "images/Pasted%20image%2020230727144802.png" {width="500px" title=""}

例如：第 5 秒 keyframe 中 circle 的 `translateX` 属性值为 500，在上图中标识为 `tx=500`。

开发人员提供 timeline, keyframe, keyvalue。上例中有 5 个 keyframes，如果 JavaFX 只在 5 个不同时刻显示 5 个 keyframes，动画看起来就不顺滑。为了提供平滑的动画，JavaFX 需要在 timeline 的任意时刻插入 Circle 的位置。即 JavaFX 需要在两个连续的 keyframes 之间插入中间 keyframes。该插入操作通过**插值器**（interpolator）完成。

JavaFX 默认使用线性插值器，即随着时间线性地改变属性。

## 2. Animation 类

除了 `Duration` 类在 `javafx.util` 包中，其它动画相关的类都在 `javafx.animation` 包中。类图如下：

@import "images/Pasted%20image%2020230727150514.png" {width="px" title=""}

抽象类 `Animation` 包含动画相关的通用属性和方法。

JavaFX 支持两种类型的动画：

- timeline
- transition

在 timeline 动画中，创建 timeline 并添加 keyframes；JavaFX 使用插值器创建中间 keyframes。`Timeline` 类表示 timeline 动画。这类动画需要更多的代码，但更灵活。

有几种常用的动画，如 node 沿着路径移动、node 透明度随时间变化等。这种常见的动画称为 **transition**。

- transition 提前内置了 timeline，由 `Transition` 类表示
- `Transition` 有多个子类，用于提供不同类型的 transitions。例如，`FadeTransition` 随时间改变 node 的透明度实现渐隐动画
- 创建 `Transition` 实例（通常为其子类），为动画属性指定初始值、最终值和持续时间
- 由 JavaFX 创建 timeline 和执行动画
- transition 更容易使用

`SequentialTransition` 用于按顺序执行多个动画。`ParallelTransition` 用于同时执行多个动画。

## 3. 工具类

### 3.1. Duration

`javafx.util` 包中的 `Duration` 类表示持续时间，用于指定动画每个周期的时长。

创建 `Duration` 有三种方式：

- 构造函数
- factory 方法
- `valueOf()` 方法

构造函数的参数单位为毫秒：

```java
Duration tenMillis = new Duration(10);
```

`Duration` 为不同时间单位提供了 factory 方法：

```java
Duration tenMillis = Duration.millis(10);
Duration tenSeconds = Duration.seconds(10);
Duration tenMinutes = Duration.minutes(10);
Duration tenHours = Duration.hours(10);
```

`valueOf()` 参数为 String 类型，指定时间格式 `number[ms|s|m|h]`，其中 ms, s, m, h 分别表示分毫秒、秒、分钟和小时。

```java
Duration tenMillis = Duration.valueOf("10.0ms");
Duration tenMillisNeg = Duration.valueOf("-10.0ms");
```

`Duration` 还定义了几个常量：

- `UNKNOWN`：持续时间未知，使用 `isUnknown()` 检查 duration 是否为该类型
- `INDEFINITE`：持续时间无线，使用 `isIndefinite()` 检查 duration 是否为该类型
- `ONE`：1 毫秒
- `ZERO`：0

**示例：** Duration

```java{.line-numbers}
import javafx.util.Duration;

public class DurationTest {

    public static void main(String[] args) {
        Duration d1 = Duration.seconds(30.0);
        Duration d2 = Duration.minutes(1.5);
        Duration d3 = Duration.valueOf("35.25ms");
        System.out.println("d1  = " + d1);
        System.out.println("d2  = " + d2);
        System.out.println("d3  = " + d3);

        System.out.println("d1.toMillis() = " + d1.toMillis());
        System.out.println("d1.toSeconds() = " + d1.toSeconds());
        System.out.println("d1.toMinutes() = " + d1.toMinutes());
        System.out.println("d1.toHours() = " + d1.toHours());

        System.out.println("Negation of d1  = " + d1.negate());
        System.out.println("d1 + d2 = " + d1.add(d2));
        System.out.println("d1 / 2.0 = " + d1.divide(2.0));

        Duration inf = Duration.millis(1.0 / 0.0);
        Duration unknown = Duration.millis(0.0 / 0.0);
        System.out.println("inf.isIndefinite() = " + inf.isIndefinite());
        System.out.println("unknown.isUnknown() = " + unknown.isUnknown());
    }
}
```

```
d1  = 30000.0 ms
d2  = 90000.0 ms
d3  = 35.25 ms
d1.toMillis() = 30000.0
d1.toSeconds() = 30.0
d1.toMinutes() = 0.5
d1.toHours() = 0.008333333333333333
Negation of d1  = -30000.0 ms
d1 + d2 = 120000.0 ms
d1 / 2.0 = 15000.0 ms
inf.isIndefinite() = true
unknown.isUnknown() = true
```

### 3.2. KeyValue

`KeyValue` 类表示 keyvalue，`KeyValue` 封装了三个信息：

- target
- target 最终值
- 插值器

target 为 `WritableValue` 类型，所以所有 JavaFX 属性都可以为 target。

keyframe 定义了 timeline 上特定点，一个 keyframe 包含 1 到多个 keyvalues。下图展示了 timeline 上的一个 interval。

@import "images/Pasted%20image%2020230727161731.png" {width="400px" title=""}

interval 由两个时间点 instant1 和 instant2 定义。两个时间点都有关联的 keyframes，每个 keyframe 包含一个 keyvalue。

在 timeline 上动画可能向前，也可能后退。当 interval 开始，从该 interval 的 end keyframe 的 keyvalue 获取 target end value，然后用插值器计算中间 keyframes。

在上图中：

- 如果动画正向进行，从 instant1 到 instant2，使用 key-value2 的插值器计算该 interval 的中间 keyframes
- 如果动画逆向进行，从 instant2 到 instant1，则使用 key-value1 的插值器计算该 interval 的中间 keyframes

KeyValue 为 Immutable 类型，提供两个构造函数：

```java
KeyValue(WritableValue<T> target, T endValue)
KeyValue(WritableValue<T> target, T endValue, Interpolator interpolator)
```

默认使用 `Interpolator.LINEAR` 插值器。

**示例：** Text

- 创建 1 个 Text 和 2 个 KeyValue
- translateX 属性作为 target
- 0 和 100 为 target 的端点值

```java
Text msg = new Text("JavaFX animation is cool!");
KeyValue initKeyValue = new KeyValue(msg.translateXProperty(), 0.0);
KeyValue endKeyValue = new KeyValue(msg.translateXProperty(), 100.0);
```

**示例：** Text

`Interpolator.EASE_BOTH`  插值器在动画开始和结束时减慢速度

```java
Text msg = new Text("JavaFX animation is cool!");
KeyValue initKeyValue = new KeyValue(msg.translateXProperty(), 0.0,
                                     Interpolator.EASE_BOTH);
KeyValue endKeyValue = new KeyValue(msg.translateXProperty(), 100.0,
                                    Interpolator.EASE_BOTH);
```

### 3.3. KeyFrame

`keyframe` 定义 timeline 上指定时间点 node 的 target 状态。target 状态由 keyvalue 定义。

`keyframe` 包含 4 部分：

- instant on the timeline
- KeyValue
- name
- ActionEvent handler

keyframe 的 instant 由 Duration 定义，表示 keyframe 相对起始点的偏移时间。

KeyValue 定义 keyframe 中 target 的端点值。

可以为 keyframe 指定一个 name。

还可以为 keyframe 指定 ActionEvent handler，当动画执行到该 keyframe，其 handler 被调用。

KeyFrame 类表示 keyframe，提供如下构造函数：

```java
KeyFrame(Duration time, EventHandler<ActionEvent> onFinished, KeyValue... values)

KeyFrame(Duration time, KeyValue... values)

KeyFrame(Duration time, String name, 
         EventHandler<ActionEvent> onFinished, Collection<KeyValue> values)

KeyFrame(Duration time, String name, 
         EventHandler<ActionEvent> onFinished, KeyValue... values)

KeyFrame(Duration time, String name, KeyValue... values)
```

**示例：** 使用 Text 的 translateX 属性作为 target，创建 2 个 KeyFrame

```java
Text msg = new Text("JavaFX animation is cool!");
KeyValue initKeyValue = new KeyValue(msg.translateXProperty(), 0.0);
KeyValue endKeyValue = new KeyValue(msg.translateXProperty(), 100.0);

KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);
KeyFrame endFrame = new KeyFrame(Duration.seconds(3), endKeyValue);
```

