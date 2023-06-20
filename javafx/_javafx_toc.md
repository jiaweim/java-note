# JavaFX

## 学习笔记

- [Node 概述](node/1_node_intro.md)
	- [Node 边框](node/2_node_bounds.md)
	- [Node 的位置和大小](node/3_node_pos_size.md)
- [颜色](color/color1_intro.md)
	- [ImagePattern](color/color2_image_pattern.md)
	- [线性渐变色](color/color3_linear_gradient.md)
	- [径向渐变色](color/color4_radial_gradient.md)
- [[stroke|Stroke]]
- [Layout](layout/_layout.md)
	- [FlowPane](layout/FlowPane.md)
- [Canvas 概述](canvas/1_canvas_intro.md)
	- [Canvas 操作](canvas/2_operations.md)
- [自定义样式快速入门](style/style1_intro.md)
	- [JavaFX CSS](style/style2_javafx_css.md)
	- [样式优先级](stype/style3_priority.md)




- [概述](1.intro.md)
- [Stage](2.stage_scene.md)
- [Scene](scene.md)
- [Node](3.node.md)
- [Color](4.color.md)
- [Data Binding](5.databinding.md)
- [Shape](6.shapes.md)
- [Text](7.text.md)
- [组件](8.controls.md)
  - [ChoiceBox](control_choicebox.md)
  - [ComboBox](controls/ComboBox.md)
  - [Chooser](chooser.md)
  - [ColorPicker](control_colorpicker.md)
  - [TextField](control_textfield.md)
  - [ToolTip](toolTip.md)
  - [Scrolling](scrolling.md)
  - [ListView](control_listview.md)
  - [TableView](tableview.md)
  - [Spinner](spinner.md)
  - [Alert](08_alert.md)
- [Layout](11_layout.md)
  - [TabPane](11_tabpane.md)
  - [TitledPane](pane_titledpane.md)
- [事件处理](13_event.md)
- [Transformation](11.transformation.md)
- [并发](concurrency/intro.md)
  - [JavaFX 并发框架](concurrency/framework.md)
- [CSS](style1_intro.md)
- [FXML](19_fxml.md)

## 工具

JavaFX [下载地址](https://gluonhq.com/products/javafx/).

### DataFX

https://bitbucket.org/datafx/datafx

帮助JavaFXUI组件进行数据查找，更新和编辑等工作。

### ControlsFX

[ControlsFX 主页](http://fxexperience.com/controlsfx/)

[ControlsFX 特征](https://github.com/controlsfx/controlsfx/wiki/ControlsFX-Features)

- [Font Awesome](font_awesome.md)

#### ScenicView

http://fxexperience.com/scenic-view/

用于 debugging JavaFX 程序，可以很方便的查看 `Node` 属性，查看当前 scenegraph 的状态。

#### ScenicView 运行

运行 Scenic View 有三种方法。

1. 从代码运行

```java
ScenicView.show(node);
```

或者

```java
ScenicView.show(scene);
```

只显示特定的 Scene/Node。一般不推荐使用这种方法，而推荐使用 Java Agent.

2. Java Agent

命令：`-javaagent:ScenicView.jar`

Scenic View 会自动查找运行程序中的JavaFX Stage.

3. Standalone

最简单的使用方法，即双击 ScenicView.jar 文件运行。

## References

- [ ] JavaFX 8 Introduction by Example
- [ ] Learn JavaFX 8 Building User Experience and Interfaces with Java 8, book
- [ ] [JENKOV 教程](http://tutorials.jenkov.com/javafx/index.html)
- [ ] [Java2S 教程](http://www.java2s.com/Tutorials/Java/JavaFX/index.htm)
- [ ] [code.markery 教程](https://code.makery.ch/library/topic/javafx/)
- [ ] [JavaFX Java GUI Design Tutorials, Youtube](https://www.youtube.com/playlist?list=PL6gx4Cwl9DGBzfXLWLSYVy8EbTdpGbUIG)
- [ ] [JavaFX Tutorials For Beginners in Youtube](https://www.youtube.com/playlist?list=PLS1QulWo1RIaUGP446_pWLgTZPiFizEMq)
- [ ] [Oracle 官方教程](https://docs.oracle.com/javase/8/javase-clienttechnologies.htm)
- [ ] [JavaFX 8 API](https://docs.oracle.com/javase/8/javafx/api/toc.htm)
- [ ] [JavaFX 11 API](https://openjfx.io/javadoc/11/index.html)

- [x] [Zetcode 教程](http://zetcode.com/gui/javafx/)  
没有 `TableView` 实例。

- http://www.guigarage.com/javafx-training-tutorials/
