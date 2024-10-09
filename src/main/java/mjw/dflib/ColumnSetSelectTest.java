package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 12:41 PM
 */
public class ColumnSetSelectTest {

    @Test
    public void colsAppend() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsAppend("b", "c")
                .select($int(0).mul(100), $int(0).mul(10));

        new DataFrameAsserts(df, "b_", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c").select($int(0).mul(100),
                        $int(0).mul(10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols_ByName_Duplicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "b")
                .select($int(0).mul(100),
                        $int(0).mul(10));

        new DataFrameAsserts(df, "b", "b_")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 7).select($int(0).mul(100),
                        $int(0).mul(10));

        new DataFrameAsserts(df, "b", "7")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols().select($int(0).mul(100).as("b"),
                        $int(0).mul(10));

        new DataFrameAsserts(df, "b", "a * 10")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void colsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a").select($int(0).mul(100),
                        $int(0).mul(10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void colsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1)
                .select($int(0).mul(100),
                        $int(0).mul(10));

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }


    @Test
    public void RowMappercols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c")
                .select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowMappercols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2)
                .select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowMappercols_ByMapper() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .select(
                        (f, t) -> {
                            t.set("b", f.get(0, Integer.class) * 100);
                            t.set("c", f.get(0, Integer.class) * 10);
                        }
                );

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowMappercolsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a")
                .select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowMappercolsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1)
                .select((f, t) -> t
                        .set(0, f.get(0, Integer.class) * 100)
                        .set(1, f.get(0, Integer.class) * 10));

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }


    @Test
    public void RowToValueMappercols_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("b", "c")
                .select(
                        r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowToValueMappercols_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(1, 2)
                .select(
                        r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "b", "2")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowToValueMappercols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .select(
                        r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10
                );

        new DataFrameAsserts(df, "0", "1")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowToValueMappercolsExcept_ByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept("a")
                .select(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }

    @Test
    public void RowToValueMappercolsExcept_ByPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "z", 2, "y", "a")
                .colsExcept(1)
                .select(r -> r.get(0, Integer.class) * 100,
                        r -> r.get(0, Integer.class) * 10);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 100, 10)
                .expectRow(1, 200, 20);
    }


    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "Y", "c")
                .select();

        new DataFrameAsserts(df, "a", "Y", "c")
                .expectHeight(2)
                .expectRow(0, 1, null, "a")
                .expectRow(1, 2, null, "b");
    }

    @Test
    public void byPos() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols(0, 2, 4)
                .select();

        new DataFrameAsserts(df, "a", "c", "4")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }

    @Test
    public void noTarget() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols()
                .select();
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }
}
