# 引用另一个元素

2023-08-18, 17:13
@author Jiawei Mao
****
## 简介

在 FXML 文件中，可以使用 `<fx:reference>` 元素引用文件内其它元素。使用 `fx:id` 属性指定引用元素的 `fx:id`。

```xml
<fx:reference source="fx:id of the source element"/>
```

**示例：** 使用 `<fx:reference>` 引用 `Image`

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.image.Image?>
<?import javafx.scene.image.ImageView?>
<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Image url="resources/picture/ksharan.jpg" fx:id="myImg"/>
    </fx:define>
    <ImageView>
        <image>
            <fx:reference source="myImg"/>
        </image>
    </ImageView>
</VBox>
```

也可以使用变量解引用方法重写上面内容：

```xml
<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Image url="resources/picture/ksharan.jpg" fx:id="myImg"/>
    </fx:define>
    <ImageView image="$myImg"/>
</VBox>
```

