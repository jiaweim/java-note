package mjw.java.util;

import com.google.common.collect.Streams;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 01 Dec 2023, 4:37 PM
 */
public class StreamsDemo {

    @Test
    void index() {

//        Stream<String> a = Stream.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
//        Streams.mapWithIndex(a.parallel(), (from, index) -> from + ":" + index)
//                .forEach(s -> System.out.println(Thread.currentThread().getName() + ":" + s));
        Streams.mapWithIndex(IntStream.rangeClosed('a', 'z').parallel(),
                        (from, index) -> ((char) from) + ":" + index)
                .forEach(s -> System.out.println(Thread.currentThread().getName() + ":" + s));

    }
}
