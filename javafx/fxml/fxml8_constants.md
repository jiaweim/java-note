# 使用常量

2023-08-18, 16:53
@author Jiawei Mao
****
## 简介

类、接口和 enum 都可以定义常量，常量是 static final 变量。FXML 可以使用 `fx:constant` 引用这些常量。

属性值为常量名称，元素名称为包含常量的类名。例如，引用 `Long.MAX_VALUE` 的方式：

```xml
<Long fx:constant="MAX_VALUE"/>
```

所有 enum 常量都属于这个类别。例如，访问 `Pos.CENTER` enum 常量：

```xml
<Pos fx:constant="CENTER"/>
```

**示例：** 在 FXML 中访问 Integer, Long 和 Pos enum 的常量

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.TextField?>
<?import java.lang.Integer?>
<?import java.lang.Long?>
<?import javafx.scene.text.FontWeight?>
<?import javafx.geometry.Pos?>

<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Integer fx:constant="MAX_VALUE" fx:id="minInt"/>
    </fx:define>
    <alignment><Pos fx:constant="CENTER"/></alignment>
    <TextField text="$minInt"/>
    <TextField>
        <text><Long fx:constant="MIN_VALUE"/></text>
    </TextField>
</VBox>
```


