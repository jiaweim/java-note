package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.dflib.series.BooleanArraySeries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 9:28 AM
 */
public class SeriesDiffTest {
    @Test
    void boolEmpty() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    void boolSelf() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    void boolDiff() {
        BooleanSeries s1 = new BooleanArraySeries(true, false);
        Series<Boolean> s2 = Series.of(false, false);
        BooleanSeries c = s1.diff(s2);
        new SeriesAsserts(c).expectData(true);
    }

    @Test
    void boolDiffPrimitive() {
        BooleanSeries s1 = new BooleanArraySeries(true, false);
        BooleanSeries s2 = new BooleanArraySeries(false, false);

        Series<Boolean> c = s1.diff(s2);
        new SeriesAsserts(c).expectData(true);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void diff(SeriesType type) {
        Series<String> s1 = type.createSeries("a", null, "b");
        Series<String> s2 = type.createSeries("b", "c", null);

        Series<String> c = s1.diff(s2);
        new SeriesAsserts(c).expectData("a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withEmpty(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        assertSame(s, s.diff(Series.of()));
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withSelf(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        Series<String> c = s.diff(s);
        new SeriesAsserts(c).expectData();
    }
}
