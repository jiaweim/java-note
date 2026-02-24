# 插件

## 简介

`ChartPlugin` 表示 Chart 的插件，为 Chart 添加**注释**或**装饰**，也可以与 Chart 进行交互。

插件实现类可以通过 getChartChildren() 将自定义 Node 添加到 Chart。getChartChildren() 返回一个 observable 和 modifiable list of nodes，这些 Nodes 将添加到 XYChartPane 的顶部。

插件也可以通过 `registerInputEventHandler(EventType, EventHandler)` 方法监听和响应 XYChartPane 上生成的事件。

@import "images/Pasted%20image%2020230807201720.png" {width="px" title=""}

- [Panner: 拖动图表区域](./panner.md)
- [EditAxis: 编辑坐标轴](./editaxis.md)

## 事件

插件可以通过 `registerInputEventHandler(EventType, EventHandler)` 方法监听和响应 `XYChartPane` 上生成的事件。

`ChartPlugin` 的 `chart` 属性指向关联的 Chart。

```java
private final ObjectProperty<Chart> chart = new SimpleObjectProperty<>(this, "chart");
    
public final ObjectProperty<Chart> chartProperty() {
    return chart;
}

public final Chart getChart() {
    return chartProperty().get();
}

public final void setChart(final Chart chart) {
    chartProperty().set(chart);
}
```

在 `ChartPlugin` 构造函数中，通过 chart 属性设置 event handler：

```java
protected ChartPlugin() {
    chartProperty().addListener((obs, oldChart, newChart) -> {
        if (oldChart != null) {
            removeEventHandlers(oldChart.getPlotArea());
            removeEventHandlers(oldChart.getPlotBackground());
        }
        if (newChart != null) {
            addEventHandlers(newChart.getPlotArea());
            addEventHandlers(newChart.getPlotBackground());
        }
    });
}
```

