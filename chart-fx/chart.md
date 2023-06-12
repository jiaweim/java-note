# Chart 类型

## Chart

Chart 定义图表，其类图如下：

![[Pasted image 20230605162405.png|400]]

### 设置坐标轴

一般在创建 `Chart` 指定坐标轴，例如：

```java
final XYChart chart = new XYChart(new DefaultNumericAxis(), yAxis);
```

### 设置数据集

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
