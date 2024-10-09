package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.junit5.BooleanSeriesAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.concat;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 3:54 PM
 */
public class SeriesTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a")
                .eval(concat(Exp.$col(""), ", ", $col("")));
        new SeriesAsserts(s).expectData("x, x", "b, b", "c, c", "a, a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void locate(SeriesType type) {
        BooleanSeries evens = type.createSeries(3, 4, 2).locate(i -> i % 2 == 0);
        new BooleanSeriesAsserts(evens).expectData(false, true, true);
    }
}
