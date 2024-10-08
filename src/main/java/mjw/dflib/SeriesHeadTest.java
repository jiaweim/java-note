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
 * @since 08 Oct 2024, 9:54 AM
 */
public class SeriesHeadTest {
    @Test
    void boolTest() {
        BooleanSeries s = Series.ofBool(true, false, true).head(2);
        new BooleanSeriesAsserts(s).expectData(true, false);
    }

    @Test
    void boolZero() {
        BooleanSeries s = Series.ofBool(true, false, true).head(0);
        new BooleanSeriesAsserts(s).expectData();
    }

    @Test
    void boolOutOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).head(4);
        new BooleanSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    void boolNegative() {
        BooleanSeries s = Series.ofBool(true, false, true).head(-2);
        new BooleanSeriesAsserts(s).expectData(true);
    }
}
