package mjw.hansolo;

import eu.hansolo.fx.charts.Axis;
import eu.hansolo.fx.charts.tools.Helper;
import eu.hansolo.toolbox.unit.Converter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static eu.hansolo.toolbox.unit.Category.TEMPERATURE;
import static eu.hansolo.toolbox.unit.UnitDefinition.CELSIUS;
import static eu.hansolo.toolbox.unit.UnitDefinition.FAHRENHEIT;


/**
 * User: hansolo
 * Date: 22.07.17
 * Time: 14:12
 */
public class AxisTest extends Application {

    private static final double AXIS_WIDTH = 20;
    private static final double AXIS_HEIGHT = 20;

    private Axis xAxisBottom;
    private Axis xAxisTop;
    private Axis yAxisLeft;
    private Axis yAxisRight;

    @Override
    public void init() {
        xAxisBottom = Helper.createBottomAxis(-20, 20, AXIS_HEIGHT);
        xAxisTop = Helper.createTopAxis(0, 100, AXIS_HEIGHT);
        yAxisLeft = Helper.createLeftAxis(-20, 20, AXIS_WIDTH);

        Converter tempConverter = new Converter(TEMPERATURE, CELSIUS); // Type Temperature with BaseUnit Celsius
        double tempFahrenheitMin = tempConverter.convert(-20, FAHRENHEIT);
        double tempFahrenheitMax = tempConverter.convert(20, FAHRENHEIT);
        yAxisRight = Helper.createRightAxis(tempFahrenheitMin, tempFahrenheitMax, false, AXIS_WIDTH);

        AnchorPane.setTopAnchor(yAxisLeft, AXIS_HEIGHT);
        AnchorPane.setTopAnchor(xAxisTop, 0d);
        AnchorPane.setTopAnchor(yAxisRight, AXIS_HEIGHT);
    }

    @Override
    public void start(Stage stage) {
        AnchorPane pane = new AnchorPane(xAxisBottom, xAxisTop, yAxisLeft, yAxisRight);
        pane.setPadding(new Insets(10));
        pane.setPrefSize(400, 400);

        Scene scene = new Scene(pane);

        stage.setTitle("Axis Test");
        stage.setScene(scene);
        stage.show();

        xAxisTop.setMinValue(50);
        xAxisTop.setMaxValue(150);
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
