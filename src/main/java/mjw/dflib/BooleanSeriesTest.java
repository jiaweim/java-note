package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.BooleanSeriesAsserts;
import org.dflib.series.BooleanArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 9:21 AM
 */
public class BooleanSeriesTest {

    @Test
    void concatNone() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        assertSame(s, s.concatBool());
    }

    @Test
    void concatSelf() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        BooleanSeries c = s.concatBool(s);
        new BooleanSeriesAsserts(c).expectData(true, false, true, false);
    }

    @Test
    void concat() {
        BooleanSeries s1 = new BooleanArraySeries(true, false);
        BooleanSeries s2 = new BooleanArraySeries(false, false);
        BooleanSeries s3 = new BooleanArraySeries(true, true);
        BooleanSeries c = s1.concatBool(s2, s3);
        new BooleanSeriesAsserts(c).expectData(true, false, false, false, true, true);
    }

    @Test
    void firstTrue() {
        assertEquals(-1, Series.ofBool().firstTrue());
        assertEquals(0, Series.ofBool(true, true, true).firstTrue());
        assertEquals(2, Series.ofBool(false, false, true).firstTrue());
        assertEquals(-1, Series.ofBool(false, false, false).firstTrue());
    }

    @Test
    void andAll() {
        BooleanSeries and = BooleanSeries.andAll(
                Series.ofBool(true, false, true, false),
                Series.ofBool(false, true, true, false)
        );
        new BooleanSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void orAll() {
        BooleanSeries or = BooleanSeries.orAll(
                Series.ofBool(true, false, true, false),
                Series.ofBool(false, true, true, false));
        new BooleanSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void and() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries and = s.and(Series.ofBool(false, true, true, false));
        new BooleanSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void or() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries or = s.or(Series.ofBool(false, true, true, false));
        new BooleanSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void not() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries and = s.not();
        new BooleanSeriesAsserts(and).expectData(false, true, false, true);
    }

    @Test
    public void isTrue() {
        assertTrue(Series.ofBool().isTrue());
        assertTrue(Series.ofBool(true, true, true).isTrue());
        assertFalse(Series.ofBool(true, false, true).isTrue());
    }

    @Test
    public void isFalse() {
        assertFalse(Series.ofBool().isFalse());
        assertTrue(Series.ofBool(false, false, false).isFalse());
        assertFalse(Series.ofBool(true, false, true).isFalse());
    }

}
