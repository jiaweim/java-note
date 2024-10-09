package mjw.dflib;

import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 4:52 PM
 */
public class SeriesShiftTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void defaultNull(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(2);
        new SeriesAsserts(s).expectData(null, null, "a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positive(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(2, "X");
        new SeriesAsserts(s).expectData("X", "X", "a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(-2, "X");
        new SeriesAsserts(s).expectData("c", "d", "X", "X");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void zero(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(0, "X");
        new SeriesAsserts(s).expectData("a", "b", "c", "d");
    }
}
