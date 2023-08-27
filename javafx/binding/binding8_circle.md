# 示例

2023-06-28, 15:17
****
## 使用绑定将 Circle 居中

这是在 GUI 中使用绑定的一个简单例子。

创建一个带 `Circle` 的 `Scene`，`Circle` 在 `Scene` 中总是居中，即使调整 `Scene` 尺寸，`Circle` 也保持居中。`Circle` 的半径会随着 `Scene` 变化而调整，总是挨着 `Scene` 边界。

使用 binding 很容易实现该功能。`javafx.scene.shape` 包中的 `Circle` 表示圆，它包含三个属性：`centerX`, `centerY` 和 `radius`，均为 `DoubleProperty` 类型。

```java{.line-numbers}
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CenteredCircle extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Circle c = new Circle();
        Group root = new Group(c);
        Scene scene = new Scene(root, 100, 100);

        // Bind the centerX, centerY, and radius to the scene width and height
        c.centerXProperty().bind(scene.widthProperty().divide(2));
        c.centerYProperty().bind(scene.heightProperty().divide(2));
        c.radiusProperty().bind(Bindings.min(scene.widthProperty(),
                        scene.heightProperty())
                .divide(2));

        // Set the stage properties and make it visible
        stage.setTitle("Binding in JavaFX");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
```

@import "images/2023-06-28-14-57-50.png" {width="250px" title=""}

## 登录对话框

该示例主要演示 JavaFX 属性与 UI 控件的绑定。

登录对话框的基本要求：

1. 用户有三次输入机会
2. 用户输入密码错误，右侧显示红色 X
3. 用户输入密码正确，右侧显示绿色 √

@import "images/2023-08-10-14-52-56.png" {width="360px" title=""}

`User` 作为 domain 对象与 UI 交互：

```java{.line-numbers}
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private final static String USERNAME_PROP_NAME = "userName";
    private final ReadOnlyStringWrapper userName;

    private final static String PASSWORD_PROP_NAME = "password";
    private StringProperty password;

    public User() {
        userName = new ReadOnlyStringWrapper(this, USERNAME_PROP_NAME, System.getProperty("user.name"));
        password = new SimpleStringProperty(this, PASSWORD_PROP_NAME, "");
    }

    public final String getUserName() {
        return userName.get();
    }

    public ReadOnlyStringProperty userNameProperty() {
        return userName.getReadOnlyProperty();
    }

    public final String getPassword() {
        return password.get();
    }

    public final void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }
}
```

`FormValidation` 演示 lambda，属性和绑定

```java{.line-numbers}
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FormValidation extends Application {

    private final static String MY_PASS = "password1";
    private final static BooleanProperty GRANTED_ACCESS = new SimpleBooleanProperty();
    private final static int MAX_ATTEMPTS = 3;
    private final IntegerProperty ATTEMPTS = new SimpleIntegerProperty();

    @Override
    public void start(Stage primaryStage) {
        // create a model representing a user
        User user = new User();

        // 设置 Stage 透明
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);

        Group root = new Group();
        // Scene 的 fill 设置为 null，保持透明
        Scene scene = new Scene(root, 320, 112, null);

        // load style.css to style JavaFX nodes
        scene.getStylesheets().add(getClass().getResource("/css/style.css")
                        .toExternalForm());

        primaryStage.setScene(scene);

        // 圆角矩形作为背景
        Rectangle background = new Rectangle();
        background.setId("background-rect");

        background.widthProperty()
                .bind(scene.widthProperty()
                        .subtract(5));
        background.heightProperty()
                .bind(scene.heightProperty()
                        .subtract(5));

        // a read only field holding the user name.
        Label userName = new Label();
        userName.setId("username");
//        userName.setText("A very long username");
        userName.textProperty()
                .bind(user.userNameProperty());

        HBox userNameCell = new HBox();
        userNameCell.getChildren()
                .add(userName);

        // When Label's text is wider than the background minus the padlock icon.
        userNameCell.maxWidthProperty()
                .bind(primaryStage.widthProperty()
                        .subtract(45));
        userNameCell.prefWidthProperty()
                .bind(primaryStage.widthProperty()
                        .subtract(45));

        // padlock
        Region padlock = new Region();
        padlock.setId("padlock");

        HBox padLockCell = new HBox();
        padLockCell.setId("padLockCell");
        HBox.setHgrow(padLockCell, Priority.ALWAYS);
        padLockCell.getChildren().add(padlock);

        // first row 
        HBox row1 = new HBox();
        row1.getChildren()
                .addAll(userNameCell, padLockCell);

        // password text field 
        PasswordField passwordField = new PasswordField();
        passwordField.setId("password-field");
        passwordField.setPromptText("Password");
        passwordField.prefWidthProperty()
                .bind(primaryStage.widthProperty()
                        .subtract(55));

        // populate user object's password from password field
        user.passwordProperty()
                .bind(passwordField.textProperty());

        // error icon 
        Region deniedIcon = new Region();
        deniedIcon.setId("denied-icon");
        deniedIcon.setVisible(false);

        // granted icon
        Region grantedIcon = new Region();
        grantedIcon.setId("granted-icon");
        grantedIcon.visibleProperty()
                .bind(GRANTED_ACCESS);

        // hide and show denied icon and granted icon
        StackPane accessIndicator = new StackPane();
        accessIndicator.getChildren().addAll(deniedIcon, grantedIcon);

        // second row
        HBox row2 = new HBox(3);
        row2.getChildren().addAll(passwordField, accessIndicator);
        HBox.setHgrow(accessIndicator, Priority.ALWAYS);

        // user hits the enter key on the password field
        passwordField.setOnAction(actionEvent -> {
            if (GRANTED_ACCESS.get()) {
                System.out.printf("User %s is granted access.\n",
                        user.getUserName());
                System.out.printf("User %s entered the password: %s\n",
                        user.getUserName(), user.getPassword());
                Platform.exit();
            } else {
                deniedIcon.setVisible(true);
                ATTEMPTS.set(ATTEMPTS.add(1).get());
            }
        });

        // listener when the user types into the password field
        passwordField.textProperty().addListener((obs, ov, nv) -> {
            GRANTED_ACCESS.set(passwordField.getText().equals(MY_PASS));
            if (GRANTED_ACCESS.get()) {
                deniedIcon.setVisible(false);
            }
        });

        // listener on number of attempts
        ATTEMPTS.addListener((obs, ov, nv) -> {
            // failed attempts
            System.out.println("Attempts: " + ATTEMPTS.get());
            if (MAX_ATTEMPTS == nv.intValue()) {
                System.out.printf("User %s is denied access.\n", user.getUserName());
                Platform.exit();
            }
        });

        VBox formLayout = new VBox(4);
        formLayout.getChildren().addAll(row1, row2);
        formLayout.setLayoutX(12);
        formLayout.setLayoutY(12);

        root.getChildren().addAll(background, formLayout);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

style.css 文件设置样式：

```css{.line-numbers}
.root {
    common-foreground-color: rgb(255, 255, 255, 0.90);
}

