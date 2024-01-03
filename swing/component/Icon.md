# Icon

- [Icon](#icon)
  - [简介](#简介)
  - [创建 Icon](#创建-icon)
  - [使用 Icon](#使用-icon)
  - [ImageIcon](#imageicon)
    - [创建 ImageIcon](#创建-imageicon)
    - [使用 ImageIcon](#使用-imageicon)
    - [ImageIcon 属性](#imageicon-属性)
  - [GrayFilter](#grayfilter)

2024-01-02, 21:58⭐    
***

## 简介

`Icon` 接口为组件提供 glyph 功能。glyph 以非文字的方式传递信息，可以直接绘图，或者使用 `ImageIcon` 类加载 GIF 文件。该接口包含两个描述大小的属性和一个绘制 glypy 的方法：

```java
public interface Icon{
    
    void paintIcon(Component c, Graphics g, int x, int y);

    int getIconWidth();

    int getIconHeight();
}
```

## 创建 Icon

创建 `Icon`，即实现 `Icon` 接口，只需要指定大小，然后绘制其内容。

!!! tip
    在实现 `Icon` 接口的 `paintIcon()` 方法时，将传入的 $(x,y)$ 转换为 graphics-context 的绘图坐标，绘图完成后再转换回来，可以简化绘图操作。

**示例：** 创建一个菱形的 icon

```java
import javax.swing.*;
import java.awt.*;

public class DiamondIcon implements Icon {

    private Color color;
    private boolean selected;
    private int width;
    private int height;
    private Polygon poly;
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;

    public DiamondIcon(Color color) {
        this(color, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public DiamondIcon(Color color, boolean selected) {
        this(color, selected, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public DiamondIcon(Color color, boolean selected, int width, int height) {
        this.color = color;
        this.selected = selected;
        this.width = width;
        this.height = height;
        initPolygon();
    }

    private void initPolygon() {
        poly = new Polygon();
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        poly.addPoint(0, halfHeight);
        poly.addPoint(halfWidth, 0);
        poly.addPoint(width, halfHeight);
        poly.addPoint(halfWidth, height);
    }

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.translate(x, y);
        if (selected) {
            g.fillPolygon(poly);
        } else {
            g.drawPolygon(poly);
        }
        g.translate(-x, -y);
    }
}
```

## 使用 Icon

使用 `Icon` 十分简单，只需要为支持 `Icon` 的组件设置对应属性。例如，为 `JLabel` 设置 icon：

```java
Icon icon = new DiamondIcon(Color.RED, true, 25, 25);
JLabel label = new JLabel(icon);
```

## ImageIcon

`ImageIcon` 实现了 `Icon` 接口，使用 AWT `Image` 对象创建 glyph。`Image` 来源包括内存（`byte[]`）、文件或网络（`URL`）。

和常规 `Image` 不同的是，`ImageIcon` 在创建时立即开始加载，并且可以序列化。

### 创建 ImageIcon

`ImageIcon` 提供了 9 个构造函数：

```java
public ImageIcon()
Icon icon = new ImageIcon();
icon.setImage(anImage);

public ImageIcon(Image image)
Icon icon = new ImageIcon(anImage);

public ImageIcon(String filename)
Icon icon = new ImageIcon(filename);

public ImageIcon(URL location)
Icon icon = new ImageIcon(url);

public ImageIcon(byte imageData[])
Icon icon = new ImageIcon(aByteArray);

public ImageIcon(Image image, String description)
Icon icon = new ImageIcon(anImage, "Duke");

public ImageIcon(String filename, String description)
Icon icon = new ImageIcon(filename, filename);

public ImageIcon(URL location, String description)
Icon icon = new ImageIcon(url, location.getFile());

public ImageIcon(byte imageData[], String description)
Icon icon = new ImageIcon(aByteArray, "Duke");
```

无参数版本创建未初始化的 `Icon`。余下 8 个构造函数从 `Image`, `byte[]`, `String`, `URL` 等创建 Icon。

### 使用 ImageIcon

使用 `ImageIcon` 与使用 `Icon` 一样简单：

```java
Icon icon = new ImageIcon("Warn.gif");
JLabel label3 = new JLabel("Warning", icon, JLabel.CENTER)
```

### ImageIcon 属性

下面是 `ImageIcon` 的 6 个属性：

|属性|类型|权限|
|---|---|---|
|`description`|`String`|Read-write|
|`iconHeight`|`int`|Read-only|
|`iconWidth`|`int`|Read-only|
|`image`|`Image`|Read-write|
|`imageLoadStatus`|`int`|Read-only|
|`imageObserver`|`ImageObserver`|Read-write|

- `iconHeight` 和 `iconWidth` 是 ImageIcon 的实际尺寸
- `imageLoadStatus` 表示 `ImageIcon` 加载状态，可选值包括：`MediaTracker.ABORTED`, `MediaTracker.ERRORED`, `MediaTracker.COMPLETE`

有时，使用 ImageIcon 加载图像，然后从 Icon 获取 Image 也很有用：

```java
ImageIcon imageIcon = new ImageIcon(...);
Image image = imageIcon.getImage();
```

使用 `ImageIcon` 的一个主要问题是，当 image 和类文件都在 JAR 文件中，必须显式指定 JAR 中 image 的完整 URL，否则不起作用。

下面提供加载 JAR 文件之外的图像：

```java
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageLoader {

    private ImageLoader() {}

    public static Image getImage(Class relativeClass, String filename) {
        Image returnValue = null;
        InputStream is = relativeClass.getResourceAsStream(filename);
        if (is != null) {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                int ch;
                while ((ch = bis.read()) != -1) {
                    baos.write(ch);
                }
                returnValue = Toolkit.getDefaultToolkit().createImage(baos.toByteArray());
            } catch (IOException exception) {
                System.err.println("Error loading: " + filename);
            }
        }
        return returnValue;
    }
}
```

可以指定图像文件相对的基类和图像文件名，然后将返回的 `Image` 传递给 `ImageIcon` 的构造函数：

```java
Image warnImage = ImageLoader.getImage(LabelJarSample.class, "Warn.gif");
Icon warnIcon = new ImageIcon(warnImage);
JLabel label2 = new JLabel(warnIcon);
```

## GrayFilter

许多 Swing 组件都依赖于 GrayFilter 来创建用作禁用组件 Icon 的 Image。组件会自动使用这个类。也可以显式调用这个类的 `public static Image createDisabledImage(Image image)` 方法将正常图像转换为灰色：

```java
Image normalImage = ...
Image grayImage = GrayFilter.createDisabledImage(normalImage)
```

然后，使用灰色图像作为组件的 Icon：

```java
Icon warningIcon = new ImageIcon(grayImage);
JLabel warningLabel = new JLabel(warningIcon);
```