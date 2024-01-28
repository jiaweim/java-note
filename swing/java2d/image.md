# 图像

- [图像](#图像)
  - [简介](#简介)
  - [图像类型](#图像类型)
    - [Toolkit Image](#toolkit-image)
    - [兼容图像](#兼容图像)
  - [BufferedImage](#bufferedimage)
  - [图像缩放](#图像缩放)

2024-01-11, 12:40
***

## 简介

图像只是一个图形基本元素。Image 对象可以用于许多不同的用途。它们可以保持图像文件的内容，还可以担当缓冲应用程序中的后台缓冲使动画变得平滑。在 Swing 双缓冲中，通过缓存中间的渲染结果，图像可以提供一个提高应用程序性能的、便捷的机制。

首先，我们应该定义一些术语。在非 Java 世界中，图像通常指图像文件。虽然 Java Image 对象可以保持这些文件的内容，但是它们不仅代表这个意思。一般而言，当我们在这本书中提到图像时，指的是 Java Image 对象，并不是图像文件。Java Image 对象本质上是符合一些特定格式的像素数据的矩阵列。

图像的基本用法如下所示：

- 图像创建：图像通过装载一个图像文件或新建一个图像来创建。
- 图像渲染：从一个文件装载的图像已经拥有由那个文件中的数据定义的内容。新建的 图像的渲染通过为这个图像创建一个 `Graphics` 对象、设置那个 Graphics 对象的状态并利用那个对象的渲染来进行。如果不考虑 Swing 的 `paintComponent()` 方法，渲染一个图像和渲染一个组件完全相同。
- 图像复制：利用 `drawImage()` 方法复制到其他图像或复制到屏幕。
- 图像保存：图像利用 Image 的 I/O API 保存到文件。请注意，任何 Image 都可以保存为 一个图像文件，无论它是从一个文件中装载的还是新创建的。

## 图像类型

**java.awt.Image**

这是所有图像的抽象的超类。通常，通过使用 Image 的子类的功能更强的方法可以得到想要的图像，但是，Image 可以当作一个便捷的泛型类来引用所有类型的图像。例如，bufferedImage 是 Image 的一个子类，除此之外，它还以多种有用的方式扩展了这个类。 虽然没有直接用具体例子说明 Ｉｍａｇｅ，但是，在代码中我们可以看到 Ｉｍａｇｅ的引用，因为 各种各样的方法都依赖这个泛型的超类。例如，所有的 ＧｒａｐｈｉｃｓｄｒａｗＩｍａｇｅ（）方法都接受一个 Ｉｍａｇｅ作为参数，这意味着它们可以绘制任意的图像类型。

### Toolkit Image

工具包图像是通过最初的 Java.awt.Toolkit 装载进来的图像：

```java
Image Toolkit.getImage(String filename)
Image Toolkit.getImage(URL rul)
```

工具包图像的最大限制是它们只能用于显示。不能从一个工具包图像得到 Graphics 对象，从而进行渲染。因为极富客户端广泛地使用图像，经常新建图像或修改已装载的图像，对于我们来说工具包图像不是非常有用。Image I/O API 提供了与我们首选的图像类型 BufferedImage 集成得更好的图像装载机制。

### 兼容图像

兼容图像是它的像素数据的格式与显示应用程序的显示器的格式最相配的图像。例如，如果用户使用一个运行在 32 位模式的显示器，可以用 `Buffered.TYPE_INT_RGB` 类型（其中的红色、绿色和蓝色字节的排列方式和它们在显存中的排列方式相同）创建一个兼容图像。

通常使用 `GraphicsConfiguration.createCompatibleImage()` 方法创建兼容图像。这个方法返回一个与给定的 GraphicsConfiguration 兼容的图像。兼容图像有 一定的性能优势。

## BufferedImage

将任意图像转换为 BufferedImage:

```java
public BufferedImage makeBufferedImage(Image oldImage){
    int w = oldImage.getWidth(null);
    int h = oldImage.getHeight(null);

    // Assume we have a handle to a GraphicsConfig object
    //  create a compatible image
    BufferedImage bIMg = graphicsConcig.createCompatibleImage(w, h);
    // get the image graphics
    Graphics g = bImg.getGraphics();

    // copy the content from old image into the new one
    g.drawImage(oldImage, 0, 0, null);

    // dispose the temporary Graphics object we used
    g.dispose();

    return bImg;
}
```

请注意，透明和半透明图像的细节有一些不同。一个半透明或透明的图像会需要一个半透明的或透明的 `BufferedImage`，上面创建的是不透明的 BufferedImage。

透明的图像是其中的每个像素要么完全透明要么完全不透明的图像。可以通过下面这种方式创建这样的图像：

```java
BufferedImage bImg = graphicsConfig.createCompatibleImage(w, h, Transparency.BITMASK);
```

半透明的图像是其中的每个像素可以有不同的不透明值的图像，不透明值的范围从完全透 明到完全不透明或两者之间的任意值。可以通过下面这种方式创建半透明的图像：

```java
BufferedImage bImg = graphicsConfig.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
```

许多图形对象可以非常容易地表示为图像，在 BufferedImage 上的操作也可以比其他的方式开销更小、更快和更容易。

## 图像缩放

有非常多的方法可以用来缩放一个图像。对于需要执行的任何缩放操作，应该使用哪个方法？下面是一些需要考虑的最显而易见的方法：

- 方法一

```java
g.drawImage(img, x, y, width, height, null);
```

- 方法二

```java
g.drawImage(img, dx1, dy1, dx2, dy2
            sx1, sy1, sx2, sxy2, null);
```

- 方法三

```java
g.translate(x, y)

```


