# Host 环境

2023-07-06, 14:05
****
`javafx.appliction.HostServices` 类提供托管 JavaFX 应用的启动环境相关的服务。无法直接创建 HostServices，而是通过 Application.getHostServices() 获得：

```java
HostServices host = getHostServices();
```

HostServices 包含如下方式：

- String getCodeBase()
- String getDocumentBase()
- String resolveURI(String base, String relativeURI)
- void showDocument(String uri)

`getCodeBase()` 返回应用代码库的 URI。在独立模式，返回启动应用 JAR 所在目录的 URI。如果是从 class 文件启动，返回空字符串。

`getDocumentBase()` 返回文档库的 URI。在独立模式，返回应用进程当前目录的 URI。

`resolveURI()` 使用指定的 base URI 和相对 URI 生成解析后的 URI。

`showDocument(String uri)` 在浏览器打开指定 URI。

**示例：** 演示 HostServices 的所有方法



```java
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;

public class KnowingHostDetailsApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        String yahooURL = "http://www.yahoo.com";
        Button openURLButton = new Button("Go to Yahoo!");
        openURLButton.setOnAction(e -> getHostServices().showDocument(yahooURL));

        Button showAlert = new Button("Show Alert");
        showAlert.setOnAction(e -> showAlert());

        VBox root = new VBox();

        // Add buttons and all host related details to the VBox
        root.getChildren().addAll(openURLButton, showAlert);

        Map<String, String> hostdetails = getHostDetails();
        for (Map.Entry<String, String> entry : hostdetails.entrySet()) {
            String desc = entry.getKey() + ": " + entry.getValue();
            root.getChildren().add(new Label(desc));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Knowing the Host");
        stage.show();
    }

    protected Map<String, String> getHostDetails() {
        Map<String, String> map = new HashMap<>();
        HostServices host = this.getHostServices();

        String codeBase = host.getCodeBase();
        map.put("CodeBase", codeBase);

        String documentBase = host.getDocumentBase();
        map.put("DocumentBase", documentBase);

        String splashImageURI = host.resolveURI(documentBase, "splash.jpg");
        map.put("Splash Image URI", splashImageURI);

        return map;
    }

    protected void showAlert() {
        Stage s = new Stage(StageStyle.UTILITY);
        s.initModality(Modality.WINDOW_MODAL);

        Label msgLabel = new Label("This is an FX alert!");
        Group root = new Group(msgLabel);
        Scene scene = new Scene(root);
        s.setScene(scene);

        s.setTitle("FX Alert");
        s.show();
    }
}
```

![|350](Pasted%20image%2020230706133816.png)