# ikonli

## 简介

ikonli 为 Java 应用提供 icon 包。目前支持 Swing 和 JavaFX。

### 下载

|        | JDK 11+              | JDK 8               |
| ------ | -------------------- | ------------------- |
| Swing  | ikonli-swing-12.3.1  | ikonli-swing-2.6.0  |
| JavaFX | ikonli-javafx-12.3.1 | ikonli-javafx-2.6.0 |

```ad-warning
有些 icon 在 JDK8 版本中没有。
```

## 使用

### JavaFX

`ikonli-javafx:12.3.1` 提供了一个新的 `Node` 类：`org.kordamp.ikonli.javafx.FontIcon`。可以在支持 graphic 属性的任何 JavaFX control 中使用该类。也可以直接使用 icon，因为它是 `javafx.scene.text.Text` 的子类。

也可以在 FXML 文件中使用 icon 类：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.Button?>
<?import javafx.scene.layout.GridPane?>
<?import org.kordamp.ikonli.javafx.FontIcon?>

<GridPane prefHeight="60.0" prefWidth="200.0"
          xmlns:fx="http://javafx.com/fxml"
          fx:controller="org.example.AppController">
    <Button GridPane.columnIndex="0" GridPane.rowIndex="0"
            mnemonicParsing="false"
            prefWidth="200.0">
        <graphic>
            <FontIcon iconLiteral="di-java" iconSize="64"/>
        </graphic>
    </Button>
</GridPane>
```



## 参考

- https://kordamp.org/ikonli/