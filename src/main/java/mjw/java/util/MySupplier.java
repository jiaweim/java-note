package mjw.java.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 27 Nov 2023, 9:18 AM
 */
public class MySupplier implements Supplier<String> {

    private final AtomicInteger counter;

    public MySupplier() {
        this.counter = new AtomicInteger(0);
    }

    @Override
    public String get() {
        int value = counter.getAndAdd(1);
        return "String " + value;
    }
}
