# MVP 示例

2023-07-11, 16:52
****
## 1. 简介

下面开发一个 GUI 应用，让用户输入 person 的详细信息，验证数据，并保存。需要包含：

- Person ID 字段: 自动生成独有的不可编辑的字段
- First name 字段：可编辑 text 字段
- Last name 字段：可编辑 text 字段
- Birth date: 可编辑 text 字段
- Age category: 根据 birth date 自动生成的不可编辑字段
- Save button: 保存数据
- Close button: 关闭窗口

验证 person 数据：

- first 和 last names 至少包含 1 个字符
- birth date 不能是未来日期

## 2. 设计

定义 3 个类表示 MVP：

- Person，表示 model
- PersonView，表示 view
- PersonPresenter，表示 presenter

根据 MVP 模式要求，Person 类独立于 PersonView 和 PersonPresenter。

PersonView 和 PersonPresenter 相互交互，并直接使用 Person。最终界面如下：

![|400](Pasted%20image%2020230711154942.png)

## 3. 实现

### 3.1. Model

Person 类包含业务数据和逻辑。

person ID, first name, last name, birth date 都用 JavaFX Property 表示。personId 自动生成，表示为 read-only Property。

`isValidBirthDate()` 和 `isValidPerson()` 用于验证。

`getAgeCategory()` 根据出生日期计算 age 分类。其中定义了一些年龄范围。

save() 方法保存 person 数据。这里只是将 person 数据输出到 stdout。在实际应用中，可能需要将数据保存到文件或数据库。

```java
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Person {

    // An enum for age categories
    public enum AgeCategory {
        BABY, CHILD, TEEN, ADULT, SENIOR, UNKNOWN
    }

    private final ReadOnlyIntegerWrapper personId = new ReadOnlyIntegerWrapper(this, 
                                        "personId", personSequence.incrementAndGet());
    private final StringProperty firstName =
            new SimpleStringProperty(this, "firstName", null);
    private final StringProperty lastName =
            new SimpleStringProperty(this, "lastName", null);
    private final ObjectProperty<LocalDate> birthDate =
            new SimpleObjectProperty<>(this, "birthDate", null);

    // Keeps track of last generated person id
    private static AtomicInteger personSequence = new AtomicInteger(0);

    public Person() {
        this(null, null, null);
    }

    public Person(String firstName, String lastName, LocalDate birthDate) {
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.birthDate.set(birthDate);
    }

    public final int getPersonId() {
        return personId.get();
    }

    public final ReadOnlyIntegerProperty personIdProperty() {
        return personId.getReadOnlyProperty();
    }

    public final String getFirstName() {
        return firstName.get();
    }

    public final void setFirstName(String firstName) {
        firstNameProperty().set(firstName);
    }

    public final StringProperty firstNameProperty() {
        return firstName;
    }

    public final String getLastName() {
        return lastName.get();
    }

    public final void setLastName(String lastName) {
        lastNameProperty().set(lastName);
    }

    public final StringProperty lastNameProperty() {
        return lastName;
    }

    public final LocalDate getBirthDate() {
        return birthDate.get();
    }

    public final void setBirthDate(LocalDate birthDate) {
        birthDateProperty().set(birthDate);
    }

    public final ObjectProperty<LocalDate> birthDateProperty() {
        return birthDate;
    }

    public boolean isValidBirthDate(LocalDate bdate) {
        return isValidBirthDate(bdate, new ArrayList<>());
    }

    public boolean isValidBirthDate(LocalDate bdate, List<String> errorList) {
        if (bdate == null) {
            return true;
        }

        // Birth date cannot be in the future
        if (bdate.isAfter(LocalDate.now())) {
            errorList.add("Birth date must not be in future.");
            return false;
        }

        return true;
    }

    public boolean isValidPerson(List<String> errorList) {
        return isValidPerson(this, errorList);
    }

    public boolean isValidPerson(Person p, List<String> errorList) {
        boolean isValid = true;

        String fn = p.firstName.get();
        if (fn == null || fn.trim().length() == 0) {
            errorList.add("First name must contain minimum one character.");
            isValid = false;
        }

        String ln = p.lastName.get();
        if (ln == null || ln.trim().length() == 0) {
            errorList.add("Last name must contain minimum one character.");
            isValid = false;
        }

        if (!isValidBirthDate(this.birthDate.get(), errorList)) {
            isValid = false;
        }

        return isValid;
    }

    public AgeCategory getAgeCategory() {
        if (birthDate.get() == null) {
            return AgeCategory.UNKNOWN;
        }

        long years = ChronoUnit.YEARS.between(birthDate.get(), LocalDate.now());
        if (years >= 0 && years < 2) {
            return AgeCategory.BABY;
        } else if (years >= 2 && years < 13) {
            return AgeCategory.CHILD;
        } else if (years >= 13 && years <= 19) {
            return AgeCategory.TEEN;
        } else if (years > 19 && years <= 50) {
            return AgeCategory.ADULT;
        } else if (years > 50) {
            return AgeCategory.SENIOR;
        } else {
            return AgeCategory.UNKNOWN;
        }
    }

    public boolean save(List<String> errorList) {
        boolean isSaved = false;
        if (isValidPerson(errorList)) {
            System.out.println("Saved " + this.toString());
            isSaved = true;
        }

        return isSaved;
    }

    @Override
    public String toString() {
        return "[personId=" + personId.get() +
                ", firstName=" + firstName.get() +
                ", lastName=" + lastName.get() +
                ", birthDate=" + birthDate.get() + "]";
    }
}
```

