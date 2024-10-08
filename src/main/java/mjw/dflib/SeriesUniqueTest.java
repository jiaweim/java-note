package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 12:43 PM
 */
public class SeriesUniqueTest {
    @Test
    public void test() {
        BooleanSeries s1 = Series.ofBool(true, false, true, false, true).unique();
        new BooleanSeriesAsserts(s1).expectData(true, false);
    }

    @Test
    public void trueOnly() {
        BooleanSeries s1 = Series.ofBool(true, true, true).unique();
        new BooleanSeriesAsserts(s1).expectData(true);
    }

    @Test
    public void falseOnly() {
        BooleanSeries s1 = Series.ofBool(false, false, false).unique();
        new BooleanSeriesAsserts(s1).expectData(false);
    }

    @Test
    public void small() {
        BooleanSeries s1 = Series.ofBool(false, true).unique();
        new BooleanSeriesAsserts(s1).expectData(false, true);
    }

    @Test
    void falseMultiple() {
        BooleanSeries s = Series.ofBool(false, true, false, true).unique();
        new BooleanSeriesAsserts(s).expectData(false, true);
    }
}
