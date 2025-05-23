package mjw.javafx.bean;

import javafx.beans.property.*;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 May 2025, 4:26 PM
 */
public class NumericPropertiesExample {
    public static void main(String[] args) {
        IntegerProperty i = new SimpleIntegerProperty(null, "i", 1024);
        LongProperty l = new SimpleLongProperty(null, "l", 0L);
        FloatProperty f = new SimpleFloatProperty(null, "f", 0.0F);
        DoubleProperty d = new SimpleDoubleProperty(null, "d", 0.0);

        System.out.println(i.get());
        System.out.println(l.get());
        System.out.println(f.get());
        System.out.println(d.get());

        l.bind(i);
        f.bind(l);
        d.bind(f);

        System.out.println("Binding d->f->l->i");
        System.out.println(i.get());
        System.out.println(l.get());
        System.out.println(f.get());
        System.out.println(d.get());

        System.out.println("i.set(2048)");
        i.set(2048);
        System.out.println(i.get());
        System.out.println(l.get());
        System.out.println(f.get());
        System.out.println(d.get());

        d.unbind();
        f.unbind();
        l.unbind();

        f.bind(d);
        l.bind(f);
        i.bind(l);

        System.out.println("Binding i->l->f->d");
        d.set(10000000000L);
        System.out.println(d.get());
        System.out.println(f.get());
        System.out.println(l.get());
        System.out.println(i.get());
    }
}
