# 坐标轴


***
## Axis

支持线性、对数、反向、时间序列等坐标轴，类图如下：

![[Pasted image 20230605160919.png]]
其中：

- `DefaultNumericAxis` 是默认数值坐标轴实现
- `CategoryAxis` 是分类坐标轴实现

这两个使用最多。

### 属性

| 属性                    | 类型                   | 说明                                     |
| ----------------------- | ---------------------- | ---------------------------------------- |
| autoRangingProperty     | BooleanProperty        | 根据数据自动确定坐标轴范围               |
| autoUnitScalingProperty | BooleanProperty        | 根据数据范围自动缩放到最近的 SI 单位前缀 |
| invertAxisProperty      | BooleanProperty        | 翻转坐标轴标签和数据点顺序               |
| minProperty             | DoubleProperty         | 最小值。启用 autoRanging 时自动设置      |
| maxProperty             | DoubleProperty         | 最大值。启用 autoRanging 时自动设置      |
| nameProperty            | StringProperty         | 名称                                     |
| sideProperty            | `ObjectProperty<Side>` | 位置                                     |
| tickUnitProperty        | DoubleProperty         | 单位                                     |
| unitProperty            | StringProperty         | 单位名称                                 |
| unitScalingProperty     | DoubleProperty         | 单位缩放                                 |
| timeAxisProperty        | BooleanProperty        | 是否为时间轴                             |

### 方法

| 方法                                           | 说明                               |
| ---------------------------------------------- | ---------------------------------- |
| `void setUnit(final String value)`             | 设置坐标轴名称                     |
| `void setAnimated(boolean value)`              | 是否动画显示坐标轴范围的变化       |
| `boolean isAutoGrowRanging()`                  | 是否自动确定范围，在需要时增加范围 |
| `void setAutoGrowRanging(boolean value)`       | 同上                               |
| `AxisRange getAutoRange()`                     | 返回上一次自动计算的坐标轴范围     |
| `boolean isAutoRanging()`                      | 是否自动从数据自动确定坐标轴范围   |
| `void setAutoRanging(boolean value)`           | 同上                               |
| `boolean isAutoUnitScaling()`                  | 是否自动缩放单位（通过乘上特定值） |
| `void setAutoUnitScaling(final boolean value)` | 同上                               |
| `AxisTransform getAxisTransform()`             | 坐标轴变化，如指数轴/对数轴        |
|                                                |                                    |

## AbstractAxisParameter

定义 `AbstractNumericAxis` 类的属性，该类的目的是将模板代码从 `AbstractNumericAxis` 移除，增加其代码可读性。

### 属性

| 属性                           | 类型                                     | 说明                                                                                                                                    |
| ------------------------------ | ---------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| `autoRangePaddingProperty`     | `DoubleProperty`                         | 在坐标轴范围两边填充的比例。例如，如果数据范围是 [10,20]，该属性设置为 0.1，那么填充后的范围是 [9,21]                                   |
| `animatedProperty`             | `BooleanProperty`                        | 是否动画显示坐标轴发生的变化                                                                                                            |
| `autoRangeRoundingProperty`    | `BooleanProperty`                        | 是否将坐标轴范围扩展到 major tick unit 值。例如，如果数据范围是 [3, 74]，而 major tick unit 为 [5]，启用该设置后，范围将扩展到 [0,75]。 |
| `overlapPolicyProperty`        | `ObjectProperty<AxisLabelOverlapPolicy>` | 坐标轴标签重叠时的处理策略                                                                                                              |
| maxMajorTickLabelCountProperty | `IntegerProperty`                        | major tick count label 最大数                                                                                                           |

### 方法

| 方法                                         | 说明 |
| -------------------------------------------- | ---- |
| `setMaxMajorTickLabelCount(final int value)` | 设置 major tick label 数     |

### overlapPolicy

对轴标签重叠的处理策略。指定方式：

```java
axis.setOverlapPolicy(AxisLabelOverlapPolicy.SHIFT_ALT);
```

`AxisLabelOverlapPolicy` 为 enum 类型，包括

| AxisLabelOverlapPolicy | 说明                                                              |
| ---------------------- | ----------------------------------------------------------------- |
| DO_NOTHING             | 允许重叠                                                          |
| SKIP_ALT               | 使下一个标签不可见                                                |
| NARROW_FONT            | 缩小字体                                                          |
| SHIFT_ALT              | 在需要时对下一个 label 移动一个 label 的高度/宽度（适合于分类轴） |
| FORCED_SHIFT_ALT       | 同上，但强制执行                                                  |

