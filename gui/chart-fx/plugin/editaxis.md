# EditAxis

## 简介

EditAxis 插件实现编辑坐标轴的功能，包括 autoRange, 最小值和最大值等。

## AxisMode

设置模式。相关代码：

```java{.line-numbers}
private final ObjectProperty<AxisMode> axisMode = new SimpleObjectProperty<>(this, "axisMode", AxisMode.XY) {
    @Override
    protected void invalidated() {
        Objects.requireNonNull(get(), "The " + getName() + " must not be null");
    }
};
public final ObjectProperty<AxisMode> axisModeProperty() {
    return axisMode;
}
public final AxisMode getAxisMode() {
    return axisModeProperty().get();
}
public final void setAxisMode(final AxisMode mode) {
    axisModeProperty().set(mode);
}
```

AxisMode 用于设置哪些坐标轴支持编辑。

## AxisEditor

AxisEditor 提供坐标轴编辑界面。界面如下：

- layoutPane 为 BorderPane
- 第一行显示坐标轴名称
- 第二行显示坐标轴 unit
- 

@import "images/2023-08-09-20-02-50.png" {width="250px" title=""}



## MyPopOver

EditAxis 类中包含一个内部类 MyPopOver，该类继承 ControlsFX 的 PopOver，以弹窗的形式提供坐标轴编辑界面。

## animated

`PopOver` 的弹窗动画属性，即弹出窗口时是否显示淡入、淡出动画效果。相关代码：

```java{.line-numbers}
private final BooleanProperty animated = new SimpleBooleanProperty(this, "animated", false);

public final BooleanProperty animatedProperty() {
    return animated;
}

public final boolean isAnimated() {
    return animatedProperty().get();
}

public final void setAnimated(final boolean value) {
    animatedProperty().set(value);
}
```

在 `MyPopOver` 类中，将 `PopOver` 的 `animatedProperty` 属性与该属性绑定：

```java
super.animatedProperty().bind(EditAxis.this.animatedProperty());
```

## 实现原理

在构造函数中添加了 handler

```java
public EditAxis(final AxisMode editMode, final boolean animated) {
    super();
    setAxisMode(editMode);
    setAnimated(animated);

    chartProperty().addListener((obs, oldChart, newChart) -> {
        removeMouseEventHandlers(oldChart);
        addMouseEventHandlers(newChart);
    });
}
```

其中 `addMouseEventHandlers` 为执行逻辑：

```java
protected void addMouseEventHandlers(final Chart newChart) {
    if (newChart == null) {
        return;
    }
    newChart.getAxes().forEach(axis -> popUpList.add(new MyPopOver(axis, axis.getSide().isHorizontal())));
    newChart.getAxes().addListener(this::axesChangedHandler);
}

private void axesChangedHandler(@SuppressWarnings("unused") Change<? extends Axis> ch) {
    removeMouseEventHandlers(null);
    addMouseEventHandlers(getChart());
}
```


