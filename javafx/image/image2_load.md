# 加载图像

2023-07-31, 18:07
****
## 1. 简介

Image 类为图像的内存表示。Image 支持 BMP, PNG, JPEG 和 GIF 图像格式。图像源可以为 URL 字符串或 InputStream。

Image 类提供了多个构造函数：

```java
Image(InputStream is)

Image(InputStream is, double requestedWidth, double requestedHeight, 
      boolean preserveRatio, boolean smooth)

Image(String url)

Image(String url, boolean backgroundLoading)

Image(String url, double requestedWidth, double requestedHeight, 
      boolean preserveRatio, boolean smooth)

Image(String url, double requestedWidth, double requestedHeight, 
      boolean preserveRatio, boolean smooth, boolean backgroundLoading)
```

使用 InputStream 作为图像源不存在歧义。如果使用 URL，则可以是有效的 URL 或 CLASSPATH 中的有效路径。即如果指定的 URL 不是有效 URL，则作为路径使用，在 CLASSPATH 搜索对应路径。

```java
// 使用 InputStream 加载图像
String sourcePath = "C:\\mypicture.png";
Image img = new Image(new FileInputStream(sourcePath));

// 从 URL 加载图像
Image img = new Image("http://jdojo.com/wp-content/uploads/2013/03/randomness.jpg");

// 从 CLASSPATH 加载图像. The image is located in the resources.picture package
Image img = new Image("resources/picture/randomness.jpg");
```

上例中，`resources/picture/randomness.jpg` 不是有效的 URL，所以将其作为 CLASSPATH 中的路径处理。将 resources.picture 作为 package 名处理，randomness.jpg 为包中的资源文件。

## 2. 指定加载属性

Image 构造函数中可以指定图像加载属性来控制图像质量和加载过程：

- requestedWidth
- requestedHeight
- preserveRatio
- smooth
- backgroundLoading

requestedWidth 和 requestedHeight 指定图像缩放后的尺寸，默认以原始尺寸加载。

`preserveRatio` 属性指定缩放时是否保持图像的长宽比，默认 false。

smooth 属性指定缩放图像时是使用速度更快还是质量更高的滤波算法。默认 false，即使用速度更快但质量较差的滤波算法。

backgroundLoading 属性指定是否异步加载图像。默认 false，即同步加载图像，在创建 Image 时开始加载，true 表示在后台线程异步加载图像。

## 3. 图像属性

Image 类包含如下 read-only 属性：

- width
- height
- progress
- error
- exception

`width` 和 `height` 属性指定指定加载后图像的宽度和高度。如果加载失败，它们为 0.

`progress` 属性表示加载图像进度：

- 当 backgroundLoading 属性为 true 时，progress 属性就派上用场。progress 值在  0.0 到 1.0 之间，对应 0% 到 100% 加载进度
- 当 backgroundLoading 属性为 false（默认），progress 的值总是 1.0

可以为 progress 属性添加 ChangeListener，以了解图像加载进度。比如，在加载图像时显示占位符，并用 Text 显示当前进度：

```java
// Load an image in the background
String imagePath = "resources/picture/randomness.jpg";
Boolean backgroundLoading = true;
Image image = new Image(imagePath, backgroundLoading);

// Print the loading progress on the standard output
image.progressProperty().addListener((prop, oldValue, newValue) -> {
    System.out.println("Loading:" + Math.round(newValue.doubleValue() * 100.0) + "%");
});
```

error 属性表示加载图像过程中是否发生错误。error 为 true 表示加载出错，exception 属性包含具体错误。
