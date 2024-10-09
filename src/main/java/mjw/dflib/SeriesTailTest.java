package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.BooleanSeriesAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 12:35 PM
 */
public class SeriesTailTest {
    @Test
    public void booltail2() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(2);
        new BooleanSeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void booltail1() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(1);
        new BooleanSeriesAsserts(s).expectData(true);
    }

    @Test
    public void boolzero() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(0);
        new BooleanSeriesAsserts(s).expectData();
    }

    @Test
    public void booloutOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(4);
        new BooleanSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void boolnegative() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(-2);
        new BooleanSeriesAsserts(s).expectData(true);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(2);
        new SeriesAsserts(s).expectData("b", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void zero(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(0);
        new SeriesAsserts(s).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void outOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(-2);
        new SeriesAsserts(s).expectData("a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative_OutOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(-4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }
}
