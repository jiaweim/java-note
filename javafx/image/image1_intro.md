# 图像 API

2023-07-31, 17:29
****
JavaFX 图像 API 提供了加载和显示图像功能，相关功能在 `javafx.scene.image` 包中定义。

JavaFX 图像 API 功能包括：

- 加载图像到内存
- 在 scene graph 中以 Node 显示显示图像
- 从图像读取像素值
- 将像素写入图像
- 将 scene graph 中的 node 转换为图像并保存为文件

类图如下：

![](Pasted%20image%2020230731171944.png)

`Image` 类表示内存中的图像。在 JavaFX 中可以通过为 `WritableImage` 提供像素创建图像。

ImageView 为 Node 类型，用于在 scene graph 中显示图像。如果需要显示图像，需要将图像加载为 Image 对象，然而在 ImageView 显示。

图像由像素组成，而像素数据有不同存储格式。`PixelFormat` 定义了像素数据的存储方式。`WritablePixelFormat` 表示可写入的目标格式。

PixelReader 和 PixelWriter 接口定义从 Image 读取数据和写入数据到 WritableImage. 的方法。
