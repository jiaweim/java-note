package mjw.javafx.collections;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;


public class ListExtractorDemo {

    public static void main(String[] args) {
        ObservableList<IntegerProperty> listWithoutExtractor =
                FXCollections.observableArrayList();

        ObservableList<IntegerProperty> listWithExtractor =
                FXCollections.observableArrayList(p -> new Observable[]{p});

        listWithoutExtractor.addListener(createListener("listWithoutExtractor"));
        listWithExtractor.addListener(createListener("listWithExtractor"));

        IntegerProperty p1 = new SimpleIntegerProperty(1);
        IntegerProperty p2 = new SimpleIntegerProperty(2);

        // both lists will fire change events when items are added or removed:
        listWithoutExtractor.addAll(p1, p2);
        listWithExtractor.addAll(p1, p2);

        // only the list with the extractor will fire a change event when the observable value of an element changes:
        p2.set(3);
    }

    private static ListChangeListener<IntegerProperty> createListener(String listId) {
        return (Change<? extends IntegerProperty> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    System.out.println(listId + " added: " + c.getAddedSubList());
                }
                if (c.wasRemoved()) {
                    System.out.println(listId + " removed: " + c.getRemoved());
                }
                if (c.wasUpdated()) {
                    System.out.println(listId + " updated");
                }
            }
        };
    }
}