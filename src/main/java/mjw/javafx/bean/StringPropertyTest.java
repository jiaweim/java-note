package mjw.javafx.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 11 8月 2023, 16:21
 */
public class StringPropertyTest {

    public static void main(String[] args) {
        StringProperty a = new SimpleStringProperty();
        a.setValue(null);
        System.out.println(a.get());
    }
}
