package mjw.dflib;

import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 5:11 PM
 */
public class SeriesToListTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void contents(SeriesType type) {
        List<String> l = type.createSeries("a", "b", "c", "d", "e").toList();
        assertEquals(asList("a", "b", "c", "d", "e"), l);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void mutability(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        List<String> l = s.toList();
        l.set(0, "c");

        assertEquals(asList("c", "b"), l);
        new SeriesAsserts(s).expectData("a", "b");
    }
}
