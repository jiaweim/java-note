package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.dflib.Exp.$str;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Oct 2024, 12:23 PM
 */
public class GroupByAggTest {

    @Test
    public void empty() {
        DataFrame df1 = DataFrame.empty("a", "b");

        DataFrame df = df1.group("a").agg(
                $long("a").sum(),
                $str(1).vConcat(";"));

        new DataFrameAsserts(df, "sum(a)", "b").expectHeight(0);
    }

    @Test
    public void cols_Implicit() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").agg(
                $long("a").sum(),
                $str(1).vConcat(";"));

        new DataFrameAsserts(df, "sum(a)", "b")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void multipleAggregationsForColumn() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1
                .group("b")
                .agg(
                        $col("b").first(),
                        $long("a").sum(),
                        $double("a").median());

        new DataFrameAsserts(df, "b", "sum(a)", "median(a)")
                .expectHeight(3)
                .expectRow(0, "x", 2L, 1.)
                .expectRow(1, "y", 3L, 1.5)
                .expectRow(2, "a", 0L, 0.);
    }

    @Test
    public void cols_Implicit_NamedExp() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "y",
                0, "a",
                1, "x");

        DataFrame df = df1.group("b").agg(
                $col("b").first().as("first"),
                $col("b").last().as("last"),
                $long("a").sum().as("a_sum"),
                $double("a").median().as("a_median")
        );

        new DataFrameAsserts(df, "first", "last", "a_sum", "a_median")
                .expectHeight(3)
                .expectRow(0, "x", "x", 2L, 1.)
                .expectRow(1, "y", "y", 3L, 1.5)
                .expectRow(2, "a", "a", 0L, 0.);
    }

    @Test
    public void cols_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").cols("A", "B").agg(
                $long("a").sum(),
                $str(1).vConcat(";"));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void cols_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame df = df1.group("a").cols(1, 0).agg(
                $long("a").sum(),
                $str(1).vConcat(";"));

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void cols_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("aA", "b", "aB").of(
                1, "x", 50,
                2, "y", 51,
                1, "z", 52,
                0, "a", 53,
                1, "x", 54);

        DataFrame df = df1.group("aA").cols(s -> s.startsWith("a")).agg(
                $long("aA").sum(),
                $str(1).vConcat(";"));

        new DataFrameAsserts(df, "aA", "aB")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df1 = DataFrame.foldByRow("a", "C", "b").of(
                1, 60, "x",
                2, 61, "y",
                1, 62, "z",
                0, 63, "a",
                1, 64, "x");

        DataFrame df = df1.group("a").colsExcept("C").agg(
                $long("a").sum(),
                $str(2).vConcat(";"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df1 = DataFrame.foldByRow("a", "skip", "b").of(
                1, 40, "x",
                2, 41, "y",
                1, 42, "z",
                0, 43, "a",
                1, 44, "x");

        DataFrame df = df1.group("a").colsExcept(1).agg(
                $long("a").sum(),
                $str(2).vConcat(";"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }

    @Test
    public void colsExcept_ByPredicate() {
        DataFrame df1 = DataFrame.foldByRow("aA", "b", "aB").of(
                1, "x", 50,
                2, "y", 51,
                1, "z", 52,
                0, "a", 53,
                1, "x", 54);

        DataFrame df = df1.group("aA").colsExcept(s -> !s.startsWith("a")).agg(
                $long("aA").sum(),
                $str(1).vConcat(";"));
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "aA", "aB")
                .expectHeight(3)
                .expectRow(0, 3L, "x;z;x")
                .expectRow(1, 2L, "y")
                .expectRow(2, 0L, "a");
    }
}
