package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Oct 2024, 5:46 PM
 */
public class RowColumnSetDropTest {

    @Test
    public void rowsAll_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows().cols("b", "a")
                .drop();

        new DataFrameAsserts(df, "c").expectHeight(0);
    }

    @Test
    public void rowsAll_colsDeferred() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows().cols()
                .drop();

        new DataFrameAsserts(df).expectHeight(0);
    }

    @Test
    public void rowsByIndex_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2).cols("b", "a")
                .drop();

        new DataFrameAsserts(df, "c")
                .expectHeight(1)
                .expectRow(0, "b");
    }

    @Test
    public void rowsByIndex_colsDeferred() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2).cols()
                .drop();

        new DataFrameAsserts(df).expectHeight(0);
    }

    @Test
    public void rowsByIndex_colsByName_ExpandRows_ExpandCols() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(0, 2, 2).cols("b", "a", "x")
                .drop();

        new DataFrameAsserts(df, "c")
                .expectHeight(1)
                .expectRow(0, "b");
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(
                        1, "x", "a",
                        2, "y", "b",
                        -1, "m", "n")
                .rows(Series.ofBool(true, false, true)).cols("b", "a")
                .drop();

        new DataFrameAsserts(df, "c")
                .expectHeight(1)
                .expectRow(0, "b");
    }
}
