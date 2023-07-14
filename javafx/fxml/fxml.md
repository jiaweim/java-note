# FXML

2020-05-20, 15:12
***

## 多个FXML文件

因为载入 FXML 文件返回的是 JavaFX `Node` 类型，一个 `Scene` 包含多个 `Node`，所以也可以包含多个 FXML 文件。

除了使用 `FXMLLoader.load()` 方法载入 FXML 文件，也可以

## 自定义组件

使用 `FXMLLoader` 的 `setRoot()` 和 `setController()` 方法设置根节点和控制器。这两个方法在创建基于FXML的自定义组件时使用较多，方便创建可重复使用的组件。

如下，创建一个简单的自定义的 TextField 和 Button，根节点为 `javafx.scene.layout.VBox`:

```xml
<?import javafx.scene.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<fx:root type="javafx.scene.layout.VBox" xmlns:fx="http://javafx.com/fxml">
    <TextField fx:id="textField"/>
    <Button text="Click Me" onAction="#doSomething"/>
</fx:root>
```

其中，`<fx:root>`引用预定义的 root element。该element 可以通过 `FXMLLoader` 的 `getRoot()` 获得，在调用 `load()` 方法之前，必须通过 `setRoot()` 方法指定该值。

下面定义的 `CustomControl` 类扩展 `VBox`将其自身设置为载入的FXML文档的 root 和 controller。

```java
package fxml;
import java.io.IOException;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
public class CustomControl extends VBox {

    @FXML private TextField textField;

    public CustomControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("custom_control.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public String getText() {
        return textProperty().get();
    }
    public void setText(String value) {
        textProperty().set(value);
    }
    public StringProperty textProperty() {
        return textField.textProperty();
    }
    @FXML
    protected void doSomething() {
        System.out.println("The button was clicked!");
    }
}
```

注意构造函数中调用的 `fxmlLoader.setRoot(this)` 和 `fxmlLoader.setController(this)`，这在其中是必须的。

## 参考

- https://openjfx.io/javadoc/17/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html
