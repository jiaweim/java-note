# Panner

2023-08-09, 15:32
@author Jiawei Mao
****
## 1. 简介

支持沿 X 轴和/或 Y 轴拖动可见绘图区域，改变可见轴的范围。

## 2. 模式

模式以 enum 类 AxisMode 表示，默认为 `AxisMode.XY`，表示可以在 X 轴和 Y 轴两个方向拖动。相关方法：

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

另外，在构造函数中设置了默认模式为 `AxisMode.XY`：

```java{.line-numbers}
public Panner() {
    this(AxisMode.XY);
}

public Panner(final AxisMode panMode) {
    setAxisMode(panMode);
    setDragCursor(Cursor.CLOSED_HAND);
    registerMouseHandlers();
}
```

## 3. 光标样式

拖动时采用不同的光标样式。光标相关字段和方法：

```java
private Cursor originalCursor;

// 设置 drag cursor
private void installCursor() {
    originalCursor = getChart().getCursor();
    if (getDragCursor() != null) {
        getChart().setCursor(getDragCursor());
    }
}

// 恢复到原来的 cursor
private void uninstallCursor() {
    getChart().setCursor(originalCursor);
}

private final ObjectProperty<Cursor> dragCursor = new SimpleObjectProperty<>(this, "dragCursor");

public final ObjectProperty<Cursor> dragCursorProperty() {
    return dragCursor;
}

public final Cursor getDragCursor() {
    return dragCursorProperty().get();
}

public final void setDragCursor(final Cursor cursor) {
    dragCursorProperty().set(cursor);
}
```

`originalCursor` 表示 Chart 原来的光标样式，`dragCursor` 表示拖动时的光标样式。

## 4. 鼠标事件过滤

`mouseFilter` 用于过滤 MouseEvent，只有 MouseEvent 通过该 filter，才触发 pan 操作。

相关代码：

```java
public static final Predicate<MouseEvent> DEFAULT_MOUSE_FILTER = MouseEventsHelper::isOnlyMiddleButtonDown;

private Predicate<MouseEvent> mouseFilter = Panner.DEFAULT_MOUSE_FILTER;

private final EventHandler<MouseEvent> panStartHandler = event -> {
    if (mouseFilter == null || mouseFilter.test(event)) {
        panStarted(event);
        event.consume();
    }
};

public Predicate<MouseEvent> getMouseFilter() {
    return mouseFilter;
}

public void setMouseFilter(final Predicate<MouseEvent> mouseFilter) {
    this.mouseFilter = mouseFilter;
}
```

## 5. 实现逻辑

Panner 类的构造函数：

```java
public Panner(final AxisMode panMode) {
    setAxisMode(panMode);
    setDragCursor(Cursor.CLOSED_HAND);
    registerMouseHandlers();
}
```

在其中注册了 3 鼠标事件 handlers，分别相应鼠标按下、拖动和释放事件：

```java
private void registerMouseHandlers() {
    registerInputEventHandler(MouseEvent.MOUSE_PRESSED, panStartHandler);
    registerInputEventHandler(MouseEvent.MOUSE_DRAGGED, panDragHandler);
    registerInputEventHandler(MouseEvent.MOUSE_RELEASED, panEndHandler);
}
```

**`panStartHandler` 实现**

- 其中 `mouseFilter` 默认为按下鼠标中键，即按下鼠标中间键拖动
- 记录鼠标在 plot-area 中的当前位置，设置 drag cursor

```java
private final EventHandler<MouseEvent> panStartHandler = event -> {
    if (mouseFilter == null || mouseFilter.test(event)) {
        panStarted(event);
        event.consume();
    }
};

private void panStarted(final MouseEvent event) {
    previousMouseLocation = getLocationInPlotArea(event);
    installCursor();
}
```

**`panDragHandler` 实现**

- panDragHandler 实现拖动鼠标时的效果
- 获取鼠标当前位置
- 设置 chart 位置
- 将 previousMouseLocation 设置为当前 mouseLocation

关键是 `panChart` 方法。

```java
private final EventHandler<MouseEvent> panDragHandler = event -> {
    if (panOngoing()) {
        panDragged(event);
        event.consume();
    }
};

private void panDragged(final MouseEvent event) {
    final Point2D mouseLocation = getLocationInPlotArea(event);
    panChart(getChart(), mouseLocation);
    previousMouseLocation = mouseLocation;
}
```

**panChart 实现**

- 获取之前鼠标位置在对应 Axis 的值
- 获得当前鼠标位置在对应 Axis 的值
- 计算两个值之间的差值
- 判断当前模式是否允许该拖动
- 判断 Axis 的最小值和最大值是否为绑定值
- 如果不是绑定值，且当前模式允许该拖动，关掉 axis 的 autoRanging，根据拖动距离重新设置 axis 的最小值和最大值

```java
private void panChart(final Chart chart, final Point2D mouseLocation) {
    if (!(chart instanceof XYChart)) {
        return;
    }
    for (final Axis axis : chart.getAxes()) {
        if (axis.getSide() == null) {
            continue;
        }
        final Side side = axis.getSide();

        final double prevData = axis.getValueForDisplay(
                side.isHorizontal() ? previousMouseLocation.getX() : previousMouseLocation.getY());
        final double newData = axis
                .getValueForDisplay(side.isHorizontal() ? mouseLocation.getX() : mouseLocation.getY());
        final double offset = prevData - newData;

        final boolean allowsShift = side.isHorizontal() ? getAxisMode().allowsX() : getAxisMode().allowsY();
        if (!(axis.minProperty().isBound() || axis.maxProperty().isBound()) && allowsShift) {
            axis.setAutoRanging(false);
            shiftBounds(axis, offset);
        }
    }
}
```

**panEndHandler 实现**

- 如果 previousMouseLocation 不为 null，表示 pan 还在进行
- 结束 pan，即将 previousMouseLocation 设置为 null
- 将鼠标光标恢复原来值

```java{.line-numbers}
private final EventHandler<MouseEvent> panEndHandler = event -> {
    if (panOngoing()) {
        panEnded();
        event.consume();
    }
};

private boolean panOngoing() {
    return previousMouseLocation != null;
}

private void panEnded() {
    previousMouseLocation = null;
    uninstallCursor();
}
```