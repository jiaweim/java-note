package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.BooleanSeriesAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void eq1() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a", "b", "n", "c");

        BooleanSeries cond = s1.eq(s2);
        new BooleanSeriesAsserts(cond).expectData(true, true, true, true);
    }

    @Test
    public void eq2() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a ", "b", "N", "c");

        BooleanSeries cond = s1.eq(s2);
        new BooleanSeriesAsserts(cond).expectData(false, true, false, true);
    }

    @Test
    public void eq_SizeMismatch() {
        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a", "b", "n");

        assertThrows(IllegalArgumentException.class, () -> s1.eq(s2));
    }

    @Test
    public void ne1() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a", "b", "n", "c");

        BooleanSeries cond = s1.ne(s2);
        new BooleanSeriesAsserts(cond).expectData(false, false, false, false);
    }

    @Test
    public void ne2() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a ", "b", "N", "c");

        BooleanSeries cond = s1.ne(s2);
        new BooleanSeriesAsserts(cond).expectData(true, false, true, false);
    }
}
