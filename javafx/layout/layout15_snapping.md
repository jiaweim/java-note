# Snapping

2023-07-11, 13:57
****

下图是一个 (5px, 5px) 的屏幕：

![|250](Pasted%20image%2020230711134919.png)

图中的圆圈表示像素点：

- 坐标 (0, 0) 映射到左上角像素点的左上角
- 坐标 (0.5, 0.5) 映射到左上角像素的中心

整数坐标落在像素点中间，对应上图中的实线。

JavaFX 的坐标系统支持 float，从而可以指定像素的任意部分。如 (2.0, 3.0) 指向像素点角。

Region 使用 float 作为坐标，在 pixel 边界可能没有完全对齐，使边界看起来模糊。Region 的 `snapToPixel` 属性解决该问题。snapToPixel 默认为 true，即 Region 会调整 children 的 position, spacing, size 为 integer，从而与 pixel 边界匹配，使得 children 的边界清晰。

