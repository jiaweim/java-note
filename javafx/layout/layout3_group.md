# Group

2023-08-10, 15:28
modify: 样式
2023-07-06, 19:17
@author Jiawei Mao
****
## 1. 简介

`Group` 继承 `Parent`，具有容器特性：具有自己的布局策略和坐标系。

@import "images/Pasted%20image%2020230706173058.png" {width="250px" title=""}

但是，`Group` 更适合看作 node 集合，而非容器。它用于对多个 nodes 执行相同操作，对 `Group` 的转换、特效及属性，均会应用到其子节点。

`Group` 的布局策略：将子节点尺寸设置为 preferred size。没有任何其它操作，也不设置 nodes 位置，主要特征：

- 将子节点保存在 `ObservableList` 中， 按照添加顺序依次渲染子节点
- 不设置子节点位置，所有子节点默认在 (0,0)。需要使用子节点的 `layoutX` 和 `layoutY` 属性手动设置子节点在 `Group` 中的位置；否则子节点会互相重叠
-  默认将 resizable 子节点尺寸设置为 preferred size，将 `autoSizeChildren` 属性设置为 false 取消该功能。取消该功能会使除 `Shape` 外的所有 nodes 不可见，因为默认尺寸为 0

不能直接调整 Group 尺寸，`Group` 采用所有子节点边框的加和作为其大小。

对 `Group` 施加的转换、特效和属性修改会作用于其子节点，但是转换为特效不在 Group layout bounds 内；如果单独给各个子节点添加转换、特效等，就会包含在 Group layout bounds 内。

## 2. 创建 Group

容器的创建方式大同小异，Group 有 3 种构造函数：

```java{.line-numbers}
// 1. 创建一个空的 Group
Group emptyGroup = new Group();

// 2. 创建 Group 时同时添加子节点
Button smallBtn = new Button("Small button");
Button bigBtn = new Button("This is a big button");
Group group1 = new Group(smallBtn, bigBtn);

// 3. 通过集合添加节点
List<Node> initailList = new ArrayList<>();
initailList.add(smallBtn);
initailList.add(bigBtn);

Group group2 = new Group(initailList);
```

## 3. 渲染 Nodes

`Group` 的子节点按照添加的顺序进行渲染。例如：

```java
Button smallBtn = new Button("Small button");
Button bigBtn = new Button("This is a big button");
Group root = new Group();
root.getChildren().addAll(smallBtn, bigBtn);
Scene scene = new Scene(root);
```

@import "images/Pasted%20image%2020230706183329.png" {width="120px" title=""}

`Group` 按顺序渲染，依次渲染 `smallBtn` 和 `bigBtn`，`bigBtn` 将 `smallBtn` 完全覆盖。如果交换添加顺序：

```java
root.getChildren().addAll(bigBtn, smallBtn);
```

@import "images/Pasted%20image%2020230706183617.png" {width="120px" title=""}

## 4. 设置子节点位置

在 `Group` 中使用 node 的 `layoutX` 和 `layoutY` 属性设置 node 的绝对位置。

**示例：** 在 `Group` 中设置绝对位置和相对位置

为 `Group` 添加两个按钮（OK 和 Cancel）:

- 设置 OK 按钮在 `Group` 中的绝对位置
- 设置 Cancel 按钮相对 OK 的相对位置

```java{.line-numbers}
public class NodesLayoutInGroup extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) {
		Button okBtn = new Button("OK");
		Button cancelBtn = new Button("Cancel");

		// 设置 OK 的绝对位置
		okBtn.setLayoutX(10);
		okBtn.setLayoutY(10);

        // 设置 Cancel 相对 OK 的位置
		NumberBinding layoutXBinding = okBtn.layoutXProperty()
                                            .add(okBtn.widthProperty().add(10));
		cancelBtn.layoutXProperty().bind(layoutXBinding);
		cancelBtn.layoutYProperty().bind(okBtn.layoutYProperty());

		Group root = new Group();		
		root.getChildren().addAll(okBtn, cancelBtn);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Positioning Nodes in a Group");
		stage.show();
	}
}
```

@import "images/Pasted%20image%2020230706190319.png" {width="120px" title=""}

## 5. 设置特效和变换

应用于 `Group` 的特效和变化，会自动应用于 `Group` 中包含的子节点。设置 `Group` 的 `disable`、`opacity` 等属性（状态），同样会自动应用于 `Group` 子节点。

**示例：** 为 `Group` 添加特效、变化和状态设置

```java{.line-numbers}
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

public class GroupEffect extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        // 设置 button 位置
        okBtn.setLayoutX(10);
        okBtn.setLayoutY(10);
        cancelBtn.setLayoutX(80);
        cancelBtn.setLayoutY(10);

        Group root = new Group();
        root.setEffect(new DropShadow()); // 添加阴影特效
        root.setRotate(10);    // 旋转变换：顺时针 10°
        root.setOpacity(0.80);  // 透明度设置

        root.getChildren().addAll(okBtn, cancelBtn);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Applying Transformations and Effects to a Group");
        stage.show();
    }
}
```

@import "images/Pasted%20image%2020230706191434.png" {width="150px" title=""}

## 6. Group CSS

`Group` 主要从 `Node` 继承的 CSS，如 -fx-cursor, -fx-opacity, -fx-rotate 等。

`Group` 没有 padding, backgrounds, borders 等外观设置。
