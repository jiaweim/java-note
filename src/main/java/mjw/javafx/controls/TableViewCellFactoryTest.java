// TableViewCellFactoryTest.java
package mjw.javafx.controls;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mjw.javafx.mvc.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TableViewCellFactoryTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        TableView<Person> table = new TableView<>(PersonTableUtil.getPersonList());

        // Create the birth date column
        TableColumn<Person, LocalDate> birthDateCol = PersonTableUtil.getBirthDateColumn();

        // Set a custom cell factory for Birth Date column
        birthDateCol.setCellFactory(col -> {
            TableCell<Person, LocalDate> cell = new TableCell<>() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    // Cleanup the cell before populating it
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        String formattedDob = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                                .format(item);
                        this.setText(formattedDob);
                    }
                }
            };
            return cell;
        });

        // Create and configure the baby column
        TableColumn<Person, Boolean> babyCol = new TableColumn<>("Baby?");
        babyCol.setCellValueFactory(
                cellData -> {
                    Person p = cellData.getValue();
                    Boolean v = (p.getAgeCategory() == Person.AgeCategory.BABY);
                    return new ReadOnlyBooleanWrapper(v);
                });

        // Set a custom cell factory for the baby column
        babyCol.setCellFactory(
                CheckBoxTableCell.<Person>forTableColumn(babyCol));

        // Add columns to the table
        table.getColumns().addAll(PersonTableUtil.getIdColumn(),
                PersonTableUtil.getFirstNameColumn(),
                PersonTableUtil.getLastNameColumn(),
                birthDateCol,
                babyCol);

        HBox root = new HBox(table);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using a Custom Cell Factory for a TableColumn");
        stage.show();
    }
}
