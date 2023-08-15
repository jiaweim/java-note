# Chart 类型

## 简介

Chart 定义图表，其类图如下：

@import "images/Pasted%20image%2020230605162405.png" {width="350px" title=""}

## Chart 结构

如图所示：

@import "images/Pasted%20image%2020230803104444.png" {width="px" title=""}

### 创建坐标轴

Chart 可以包含 3 个 x 轴，4 个 y 轴。创建这些坐标轴：

```java
final DefaultNumericAxis xAxis1 = new DefaultNumericAxis("x-Axis1", 0, 100, 1);  
final DefaultNumericAxis xAxis2 = new DefaultNumericAxis("x-Axis2", 0, 100, 1);  
final DefaultNumericAxis xAxis3 = new DefaultNumericAxis("x-Axis3", -50, +50, 10);  
final DefaultNumericAxis yAxis1 = new DefaultNumericAxis("y-Axis1", 0, 100, 1);  
final DefaultNumericAxis yAxis2 = new DefaultNumericAxis("y-Axis2", 0, 100, 1);  
final DefaultNumericAxis yAxis3 = new DefaultNumericAxis("y-Axis3", 0, 100, 1);  
final DefaultNumericAxis yAxis4 = new DefaultNumericAxis("y-Axis4", -50, +50, 10);
```

设置坐标轴位置： 

```java
xAxis1.setSide(Side.BOTTOM);
xAxis2.setSide(Side.TOP);
xAxis3.setSide(Side.CENTER_HOR);

yAxis1.setSide(Side.LEFT);
yAxis2.setSide(Side.RIGHT);
yAxis3.setSide(Side.RIGHT);
yAxis4.setSide(Side.CENTER_VER);
```

xAxis1 的位置如下图所示：

@import "images/Pasted%20image%2020230803212803.png" {width="px" title=""}

其中 xAxis3 水平居中，为了美观，修改其 minorTick 数和 axisLabel：

```java
xAxis3.setMinorTickCount(2);  
xAxis3.setAxisLabelTextAlignment(TextAlignment.RIGHT); 
```

yAxis4 垂直居中，同样修改其 minorTick 数和 axisLabel：

```java
yAxis4.setMinorTickCount(2);  
yAxis4.setAxisLabelTextAlignment(TextAlignment.RIGHT);
```

### 添加坐标轴

Chart 将不同位置的坐标轴，放在不同 layoutPane 中：

- 对垂直位置，如左、中、右，采用 HBox
- 对水平位置，如上、中、下，采用 VBox

对简单情况，在创建 `Chart` 指定坐标轴，例如：

```java
final XYChart chart = new XYChart(new DefaultNumericAxis(), yAxis);
```

对多个坐标轴情况，需要设置坐标轴位置：

```java
chart.getAxesPane(Side.BOTTOM).getChildren().add(xAxis1);  
chart.getAxesPane(Side.TOP).getChildren().add(xAxis2);  
chart.getAxesPane(Side.CENTER_HOR).getChildren().add(xAxis3);  
chart.getAxesPane(Side.LEFT).getChildren().add(yAxis1);  
chart.getAxesPane(Side.RIGHT).getChildren().add(yAxis2);  
chart.getAxesPane(Side.RIGHT).getChildren().add(yAxis3);  
chart.getAxesPane(Side.CENTER_VER).getChildren().add(yAxis4);
```

其中 `getAxesPane(side)` 返回不同位置用于坐标轴的 layoutPane。


### 标题

设置标题：

```java
chart.setTitle("<Title> Hello World Chart </Title>");
```

标题默认顶端居中显示。

### 工具栏

Chart 的工具栏以 FlowPane 实现。添加 Label 和 Button 到工具栏：

```java
chart.getToolBar().getChildren().add(new Label("ToolBar Menu: "));
for (final Side side : Side.values()) {
    final Button toolBarButton = new Button("ToolBar to " + side);
    toolBarButton.setOnMouseClicked(mevt -> chart.setToolBarSide(side));
    chart.getToolBar().getChildren().add(toolBarButton);
}
```

### titleLegend

titleLegend 采用与 Axis 一样的策略，即不同位置采用不同 layoutPane 中：

- 对垂直位置，如左、中、右，采用 HBox
- 对水平位置，如上、中、下，采用 VBox

上图显示 4 个 titleLegend，均为 Label 类型。

添加方式：