### 3.2. View

```java
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonView extends GridPane {

    private final Person model;

    // Labels
    Label personIdLbl = new Label("Person Id:");
    Label fNameLbl = new Label("First Name:");
    Label lNameLbl = new Label("Last Name:");
    Label bDateLbl = new Label("Birth Date:");
    Label ageCategoryLbl = new Label("Age Category:");

    // Fields
    TextField personIdFld = new TextField();
    TextField fNameFld = new TextField();
    TextField lNameFld = new TextField();
    TextField bDateFld = new TextField();
    TextField ageCategoryFld = new TextField();

    // Buttons
    Button saveBtn = new Button("Save");
    Button closeBtn = new Button("Close");

    // Date format
    String dateFormat;

    public PersonView(Person model, String dateFormat) {
        this.model = model;
        this.dateFormat = dateFormat;
        layoutForm();
        initFieldData();
        bindFieldsToModel();
    }

    private void initFieldData() {
        // Id and names are populated using bindings. 
        // Populate birth date and age category
        syncBirthDate();
    }

    private void layoutForm() {
        this.setHgap(5);
        this.setVgap(5);

        this.add(personIdLbl, 1, 1);
        this.add(fNameLbl, 1, 2);
        this.add(lNameLbl, 1, 3);
        this.add(bDateLbl, 1, 4);
        this.add(ageCategoryLbl, 1, 5);

        this.add(personIdFld, 2, 1);
        this.add(fNameFld, 2, 2);
        this.add(lNameFld, 2, 3);
        this.add(bDateFld, 2, 4);
        this.add(ageCategoryFld, 2, 5);

        // Add buttons and make them the same width
        VBox buttonBox = new VBox(saveBtn, closeBtn);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setMaxWidth(Double.MAX_VALUE);

        this.add(buttonBox, 3, 1, 1, 5);

        // Disable the personId field
        personIdFld.setDisable(true);
        ageCategoryFld.setDisable(true);

        // Set the prompt text for the birth date field
        bDateFld.setPromptText(dateFormat.toLowerCase());
    }

    public void bindFieldsToModel() {
        personIdFld.textProperty().bind(model.personIdProperty().asString());
        fNameFld.textProperty().bindBidirectional(model.firstNameProperty());
        lNameFld.textProperty().bindBidirectional(model.lastNameProperty());
    }

    public void syncBirthDate() {
        LocalDate bdate = model.getBirthDate();
        if (bdate != null) {
            bDateFld.setText(bdate.format(DateTimeFormatter.ofPattern(dateFormat)));
        }

        syncAgeCategory();
    }

    public void syncAgeCategory() {
        ageCategoryFld.setText(model.getAgeCategory().toString());
    }
}
```

PersonView 继承 GridPane。对每个 UI 组件都有一个实例变量:

