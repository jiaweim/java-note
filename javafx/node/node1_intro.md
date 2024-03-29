# Node 概述

2023-08-10, 10:45
modify: 样式
2023-06-12
@author Jiawei Mao
****
## 1. 简介

scene graph 是树形结构，scene graph 上的节点称为 node。`Node` 是 scene graph 上节点的基类，对应可视化组件（`javafx.scene.Node`）。

- 每个节点包含 0 或 1 个父节点
- 不包含子节点的称为叶节点（leaf）
- 包含 1 个或多个子节点的称为分支（branch）
- 在一个 `scene graph` 中，只有一个节点没有父节点，称为根节点（root）
- 一个 `scene graph` 中可以有多棵树，有些树是 `Scene` 的一部分，可以被显示
- 在 `Node` 添加到 scene graph 前，可以在任意线程创建或修改，添加到 scene graph 后，就只能在 JavaFX 应用线程中操作

`Node` 为抽象类，包含许多具体实现：

- 分支节点为 `Parent` 类型，其具体实现有 `Group`, `Region`, `Control`, `WebView` 和它们的子类；
- 叶节点有 `Rectangle`, `Text`, `ImageView`, `MediaView` 等任意不能有子节点的类。

!!! note Node 位置具有唯一性
    如果将一个 `Node` 作为 child 添加到 `Parent` (包括 `Group`, `Region` 等)，而该 `Node` 已经是另一个 `Parent` 的 child，则该节点自动从当前所属 `Parent` 移除。所以在修改 scene graph 时，将一个 subtree 从一个位置移到另一个位置：
    
    - 可以按照常规操作，将这个 subtree 从原来的位置移除，然后添加到新的位置
    - 也可以直接将 subtree 添加到新的位置，即使没有从显式移除操作，该操作也自动完成

## 2. 笛卡尔坐标系

笛卡尔坐标系大家已熟知。下图是常见的二维笛卡尔坐标系：

@import "images/Pasted image 20230612102105.png" {width="360px" title=""}

**变换**（transformation）指在保留一组预定义的几何属性前提下，将坐标空间的点映射到相同坐标空间。常见的变换操作包括平移（translation）、旋转（rotation）、缩放（scaling）和剪切（shearing）。

在**平移**变换中，在所有点的坐标上加上一对固定的数字。例如，在坐标空间平移 (a,b)，那么坐标点 (x,y) 平移后的坐标为 (x+a,y+b)。

在**旋转**变换中，坐标轴沿着坐标空间的一个点（pivot point）旋转，点的坐标映射到新的坐标轴上。

@import "images/Pasted image 20230612103244.png" {width="600px" title=""}

如图所示，转换前的坐标轴用实线表示，转换后的坐标轴用虚线表示。原始坐标空间中的点为黑色，变换后的坐标不填充。

## 3. Node 坐标系

scene graph 的每个 node 都有自己的坐标系。如下图，在计算机中，坐标系一般如此定义：

@import "images/Pasted image 20230612103828.png" {width="300px" title=""}

在典型的 GUI 程序中，节点放在其父节点中，根节点放在 scene 中，scene 放在 stage 中，stage 放在 window 中，一层一层，各层都有对应的坐标系：

@import "images/Pasted image 20230612104155.png" {width="600px" title=""}

最外层的矩形区域为屏幕。JavaFX Stage 区域的背景为浅灰色。这个简单的窗口使用了 5 个坐标空间。

那么，上图中蓝色矩形的左上角坐标是多少？坐标是相对坐标系定义的，上图中有 5 个坐标系，因此有 5 个坐标空间。在 Node 坐标系为 (10,15)，在 Parent 坐标系为 (40,45)，在 Scene 坐标系为 (60,55)，而在 stage 坐标系为 (64,83)，在 screen 坐标系为 (80,99)。

!!! note
    从设备的像素考虑，整数坐标值对应边角和像素之间的位置，而像 (0.5, 0.5) 这样的值则指向像素点的中心位置。由于坐标值采用的浮点数，所以可以准确控制指向像素点的任意位置。

    例：在 (0, 0) 大小为 10, 10 的矩形，矩形右下角最后一个像素的中间位置的坐标为 (9.5, 9.5)。