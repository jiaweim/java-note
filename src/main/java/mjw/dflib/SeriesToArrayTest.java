package mjw.dflib;

import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 5:06 PM
 */
public class SeriesToArrayTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void contents(SeriesType type) {
        String[] a = type.createSeries("a", "b", "c", "d", "e").toArray(new String[0]);
        assertArrayEquals(new Object[]{"a", "b", "c", "d", "e"}, a);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void mutability(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        String[] a = s.toArray(new String[0]);
        a[0] = "c";

        new SeriesAsserts(s).expectData("a", "b");
    }
}
