package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.junit5.BooleanSeriesAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;

import static org.dflib.Exp.$bool;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 12:28 PM
 */
public class SeriesSortTest {
    @Test
    public void sort_Comparator() {
        BooleanSeries s = Series.ofBool(true, false, true, false)
                .sort((b1, b2) -> b1 == b2 ? 0 : b1 ? -1 : 1);

        new BooleanSeriesAsserts(s).expectData(true, true, false, false);
    }

    @Test
    public void sort_Sorter() {
        BooleanSeries s = Series.ofBool(true, false, true, false)
                .sort($bool(0).desc());

        new BooleanSeriesAsserts(s).expectData(true, true, false, false);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void comparator(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort(Comparator.naturalOrder());
        new SeriesAsserts(s).expectData("a", "b", "c", "x");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void sorter(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort(Exp.$col(0).desc());
        new SeriesAsserts(s).expectData("x", "c", "b", "a");
    }
}
