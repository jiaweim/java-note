# MigLayout

## 简介

MigLayout 是一个通用且强大的 layout-manager。MigLayout 基于 grid，但也支持 docking 和 grouping。

![MigLayout](.\images\miglayout.png)

相对其它 layout-managers，MigLayout 有如下优势：

- column/row 中组件默认对齐；
- 可以指定 column width 或 row height 的最小和最大值；
- 支持不同单位：LogicalPixel, Pixel, Point, Millimeter, Centimeter, Inch, Percent and ScreenPercent，其中 LogicalPixel 对创建随屏幕分辨率缩放的布局非常有用；
- 支持 columns, rows 以及 components 之间的 gaps；
- 灵活的 growing and shrinking；
- column/row grouping；
- in-cell flow 支持将多个 component 放入单个 grid-cell；
- 将组件 docking 到容器边缘；
- 

## 参考

- https://www.formdev.com/jformdesigner/doc/layouts/miglayout/