#background-rect {
    -fx-translate-x: 5px;
    -fx-translate-y: 5px;
    -fx-arc-height: 15;
    -fx-arc-width: 15;
    -fx-fill: rgba(0, 0, 0, .55);
    -fx-stroke: common-foreground-color;
    -fx-stroke-width: 1.5;
}

#username {
    -fx-font-family: "Helvetica";
    -fx-font-weight: bold;
    -fx-font-size: 30;
    -fx-text-fill: common-foreground-color;
    -fx-smooth: true;
}

#padLockCell {
    -fx-alignment: center-right;
}

#padlock {
    -fx-position-shape: true;
    -fx-padding: 0 0 0 20;
    -fx-scale-shape: false;
    -fx-background-color: common-foreground-color;
    -fx-shape: "M24.875,15.334v-4.876c0-4.894-3.981-8.875-8.875-8.875s-8.875,3.981-8.875,8.875v4.876H5.042v15.083h21.916V15.334H24.875zM10.625,10.458c0-2.964,2.411-5.375,5.375-5.375s5.375,2.411,5.375,5.375v4.876h-10.75V10.458zM18.272,26.956h-4.545l1.222-3.667c-0.782-0.389-1.324-1.188-1.324-2.119c0-1.312,1.063-2.375,2.375-2.375s2.375,1.062,2.375,2.375c0,0.932-0.542,1.73-1.324,2.119L18.272,26.956z";
}

#denied-icon {
    -fx-position-shape: true;
    -fx-padding: 0 0 0 20;
    -fx-scale-shape: false;
    -fx-border-color: white;
    -fx-background-color: rgba(255, 0, 0, .9);
    -fx-shape: "M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z";
}

#granted-icon {
    -fx-position-shape: true;
    -fx-padding: 0 0 0 20;
    -fx-scale-shape: false;
    -fx-border-color: white;
    -fx-background-color: rgba(0, 255, 0, .9);
    -fx-shape: "M2.379,14.729 5.208,11.899 12.958,19.648 25.877,6.733 28.707,9.561 12.958,25.308z";
}

#password-field {
    -fx-font-family: "Helvetica";
    -fx-font-size: 20;
    -fx-text-fill: black;
    -fx-prompt-text-fill: gray;
    -fx-highlight-text-fill: black;
    -fx-highlight-fill: gray;
    -fx-background-color: rgba(255, 255, 255, .80);
}
```

类成员变量：

- `MY_PASS` 硬编码的密码
- `GRANTED_ACCESS`：`SimpleBooleanProperty` 类型，默认 false。绿色 √ 的 `visible` 属性与其绑定
  - 用户输入密码正确，`GRANTED_ACCESS` 变为 true，转而将绿色 √ 的 `visible` 属性设置为 true
  - 用户输入密码错误，`visible` 属性为 false，隐藏绿色 √ node (Region)
- ATTEMPTS 属性表示尝试次数，常量 MAX_ATTEMPTS 表示允许最大尝试次数


