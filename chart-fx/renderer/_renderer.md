# 渲染器

## Renderer

Renderer 是实际绘制数据点的类，不同 `Renderer` 对应不同的图表类型。类图如下：

![](images/Pasted%20image%2020230713171016.png)

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

