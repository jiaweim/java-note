package mjw.dflib;

import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 5:16 PM
 */
public class SeriesToSetTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void contents(SeriesType type) {
        Set<String> set = type.createSeries("a", "b", "a", "d", "b").toSet();
        assertEquals(new HashSet<>(asList("a", "b", "d")), set);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void mutability(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        Set<String> set = s.toSet();
        set.remove("b");

        assertEquals(new HashSet<>(asList("a")), set);
        new SeriesAsserts(s).expectData("a", "b");
    }
}
