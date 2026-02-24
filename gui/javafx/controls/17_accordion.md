# Accordion

2025-07-14 update
2023-07-24⭐
@author Jiawei Mao

****
## 1. 简介

`Accordion` 是一个简单的控件。它显示一组 [TitledPane](control16_titledpane.md) 控件，每次只有一个 `TitledPane` 处于展开状态。

下图的 `Accordion` 包含 3 个 `TitledPane`，其中 General `TitledPane` 展开，Address 和 Phone TitledPanes 折叠。

<img src="images/Pasted%20image%2020230724224533.png" width="300" />

`Accordion` 有两个构造函数：

```java
// 创建空 Accordion
Accordion root = new Accordion();
// 指定包含的 TitledPane
Accordion(TitledPane... titledPanes)
```

`Accordion` 将 `TitledPane` 存储在 `ObservableList<TitledPane>`，调用 `getPanes()` 返回该 list。通过该 list 添加和删除 `TitledPane`：

```java
TitledPane generalPane = new TitledPane();
TitledPane addressPane = new TitledPane();
TitledPane phonePane = new TitledPane();
...
Accordion root = new Accordion();
root.getPanes().addAll(generalPane, addressPane, phonePane);
```



`Accordion` 包含一个 `expandedPane` 属性，存储当前展开 `TitledPane` 的引用。`Accordion` 默认以折叠状态显示所有 `TitledPane`，此时 `expandedPane` 属性值为 null。

点击 `TitledPane` 的标题栏或使用 `setExpandedPane()` 方法展开 `TitledPane`。为 `expandedPane` 属性添加 `ChangeListener` 可监听展开的 `TitledPane`。

**示例：** Accordion

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AccordionTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        TitledPane generalPane = this.getGeneralPane();
        TitledPane addressPane = this.getAddressPane();
        TitledPane phonePane = this.getPhonePane();

        Accordion root = new Accordion();
        root.getPanes().addAll(generalPane, addressPane, phonePane);
        root.setExpandedPane(generalPane);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using Accordion Controls");
        stage.show();
    }

    public TitledPane getGeneralPane() {
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("First Name:"), new TextField());
        grid.addRow(1, new Label("Last Name:"), new TextField());
        grid.addRow(2, new Label("DOB:"), new DatePicker());

        TitledPane generalPane = new TitledPane("General", grid);
        return generalPane;
    }

    public TitledPane getAddressPane() {
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Street:"), new TextField());
        grid.addRow(1, new Label("City:"), new TextField());
        grid.addRow(2, new Label("State:"), new TextField());
        grid.addRow(3, new Label("ZIP:"), new TextField());

        TitledPane addressPane = new TitledPane("Address", grid);
        return addressPane;
    }

    public TitledPane getPhonePane() {
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Home:"), new TextField());
        grid.addRow(1, new Label("Work:"), new TextField());
        grid.addRow(2, new Label("Cell:"), new TextField());

        TitledPane phonePane = new TitledPane("Phone", grid);
        return phonePane;
    }
}
```

<img src="images/Pasted%20image%2020230724225737.png" width="350" />

## 2. CSS

`Accordion` 的 CSS 样式类名默认为 `accordion`。

`Accordion` 没有添加额外的 CSS 属性。

`Accordion`  包含一个 first-titled-pane 子结构，表示第一个 `TitledPane`。

下面的样式为所有 `TitlePane` 设置 background 和标题栏的 insets：

```css
.accordion > .titled-pane > .title {
    -fx-background-color: burlywood;
    -fx-background-insets: 1;
}
```

下面设置 `Accordion` 的第一个 `TitledPane` 标题栏的 background：

```css
.accordion > .first-titled-pane > .title {
    -fx-background-color: derive(red, 80%);
}
```