## DefaultNumericAxis

数值坐标轴的默认实现，支持 Long, Double, BigDecimal 等数值类型。相比 JavaFX 的 `NumberAxis`，包含一些额外功能：

- 关闭 `autoRangingProperty()` 属性后重新计算 tick unit
- 支持 auto-range padding : `autoRangePaddingProperty()`
- 支持 auto-range rounding

当启用 `autoRangingProperty()` 属性，是否将范围扩展到 major tick unit 值。例如，如果范围是 [3,74]，而 major tick unit 为 [5]，那么将范围扩展到 [0,75]。默认 true。

- 支持自定义 `tickUnitSupplierProperty`

### logAxis

```java
public BooleanProperty logAxisProperty()

public DoubleProperty logarithmBaseProperty() // log 底数
```

用于设置是否将坐标轴数值转换为 log 形式，log 转换的底数使用 `logarithmBaseProperty` 属性设置，默认底数为 10。例如：

```java
import io.fair_acc.chartfx.XYChart;
import io.fair_acc.chartfx.axes.spi.DefaultNumericAxis;
import io.fair_acc.chartfx.plugins.EditAxis;
import io.fair_acc.chartfx.plugins.Zoomer;
import io.fair_acc.dataset.spi.DoubleDataSet;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LogAxisSample extends ChartSample {
    private static final int N_SAMPLES = 1000;

    @Override
    public Node getChartPanel(final Stage primaryStage) {

        final StackPane root = new StackPane();
        DefaultNumericAxis xAxis = new DefaultNumericAxis();
        DefaultNumericAxis yAxis = new DefaultNumericAxis();
        yAxis.setLogAxis(true); // y 轴转换为 log
        // yAxis.setLogarithmBase(2);

        final XYChart chart = new XYChart(xAxis, yAxis);
        final Zoomer zoomer = new Zoomer();
        zoomer.setPannerEnabled(false); // 不推荐（按住鼠标中键拖动 chart）
        chart.getPlugins().add(zoomer); // 允许缩放
        chart.getPlugins().add(new EditAxis()); // 允许手动设置坐标轴信息

        root.getChildren().add(chart);

        final DoubleDataSet dataSet1 = new DoubleDataSet("data set #1");
        final DoubleDataSet dataSet2 = new DoubleDataSet("data set #2");
        final DoubleDataSet dataSet3 = new DoubleDataSet("data set #3");
        chart.getDatasets().addAll(dataSet1, dataSet2, dataSet3);

		// 添加数据点的经典方式
		// 每添加一个点都会触发重绘 chart，避免该行为的方式有两种：
		// 1. 使用 set(double[] xValues, double[] yValues) 一次设置所有数据
		// 2. 设置 dataSet.setAutoNotification(false) 
        dataSet1.autoNotification().set(false);
        dataSet2.autoNotification().set(false);
        dataSet3.autoNotification().set(false);
        for (int n = 0; n < N_SAMPLES; n++) {
            final double x = n + 1.0;
            double y = 0.01 * (n + 1);

            dataSet1.add(x, 2.0 * x);
            dataSet2.add(x, Math.pow(2, y));
            dataSet3.add(x, Math.exp(y));
        }
        // 调用完后，记得重新打开自动重绘功能
        dataSet1.autoNotification().set(true);
        dataSet2.autoNotification().set(true);
        dataSet3.autoNotification().set(true);

        return root;
    }

    public static void main(final String[] args) {
        Application.launch(args);
    }
}
```

![[Pasted image 20230609140458.png|500]]

可以发现，Y 轴的 major tick 是 10 为底数的 log 值。



## CategoryAxis

`CategoryAxis` 用于字符串类型的分类数据。

- `public CategoryAxis(final String axisLabel)`

创建指定轴标签的分类轴。

## 示例

### rangeScaling 示例

下面是坐标轴缩放的完整示例：

