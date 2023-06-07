# 坐标轴

## Axis

支持线性、对数、反向、时间序列等坐标轴，类图如下：

![[Pasted image 20230605160919.png]]
其中：

- `DefaultNumericAxis` 是默认数值坐标轴实现
- `CategoryAxis` 是分类坐标轴实现

这两个使用最多。

### autoRanging

自动根据数据确定坐标轴范围。

## AbstractAxisParameter

定义 `AbstractNumericAxis` 类的属性，该类的目的是将模板代码从 `AbstractNumericAxis` 移除，增加其代码可读性。

### animatedProperty

```java
public BooleanProperty animatedProperty()
```

当坐标轴发生变化时，是否带动画效果。

### sideProperty

```java
public ObjectProperty<Side> sideProperty()
```

设置坐标轴的位置。默认在底部（BOTTOM）。

```java
public enum Side {
    /**
     * Represents top side of a rectangle.
     */
    TOP,

    /**
     * Represents bottom side of a rectangle.
     */
    BOTTOM,

    /**
     * Represents left side of a rectangle.
     */
    LEFT,

    /**
     * Represents right side of a rectangle.
     */
    RIGHT,
    /**
     * Represents horizontal centre axis of a rectangle.
     */
    CENTER_HOR,
    /**
     * Represents vertical centre axis of a rectangle.
     */
    CENTER_VER;
}
```

### overlapPolicy

对轴标签重叠的处理策略。指定方式：

```java
axis.setOverlapPolicy(AxisLabelOverlapPolicy.SHIFT_ALT);
```

`AxisLabelOverlapPolicy` 为 enum 类型：

| AxisLabelOverlapPolicy | 说明                                                      |
| ---------------------- | --------------------------------------------------------- |
| DO_NOTHING             | 允许重叠                                                  |
| SKIP_ALT               | 使下一个标签不可见                                        |
| NARROW_FONT            | 缩小字体                                                  |
| SHIFT_ALT              | 对下一个 label 移动一个 label 的高度/宽度（适合于分类轴） |
| FORCED_SHIFT_ALT       | 同上                                                          |

### maxMajorTickLabelCount

major tick 的最大个数。设置：

```java
axis.setMaxMajorTickLabelCount(N_SAMPLES + 1);
```

## DefaultNumericAxis

数值坐标轴的默认实现，支持 Long, Double, BigDecimal 等数值类型。相比 JavaFX 的 `NumberAxis`，包含一些额外功能：

- 关闭 `autoRangingProperty()` 属性后重新计算 tick unit
- 支持 auto-range padding : `autoRangePaddingProperty()`
- 支持 auto-range rounding

当启用 `autoRangingProperty()` 属性，是否将范围扩展到 major tick unit 值。例如，如果范围是 [3,74]，而 major tick unit 为 [5]，那么将范围扩展到 [0,75]。默认 true。

- 支持自定义 `tickUnitSupplierProperty`

### autoRangePadding

通过 `autoRangePaddingProperty()` 属性设置，表示在 axis range 两边填充的比例。例如，如果 axis 的数据范围是 [10,20]，将其设置为 0.1，那么填充后的范围是 [9,21]。

## CategoryAxis

`CategoryAxis` 用于字符串类型的分类数据。

- `public CategoryAxis(final String axisLabel)`

创建指定轴标签的分类轴。


