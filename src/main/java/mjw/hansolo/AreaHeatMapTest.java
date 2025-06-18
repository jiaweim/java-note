package mjw.hansolo;

import eu.hansolo.fx.charts.areaheatmap.AreaHeatMap;
import eu.hansolo.fx.charts.areaheatmap.AreaHeatMap.Quality;
import eu.hansolo.fx.charts.areaheatmap.AreaHeatMapBuilder;
import eu.hansolo.fx.charts.data.DataPoint;
import eu.hansolo.fx.heatmap.ColorMapping;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 22.11.17
 * Time: 19:55
 */
public class AreaHeatMapTest extends Application {
    private static final Random RND = new Random();
    private AreaHeatMap areaHeatMap;

    @Override
    public void init() {
        List<DataPoint> randomPoints = new ArrayList<>(29);
        //randomPoints.add(new DataPoint(0, 0, 0));
        //randomPoints.add(new DataPoint(400, 0, 0));
        //randomPoints.add(new DataPoint(400, 400, 0));
        //randomPoints.add(new DataPoint(0, 400, 0));
        for (int counter = 0; counter < 25; counter++) {
            double x = RND.nextDouble() * 400;
            double y = RND.nextDouble() * 400;
            double v = RND.nextDouble() * 100 - 50;
            randomPoints.add(new DataPoint(x, y, v));
        }

        areaHeatMap = AreaHeatMapBuilder.create()
                .prefSize(400, 400)
                .colorMapping(ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED)
                .quality(Quality.FINE)
                .heatMapOpacity(0.5)
                .useColorMapping(true)
                .dataPointsVisible(true)
                .noOfCloserInfluentialPoints(5)
                .dataPoints(randomPoints)
                .build();
    }

    @Override
    public void start(Stage stage) {
        StackPane pane = new StackPane(areaHeatMap);

        Scene scene = new Scene(pane);

        stage.setTitle("Area HeatMap");
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
