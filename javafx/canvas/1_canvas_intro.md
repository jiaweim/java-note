# Canvas 概述

2023-06-12
****
`javafx.scene.canvas` 包提供了 Canvas API，以支持形状/图像和文本的绘制。该 API 支持像素级的绘图，可以在面板写入任何像素值。该 API 主要包含两个类：

- Canvas
- GraphicsContext

`Canvas` 是一个图片（位图，bitmap），作为绘图面板。该类扩展自 `Node` 类，因此可以将 `Canvas` 添加到 scene graph，并添加特效和转换等所有 `Node` 支持的操作。

每个 Canvas 有一个与之关联的 `GraphicsContext`，用于执行绘图命令。

- `GraphicsContext` 通过一个缓冲区，对 `Canvas` 执行绘图操作。`GraphicsContext` 包含一系列的绘图命令，实现对基本几何形状、图片、文本等对象的绘制，以及绘图所需的参数（图形上下文）。
- 每个 Canvas 只包含一个 `GraphicsContext`，以及一个缓冲区。如果没添加到 Scene，则可以在任意线程修改，添加到 scene 后，只能在 JavaFX Application Thread 修改。

`Canvas` 和 `GraphicsContext` 一起，可以完成各种绘图工作，实现像素级别的绘图操作。

`GraphicsContext` 包含如下的渲染属性：

|Attribute|Save/Restore?|Default Value|Description|
|---|---|---|---|
|Clip|Yes|No Clipping|渲染受限的各种剪辑路径反锯齿交叉点|
|Global Alpha|Yes|1.0|控制每个渲染操作的透明度|
|Global Blend Mode|Yes|SRC_OVER|BlendMode enum 用于控制每个渲染操|作的像素如何合并到现有图像中|
|Transform|Yes|Identity|一个 3x2 的2D 放射变换矩阵，用于控制 |canvas 图像映射到逻辑像素的方式|
|Effect|Yes|null|应用于渲染操作的特效|
|Fill Paint|Yes|BLACK|形状填充色|
|Stroke Paint|Yes|BLACK|形状轮廓颜色|
|Line Width|Yes|1.0|stroke 宽度|
|Line Cap|Yes|SQUARE|线段末端的样式|
|Line Join|Yes|MITER|线段连接样式|
|Miter Limit|Yes|10.0|MITER 连接样式中，相对线段宽度延伸最长的比值|
|Dashes|Yes|null|虚线模式|
|Dash Offset|Yes|0.0|虚线模式距离起点的距离|
|Font|Yes|Default Font|文本字体|
|Text Align|Yes|LEFT|文本水平对齐方式|
|Text Baseline|Yes|BASELINE|文本垂直对齐位置|
|Font Smoothing|Yes|GRAY|文本平滑处理类型|
|Current Path|No|Empty path|构建的路径，用于填充，绘图，剪辑操作|
|Fill Rule|Yes|NON_ZERO|确定路径内部的方法|
