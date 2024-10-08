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
 * @since 08 Oct 2024, 12:35 PM
 */
public class SeriesTailTest {
    @Test
    public void tail2() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(2);
        new BooleanSeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void tail1() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(1);
        new BooleanSeriesAsserts(s).expectData(true);
    }

    @Test
    public void zero() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(0);
        new BooleanSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(4);
        new BooleanSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void negative() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(-2);
        new BooleanSeriesAsserts(s).expectData(true);
    }
}
