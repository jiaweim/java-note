package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 11:22 AM
 */
public class SeriesSelectTest {
    @Test
    public void boolPositional() {
        Series<Boolean> s = Series.ofBool(true, false, true).select(2, 1);
        new SeriesAsserts(s).expectData(true, false);
        assertInstanceOf(BooleanSeries.class, s);
    }

    @Test
    public void boolPositional_Empty() {
        Series<Boolean> s = Series.ofBool(true, false, true).select();
        new SeriesAsserts(s).expectData();
        assertInstanceOf(BooleanSeries.class, s);
    }

    @Test
    public void position_OutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> Series.ofBool(true, false, true).select(0, 3).materialize());
    }

    @Test
    public void positional_Nulls() {
        Series<Boolean> s = Series.ofBool(true, false, true).select(2, 1, -1);
        new SeriesAsserts(s).expectData(true, false, null);
        assertFalse(s instanceof BooleanSeries);
    }

    @Test
    public void booleanCondition() {
        BooleanSeries condition = Series.ofBool(false, true, true);
        Series<Boolean> s = Series.ofBool(true, false, true).select(condition);
        new SeriesAsserts(s).expectData(false, true);
        assertInstanceOf(BooleanSeries.class, s);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positional(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2, 4);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positional_Empty(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positional_OutOfBounds(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select(0, 3);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.materialize());
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positionalNulls(SeriesType type) {
        Series<Integer> s = type.createSeries(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2, 4, null);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void booleanCondition(SeriesType type) {
        BooleanSeries condition = Series.ofBool(false, true, true);
        Series<Integer> s = type.createSeries(3, 4, 2).select(condition);
        new SeriesAsserts(s).expectData(4, 2);
    }
}
