package mjw.dflib;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 10:56 AM
 */
public class SeriesReplaceTest {

    @Test
    public void boolReplace_positions() {

        Series<Boolean> s1 = Series.ofBool(true, false, false, true).replace(
                Series.ofInt(1, 3),
                Series.ofBool(true, false));

        new SeriesAsserts(s1).expectData(true, true, false, false);
    }

    @Test
    public void boolReplace_positions_nulls() {

        Series<Boolean> s1 = Series.ofBool(true, false, false, true).replace(
                Series.ofInt(1, 3),
                Series.of(true, null));

        new SeriesAsserts(s1).expectData(true, true, false, null);
    }

    @Test
    public void boolReplace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void boolReplace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(null, null, true, true);
    }


    @Test
    public void boolReplace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void boolReplaceMap() {

        Series<Boolean> s1 = Series.ofBool(true, false, true, true)
                .replace(Map.of(true, false, false, true));
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(false, true, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true)
                .replace(Collections.singletonMap(true, null));
        new SeriesAsserts(s2).expectData(null, false, null, null);
    }


    @Test
    public void boolReplaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true)
                .replaceExcept(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true)
                .replaceExcept(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void boolReplaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceExcept(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, null, null);
    }

    @Test
    public void boolReplaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true)
                .replaceExcept(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true)
                .replaceExcept(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void boolReplaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true)
                .replaceExcept(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true)
                .replaceExcept(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }


    @Test
    public void replace_positions() {

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(
                Series.ofInt(1, 3),
                Series.of("B", "C"));

        new SeriesAsserts(s1).expectData("a", "B", "n", "C");
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(cond, "X");
        new SeriesAsserts(s1).expectData("X", "X", "n", "c");
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(cond, null);
        new SeriesAsserts(s1).expectData(null, null, "n", "c");
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(cond, "X");
        new SeriesAsserts(s1).expectData("X", "X", "n", "c");
    }

    @Test
    public void replaceMap() {

        Map<String, String> replacement = new HashMap<>();
        replacement.put("a", "A");
        replacement.put("n", null);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(replacement);
        new SeriesAsserts(s1).expectData("A", "b", null, "c");
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, null);
        new SeriesAsserts(s1).expectData("a", "b", null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }
}
