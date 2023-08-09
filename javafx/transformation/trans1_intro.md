# Transformation 概述

2023-07-28, 13:52
****
## 1. 简介

变换（Transformation）是对同一个坐标系中点的映射，映射后部分几何属性保持不变。

JavaFX 支持以下几种变换：

| 变换操作    | 说明                               |
| ----------- | ---------------------------------- |
| Translation | 平移                               |
| Rotation    | 旋转                               |
| Shear       | 剪切                               |
| Scale       | 缩放                               |
| Affine      | 仿射变换，保留点、线和面的广义变换 |

变换由抽象类 `Transform` 表示：

- `Transform` 定义了所有变换的共同属性和方法
- `Transform` 定义了创建各种类型变换的 factory 方法

JavaFX 中的变换相关的类图如下：

![|500](Pasted%20image%2020230726161755.png)

仿射变换（`Affine`）是广义的变化，它保持了点的数量和唯一性，保持了直线的线性。平行线变换后依然平行，但可能无法保持线与线之间的角度和距离。平移、反射、旋转、剪切等都是仿射变换的特例。

`Affine` 类表示仿射变换。使用起来不容易，需要对矩阵变换比较了解。其它变换类使用起来要简单许多，组合起来也能实现 `Affine` 的功能。下面不讨论 `Affine` 类。

## 2. Transform

创建 `Transform` 的方法有两种：

- 使用 `Transform` 类的工厂方法
- 使用特定类的构造函数，如用 `Rotate` 类创建旋转变换

下面两种方法创建的变换相同：

```java
double tx = 20.0;
double ty = 10.0;

// Using the factory method in the Transform class
Translate translate1 = Transform.translate(tx, ty);

// Using the Translate class constructor
Translate translate2 = new Translate(tx, ty);
```

应用变换的方法也有两种：

- 使用 `Node` 类的属性。如，使用 `Node` 的`translateX`, `translateY`, `translateZ` 属性实现变换，不过该方法不能实现 shear 变换
- 使用 `Node` 类的 `transforms` 列表。通过 `Node.getTransforms()` 方法获得 `ObservableList<Transform>`，添加转换到该列表即可

这两种应用转换的方法略微有所不同。还可以同时使用这两种方式，使用 `transforms` 列表会先被应用，随后应用 node 的变换属性。

**示例：** 为 Rectangle 应用三种变换

- shear 和 scale 变化通过 transforms 应用
- translation 变换通过 Node 的 translateX 和 translateY 属性应用

```java
Rectangle rect = new Rectangle(100, 50, Color.LIGHTGRAY);
// Apply transforms using the transforms sequence of the Rectangle
Transform shear = Transform.shear(2.0, 1.2);
Transform scale = Transform.scale(1.1, 1.2);
rect.getTransforms().addAll(shear, scale);
// Apply a translation using the translatex and translateY
// properties of the Node class
rect.setTranslateX(10);
rect.setTranslateY(10);
```
