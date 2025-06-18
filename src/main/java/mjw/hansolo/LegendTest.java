package mjw.hansolo;

import eu.hansolo.fx.charts.Legend;
import eu.hansolo.fx.charts.LegendItem;
import eu.hansolo.fx.charts.Symbol;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * User: hansolo
 * Date: 05.01.18
 * Time: 21:20
 */
public class LegendTest extends Application {
    private Legend legend;

    @Override
    public void init() {
        LegendItem item1 = new LegendItem(Symbol.CIRCLE, "Item 1", Color.RED, Color.BLACK);
        LegendItem item2 = new LegendItem(Symbol.SQUARE, "Item 2", Color.GREEN, Color.BLACK);
        LegendItem item3 = new LegendItem(Symbol.TRIANGLE, "Item 3", Color.BLUE, Color.BLACK);

        legend = new Legend(item1, item2, item3);
        legend.setOrientation(Orientation.VERTICAL);
    }

    @Override
    public void start(Stage stage) {
        StackPane pane = new StackPane(legend);

        Scene scene = new Scene(pane);

        stage.setTitle("Legend");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
