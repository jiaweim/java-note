package mjw.study.javafx.shape;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 05 Jun 2023, 14:59
 */
public class DashArrayTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Polygon p1 = new Polygon(0, 0, 100, 0, 100, 50, 0, 50, 0, 0);
        p1.setFill(null);
        p1.setStroke(Color.BLACK);
        p1.getStrokeDashArray().addAll(15.0, 5.0, 5.0, 5.0);

        Polygon p2 = new Polygon(0, 0, 100, 0, 100, 50, 0, 50, 0, 0);
        p2.setFill(null);
        p2.setStroke(Color.BLACK);
        p2.getStrokeDashArray().addAll(15.0, 5.0, 5.0, 5.0);
        p2.setStrokeDashOffset(20.0);

        HBox root = new HBox(p1, p2);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
//        root.setStyle("-fx-padding: 10;" +
//                "-fx-border-style: solid inside;" +
//                "-fx-border-width: 2;" +
//                "-fx-border-insets: 5;" +
//                "-fx-border-radius: 5;" +
//                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Using different stroke types for shapes");
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
