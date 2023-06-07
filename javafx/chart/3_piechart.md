# PieChart

2023-06-01
****
## 简介

饼图由一个分成不同圆心角的扇区组成，每个扇区的面积代表数据量的大小。

通过 `PieChart` 类创建饼图。该类包含两个构造函数：

- `PieChart()`
- `PieChart(ObservableList<PieChart.Data> data)`

创建过程很简单，为 `PieChart` 指定数据，然后添加到面板中即可。

饼图的扇区用 `PieChart.Data` 类表示。每个扇区包含名字和数值，对应 `PieChart.Data` 的 `name` 和 `pieValue` 属性。例如，创建一个扇区：

```java
PieChart.Data chinaSlice = new PieChart.Data("China", 1275);
```

饼图的数据使用 `ObservableList<PieChart.Data>` 指定。例如，创建包含三个数据的饼图：

```java
ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

chartData.add(new PieChart.Data("China", 1275));
chartData.add(new PieChart.Data("India", 1017));
chartData.add(new PieChart.Data("Brazil", 172));
```

然后使用构造函数创建饼图：

```java
PieChart charts = new PieChart(chartData);
```

## 属性

|PieChart 属性|说明|
|---|---|
|data|指定数据，类型为 ObservableList<PieChart.Data>|
|startAngle|第一个 pie slice 起始角度，默认 0，对应三点钟方向；默认逆时针方向计算|
|clockwise|从 startAngle，顺时针或逆时针，默认顺时针|
|labelsVisible|slice labels 是否可见，label 通过 PieChart.Data 指定|
|labelLineLength|label 和对应 slice 连线的长度，默认 20.0 px|

PieChart 默认包含标签和 legend。
`PieChart.Data` 包括数据名称和数值，其标签为数据名称。

## 示例

首先，定义所需数据：

```java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
 
public class PieChartUtil {
 
	public static ObservableList<PieChart.Data> getChartData() {
		ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
		data.add(new PieChart.Data("China", 1275));
		data.add(new PieChart.Data("India", 1017));
		data.add(new PieChart.Data("Brazil", 172));
		data.add(new PieChart.Data("UK", 59));
		data.add(new PieChart.Data("USA", 285));
		return data;
	}
}
```

绘制饼图：
```java
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
 
public class PieChartEx3 extends Application {
 
	@Override
	public void start(Stage stage) {
		PieChart chart = new PieChart();
		chart.setTitle("Population in 2000");
 
		// Place the legend on the left side
		chart.setLegendSide(Side.LEFT);
		// set the data for the chart
		ObservableList<PieChart.Data> chartData = PieChartUtil.getChartData();
		chart.setData(chartData);
 
		StackPane root = new StackPane(chart);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("A Pie Chart");
		stage.show();
	}
 
	public static void main(String[] args) {
		launch(args);
	}
}
```
效果：

![](2019-06-05-19-19-15.png)

## 自定义 Slice

每个 pie slice 由 `Node` 表示，通过 `PieChart.Data` 的 `getNode()` 获得其引用。不过该 `Node` 在对应的 slice 添加到 pie chart 后才创建，所以提前调用返回 null。

例：为每个 slice 设置 tooltip，显示鼠标下数据点：

- slice name
- pie value
- percent pie value

```java
import javafx.application.Application;  
import javafx.collections.ObservableList;  
import javafx.geometry.Side;  
import javafx.scene.Node;  
import javafx.scene.Scene;  
import javafx.scene.chart.PieChart;  
import javafx.scene.control.Tooltip;  
import javafx.scene.layout.StackPane;  
import javafx.stage.Stage;  
  
public class PieChartEx4_slice extends Application {  
  
    @Override  
    public void start(Stage stage) {  
  
        PieChart chart = new PieChart();  
        chart.setTitle("Population in 2000");  
        // legend 放左边  
        chart.setLegendSide(Side.LEFT);  
        // 设置数据  
        ObservableList<PieChart.Data> chartData = PieChartUtil.getChartData();  
        chart.setData(chartData);  
        // 添加提示  
        this.addSliceTooltip(chart);  
        StackPane root = new StackPane(chart);  
        Scene scene = new Scene(root);  
        stage.setScene(scene);  
        stage.setTitle("Customizing Pie Slices");  
        stage.show();  
    }  
  
    private void addSliceTooltip(PieChart chart) {  
        // 计算加和  
        double totalPieValue = 0.0;  
        for (PieChart.Data d : chart.getData()) {  
            totalPieValue += d.getPieValue();  
        }  
        // 为每个 silce 分别添加 tooltip        
        for (PieChart.Data d : chart.getData()) {  
            Node sliceNode = d.getNode();  
            double pieValue = d.getPieValue();  
            double percentPieValue = (pieValue / totalPieValue) * 100;  
            // Create and install a Tooltip for the slice  
            String msg = d.getName() + "=" + pieValue + " (" + String.format("%.2f", percentPieValue) + "%)";  
            Tooltip tt = new Tooltip(msg);  
            tt.setStyle("-fx-background-color: yellow;" + "-fx-text-fill: black;");  
            Tooltip.install(sliceNode, tt);  
        }  
    }  
  
    public static void main(String[] args) {  
  
        launch(args);  
    }  
}
```
效果：

![](2019-06-05-19-19-48.png)

