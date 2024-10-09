package mjw.dflib;

import org.dflib.IntSeries;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.IntSeriesAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 2:06 PM
 */
public class SeriesExpandTest {

    @Test
    public void expand() {
        Series<?> s = Series.ofInt(3, 28).expand("abc");
        new SeriesAsserts(s).expectData(3, 28, "abc");
    }

    @Test
    public void expandInt() {
        IntSeries s = Series.ofInt(3, 28).expandInt(5);

        new IntSeriesAsserts(s).expectData(3, 28, 5);
    }

    @Test
    public void add_Series() {
        IntSeries s0 = Series.ofInt(1, 2, 3, 4, 5, 6);
        IntSeries s = Series.ofInt(3, 28, 15, -4, 3, 11).add(s0);
        System.out.println(Printers.tabular.toString(s));

        new IntSeriesAsserts(s).expectData(4, 30, 18, 0, 8, 17);
    }
}
