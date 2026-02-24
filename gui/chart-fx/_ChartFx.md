# ChartFx

2023-06-08
****
## 简介

ChartFx 是 GSI 为 FAIR 开发的科学图表库，专注于性能优化的数据**实时可视化**。基于 GSI 和 CERN 早期使用的 Swing 实现，ChartFx 对 JavaFX 默认 Chart 实现进行了重写，旨在保留早期 Swing 库的丰富功能和可扩展性的同时，解决性能问题。

GSI: 德国亥姆霍兹协会

FAIR (Facility for Antiproton and Ion Research) 反质子与离子研究装置（粒子加速器）

## 功能

该库提供了科学信号处理领域常见的各种图类型，灵活的插件系统，以及实验室仪器中常见的参数策略。其框架如下：

- `Chart` 使用 `Canvas` 作为后端绘制数据
- `Axis` 负责缩放，数据与屏幕像素坐标之间的转换
- `Renderer` 负责绘制具体的数据点
- `ChartPlugin` 提供与 chart 或数据的交互

<img src="images/Pasted image 20230602142242.png" style="zoom: 50%;" />

`Canvas` 是优化性能的关键，它提供了很好的图形硬件加速。

N.B. 
拉丁语 nota bene 的缩写，英文为 note well，中文为特别注意

## 参考

- https://github.com/fair-acc/chart-fx

