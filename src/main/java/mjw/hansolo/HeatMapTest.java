package mjw.hansolo;


import eu.hansolo.fx.heatmap.ColorMapping;
import eu.hansolo.fx.heatmap.HeatMap;
import eu.hansolo.fx.heatmap.HeatMapBuilder;
import eu.hansolo.fx.heatmap.OpacityDistribution;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class HeatMapTest extends Application {
    private HeatMap heatMap;

    @Override
    public void init() {
        heatMap = HeatMapBuilder.create()
                .prefSize(400, 400)
                .colorMapping(ColorMapping.INFRARED_4)
                .spotRadius(20)
                .opacityDistribution(OpacityDistribution.CUSTOM)
                .fadeColors(true)
                .build();

        heatMap.setOnMouseMoved(e -> heatMap.addSpot(e.getX(), e.getY()));
    }

    @Override
    public void start(Stage stage) {
        StackPane pane = new StackPane(heatMap);

        // Setup a mouse event filter which adds spots to the heatmap as soon as the mouse will be moved across the pane
        pane.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            double x = event.getX();
            double y = event.getY();
            if (x < heatMap.getSpotRadius()) x = heatMap.getSpotRadius();
            if (x > pane.getWidth() - heatMap.getSpotRadius()) x = pane.getWidth() - heatMap.getSpotRadius();
            if (y < heatMap.getSpotRadius()) y = heatMap.getSpotRadius();
            if (y > pane.getHeight() - heatMap.getSpotRadius()) y = pane.getHeight() - heatMap.getSpotRadius();

            heatMap.addSpot(x, y);
        });
        pane.widthProperty().addListener((ov, oldWidth, newWidth) -> heatMap.setSize(newWidth.doubleValue(), pane.getHeight()));
        pane.heightProperty().addListener((ov, oldHeight, newHeight) -> heatMap.setSize(pane.getWidth(), newHeight.doubleValue()));

        Scene scene = new Scene(pane, 400, 400);

        stage.setTitle("HeatMap (move mouse over pane)");
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
