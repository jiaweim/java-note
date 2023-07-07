package mjw.study.javafx.layout;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 06 7月 2023, 18:32
 */

public class GroupRender extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Button smallBtn = new Button("Small button");
        Button bigBtn = new Button("This is a big button");
        Group root = new Group();
        root.getChildren().addAll(bigBtn, smallBtn);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
