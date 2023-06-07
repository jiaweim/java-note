# CSS

2023-06-01
****
## 简介

使用 CSS 个性化，`Chart` 类定义所有 chart 的共有属性，其 CSS 类名为 `chart`。例如，定义所有 chart 的 `legendSide`, `legendVisible` 和 `titleSide` 属性： 

```css
.chart {
    -fx-legend-side: top;
    -fx-legend-visible: true;
    -fx-title-side: bottom;
}
```

chart 有两个子类：

- chart-title
- chart-content

`chart-title` 为 `Label` 类型，`chart-content` 为 `Pane` 类型。

例：设置所有 charts 的背景为 yellow, title 字体为 Arial 16px bold

```css
.chart-content{
    -fx-background-color:  yellow;
}
.chart-title{
    -fx-font-family: "Arial";
    -fx-font-size: 16px;
    -fx-font-weight: bold;
}
```


## Legend

legend 的默认类名为 `chart-legend`。例：设置 legend 背景

```css
.chart-legend{
    -fx-background-color: lightgray;
}
```

每个 legend 有两个子结构：

- `chart-legend-item`, `Label` 类型，指定 legend 的文本；
- `chart-legend-item-symbol`, `Node` 类型，指定 legend 的图标，默认为圆。

例：设置 legend 的字体，并将图标设置为箭头

```css
.chart-legend-item{
    -fx-font-size: 16px;
}
.chart-legend-item-symbol{
    -fx-shape: "M0 -3.5 v7 l 4 -3.5z"
}
```