```java  
import io.fair_acc.chartfx.axes.spi.DefaultNumericAxis;
import io.fair_acc.chartfx.axes.spi.MetricPrefix;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AxisRangeScalingSample extends ChartSample {

    @Override
    public Node getChartPanel(final Stage primaryStage) {

        final VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        final DefaultNumericAxis xAxis1 = new DefaultNumericAxis("standard axis label w/o unit", 0, 100, 1);
        VBox.setMargin(xAxis1, new Insets(20, 10, 20, 10));
        root.getChildren().add(xAxis1);

        final DefaultNumericAxis xAxis2 = new DefaultNumericAxis("axis label", 0, 100, 1);
        VBox.setMargin(xAxis2, new Insets(20, 10, 20, 10));
        xAxis2.setUnit("m");
        root.getChildren().add(xAxis2);

        final DefaultNumericAxis xAxis3 = new DefaultNumericAxis("current", 0, 100, 1);
        VBox.setMargin(xAxis3, new Insets(20, 10, 20, 10));
        xAxis3.setUnit("A");
        xAxis3.getAxisLabel().setFill(Color.RED.darker());
        root.getChildren().add(xAxis3);

        // to force unit scaling to '1000' 'k' suffix
        // N.B. tick unit is being overwritten by scaling
        final DefaultNumericAxis xAxis4 = new DefaultNumericAxis("very large current", 1e3, 100e3, 1e2);
        VBox.setMargin(xAxis4, new Insets(20, 10, 20, 10));
        xAxis4.setUnitScaling(MetricPrefix.KILO);
        xAxis4.setUnit("A");
        xAxis4.getAxisLabel().setFont(Font.font("Times", 25));
        xAxis4.getAxisLabel().setFill(Color.RED.darker());
        root.getChildren().add(xAxis4);

        // to force unit scaling to '1e-3' '\mu' suffix
        final DefaultNumericAxis xAxis5 = new DefaultNumericAxis("small voltage", 0, 10e-6, 1e-6);
        VBox.setMargin(xAxis5, new Insets(20, 10, 20, 10));
        xAxis5.setUnitScaling(MetricPrefix.MICRO);
        xAxis5.setUnit("V");
        root.getChildren().add(xAxis5);

        // to force unit scaling to '1e-9' 'n' suffix
        final DefaultNumericAxis xAxis6 = new DefaultNumericAxis("tiny voltage", 0, 11e-9, 1e-9);
        VBox.setMargin(xAxis6, new Insets(20, 10, 20, 10));
        xAxis6.setUnitScaling(MetricPrefix.NANO);
        xAxis6.setUnit("V");
        root.getChildren().add(xAxis6);

        // example for scaling with non metric prefix
        final DefaultNumericAxis xAxis7 = new DefaultNumericAxis("non-metric scaling voltage variable", 0, 25e-6, 1e-6);
        VBox.setMargin(xAxis7, new Insets(20, 10, 20, 10));
        xAxis7.setUnitScaling(2.5e-6);
        xAxis7.setUnit("V");
        root.getChildren().add(xAxis7);

        // example for scaling with non metric prefix and w/o unit
        final DefaultNumericAxis xAxis8 = new DefaultNumericAxis("non-metric scaling voltage variable w/o unit", 0,
                25e-6, 1e-6);
        VBox.setMargin(xAxis8, new Insets(20, 10, 20, 10));
        xAxis8.setUnitScaling(2.5e-6);
        // or alternatively:
        // xAxis7.setUnit(null);
        root.getChildren().add(xAxis8);

        // example for dynamic scaling with metric prefix and unit
        final DefaultNumericAxis xAxis9 = new DefaultNumericAxis("dynamic Axis", -1e-6 * 0, 0.001, 1);
        VBox.setMargin(xAxis9, new Insets(20, 10, 20, 10));
        xAxis9.setUnit("V");
        xAxis9.setAutoUnitScaling(true);
        xAxis9.setMinorTickCount(10);
        xAxis9.setAutoRangeRounding(true);
        root.getChildren().add(xAxis9);
        final Label xAxis9Text = new Label();
        root.getChildren().add(xAxis9Text);

        final Timer timer = new Timer("sample-update-timer", true);
        final TimerTask task = new TimerTask() {
            private int counter = -9;
            private boolean directionUpwards = true;

            @Override
            public void run() {

                if (directionUpwards) {
                    counter++;
                } else {
                    counter--;
                }
                Platform.runLater(() -> {
                    final double power = Math.pow(10, counter);
                    xAxis9.maxProperty().set(power);
                    final String text = "actual SI range for dynamic axis: [" + xAxis9.getMin() + " V, "
                            + xAxis9.getMax() + " V]";
                    xAxis9Text.setText(text);
                });
                if ((counter >= 9) || (counter <= -9)) {
                    directionUpwards = !directionUpwards;
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, TimeUnit.SECONDS.toMillis(2));

        return root;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }
}

```

![[Pasted image 20230609095015.png]]