```java
chart.getTitleLegendPane(Side.LEFT).getChildren()
                                    .add(new MyLabel("Title/Legend - left", true));
chart.getTitleLegendPane(Side.RIGHT).getChildren()
                                    .add(new MyLabel("Title/Legend - right", true));
chart.getTitleLegendPane(Side.TOP).getChildren()
                                    .add(new MyLabel("Title/Legend - top"));
chart.getTitleLegendPane(Side.BOTTOM).getChildren()
                                    .add(new MyLabel("Title/Legend - bottom"));
```

### axesCorner

在坐标轴区域的 4 个角各有一个 StackPane。

每个角放一个 Label：

```java
chart.getAxesCornerPane(Corner.BOTTOM_LEFT).getChildren().add(new MyLabel("(BL)"));  
chart.getAxesCornerPane(Corner.BOTTOM_RIGHT).getChildren().add(new MyLabel("(BR)"));  
chart.getAxesCornerPane(Corner.TOP_LEFT).getChildren().add(new MyLabel("(TL)"));  
chart.getAxesCornerPane(Corner.TOP_RIGHT).getChildren().add(new MyLabel("(TR)"));
```

设置背景颜色：

```java
for (final Corner corner : Corner.values()) {
    chart.getAxesCornerPane(corner)
        .setStyle("-fx-background-color: rgba(125, 125, 125, 0.5);");
}
```

### titleLegendCorner

在 titleLegend 边框对应的 4 个角，也是由 StackPane 表示。下面设置这 4 个角的颜色：

```java
for (final Corner corner : Corner.values()) {
    chart.getTitleLegendCornerPane(corner)
                    .setStyle("-fx-background-color: rgba(175, 175, 175, 0.5);");
}
```

### measurementBar

最外围的边框，称为 measurementBar，为 Pane 类型。

添加文本并设置背景：

```java
for (final Side side : Side.values()) {
    chart.getMeasurementBar(side).getChildren().add(new MyLabel("ParBox - " + side));
    chart.getMeasurementBar(side)
                .setStyle("-fx-background-color: rgba(125, 125, 125, 0.5);");
}
```

### canvas

Chart 最中间的区域为 Canvas，用于绘制数据点。

下面为 Canvas 上鼠标点击事件设置 handler：

```java
chart.getCanvas().setMouseTransparent(false);
chart.getCanvas().setOnMouseClicked(mevt -> LOGGER.atInfo().log("clicked on canvas"));
chart.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED,
        mevt -> LOGGER.atInfo().log("clicked on canvas - alt implementation"));
```

### 完整示例

