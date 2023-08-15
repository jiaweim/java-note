# Text Origin

2023-08-15, 09:44
modify: 样式
2023-07-26, 15:15
****
除了 local 和 parent 坐标系，`Text` 还有一个用于渲染文本的坐标系。

`Text` 使用三个属性定义该坐标系：x, y, textOrigin.

- x, y 定义了 Text 的原点坐标
- `textOrigin` 为 `VPos` enum 类型： `BASELINE`, `TOP`, `CENTER`, `BOTTOM`，默认值为 `BASELINE`。

如下图所示，该图显示了 local 坐标系和文本坐标系。 local  坐标系为实线，text 坐标系为虚线：

@import "images/Pasted%20image%2020230726150218.png" {width="600px" title=""}

`textOrigin` 定义了文本坐标系的 x 轴相对文本高度的位置：

- `VPos.TOP` 文本顶端和 text 坐标系的 x 轴对齐，此时 Text 的属性 y 是 text 顶端到 local 坐标系 x 轴的距离
- `VPos.BASELINE` 文本基线和 text 坐标系的 x 轴对齐，部分字符 (g, y, j, p 等) 下面会超出基线
- `VPos.BOTTOM` 文本底端和 text 坐标系的 x 轴对齐
- `VPos.CENTER` 文本坐标系的 x-axis 和文本中间对齐

`Text` 类的 `baselineOffset` 属性表示 text 的 baseline 和 top 的距离，该值和字体的最大 ascent 值相等。

当需要将 `Text` 和其它 node 垂直对齐，需要考虑 `textOrigin` 属性。如果要将 `Text` 垂直居中，需要设置 `textOrigin` 为 `VPos.TOP`。否则采用默认值 `VPos.BASELINE`，文本会明显高于中间位置。

**示例：** 将 `Text` 水平和垂直居中

- 为了将 `Text` 垂直居中，需要将 `textOrigin` 设置为 `VPos.TOP`
- 若不设置 `textOrigin`，y 轴默认与 baseline 对齐

```java{.line-numbers}
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextCentering extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Text msg = new Text("A Centered Text Node");

        // Must set the textOrigian to VPos.TOP to center 
        // the text node vertcially within the scene
        msg.setTextOrigin(VPos.TOP);

        Group root = new Group();
        root.getChildren().addAll(msg);
        Scene scene = new Scene(root, 200, 50);
        msg.layoutXProperty().bind(
                scene.widthProperty().subtract(
                        msg.layoutBoundsProperty().get().getWidth()).divide(2));
        msg.layoutYProperty().bind(
                scene.heightProperty().subtract(
                        msg.layoutBoundsProperty().get().getHeight()).divide(2));

        stage.setTitle("Centering a Text Node in a Scene");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230726151449.png" {width="400px" title=""}
