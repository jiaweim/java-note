package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.count;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 2:40 PM
 */
public class SeriesAggTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void agg(SeriesType type) {
        String aggregated = type.createSeries("a", "b", "cd", "e", "fg")
                .agg(Exp.$col("").vConcat("_"))
                .get(0);
        assertEquals("a_b_cd_e_fg", aggregated);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void aggMultiple(SeriesType type) {

        DataFrame aggregated = type.createSeries("a", "b", "cd", "e", "fg")
                .aggMultiple(
                        $col(0).first().as("first"),
                        $col(0).vConcat("|").as("concat"),
                        $col(0).vConcat("_", "[", "]").as("concat"),
                        count().as("count"));

        System.out.println(Printers.tabular.toString(aggregated));

        new DataFrameAsserts(aggregated, "first", "concat", "concat_", "count")
                .expectRow(0, "a", "a|b|cd|e|fg", "[a_b_cd_e_fg]", 5);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void first(SeriesType type) {
        String f1 = type.createSeries("a", "b", "cd", "e", "fg").first();
        assertEquals("a", f1);

        Object f2 = type.createSeries().first();
        assertNull(f2);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void concat(SeriesType type) {
        String concat = type.createSeries("a", "b", "cd", "e", "fg").concat("_");
        assertEquals("a_b_cd_e_fg", concat);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void concat_PrefixSuffix(SeriesType type) {
        String concat = type.createSeries("a", "b", "cd", "e", "fg").concat("_", "[", "]");
        assertEquals("[a_b_cd_e_fg]", concat);
    }


    @Test
    public void aggregate_AverageDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(-0.9, Exp.$double("").avg().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void aggregate_Concat() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals("abzc", Exp.$col("").vConcat("").eval(s).get(0));
        assertEquals("[a|b|z|c]", Exp.$col("").vConcat("|", "[", "]").eval(s).get(0));
    }

    @Test
    public void aggregate_First() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals("a", Exp.$col("").first().eval(s).get(0));
    }

    @Test
    public void aggregate_List() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals(asList("a", "b", "z", "c"), Exp.$col("").list().eval(s).get(0));
    }

    @Test
    public void aggregate_Max() {
        Series<Integer> s = Series.of(4, 5, -9);
        assertEquals(5, Exp.$int("").max().eval(s).get(0));
    }

    @Test
    public void aggregate_Min() {
        Series<Integer> s = Series.of(4, 5, -9);
        assertEquals(-9, Exp.$int("").min().eval(s).get(0));
    }

    @Test
    public void aggregate_MaxDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(5.3, Exp.$double("").max().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void aggregate_MaxInt() {
        Series<Integer> s = Series.of(4, 5, -9);
        assertEquals(5, Exp.$int("").max().eval(s).get(0).intValue());
    }

    @Test
    public void aggregate_MaxLong() {
        Series<Long> s = Series.of(4L, 5L, -9L);
        assertEquals(5L, Exp.$long("").max().eval(s).get(0).longValue());
    }

    @Test
    public void aggregate_MedianDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(1.4, Exp.$double("").median().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void aggregate_SumDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(-2.7, Exp.$double("").sum().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void aggregate_SumBigDecimal() {
        Series<BigDecimal> s = Series.of(
                new BigDecimal("1.4").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("5.3").setScale(4, RoundingMode.HALF_UP),
                new BigDecimal("-9.4").setScale(2, RoundingMode.HALF_UP));

        assertEquals(BigDecimal.valueOf(-2.7000).setScale(4, RoundingMode.HALF_UP),
                Exp.$decimal("").sum().eval(s).get(0));
    }

    // TODO:  Exp.$decimal("").sum(2, RoundingMode.HALF_UP)
//    @Test
//    public void aggregate_SumBigDecimal_Scale() {
//        Series<BigDecimal> s = Series.forData(
//                new BigDecimal("1.4").setScale(2, RoundingMode.HALF_UP),
//                new BigDecimal("5.3").setScale(4, RoundingMode.HALF_UP),
//                new BigDecimal("-9.4").setScale(2, RoundingMode.HALF_UP));
//
//        assertEquals(new BigDecimal("-2.7").setScale(2, RoundingMode.HALF_UP),
//                Exp.$decimal("").sum(2, RoundingMode.HALF_UP).eval(s).get(0));
//    }

    @Test
    public void aggregate_Set() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals(new HashSet<>(asList("a", "b", "z", "c")),
                Exp.$col("").set().eval(s).get(0));
    }
}
