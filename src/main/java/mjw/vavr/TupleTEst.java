package mjw.vavr;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 09 Feb 2025, 4:12 PM
 */
public class TupleTEst {

    @Test
    void create() {
        Tuple2<String, Integer> java8 = Tuple.of("Java", 8);
        assertEquals("Java", java8._1);
        assertEquals(8, java8._2);

        Tuple2<String, Integer> that = java8.map(
                s -> s.substring(2) + "vr",
                i -> i / 8);
        assertEquals(Tuple.of("vavr", 1), that);
        Tuple2<String, Integer> that2 = java8.map(
                (s, i) -> Tuple.of(s.substring(2) + "vr", i / 8)
        );
        assertEquals(Tuple.of("vavr", 1), that2);

        String that3 = java8.apply(
                (s, i) -> s.substring(2) + "vr " + i / 8
        );
        assertEquals("vavr 1", that3);

    }
}
