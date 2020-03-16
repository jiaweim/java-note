# JavaFX Study Notes

- [JavaFX Study Notes](#javafx-study-notes)
  - [Notes](#notes)
  - [工具](#%e5%b7%a5%e5%85%b7)
    - [DataFX](#datafx)
    - [ControlsFX](#controlsfx)
    - [ScenicView](#scenicview)
    - [ScenicView 运行](#scenicview-%e8%bf%90%e8%a1%8c)
  - [References](#references)

## Notes

- [概述](1.intro.md)
- [Stage & Scene](2.stage_scene.md)
- [Node](3.node.md)
- [Color](4.color.md)
- [Data Binding](5.databinding.md)
- [Shape](6.shapes.md)
- [Text](7.text.md)
- [组件](8.controls.md)
  - [ChoiceBox](choiceBox.md)
  - [ComboBox](comboBox.md)
  - [Chooser](chooser.md)
  - [ToolTip](toolTip.md)
  - [Scrolling](scrolling.md)
  - [ListView](listview.md)
  - [TableView](tableview.md)
  - [Canvas](canvas.md)
  - [Spinner](spinner.md)
  - [Alert](08_alert.md)
- [Layout](11_layout.md)
  - [TabPane](11_tabpane.md)
- [Event](10.event.md)
- [Transformation](11.transformation.md)
- [Concurrency](12.concurrency.md)
- [Chart](13.chart.md)
- [CSS](css.md)
- [FXML](fxml.md)

## 工具

JavaFX [下载地址](https://gluonhq.com/products/javafx/).

### DataFX

https://bitbucket.org/datafx/datafx

帮助JavaFXUI组件进行数据查找，更新和编辑等工作。

### ControlsFX

http://fxexperience.com/controlsfx/  
一个开源的UI组件库，对 JavaFX 进行补充。  
http://fxexperience.com/2011/12/styling-fx-buttons-with-css/

### ScenicView
http://fxexperience.com/scenic-view/

用于 debugging JavaFX 程序，可以很方便的查看 `Node` 属性，查看当前 scenegraph 的状态。

### ScenicView 运行

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

使用如下命令：
`-javaagent:ScenicView.jar`

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