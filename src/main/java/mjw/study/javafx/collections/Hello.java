package mjw.study.javafx.collections;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 04 7月 2023, 16:08
 */

public class Hello {

    public static void main(String[] args) {
        ListProperty<String> lp = new SimpleListProperty<String>();
// No ObservableList to work with. Generates an exception.
        lp.add("Hello");
    }
}
