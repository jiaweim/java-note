package mjw.xchart;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class IntermediateExample {
    static final Random random = new Random();

    static void main() {
        // Create Chart
        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(500)
                .title("Gaussian Blobs")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build();

        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(16);

        // Series
        chart.addSeries("Gaussian Blob 1",
                getGaussian(1000, 1, 10),
                getGaussian(1000, 1, 10));
        XYSeries series = chart.addSeries("Gaussian Blob 2",
                getGaussian(1000, 1, 10),
                getGaussian(1000, 0, 5));
        series.setMarker(SeriesMarkers.CROSS);

        new SwingWrapper(chart).displayChart();
    }

    private static List<Double> getGaussian(int number, double mean, double std) {
        List<Double> seriesData = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            seriesData.add(mean + std * random.nextGaussian());
        }

        return seriesData;
    }
}