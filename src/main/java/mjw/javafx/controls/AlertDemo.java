package mjw.javafx.controls;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 10 Jun 2025, 4:31 PM
 */
public class AlertDemo extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Create Alerts");

        Button b1 = new Button("Confirmation");
        Button b2 = new Button("Error");
        Button b3 = new Button("Information");
        Button b4 = new Button("Warning");
        Button b5 = new Button("None");

        TilePane r = new TilePane();

        b1.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.showAndWait();
        });
        b2.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.showAndWait();
        });
        b3.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.showAndWait();
        });
        b4.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.showAndWait();
        });
        b5.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.showAndWait();
        });

        r.getChildren().addAll(b1, b2, b3, b4, b5);

        Scene scene = new Scene(r, 200, 200);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
