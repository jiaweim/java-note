# 创建可复用对象

2023-07-12, 17:29
****
有时我们希望定义一个对象，如 Insets 或 Color，然后在 FXML 文件其它多个位置使用。例如，ToggleGroup 就是个典型，定义一次，在多个 RadioButton 处使用。

使用 `<fx:define>` 定义的对象不是 object graph 的一部分，但是可以在 object graph 中使用其 fx:id 引用。属性值必须以 `$` 开始。

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.Button?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.ToggleGroup?>
<?import javafx.scene.control.RadioButton?>

<VBox fx:controller="com.jdojo.fxml.Test" xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Insets fx:id="margin" top="5.0" right="5.0" bottom="5.0" left="5.0"/>
        <ToggleGroup fx:id="genderGroup"/>
    </fx:define>
    <Label text="Gender" VBox.margin="$margin"/>
    <RadioButton text="Male" toggleGroup="$genderGroup"/>
    <RadioButton text="Female" toggleGroup="$genderGroup"/>
    <RadioButton text="Unknown" toggleGroup="$genderGroup" selected="true"/>
    <Button text="Close" VBox.margin="$margin"/>
</VBox>
```

上面在 `<fx:define>` 中创建了两个对象，Insets 和 ToggleGroup，并分配了 fx:id。在 object graph 中引用这两个对象。

