package mjw.study.javafx.shape;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 27 7月 2023, 14:16
 */

public class SVGDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SVGPath p = new SVGPath();
        p.setContent("M9.998,13.946L22.473,1.457L26.035,5.016L10.012,21.055L0.618,11.688L4.178,8.127L9.998,13.946Z");
        StackPane root = new StackPane(p);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
