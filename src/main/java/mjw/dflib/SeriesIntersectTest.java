package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.dflib.series.BooleanArraySeries;
import org.junit.jupiter.api.Test;

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
}
