package mjw.javafx.chart;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 28 Jul 2025, 9:24 AM
 */
public class HorizontalBarChart extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Population (in millions)");

        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Country");

        BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Population by Country and Year");

        ObservableList<XYChart.Series<Number, String>> chartData =
                this.getChartData(XYChartDataUtil.getYearSeries());
        chart.setData(chartData);

        StackPane root = new StackPane(chart);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("A Horizontal Bar Chart");
        primaryStage.show();
    }


    private ObservableList<XYChart.Series<Number, String>> getChartData(
            ObservableList<XYChart.Series<String, Number>> oldData) {

        ObservableList<XYChart.Series<Number, String>> newData =
                FXCollections.observableArrayList();

        for (XYChart.Series<String, Number> oldSeries : oldData) {
            XYChart.Series<Number, String> newSeries = new XYChart.Series<>();
            newSeries.setName(oldSeries.getName());

            for (XYChart.Data<String, Number> oldItem : oldSeries.getData()) {
                XYChart.Data<Number, String> newItem =
                        new XYChart.Data<>(oldItem.getYValue(), oldItem.getXValue());
                newSeries.getData().add(newItem);
            }
            newData.add(newSeries);
        }
        return newData;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
