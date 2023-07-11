# 图形渲染模式

2023-07-06, 11:18
****
Scene graph 在屏幕上呈现 JavaFX 应用起着至关重要的作用。

在屏幕上渲染图形的 API 有两类：

- 即时模式
- 保留模式

在即时模式，应用负责执行绘图命令，图形直接渲染到屏幕。当需要重新渲染时，应用进程需要重新向屏幕发出绘图命令。Java2D 为即时模式。

在保留模式，应用负责创建 graph。图形库（graphics library）将 graph 保留在内存，在需要时将 graph 渲染到屏幕。应用代码只负责创建 graph（what），图形库负责保存和渲染 graph（when and how）。保留模式渲染 API 将开发人员从渲染图形工作中解放出来。

对比即时模式，保留模式占用更多内存。但是使用更简单，JavaFX scene graph 使用保留模式。下图是这两种 API 的示意图：

![|400](Pasted%20image%2020230706111657.png)