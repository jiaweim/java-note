package mjw.dflib;

import org.dflib.IntSeries;
import org.dflib.junit5.IntSeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 4:54 PM
 */
public class SeriesSortIndexTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        IntSeries s = type.createSeries("x", "b", "c", "a")
                .sortIndex(Comparator.naturalOrder());
        new IntSeriesAsserts(s).expectData(3, 1, 2, 0);
    }
}
