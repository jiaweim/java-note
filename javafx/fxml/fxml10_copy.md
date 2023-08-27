# 复制元素

2023-08-18, 17:28
@author Jiawei Mao
****
## 简介

通过复制源对象的属性创建一个新对象。使用 `<fx:copy>` 元素复制：

```xml
<fx:copy source="fx:id of the source object" />
```

复制对象的类必须提供复制构造函数，即以同一个类的对象为参数的构造函数。例如：

```java
public class Item {
    private Long id;
    private String name;

    public Item() {
    }

    // The copy constructor
    public Item(Item source) {
        this.id = source.id + 100;
        this.name = source.name + " (Copied)";
    }
    ...
}
```

下面在 `<fx:define>` 中创建 `Item` 对象。多次复制 Item 对象，添加到 ComboBox 的 items 中。

```xml
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.ComboBox?>
<?import javafx.collections.FXCollections?>
<?import com.jdojo.fxml.Item?>

<VBox xmlns:fx="http://javafx.com/fxml">
    <fx:define>
        <Item name="Kishori" id="100" fx:id="myItem"/>
    </fx:define>
    <ComboBox value="$myItem">
        <items>
            <FXCollections fx:factory="observableArrayList">
                <fx:reference source="myItem"/>
                <fx:copy source="myItem" />
                <fx:copy source="myItem" />
                <fx:copy source="myItem" />
                <fx:copy source="myItem" />
            </FXCollections>
        </items>
    </ComboBox>
</VBox>
```

