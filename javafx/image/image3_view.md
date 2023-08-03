# 查看图像

## 简介

ImageView 类用于显示加载为 Image 对象的图像。ImageView 继承自 Node，因此可以添加到 scene graph。

ImageView 构造函数：

```java
ImageView()

ImageView(Image image)

ImageView(String url)
```

无参 ImageView 构造函数没有图像，可以后续通过 image 属性指定图像。

```java
// Create an empty ImageView and set an Image for it later
ImageView imageView = new ImageView();
imageView.setImage(new Image("resources/picture/randomness.jpg"));

// Create an ImageView with an Image
ImageView imageView = 
            new ImageView(new Image("resources/picture/randomness.jpg"));

// Create an ImageView with the URL of the image source
ImageView imageView = new ImageView("resources/picture/randomness.jpg");
```

**示例：** 在 scene 显示图像

- 图像加载为 Image
- 将 Image 添加到 ImageView

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ImageTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        String imagePath = getClass().getResource("/picture/randomness.jpg").toExternalForm();

        // Scale the iamge to 200 X 100
        double requestedWidth = 200;
        double requestedHeight = 100;
        boolean preserveRatio = false;
        boolean smooth = true;
        Image image = new Image(imagePath,
                requestedWidth,
                requestedHeight,
                preserveRatio,
                smooth);
        ImageView imageView = new ImageView(image);

        HBox root = new HBox(imageView);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Displaying an Image");
        stage.show();
    }
}
```

![|200](Pasted%20image%2020230731181538.png)

## Image 视图

Image 将图像载入内存。相同 Image 可以有多个视图，ImageView 是其中之一。

在加载、显示时都可以调整图像尺寸：

- 在 Image 对象中调整图像大小会直接改变内存中图像的大小， 图像的所有视图都使用调整后的图像。Image 尺寸被调整后