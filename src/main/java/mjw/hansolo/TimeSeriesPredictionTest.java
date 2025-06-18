/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2024 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mjw.hansolo;

import eu.hansolo.fx.charts.*;
import eu.hansolo.fx.charts.data.TYChartItem;
import eu.hansolo.fx.charts.data.XYChartItem;
import eu.hansolo.fx.charts.series.XYSeries;
import eu.hansolo.fx.charts.series.XYSeriesBuilder;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TimeSeriesPredictionTest extends Application {
    private XYChart<TYChartItem> timeSeriesPredictionChart;


    @Override public void init() {
        ChronoUnit timeStep = ChronoUnit.MONTHS;

        // Target data
        LocalDateTime         targetStart        = LocalDateTime.of(1949, 1, 1, 0, 0, 0, 0);
        List<Double>          targetDataList     = getDataFromTextfile(TimeSeriesPredictionTest.class.getResource("target_timeseries.txt").toExternalForm().replaceAll("file:", ""));

        // Prediction data
        LocalDateTime         predictionStart    = LocalDateTime.of(1960, 1, 1, 0, 0, 0, 0);
        List<Double>          predictionDataList = getDataFromTextfile(TimeSeriesPredictionTest.class.getResource("prediction_timeseries.txt").toExternalForm().replaceAll("file:", ""));

        // Quantile 50 data
        List<Double>          quantile50DataList = getDataFromTextfile(TimeSeriesPredictionTest.class.getResource("quantile_50_timeseries.txt").toExternalForm().replaceAll("file:", ""));

        // Quantile 90 data
        List<Double>          quantile90DataList = getDataFromTextfile(TimeSeriesPredictionTest.class.getResource("quantile_90_timeseries.txt").toExternalForm().replaceAll("file:", ""));

        // Target series
        XYSeries<TYChartItem> targetSeries       = createSeries(ChartType.LINE, targetStart, timeStep, Color.TRANSPARENT, Color.BLACK, targetDataList);

        // Create prediction
        Prediction prediction         = createPredication(predictionStart, timeStep, Color.RED, predictionDataList, quantile50DataList, quantile90DataList);

        timeSeriesPredictionChart = createChart(targetStart, timeStep, targetSeries, List.of(prediction));

        // Render chart to image and save it to given filename
        //timeSeriesPredictionChart.renderToImage("predictionChart.png", 900, 400);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(timeSeriesPredictionChart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane), 900, 400);

        stage.setTitle("TimeSeries with prediction");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private XYChart createChart(final LocalDateTime targetStart, final ChronoUnit timeStep, final XYSeries<TYChartItem> targetSeries, final List<Prediction> predictions) {
        // PredictionTimeSeriesChart
        final List<XYSeries<TYChartItem>> listOfSeries  = new ArrayList<>();
        listOfSeries.add(targetSeries);
        predictions.forEach(prediction -> {
            listOfSeries.add(prediction.getPredictionSeries());
            listOfSeries.add(prediction.getQuantile50Series());
            listOfSeries.add(prediction.getQuantile90Series());
        });

        // Calculate min,max values for y-axis
        double yAxisMinValue = Double.MAX_VALUE;
        double yAxisMaxValue = Double.MIN_VALUE;
        for (XYSeries<TYChartItem> series : listOfSeries) {
            for (int i = 0; i < series.getItems().size(); i++) {
                final XYChartItem item = series.getItems().get(i);
                yAxisMinValue = Math.min(yAxisMinValue, item.getY());
                yAxisMaxValue = Math.max(yAxisMaxValue, item.getY());
            }
        }
        final Double axisWidth = 25d;
        Axis xAxis = AxisBuilder.create(Orientation.HORIZONTAL, Position.BOTTOM)
                                .type(AxisType.TIME)
                                .start(targetStart)
                                .end(targetStart.plus(targetSeries.getNoOfItems(), timeStep))
                                .dateTimeFormatPattern("YYYY")
                                .autoScale(false)
                                .majorTickMarksVisible(false)
                                .mediumTickMarksVisible(false)
                                .minorTickMarksVisible(false)
                                .sameTickMarkLength(true)
                                .mediumTimeAxisTickLabelsVisible(true)
                                .rightAnchor(axisWidth)
                                .bottomAnchor(0d)
                                .leftAnchor(axisWidth)
                                .build();

        Axis yAxis = Helper.createLeftAxis(yAxisMinValue, yAxisMaxValue, "Value", true, axisWidth);
        yAxis.setDecimals(2);

        XYPane xyPane = new XYPane(listOfSeries);
        xyPane.getOverlays().addAll(predictions);

        return new XYChart(List.of(xyPane), yAxis, xAxis);
    }

    private Prediction<TYChartItem> createPredication(final LocalDateTime predictionStart, final ChronoUnit timeStep, final Color color, final List<Double> predictionData, final List<Double> quantile50Data, final List<Double> quantile90Data) {
        XYSeries<TYChartItem> predictionSeries = createSeries(ChartType.PREDICTION_TIMESERIES, predictionStart, timeStep, Color.TRANSPARENT, color, predictionData);
        XYSeries<TYChartItem> quantile50Series = createSeries(ChartType.PREDICTION_TIMESERIES, predictionStart, timeStep, Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.35), Color.TRANSPARENT, quantile50Data);
        XYSeries<TYChartItem> quantile90Series = createSeries(ChartType.PREDICTION_TIMESERIES, predictionStart, timeStep, Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.15), Color.TRANSPARENT, quantile90Data);
        return new Prediction(predictionStart, timeStep, predictionSeries, quantile50Series, quantile90Series);
    }

    private XYSeries<TYChartItem> createSeries(final ChartType chartType, final LocalDateTime start, final ChronoUnit timeStep, final Color fill, final Color stroke, final List<Double> data) {
        final XYSeries<TYChartItem> series = XYSeriesBuilder.create()
                                                            .chartType(chartType)
                                                            .fill(fill)
                                                            .stroke(stroke)
                                                            .symbolFill(Color.TRANSPARENT)
                                                            .symbolStroke(Color.TRANSPARENT)
                                                            .symbolsVisible(false)
                                                            .symbolSize(5)
                                                            .strokeWidth(0.5)
                                                            .build();
        final long timeStepSec = timeStep.getDuration().getSeconds();
        for (int i = 0 ; i < data.size() ; i++) {
            LocalDateTime t     = start.plusSeconds(timeStepSec * i);
            double        value = data.get(i);
            series.getItems().add(new TYChartItem(t, value));
        }
        return series;
    }

    private List<Double> getDataFromTextfile(final String filename) {
        final String data = Helper.readTextFile(filename);
        return Arrays.stream(data.split("\\s?,\\s?")).toList().stream().map(v -> Double.parseDouble(v)).toList();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
