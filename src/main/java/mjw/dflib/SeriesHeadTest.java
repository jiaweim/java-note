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

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(2);
        new SeriesAsserts(s).expectData("a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void zero(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(0);
        new SeriesAsserts(s).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void outOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(-2);
        new SeriesAsserts(s).expectData("c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative_OutOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(-4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }
}
