# JavaFX

## 学习笔记

- [JavaFX 概述](start/start1_intro.md)
    - [JavaFX 程序](start/start2_app.md)
    - [传入 JavaFX 应用参数](start/start3_passing_parameters.md)
- [属性和绑定概述](binding/binding1_intro.md)
    - [JavaFX 属性](binding/binding2_javafx_property.md)
    - [JavaFX Bean](binding/binding3_javafx_bean.md)
    - [Property 类的层次结构](binding/binding4_property_class.md)
    - [Property Event](binding/binding5_event.md)
    - [JavaFX Binding](binding/binding6_binding.md)
    - [Binding API](binding/binding7_api.md)
    - [使用绑定将 Circle 居中](binding/binding8_circle.md)
- [Observable 集合](collections/collect1_intro.md)
    - [JavaFX 集合的属性和绑定](collections/collect2_binding.md)
- [Screen](stage/stage1_screen.md)
- [Stage](stage/stage2_stage.md)
- [Scene 概述](scene/scene1_intro.md)
    - [图形渲染模式](scene/scene2_rendering_mode.md)
    - [设置 Cursor](scene/scene3_cursor.md)
    - [Focus Owner](scene/scene4_focus_owner.md)
    - [Platform](scene/scene5_platform.md)
    - [Host Environment](scene/scene6_host_environment.md)
- [Node 概述](node1_intro.md)
    - [Node 边框](node2_bounds.md)
    - [Node 的位置和大小](node3_pos_size.md)
    - [托管 Node](node/node5_managed.md)
- [颜色](color/color1_intro.md)
    - [ImagePattern](color/color2_image_pattern.md)
    - [线性渐变色](color/color3_linear_gradient.md)
    - [径向渐变色](color/color4_radial_gradient.md)
- [[shape_stroke|Stroke]]
- [Canvas 概述](canvas1_intro.md)
    - [Canvas 操作](canvas2_operations.md)
- [自定义样式快速入门](style/style1_intro.md)
    - [JavaFX CSS](style/style2_javafx_css.md)
    - [样式优先级](style/style3_priority.md)
    - [CSS 属性](style/style4_css_properties.md)
    - [CSS 背景颜色](style/style5_background_color.md)
    - [Border](style/style6_border.md)
    - [JavaFX CSS Selector](style/style7_selector.md)
- [JavaFX 事件处理概述](event/event1_startup.md)
    - [事件处理机制](event/event2_machanism.md)
    - [处理事件](event/event3_handling.md)
    - [事件过滤器和处理器的执行顺序](event/event4_order.md)
    - [消耗事件](event/event5_consume.md)
    - [输入事件](event/event6_input.md)
    - [鼠标事件](event/event7_mouse.md)
    - [键盘事件](event/event8_key.md)
    - [窗口事件](event/event9_window.md)
- [Layout 概述](layout/layout1_intro.md)
    - [Layout 工具类](layout/layout2_utility.md)
    - [Group](layout/layout3_group.md)
    - [Region](layout/layout4_region.md)
    - [Pane](layout/layout5_pane.md)
    - [HBox](layout/layout6_hbox.md)
    - [VBox](layout/layout7_vbox.md)
    - [FlowPane](layout/layout8_flowpane.md)
    - [BorderPane](layout/layout9_borderpane.md)
    - [StackPane](layout/layout10_stackpane.md)
    - [TilePane](layout/layout11_tilepane.md)
    - [GridPane](layout/layout12_gridpane.md)
    - [AnchorPane](layout/layout13_anchorpane.md)
    - [TextFlow](layout/layout14_textflow.md)
    - [Snapping](layout/layout15_snapping.md)
- [MVC](mvc/mvc1_intro.md)



- [概述](1.intro.md)
- [Node](3.node.md)
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
- [Transformation](11.transformation.md)
- [并发](concurrency/intro.md)
  - [JavaFX 并发框架](concurrency/framework.md)
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