```java
public class ChartAnatomySample extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartAnatomySample.class);

    @Override
    public void start(final Stage primaryStage) {

        final VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        final DefaultNumericAxis xAxis1 = new DefaultNumericAxis("x-Axis1", 0, 100, 1);
        final DefaultNumericAxis xAxis2 = new DefaultNumericAxis("x-Axis2", 0, 100, 1);
        final DefaultNumericAxis xAxis3 = new DefaultNumericAxis("x-Axis3", -50, +50, 10);
        final DefaultNumericAxis yAxis1 = new DefaultNumericAxis("y-Axis1", 0, 100, 1);
        final DefaultNumericAxis yAxis2 = new DefaultNumericAxis("y-Axis2", 0, 100, 1);
        final DefaultNumericAxis yAxis3 = new DefaultNumericAxis("y-Axis3", 0, 100, 1);
        final DefaultNumericAxis yAxis4 = new DefaultNumericAxis("y-Axis4", -50, +50, 10);

        xAxis1.setSide(Side.BOTTOM);
        xAxis2.setSide(Side.TOP);
        xAxis3.setSide(Side.CENTER_HOR);
        xAxis3.setMinorTickCount(2);
        xAxis3.setAxisLabelTextAlignment(TextAlignment.RIGHT);
        yAxis1.setSide(Side.LEFT);
        yAxis2.setSide(Side.RIGHT);
        yAxis3.setSide(Side.RIGHT);
        yAxis4.setSide(Side.CENTER_VER);
        yAxis4.setMinorTickCount(2);
        yAxis4.setAxisLabelTextAlignment(TextAlignment.RIGHT);

        final Chart chart = new Chart() {
            @Override
            protected void axesChanged(Change<? extends Axis> change) {
                // TODO Auto-generated method stub
            }

            @Override
            protected void redrawCanvas() {
                // TODO Auto-generated method stub
            }

            @Override
            public void updateAxisRange() {
                // TODO Auto-generated method stub
            }

            @Override
            protected void updateLegend(final List<DataSet> dataSets, final List<Renderer> renderers) {
                // TODO Auto-generated method stub
            }
        };
        VBox.setVgrow(chart, Priority.ALWAYS);
        chart.getAxes().addAll(xAxis1, yAxis1);
        chart.setTitle("<Title> Hello World Chart </Title>");
        // chart.setToolBarSide(Side.LEFT);
        // chart.setToolBarSide(Side.BOTTOM);

        chart.getToolBar().getChildren().add(new Label("ToolBar Menu: "));
        for (final Side side : Side.values()) {
            final Button toolBarButton = new Button("ToolBar to " + side); // NOPMD
            toolBarButton.setOnMouseClicked(mevt -> chart.setToolBarSide(side));
            chart.getToolBar().getChildren().add(toolBarButton);
        }

        chart.getAxesPane(Side.BOTTOM).getChildren().add(xAxis1);
        chart.getAxesPane(Side.TOP).getChildren().add(xAxis2);
        chart.getAxesPane(Side.CENTER_HOR).getChildren().add(xAxis3);
        chart.getAxesPane(Side.LEFT).getChildren().add(yAxis1);
        chart.getAxesPane(Side.RIGHT).getChildren().add(yAxis2);
        chart.getAxesPane(Side.RIGHT).getChildren().add(yAxis3);
        chart.getAxesPane(Side.CENTER_VER).getChildren().add(yAxis4);

        chart.getTitleLegendPane(Side.LEFT).getChildren().add(new MyLabel("Title/Legend - left", true));
        chart.getTitleLegendPane(Side.RIGHT).getChildren().add(new MyLabel("Title/Legend - right", true));
        chart.getTitleLegendPane(Side.TOP).getChildren().add(new MyLabel("Title/Legend - top"));
        chart.getTitleLegendPane(Side.BOTTOM).getChildren().add(new MyLabel("Title/Legend - bottom"));

        chart.getAxesCornerPane(Corner.BOTTOM_LEFT).getChildren().add(new MyLabel("(BL)"));
        chart.getAxesCornerPane(Corner.BOTTOM_RIGHT).getChildren().add(new MyLabel("(BR)"));
        chart.getAxesCornerPane(Corner.TOP_LEFT).getChildren().add(new MyLabel("(TL)"));
        chart.getAxesCornerPane(Corner.TOP_RIGHT).getChildren().add(new MyLabel("(TR)"));

        for (final Corner corner : Corner.values()) {
            chart.getAxesCornerPane(corner).setStyle("-fx-background-color: rgba(125, 125, 125, 0.5);");
            chart.getTitleLegendCornerPane(corner).setStyle("-fx-background-color: rgba(175, 175, 175, 0.5);");
        }

        for (final Side side : Side.values()) {
            chart.getMeasurementBar(side).getChildren().add(new MyLabel("ParBox - " + side)); // NOPMD
            chart.getMeasurementBar(side).setStyle("-fx-background-color: rgba(125, 125, 125, 0.5);");
            // chart.setPinned(side, true);
        }

        chart.getCanvas().setMouseTransparent(false);
        chart.getCanvas().setOnMouseClicked(mevt -> LOGGER.atInfo().log("clicked on canvas"));
        ((Node) chart.getAxes().get(0)).setOnMouseClicked(mevt -> LOGGER.atInfo().log("clicked on xAxis"));
        chart.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED,
                mevt -> LOGGER.atInfo().log("clicked on canvas - alt implementation"));

        root.getChildren().add(chart);

        final Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle(this.getClass().getSimpleName());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(evt -> Platform.exit());
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {

        Application.launch(args);
    }

    private static class MyLabel extends Label {

        public MyLabel(final String label) {

            super(label);
            VBox.setVgrow(this, Priority.ALWAYS);
            HBox.setHgrow(this, Priority.ALWAYS);
            setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        }

        public MyLabel(final String label, boolean rotate) {

            this(label);
            if (rotate) {
                setRotate(90);
            }
        }
    }
}
```



## 设置数据集

所有 chart 的抽象类，显示 `Dataset` 接口的数据。

- `public ObservableList<DataSet> getDatasets()`

返回包含的数据集。

- 添加单个数据集

```java
chart.getDatasets().add(dataSet1);
```

