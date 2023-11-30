package mjw.java.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 27 Nov 2023, 3:23 PM
 */
public class DoubleGenerator {

    public static List<Double> generateDoubleList(int size, int max) {

        Random random = new Random();
        List<Double> numbers = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double value = random.nextDouble() * max;
            numbers.add(value);
        }
        return numbers;
    }

    public static DoubleStream generateStreamFromList(List<Double> list) {
        DoubleStream.Builder builder = DoubleStream.builder();

        for (Double number : list) {
            builder.add(number);
        }
        return builder.build();
    }
}
