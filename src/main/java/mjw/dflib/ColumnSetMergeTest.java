package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.$str;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 11:07 AM
 */
public class ColumnSetMergeTest {

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void cols_ByName_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "b").merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsAppend() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c", "b").merge(
                        $int(0).mul(100),
                        $int(0).mul(10),
                        $str(1).mapVal(v -> "[" + v + "]"));

        new DataFrameAsserts(df, "a", "b", "b_", "c", "b__")
                .expectHeight(2)
                .expectRow(0, 1, "x", 100, 10, "[x]")
                .expectRow(1, 2, "y", 200, 20, "[y]");
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().merge($int(0).mul(100).as("b"), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "a * 10")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).merge($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, "x", 10)
                .expectRow(1, 200, "y", 20);
    }


    @Test
    public void rowMappercolsAppend() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c").merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "b_", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 100, 10)
                .expectRow(1, 2, "y", 200, 20);
    }

    @Test
    public void rowMappercols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void rowMappercols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10)
                );

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void rowMappercols_ByMapper() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().merge(
                        (f, t) -> {
                            t.set("b", f.get(0, Integer.class) * 100);
                            t.set("c", f.get(0, Integer.class) * 10);
                        }
                );

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void rowMappercolsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void rowMappercolsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).merge((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, "x", 10)
                .expectRow(1, 200, "y", 20);
    }


    @Test
    public void RowToValueMappercolsAppend() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c")
                .merge(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "b", "b_", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 100, 10)
                .expectRow(1, 2, "y", 200, 20);
    }

    @Test
    public void RowToValueMappercols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").merge(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void RowToValueMappercols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2).merge(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "b", "2")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void RowToValueMappercols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().merge(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "b", "2", "3")
                .expectHeight(2)
                .expectRow(0, 1, "x", 100, 10)
                .expectRow(1, 2, "y", 200, 20);
    }

    @Test
    public void RowToValueMappercolsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").merge(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 100, 10)
                .expectRow(1, 2, 200, 20);
    }

    @Test
    public void RowToValueMappercolsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1).merge(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, "x", 10)
                .expectRow(1, 200, "y", 20);
    }
}
