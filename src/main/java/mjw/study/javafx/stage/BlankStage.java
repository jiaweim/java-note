// BlankStage.java
package mjw.study.javafx.stage;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BlankStage extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("A Sized Stage with a Sized Scene");
        Group root = new Group(new Button("Hello"));
        Scene scene = new Scene(root, 300, 100);
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(100);
        stage.show();
    }
}