- 添加多个数据集

```java
chart.getDatasets().addAll(dataSet1, dataSet2);
```

- `public final BooleanProperty animatedProperty()`

true 时动画显示数据变化。

### 设置大小

```java
xyChart4.setPrefSize(600, 300);
```

### animated

是否动画显示数据的更改。

```java
public final boolean isAnimated()
public final void setAnimated(final boolean value)
```

### legendVisible

```java
public final BooleanProperty legendVisibleProperty()
```

如果支持 legend 的 chart，设置是否显示 legend。

### renderers

```java
public ObservableList<Renderer> getRenderers()
```

该 chart 包含的所有 renderers。

## XYChart

### 创建 XYChart

- `public XYChart(final Axis... axes)`

使用指定坐标轴创建 `XYChart`，例如，创建两个数值轴的 `XYChart`：

```java
XYChart chart = new XYChart(new DefaultNumericAxis(), new DefaultNumericAxis());
```

## ToolBar

Chart 提供了默认的工具栏，通过 `FlowPane` 实现：

```java
public final FlowPane getToolBar()
```

## 示例

### LineChart

`XYChart` 默认为折线图。

```java
import de.gsi.chart.XYChart;  
import de.gsi.chart.axes.spi.DefaultNumericAxis;  
import de.gsi.chart.plugins.CrosshairIndicator;  
import de.gsi.chart.plugins.EditAxis;  
import de.gsi.chart.plugins.Zoomer;  
import de.gsi.dataset.event.UpdatedDataEvent;  
import de.gsi.dataset.spi.DoubleDataSet;  
import javafx.application.Application;  
import javafx.application.Platform;  
import javafx.scene.Scene;  
import javafx.scene.layout.StackPane;  
import javafx.stage.Stage;  
  
public class SimpleChartSample extends Application {  
  
    private static final int N_SAMPLES = 100; // 数据点数  
  
    @Override  
    public void start(final Stage primaryStage) {  
  
        final DefaultNumericAxis yAxis = new DefaultNumericAxis();  
        yAxis.setAutoRanging(true); // 默认: true  
        yAxis.setAutoRangePadding(0.5); // y 轴上下各 padding 50%  
        final XYChart chart = new XYChart(new DefaultNumericAxis(), yAxis);  
        chart.getPlugins().addAll(new Zoomer(), new CrosshairIndicator(), new EditAxis()); // 标准插件，推荐使用  
  
        final DoubleDataSet dataSet1 = new DoubleDataSet("data set #1");  
        final DoubleDataSet dataSet2 = new DoubleDataSet("data set #2");  
  
        // chart.getDatasets().add(dataSet1); // 单个数据集  
        chart.getDatasets().addAll(dataSet1, dataSet2); // 多个数据集  
  
        final double[] xValues = new double[N_SAMPLES];  
        final double[] yValues1 = new double[N_SAMPLES];  
        dataSet2.autoNotification().set(false); // to suppress auto notification  
        for (int n = 0; n < N_SAMPLES; n++) {  
            final double x = n;  
            final double y1 = Math.cos(Math.toRadians(10.0 * n));  
            final double y2 = Math.sin(Math.toRadians(10.0 * n));  
            xValues[n] = x;  
            yValues1[n] = y1;  
            dataSet2.add(n, y2); // 添加数据样式 1：每次调用 `add` 都发出重绘提醒  
        }  
        dataSet1.set(xValues, yValues1); // 添加数据样式 2：只有一次重绘  
        // to manually trigger an update (optional):  
        dataSet2.autoNotification().set(true); // to suppress auto notification  
        dataSet2.invokeListener(new UpdatedDataEvent(dataSet2 /* pointer to update source */, "manual update event"));  
  
        // alternatively (optional via default constructor):  
        // final DoubleDataSet dataSet3 = new DoubleDataSet("data set #1", xValues, yValues1, N_SAMPLES, false)  
        final Scene scene = new Scene(new StackPane(chart), 800, 600);  
        primaryStage.setTitle(getClass().getSimpleName());  
        primaryStage.setScene(scene);  
        primaryStage.show();  
        primaryStage.setOnCloseRequest(evt -> Platform.exit());  
    }  
  
    /**  
     * @param args the command line arguments  
     */    public static void main(final String[] args) {  
  
        Application.launch(args);  
    }  
}
```

效果：

![[Pasted image 20230602115648.png|600]]
