package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 1:36 PM
 */
public class ColumnSetAggTest {

    @Test
    public void intDouble() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                0, 55.5);

        DataFrame agg = df.cols().agg(
                $int("a").avg(),
                $double(1).avg());

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void doubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 4L,
                5, 8L,
                0, 55.5);

        DataFrame agg = df.cols().agg(
                $double("a").avg($int(0).ne(5)),
                $double(1).avg($int(0).ne(5)));

        new DataFrameAsserts(agg, "avg(a)", "avg(b)")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void set() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols().agg($col("a").set(), $col(1).set());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, Set.of(1, 2), Set.of("x", "a"));
    }

    @Test
    public void list() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.cols().agg($col("a").list(), $col(1).list());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, List.of(1, 2, 1), List.of("x", "x", "a"));
    }

    @Test
    void countTest() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                0, "a");

        DataFrame agg = df.cols().agg(count());

        new DataFrameAsserts(agg, "count")
                .expectHeight(1)
                .expectRow(0, 2);
    }


    @Test
    public void countFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols().agg(
                count($int(0).mod(2).eq(0)),
                count($int("b").mod(2).eq(1))
        );

        new DataFrameAsserts(agg, "count", "count_")
                .expectHeight(1)
                .expectRow(0, 1, 3);
    }


    @Test
    public void first() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols().agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void firstEmpty() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols().agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, null, null);
    }

    @Test
    public void firstNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                null, 5);

        DataFrame agg = df.cols().agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, null);
    }

    @Test
    public void firstFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols().agg(
                $col(1).first($int(0).mod(2).eq(0)),
                $col("a").first($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, 5, 7);
    }

    @Test
    public void firstFilteredNoMatches() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols().agg(
                $col(1).first($val(false).castAsBool()),
                $col("a").first($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, null, 7);
    }


    @Test
    public void last() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols().agg(
                $col("a").last(),
                $col(1).last());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, 5);
    }

    @Test
    public void testAggFunc() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols().agg($col(1).agg(Series::size));

        new DataFrameAsserts(agg, "b")
                .expectHeight(1)
                .expectRow(0, 2);
    }


    @Test
    public void medianOdd() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 55.5);
    }

    @Test
    public void medianEven() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100.,
                0, 55.5,
                4, 0.,
                3, 5.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 2., 30.25);
    }

    @Test
    public void medianZero() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 0., 0.);
    }

    @Test
    public void medianOne() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, 100);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $int(1).median());


        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 100.);
    }

    @Test
    public void medianNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                0, 55.5,
                4, 0.,
                null, 5.);

        DataFrame agg = df.cols().agg(
                $int("a").median(),
                $double(1).median());

        new DataFrameAsserts(agg, "median(a)", "median(b)")
                .expectHeight(1)
                .expectRow(0, 1., 5.);
    }


    @Test
    public void minMaxInts() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 1,
                8, 1);

        DataFrame agg = df.cols().agg(
                $int("a").min(),
                $int("a").max());

        new DataFrameAsserts(agg, "min(a)", "max(a)")
                .expectHeight(1)
                .expectRow(0, -1, 8);
    }


    @Test
    public void minLongFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, 1L,
                2L, 4L,
                -1L, 5L,
                8L, 2L);

        DataFrame agg = df.cols().agg(
                $long(1).max($long(0).mod(2).eq(0L)),
                $long(1).min($long(0).mod(2).eq(0L)),
                $long("a").max($long("b").mod(2).eq(1L)),
                $long("a").min($long("b").mod(2).eq(1L))
        );


        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4L, 2L, 1L, -1L);
    }

    @Test
    public void minIntFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                2, 4,
                -1, 5,
                8, 2);

        DataFrame agg = df.cols().agg(
                $int(1).max($int(0).mod(2).eq(0)),
                $int(1).min($int(0).mod(2).eq(0)),
                $int("a").max($int("b").mod(2).eq(1)),
                $int("a").min($int("b").mod(2).eq(1))
        );


        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 4, 2, 1, -1);
    }

    @Test
    public void minDoubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1., 1.01,
                6.5, 15.7,
                -1.2, 5.1,
                8., 2.);

        DataFrame agg = df.cols().agg(
                $double(1).max($double(0).gt(5)),
                $double(1).min($double(0).gt(5)),
                $double("a").max($double("b").gt(5)),
                $double("a").min($double("b").gt(5))
        );

        new DataFrameAsserts(agg, "max(b)", "min(b)", "max(a)", "min(a)")
                .expectHeight(1)
                .expectRow(0, 15.7, 2.0, 6.5, -1.2);
    }


    @Test
    public void sumint_long() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5L);

        DataFrame agg = df.cols().agg(
                $int("a").sum(),
                $long(1).sum());


        new DataFrameAsserts(agg, "sum(a)", "sum(b)")
                .expectHeight(1)
                .expectRow(0, 0, 6L);
    }

    @Test
    public void sumintFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.cols().agg(
                // filter is applied to column 0, sum is applied to column 1
                $int(1).sum($int(0).mod(2).eq(0)),
                // filter is applied to column 1, sum is applied to column 0
                $int("a").sum($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)")
                .expectHeight(1)
                .expectRow(0, 11, -4);
    }

    @Test
    public void sumlongFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1,
                -1, 5,
                2, 6,
                -4, 5);

        DataFrame agg = df.cols().agg(
                // filter is applied to column 0, sum is applied to column 1
                $long(1).sum($int(0).mod(2).eq(0)),
                // filter is applied to column 1, sum is applied to column 0
                $long("a").sum($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)")
                .expectHeight(1)
                .expectRow(0, 11L, -4L);
    }

    @Test
    public void sumdoubleFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.1, 1.8,
                -1., 5.8,
                2.35, 6.5,
                4.6, 5.1);


        DataFrame agg = df.cols().agg(
                // filter is applied to column 0, sum is applied to column 1
                $double(1).sum($double(0).lt(4)),
                // filter is applied to column 1, sum is applied to column 0
                $double("a").sum($double("b").gt(5)));

        new DataFrameAsserts(agg, "sum(b)", "sum(a)").expectHeight(1);

        assertEquals(14.1, (Double) agg.getColumn("sum(b)").get(0), 0.000000001);
        assertEquals(5.95, (Double) agg.getColumn("sum(a)").get(0), 0.000000001);
    }


    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols()
                .agg(
                        $long("a").sum(),
                        count(),
                        $double("d").sum());


        new DataFrameAsserts(agg, "sum(a)", "count", "sum(d)")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols("sum_a", "count", "sum_d")
                .agg(
                        $long("a").sum(),
                        count(),
                        $double("d").sum());

        new DataFrameAsserts(agg, "sum_a", "count", "sum_d")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df
                .cols(0, 2, 3)
                .agg(
                        $long("a").sum(),
                        count(),
                        $double("d").sum());

        new DataFrameAsserts(agg, "a", "c", "d")
                .expectHeight(1)
                .expectRow(0, 3L, 3, 3.501);
    }

    @Test
    public void vConcat() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                0, "a");

        DataFrame agg = df.cols().agg(
                $col("a").vConcat("_"),
                $col(1).vConcat(" ", "[", "]"));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1_0", "[x a]");
    }

    @Test
    public void vConcatfiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5,
                8, 8);

        DataFrame agg = df.cols().agg(
                $col(1).vConcat($int(0).mod(2).eq(0), "_"),
                $col("a").vConcat($int("b").mod(2).eq(1), ", ", "[", "]"));
        System.out.println(Printers.tabular.toString(agg));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, "5_8", "[7, -1, -4]");
    }

}