## CSS

除了 `data` 属性，PieChart 的其他属性都可以由 CSS 定义。如：

```css
.chart{
	-fx.clockwise: false;
	-fx-pie-label-visible: true;
	-fx-label-line-length: 10;
	-fx-start-angle: 90;
}
```

pie slice 有 4 个样式类：

- `chart-pie`, 
- `data<i>`, 
- `default-color<j>`
- `negative`

`<i>` 为 slice index，如第一个 slice 对应 `data0`，第二个为 `date1`，以此推类。

`<j>` 为 series index，在 pie chart，可以将每个 slice 看作一个 series。

默认 CSS （Modena.css）定义了 8 种颜色，如果 pie slice 超过 8 哥，则循环使用颜色。

当某个 slice 的数据为负数，则具有 `negative` 样式类。

需要应用于所有 pie slices 的样式，在 `chart-pie` 中定义。例如，下面将所有 pie slices 的背景设为白色，添加 2px 的背景 insets。这样两个 slice 之间的距离更宽：

```css
.chart-pie {
	-fx-border-color: white;
	-fx-background-insets: 2;
}
```

可以使用如下方式为每个 pie slices 设置颜色：

```css
.chart-pie.default-color0 {-fx-pie-color: red;}
.chart-pie.default-color1 {-fx-pie-color: green;}
.chart-pie.default-color2 {-fx-pie-color: blue;}
.chart-pie.default-color3 {-fx-pie-color: yellow;}
.chart-pie.default-color4 {-fx-pie-color: tan;}
```

这里只定义了 5 个，后面的采用默认 颜色。

### 更多颜色

如果 pie chart 的 slice 超过 8 个，且不希望颜色重复，可以在 CSS 中定义余下的颜色，例如：

```css
/* additional_series_colors.css */
.chart-pie.default-color8 {
	-fx-pie-color: gold;
}
.chart-pie.default-color9 {
	-fx-pie-color: khaki;
}
```

该方法也可用于其它的图表类型。

下面演示如何设置颜色：

```java
// PieChartExtraColor.java  
package omics.ptm.chart;  
  
import javafx.application.Application;  
import javafx.collections.ObservableList;  
import javafx.geometry.Side;  
import javafx.scene.Node;  
import javafx.scene.Scene;  
import javafx.scene.chart.PieChart;  
import javafx.scene.layout.StackPane;  
import javafx.stage.Stage;  
  
public class PieChartExtraColor extends Application {  
  
    public static void main(String[] args) {  
  
        Application.launch(args);  
    }  
  
    @Override  
    public void start(Stage stage) {  
  
        PieChart chart = new PieChart();  
        chart.setTitle("Population in 2000");  
  
        // Place the legend on the left side  
        chart.setLegendSide(Side.LEFT);  
  
        // Set the data for the chart  
        ObservableList<PieChart.Data> chartData = PieChartUtil.getChartData();  
        this.addData(chartData);  
        chart.setData(chartData);  
  
        StackPane root = new StackPane(chart);  
        Scene scene = new Scene(root);  
  
        // TODO: fix in book  
//    scene.getStylesheets()  
//        .add(ResourceUtil.getResourceURLStr("css/additional_series_colors.css"));  
        stage.setScene(scene);  
        stage.setTitle("A Pie Chart with over 8 Slices");  
        stage.show();  
  
        // Override the default series color style class-name for slices over 8.  
        // Works only when you set it after the scene is visible        this.setSeriesColorStyles(chart);  
    }  
  
    private void addData(ObservableList<PieChart.Data> data) {  
  
        data.add(new PieChart.Data("Bangladesh", 138));  
        data.add(new PieChart.Data("Egypt", 68));  
        data.add(new PieChart.Data("France", 59));  
        data.add(new PieChart.Data("Germany", 82));  
        data.add(new PieChart.Data("Indonesia", 212));  
    }  

	// 修改第 9 和 10 slices 的默认的 style-class names
    private void setSeriesColorStyles(PieChart chart) {  
  
        ObservableList<PieChart.Data> chartData = chart.getData();  
        int size = chartData.size();  
        for (int i = 8; i < size; i++) {  
            String removedStyle = "default-color" + (i % 8);  
            String addedStyle = "default-color" + (i % size);  
  
            // Reset the pie slice colors  
            Node node = chartData.get(i).getNode();  
            node.getStyleClass().remove(removedStyle);  
            node.getStyleClass().add(addedStyle);  
  
            // Reser the legend colors  
            String styleClass = ".pie-legend-symbol.data" + i +  
                    ".default-color" + (i % 8);  
            Node legendNode = chart.lookup(styleClass);  
            if (legendNode != null) {  
                legendNode.getStyleClass().remove(removedStyle);  
                legendNode.getStyleClass().add(addedStyle);  
            }  
        }  
    }  
}
```

![[Pasted image 20230601233326.png|400]]

### 设置背景图片



### symbol

`pie-legend-symbol`

legend 里的每一项都有样式类 `pie-legend-symbol` 加上对应的`data`，例如 `pie-legend-symbol.data0.default-color1` 表示第一个数据的第二种颜色。

## 事件处理
虽然 pie chart slice 不是 Node 对象，但是每个 `PieChart.Data` 对象都有一个关联的 node ，可用于事件处理。

例，添加鼠标事件：