- date format 用于设置日期的显示格式，特定于 view，所以在 PersonView 中定义
- `initFieldData()` 使用 Person 数据初始化 view
- `bindFieldsToModel()`  将 person ID, first name, last name TextField 与 model 对应的字段绑定
- `syncBirthDate(`) 从 model 读取 birth date，格式化，在 view 显示
- syncAgeCategory() 同步 age 数据

注意：PersonView 类不依赖于 PersonPresenter，那么 PersonView 和 PersonPresenter 如何交互？presenter 主要从 view 获取用户输入，并执行相关操作。presenter 包含对 view 的引用，并添加 listeners 监听 view 。如果在 view 中需要引用 presenter，可以在 view 构造函数中添加 presenter，或添加一个 setter 方法。

### 3.3. Presenter

PersonPresenter 负责处理用户输入。直接与 view 和 model 进行通信。

```java
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PersonPresenter {

    private final Person model;
    private final PersonView view;
	
    public PersonPresenter(Person model, PersonView view) {
        this.model = model;
        this.view = view;
        attachEvents();
    }

    private void attachEvents() {
        // We need to detect the birth date change when the bDate field loses
        // focus or the user presses the Enter key while it still has focus
        view.bDateFld.setOnAction(e -> handleBirthDateChange());
        view.bDateFld.getScene().focusOwnerProperty()
                .addListener(this::focusChanged);

        // Save the data
        view.saveBtn.setOnAction(e -> saveData());

        // Close the window when the Close button is pressed
        view.closeBtn.setOnAction(e -> view.getScene().getWindow().hide());
    }

    public void focusChanged(ObservableValue<? extends Node> value,
                             Node oldNode,
                             Node newNode) {

        // The birth date field has lost focus
        if (oldNode == view.bDateFld) {
            handleBirthDateChange();
        }
    }

    private void handleBirthDateChange() {
        String bdateStr = view.bDateFld.getText();
        if (bdateStr == null || bdateStr.trim().equals("")) {
            model.setBirthDate(null);
            view.syncBirthDate();
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(view.dateFormat);
                LocalDate bdate = LocalDate.parse(bdateStr, formatter);

                List<String> errorList = new ArrayList<>();
                if (model.isValidBirthDate(bdate, errorList)) {
                    model.setBirthDate(bdate);
                    view.syncAgeCategory();
                } else {
                    this.showError(errorList);
                    view.syncBirthDate();
                }
            } catch (DateTimeParseException e) {
                // Birth date is not in the specified date format
                List<String> errorList = new ArrayList<>();
                errorList.add("Birth date must be in the " +
                        view.dateFormat.toLowerCase() + " format.");
                this.showError(errorList);

                // Refresh the view
                view.syncBirthDate();
            }
        }
    }

    private void saveData() {
        List<String> errorList = new ArrayList<>();
        boolean isSaved = model.save(errorList);
        if (!isSaved) {
            this.showError(errorList);
        }
    }

    public void showError(List<String> errorList) {
        String msg = "";
        if (errorList.isEmpty()) {
            msg = "No message to display.";
        } else {
            for (String s : errorList) {
                msg = msg + s + "\n";
            }
        }

        Label msgLbl = new Label(msg);
        Button okBtn = new Button("OK");
        VBox root = new VBox(new StackPane(msgLbl), new StackPane(okBtn));
        root.setSpacing(10);

        Scene scene = new Scene(root);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.initOwner(view.getScene().getWindow());

        // Set the Action listener for the OK button
        okBtn.setOnAction(e -> stage.close());

        stage.setTitle("Error");
        stage.sizeToScene();
        stage.showAndWait();
    }
}
```

PersonPresenter 构造函数以 model 和 view 为参数。attachEvents() 为 view 的 UI 组件添加事件处理器

## 4. 演示

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PersonApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Person model = new Person();
        String dateFormat = "MM/dd/yyyy";
        PersonView view = new PersonView(model, dateFormat);

        // Must set the scene before creating the presenter that uses
        // the scene to listen for the focus change
        Scene scene = new Scene(view);

        PersonPresenter presenter = new PersonPresenter(model, view);
        view.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        stage.setScene(scene);
        stage.setTitle("Person Management");
        stage.show();
    }
}
```
