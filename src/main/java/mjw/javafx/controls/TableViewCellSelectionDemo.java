package mjw.javafx.controls;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TableViewCellSelectionDemo extends Application {

    private TableView<Person> tableView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TableView Cell Selection Demo");

        // 创建表格列
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 创建表格并启用单元格选择模式
        tableView = new TableView<>();
        tableView.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
        tableView.getSelectionModel().setCellSelectionEnabled(true); // 启用单元格选择
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // 可选：允许多选

        // 添加选择监听器
        tableView.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                showSelectedRowInfo();
            }
        });

        // 添加示例数据
        tableView.setItems(getSampleData());

        // 创建布局
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showSelectedRowInfo() {
        // 获取选中的单元格
        ObservableList<TablePosition> selectedCells = tableView.getSelectionModel().getSelectedCells();

        if (!selectedCells.isEmpty()) {
            // 获取第一个选中的单元格
            TablePosition position = selectedCells.get(0);

            // 方法1：通过行索引获取行数据
            int rowIndex = position.getRow();
            Person selectedPerson = tableView.getItems().get(rowIndex);

            // 方法2：直接获取选中的行项（推荐）
            Person selectedItem = tableView.getSelectionModel().getSelectedItem();

            System.out.println("选中的行索引: " + rowIndex);
            System.out.println("选中的行数据: " + selectedPerson);
            System.out.println("通过getSelectedItem获取: " + selectedItem);
            System.out.println("选中的列索引: " + position.getColumn());
            System.out.println("选中的列数据: " + position.getTableColumn().getCellData(rowIndex));
            System.out.println("------------------------");
        }
    }

    // 示例数据模型
    public static class Person {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty email;

        public Person(String fName, String lName, String email) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
        }

        public String getFirstName() {return firstName.get();}

        public String getLastName() {return lastName.get();}

        public String getEmail() {return email.get();}

        @Override
        public String toString() {
            return firstName.get() + " " + lastName.get();
        }
    }

    // 获取示例数据
    private ObservableList<Person> getSampleData() {
        return FXCollections.observableArrayList(
                new Person("John", "Doe", "john.doe@example.com"),
                new Person("Jane", "Smith", "jane.smith@example.com"),
                new Person("Bob", "Johnson", "bob.johnson@example.com"),
                new Person("Alice", "Williams", "alice.williams@example.com")
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}