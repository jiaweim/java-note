package mjw.hansolo;

import eu.hansolo.fx.charts.BubbleChart;
import eu.hansolo.fx.charts.data.ChartItem;
import eu.hansolo.fx.charts.data.ChartItemBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BubbleChartTest extends Application {
    private static final Random RND = new Random();
    private BubbleChart chart;
    private List<ChartItem> items;


    @Override
    public void init() {
        items = new ArrayList<>();
        items.add(ChartItemBuilder.create().name("Item 1").value(2).fill(Color.rgb(221, 78, 77)).build());
        items.add(ChartItemBuilder.create().name("Item 2").value(7).fill(Color.rgb(215, 131, 79)).build());
        items.add(ChartItemBuilder.create().name("Item 3").value(5).fill(Color.rgb(236, 165, 57)).build());
        items.add(ChartItemBuilder.create().name("Item 4").value(8).fill(Color.rgb(135, 170, 102)).build());
        items.add(ChartItemBuilder.create().name("Item 5").value(10).fill(Color.rgb(136, 171, 173)).build());
        items.add(ChartItemBuilder.create().name("Item 6").value(6).fill(Color.rgb(76, 179, 210)).build());
        items.add(ChartItemBuilder.create().name("Item 7").value(3).fill(Color.rgb(106, 198, 255)).build());

        chart = new BubbleChart(items);

        AnchorPane.setTopAnchor(chart, 10d);
        AnchorPane.setRightAnchor(chart, 10d);
        AnchorPane.setBottomAnchor(chart, 10d);
        AnchorPane.setLeftAnchor(chart, 10d);
    }

    @Override
    public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(chart);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Bubble Chart");
        stage.setScene(scene);
        stage.show();

        chart.start();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
