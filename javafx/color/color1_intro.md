# 颜色

2023-06-19, 20:43
****
## 1. 概述

在 JavaFX 中，可以设置文本颜色、背景颜色。颜色有多种形式：单一纯色、渐变色，还可以使用图片填充背景。下图是 JavaFX 颜色相关的类，这些类都在 `javafx.scene.paint` 包中：

![](images/Pasted%20image%2020230619202936.png)

`Paint` 是抽象类，是其它颜色相关类的基类。它只包含一个 `static` 方法，用于将字符串转换为合适的具体类：

 ```java
 public static Paint valueOf(String value)
 ```

返回的 `Paint` 实例，根据参数不同，可以是 `LinearGradient`, `RadicalGradient` 和 `Color` 中的一种。

`valueOf` 很少直接使用，一般用于将 CSS 文件中的颜色值转换为对应实例。如下：

 ```java
// redColor 是 Color 类型
Paint redColor = Paint.valueOf("red");

// aLinearGradientColor 是 LinearGradient 类型
Paint aLinearGradientColor = Paint.valueOf("linear-gradient(to bottom right, red, black)" );

// aRadialGradientColor 是 RadialGradient 类型
Paint aRadialGradientColor = Paint.valueOf("radial-gradient(radius 100%, red, blue, black)");
```

`Stop` 类和 `CycleMethod` enum 用于定义梯度颜色。

```ad-tip
设置 `Node` 的 `color` 属性的方法一般以 `Paint` 类型为参数，这样就能使用四种颜色模式中的任意一种。
```

## 2. Color 类

`Color` 类表示 RGB 颜色空间的一个颜色。

每种颜色都有一个 alpha 值，范围在 `[0.0, 1.0]` 或 `[0, 255]`，表示透明度。alpha=0.0 或 0 表示完全透明，alpha=1.0 或 255表示完全不透明。alpha 值默认为1.0，即完全不透明。

创建 `Color` 实例的方法有三种：

- 构造函数
- 工厂方法
- `Color` 类中定义的常量

`Color` 只有一个构造函数：

```java
public Color(double red, double green, double blue, double opacity)
```

例如，创建一个蓝色：

```java
Color blue = new Color(0.0, 0.0, 1.0, 1.0);
```

`Color` 提供如下的工厂方法：

- `Color color(double red, double green, double blue)`
- `Color color(double red, double green, double blue, double opacity)`
- `Color hsb(double hue, double saturation, double brightness)`
- `Color hsb(double hue, double saturation, double brightness, double opacity)`
- `Color rgb(int red, int green, int blue)`
- `Color rgb(int red, int green, int blue, double opacity)`

另外，还有 `valueOf()` 和 `web()` 可以从网络颜色字符串创建颜色。例如：

```java
Color blue = Color.valueOf("blue");
Color blue = Color.web("blue");
Color blue = Color.web("#0000FF");
Color blue = Color.web("0X0000FF");
Color blue = Color.web("rgb(0, 0, 255)");
Color blue = Color.web("rgba(0, 0, 255, 0.5)"); // 50% transparent blue
```

`Color` 定义了大约 140 种颜色常量，均为完全不透明颜色。
