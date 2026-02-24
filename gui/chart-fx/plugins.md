# ChartFX 插件

## 添加插件

- 一次添加一个插件

```java
chart.getPlugins().add(new Zoomer());  
chart.getPlugins().add(new EditAxis());  
chart.getPlugins().add(new EditDataSet());  
chart.getPlugins().add(new DataPointTooltip());  
chart.getPlugins().add(new UpdateAxisLabels());
```

- 一次添加多个插件

```java
chart.getPlugins().addAll(new Zoomer(), new CrosshairIndicator(), new EditAxis());
```

## Zoomer

实现 X/Y 轴的缩放功能。每次放大时，会记住当前的 X  和 Y 范围，并在随后的缩小操作中恢复：

- zoom-in, 由通过 zoom-in 过滤器的 `MOUSE_PRESSED` 事件触发。 会显示一个矩形，松开鼠标按钮，确定缩放窗口。

默认按下鼠标左键拖动。

- zoom-out，由通过 zoom-out 过滤器的 `MOUSE_CLICKED` 事件触发。恢复两个轴上一次的范围。

默认点击鼠标右键。

- zoom-origin，由通过 zoom-origin 过滤器的 `MOUSE_CLICKED` 事件触发。两个轴恢复到初始范围。

默认，Ctrl + 点击鼠标右键。



## CrosshairIndicator

在绘图区域，以鼠标光标位置显示一个十字叉。同时显示坐标数值。

## EditAxis

允许编辑坐标轴，如 autorange, min/max range 等。

在坐标轴区域鼠标右键即可编辑这些信息。

## EditDataSet

和实现 `EditableDataSet` 接口的 `Dataset` 进行交互。

## DataPointTooltip

将光标放在数据的 symbol 上时，显示一个提示标签。

## ParameterMeasurements

