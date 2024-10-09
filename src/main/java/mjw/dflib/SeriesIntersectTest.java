package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.dflib.series.BooleanArraySeries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 10:12 AM
 */
public class SeriesIntersectTest {
    @Test
    void boolEmpty() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    void boolSelf() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        Series<Boolean> c = s.intersect(s);
        new SeriesAsserts(c).expectData(true, false);
    }

    @Test
    public void boolIntersect() {
        BooleanSeries s1 = new BooleanArraySeries(true, false, false);
        Series<Boolean> s2 = Series.of(false, false);

        Series<Boolean> c = s1.intersect(s2);
        new SeriesAsserts(c).expectData(false, false);
    }

    @Test
    public void boolIntersectPrimitive() {
        BooleanSeries s1 = new BooleanArraySeries(true, false);
        BooleanSeries s2 = new BooleanArraySeries(false, false);

        Series<Boolean> c = s1.intersect(s2);
        new SeriesAsserts(c).expectData(false);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void intersect(SeriesType type) {
        Series<String> s1 = type.createSeries("a", null, "b");
        Series<String> s2 = type.createSeries("b", "c", null);

        Series<String> c = s1.intersect(s2);
        new SeriesAsserts(c).expectData(null, "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withEmpty(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withSelf(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        new SeriesAsserts(s.intersect(s)).expectData("a", "b");
    }
}
