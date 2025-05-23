package mjw.javafx.bean;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 May 2025, 4:06 PM
 */
public class DirectExtensionExample {
    public static void main(String[] args) {
        DoubleProperty x = new SimpleDoubleProperty(null, "x", 2.0);
        DoubleProperty y = new SimpleDoubleProperty(null, "y", 3.0);
        DoubleBinding area = new DoubleBinding() {
            {
                super.bind(x, y);
            }

            @Override
            protected double computeValue() {
                return x.get() * y.get();
            }
        };
        System.out.println(area.get());
        x.set(5);
        y.set(7);
        System.out.println(area.get());
    }
}
