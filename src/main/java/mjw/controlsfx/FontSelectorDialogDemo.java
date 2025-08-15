package mjw.controlsfx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mjw.javafx.layout.StackPaneAlignment;
import org.controlsfx.dialog.FontSelectorDialog;

import java.util.Optional;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 11 Aug 2025, 1:29 PM
 */
public class FontSelectorDialogDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Font Selection Dialog");

        Button b = new Button();
        b.setText(b.getFont().toString());
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FontSelectorDialog dialog = new FontSelectorDialog(b.getFont());
                Optional<Font> font = dialog.showAndWait();
                if (font.isPresent()) {
                    b.setText(font.get().toString());
                }
            }
        });

        StackPane root = new StackPane(b);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
