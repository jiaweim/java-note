# Scenic View

2023-08-03, 14:29
****
## 简介

Scenic View 是一个 JavaFX 应用，用于 debugging JavaFX 程序，查看 `Node` 属性和 scenegraph 的状态非常方便。

![](Pasted%20image%2020230803125108.png)
## ScenicView 运行

运行 Scenic View 有三种方法。

1. 从代码运行

```java
ScenicView.show(node);
```

或者

```java
ScenicView.show(scene);
```

只显示特定的 Scene/Node。一般不推荐使用这种方法，而推荐使用 Java Agent.

2. Java Agent

命令：`-javaagent:ScenicView.jar`

Scenic View 会自动查找运行程序中的JavaFX Stage.

### Standalone

最简单的使用方法，即双击 ScenicView.jar 文件运行。

Scene View 会自动检测运行中的 JavaFX 应用。


## 参考

- https://github.com/JonathanGiles/scenic-view