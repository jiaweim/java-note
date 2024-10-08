package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 9:37 AM
 */
public class SeriesEqTest {
    @Test
    void boolEqAll() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        BooleanSeries s2 = Series.ofBool(true, false, true);

        new SeriesAsserts(s1.eq(s2)).expectData(true, true, true);
    }

    @Test
    void boolSelf() {
        BooleanSeries s = Series.ofBool(true, false, true);
        new SeriesAsserts(s.eq(s)).expectData(true, true, true);
    }

    @Test
    void boolPrimitive() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        BooleanSeries s2 = Series.ofBool(true, true, true);

        new SeriesAsserts(s1.eq(s2)).expectData(true, false, true);
    }

    @Test
    void boolObject() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        Series<Boolean> s2 = Series.of(true, true, true);

        new SeriesAsserts(s1.eq(s2)).expectData(true, false, true);
    }

    @Test
    void boolNe() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        BooleanSeries s2 = Series.ofBool(true, true, true);

        new SeriesAsserts(s1.ne(s2)).expectData(false, true, false);
    }
}
