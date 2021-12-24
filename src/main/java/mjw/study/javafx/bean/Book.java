package mjw.study.javafx.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 12 Dec 2021, 10:29 PM
 */
public class Book
{
    private StringProperty title = new SimpleStringProperty(this, "title", "Unknown");

}
