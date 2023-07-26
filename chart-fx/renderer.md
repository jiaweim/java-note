# 渲染器

## Renderer

Renderer 是实际绘制数据点的类，不同 `Renderer` 对应不同的图表类型。类图如下：

![](Pasted%20image%2020230713171016.png)

## AbstractErrorDataSetRendererParameter

包含 `ErrorDataSetRenderer` 相关的参数。

- drawMarker

```java
public BooleanProperty drawMarkerProperty()
public boolean isDrawMarker()
public R setDrawMarker(final boolean state)
```

绘制数据点。



### polyLineStyleProperty

```java
public enum LineStyle {
    NONE,
    NORMAL,
    AREA,
    ZERO_ORDER_HOLDER,
    STAIR_CASE, // aka. ZERO-ORDER_HOLDER
    HISTOGRAM,
    HISTOGRAM_FILLED, // similar to area but enclosing histogram style-type bars
    BEZIER_CURVE // smooth Bezier-type curve - beware this can be slow for many data points
}
```

不同 LineStyle 对应不同连接数据点方式。




## ErrorDataSetRenderer

渲染带有 error bar 或 error surface 的数据点，如可以渲染水平和垂直 error bar，额外功能：

- bar-type plot
- polar-axis plot
- scatter and bubble plot

### polyLineStyleProperty

```java
public ObjectProperty<LineStyle> polyLineStyleProperty()
```

设置渲染器绘制的线条样式。enum `LineStyle` 的值如下：

- NONE,  
- NORMAL,  
- AREA,  
- ZERO_ORDER_HOLDER,  
- STAIR_CASE, // aka. ZERO-ORDER_HOLDER  
- HISTOGRAM,  
- HISTOGRAM_FILLED, // similar to area but enclosing histogram style-type bars  
- BEZIER_CURVE // smooth Bezier-type curve - beware this can be slow for many data points

## GridRenderer

用于生成网格线。

在 `XYChart` 中，获得 `GridRenderer` 引用：

```java
public GridRenderer getGridRenderer()
```

`GridRenderer` 有几个字段：

| 字段                  | 类型   | 说明              |
| --------------------- | ------ | ----------------- |
| verMinorGridStyleNode | `Line` | 垂直 minor 网格线 |
| verMajorGridStyleNode | `Line`   | 垂直 major 网格线 |
| horMinorGridStyleNode | `Line` | 水平 minor 网格线 |
| horMajorGridStyleNode | `Line`   | 水平 major 网格线 |

它们都是 `Line` 类型，相关性质可以直接通过 `Line` 设置。例如：

- 设置网格是否可见

```java
xyChart.getGridRenderer().getHorizontalMajorGrid().setVisible(false);  
xyChart.getGridRenderer().getHorizontalMinorGrid().setVisible(true); // implicit major = true  
xyChart.getGridRenderer().getVerticalMajorGrid().setVisible(true);  
xyChart.getGridRenderer().getVerticalMinorGrid().setVisible(true);
```

- 设置颜色和宽度

```java
xyChart3.getGridRenderer().getHorizontalMajorGrid().setStroke(Color.BLUE);  
xyChart3.getGridRenderer().getVerticalMajorGrid().setStroke(Color.BLUE);  
xyChart3.getGridRenderer().getHorizontalMajorGrid().setStrokeWidth(1);  
xyChart3.getGridRenderer().getVerticalMajorGrid().setStrokeWidth(1);
```

