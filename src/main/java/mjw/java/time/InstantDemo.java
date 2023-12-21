package mjw.java.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 04 Dec 2023, 5:23 PM
 */
public class InstantDemo {

    @Test
    void now() {
        Instant min = Instant.MIN;
        System.out.println(min);
    }

    @Test
    void between() {
        Instant start = Instant.now();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long millis = timeElapsed.toMillis();

    }

    public static void runAlgorithm() {
        int size = 10;
        ArrayList<Integer> list = new Random().ints()
                .map(operand -> operand % 100).limit(size)
                .boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(list);
        System.out.println(list);
    }

    public static void runAlgorithm2() {
        int size = 10;
        List<Integer> list = new Random().ints()
                .map(operand -> operand % 100).limit(size)
                .boxed().collect(Collectors.toCollection(ArrayList::new));
        while (!IntStream.range(1, list.size())
                .allMatch(value -> list.get(value - 1).compareTo(list.get(value)) <= 0))
            Collections.shuffle(list);
        System.out.println(list);
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        runAlgorithm();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long millis = timeElapsed.toMillis();
        System.out.printf("%d milliseconds\n", millis);

        Instant start2 = Instant.now();
        runAlgorithm2();
        Instant end2 = Instant.now();
        Duration timeElapsed2 = Duration.between(start2, end2);
        System.out.printf("%d milliseconds\n", timeElapsed2.toMillis());

        boolean overTenTimesFaster = timeElapsed.multipliedBy(10)
                .minus(timeElapsed2).isNegative();
        System.out.printf("The first algorithm is %smore than ten times faster", overTenTimesFaster ? "" : "not ");
    }

    @Test
    void mutliply() {

    }
}


