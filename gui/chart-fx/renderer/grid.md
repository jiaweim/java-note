# GridRenderer

## 简介

`GridRenderer` 用于在 Chat 中生成网格线。

在 `XYChart` 中，获得 `GridRenderer` 引用：

```java
public GridRenderer getGridRenderer()
```

`GridRenderer` 有几个字段：

| 字段                  | 类型   | 说明              |
| --------------------- | ------ | ----------------- |
| `verMinorGridStyleNode` | `Line` | 垂直 minor 网格线 |
| `verMajorGridStyleNode` | `Line`   | 垂直 major 网格线 |
| `horMinorGridStyleNode` | `Line` | 水平 minor 网格线 |
| `horMajorGridStyleNode` | `Line`   | 水平 major 网格线 |

它们都是 `Line` 类型，用于存储网格线的样式。相关性质可以直接通过 `Line` 设置。例如：

- 设置网格是否可见

```java
xyChart.getGridRenderer().getHorizontalMajorGrid().setVisible(false);  
xyChart.getGridRenderer().getHorizontalMinorGrid().setVisible(true);

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

## 样式

- 默认只显示 major 网格

```java
final XYChart xyChart1 = new XYChart(new DefaultNumericAxis("x-Axis 1", 0, 100, 10),
        new DefaultNumericAxis("y-Axis 1", 0, 100, 20));
xyChart1.setPrefSize(600, 300);
```

![](Pasted%20image%2020230804140951.png)

- 设置显示 minor 网格

```java
final XYChart xyChart2 = new XYChart(new DefaultNumericAxis("x-Axis 2", 0, 100, 10),
        new DefaultNumericAxis("y-Axis 2", 0, 100, 20));
xyChart2.setPrefSize(600, 300);
xyChart2.getGridRenderer().getHorizontalMinorGrid().setVisible(true);
xyChart2.getGridRenderer().getVerticalMinorGrid().setVisible(true);

xyChart2.getGridRenderer().getHorizontalMajorGrid().setVisible(false);
xyChart2.getGridRenderer().getHorizontalMinorGrid().setVisible(true); // implicit major = true
xyChart2.getGridRenderer().getVerticalMajorGrid().setVisible(true);
xyChart2.getGridRenderer().getVerticalMinorGrid().setVisible(true);
```

![](Pasted%20image%2020230804141117.png)

- 设置网格颜色

```java
final XYChart xyChart3 = new XYChart(new DefaultNumericAxis("x-Axis 3", 0, 100, 10),
        new DefaultNumericAxis("y-Axis 3", 0, 100, 20));
xyChart3.setPrefSize(600, 300);
xyChart3.getGridRenderer().getHorizontalMinorGrid().setVisible(true);
xyChart3.getGridRenderer().getVerticalMinorGrid().setVisible(true);

xyChart3.getGridRenderer().getHorizontalMajorGrid().setStroke(Color.BLUE);
xyChart3.getGridRenderer().getVerticalMajorGrid().setStroke(Color.BLUE);

xyChart3.getGridRenderer().getHorizontalMajorGrid().setStrokeWidth(1);
xyChart3.getGridRenderer().getVerticalMajorGrid().setStrokeWidth(1);
```

![](Pasted%20image%2020230804142012.png)

```java
final XYChart xyChart4 = new XYChart(new DefaultNumericAxis("x-Axis 4", 0, 100, 10),
        new DefaultNumericAxis("y-Axis 4", 0, 100, 20));
xyChart4.setPrefSize(600, 300);
xyChart4.getGridRenderer().getHorizontalMajorGrid()
                        .getStrokeDashArray().setAll(15.0, 15.0);
xyChart4.getGridRenderer().getVerticalMajorGrid()
                        .getStrokeDashArray().setAll(5.0, 5.0);

xyChart4.getGridRenderer().getHorizontalMajorGrid().setStrokeWidth(2);
xyChart4.getGridRenderer().getVerticalMajorGrid().setStrokeWidth(2);
```

![](Pasted%20image%2020230804142459.png)