- 第一坐标轴，没有单位

```java
DefaultNumericAxis xAxis1 = new DefaultNumericAxis("standard axis label w/o unit", 0, 100, 1);  
```

- 第二个坐标轴，加了单位

```java
DefaultNumericAxis xAxis2 = new DefaultNumericAxis("axis label", 0, 100, 1);  
xAxis2.setUnit("m");
```

单位放在 `[]` 中。

- 第三个坐标轴，标签设置为深红色

```java
DefaultNumericAxis xAxis3 = new DefaultNumericAxis("current", 0, 100, 1);  
xAxis3.setUnit("A");  
xAxis3.getAxisLabel().setFill(Color.RED.darker());
```

- 第四个坐标轴，加了 1000 倍的缩放，修改了标签字体

```java
DefaultNumericAxis xAxis4 = new DefaultNumericAxis("very large current", 1e3, 100e3, 1e2);  
xAxis4.setUnitScaling(MetricPrefix.KILO);  
xAxis4.setUnit("A");  
xAxis4.getAxisLabel().setFont(Font.font("Times", 25));  
xAxis4.getAxisLabel().setFill(Color.RED.darker());  
```

单位的缩放倍数，使用 `MetricPrefix` 设置。当然，也可以直接使用数字，效果一样。例如：

```java
xAxis4.setUnitScaling(1000);
```

- 第五个坐标轴，加了 $10^{-6}$ 缩放

```java
DefaultNumericAxis xAxis5 = new DefaultNumericAxis("small voltage", 0, 10e-6, 1e-6);  
xAxis5.setUnitScaling(MetricPrefix.MICRO);  
xAxis5.setUnit("V");  
```

- 第六个坐标轴，加了 $10^{-9}$ 缩放

```java
DefaultNumericAxis xAxis6 = new DefaultNumericAxis("tiny voltage", 0, 11e-9, 1e-9);  
xAxis6.setUnitScaling(MetricPrefix.NANO);  
xAxis6.setUnit("V");  
```

- 第七个坐标轴，自定义缩放

`MetricPrefix` 提供了许多常用的缩放比例，便于使用，但可能不适合你的情况，此时可以直接用数值设置：

```java
DefaultNumericAxis xAxis7 = new DefaultNumericAxis("non-metric scaling voltage variable", 0, 25e-6, 1e-6);  
xAxis7.setUnitScaling(2.5e-6);  
xAxis7.setUnit("V");  
```

此时的单位显示为 `*2.5E-6V`，在美观上略有不足。

- 第八个坐标轴，不显示单位

既然单位显示不美观，可以不显示单位

```java
DefaultNumericAxis xAxis8 = new DefaultNumericAxis("non-metric scaling voltage variable w/o unit", 0,  
25e-6, 1e-6);  
xAxis8.setUnitScaling(2.5e-6);  
// or alternatively:  
// xAxis7.setUnit(null);  
```

不调用 `setUnit` 或者设置 `setUnit(null)` ，不显示单位。

- 第九个坐标轴

这个坐标轴会动态更新最大值：

```java
DefaultNumericAxis xAxis9 = new DefaultNumericAxis("dynamic Axis", -1e-6 * 0, 0.001, 1);
xAxis9.setUnit("V");
xAxis9.setAutoUnitScaling(true);
xAxis9.setMinorTickCount(10);
xAxis9.setAutoRangeRounding(true);
final Label xAxis9Text = new Label();

final Timer timer = new Timer("sample-update-timer", true);
final TimerTask task = new TimerTask() {
	private int counter = -9;
	private boolean directionUpwards = true;

	@Override
	public void run() {

		if (directionUpwards) {
			counter++;
		} else {
			counter--;
		}
		Platform.runLater(() -> {
			final double power = Math.pow(10, counter);
			xAxis9.maxProperty().set(power);
			final String text = "actual SI range for dynamic axis: [" + xAxis9.getMin() + " V, "
					+ xAxis9.getMax() + " V]";
			xAxis9Text.setText(text);
		});
		if ((counter >= 9) || (counter <= -9)) {
			directionUpwards = !directionUpwards;
		}
	}
};
timer.scheduleAtFixedRate(task, 0, TimeUnit.SECONDS.toMillis(2));
```

`xAxis9.setAutoUnitScaling(true)` 启用单位的自动缩放，即根据数据自动调整单位。

这里每两秒更新一次坐标轴范围，使得坐标轴的单位、范围以及标签随之更新。

### CategoryAxis 示例

