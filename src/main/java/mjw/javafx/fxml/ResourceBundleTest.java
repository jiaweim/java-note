package mjw.javafx.fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mjw.javafx.util.ResourceUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleTest extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = ResourceUtil.getResourceURL("fxml/greetings.fxml");

        // Create a ResourceBundle to use in FXMLLoader
        String resourcePath = "resources/resourcebundles/greetings";
        System.out.println(Locale.getDefault());
        ResourceBundle resourceBundle = ResourceBundle.getBundle(resourcePath);

        // Load the Label for default Locale
        Label defaultGreetingLbl = FXMLLoader.load(fxmlUrl, resourceBundle);

        // Change the default Locale and load the Label again
        Locale.setDefault(new Locale("hi", "in"));

        // We need to recreate the ResourceBundler to pick up the new default Locale
        resourceBundle = ResourceBundle.getBundle(resourcePath);

        Label indianGreetingLbl = FXMLLoader.load(fxmlUrl, resourceBundle);

        // Add both Labels to a Vbox
        VBox root = new VBox(5, defaultGreetingLbl, indianGreetingLbl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using a ResourceBundle in FXML");
        stage.show();
    }
}
