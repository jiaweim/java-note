package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Oct 2024, 10:57 AM
 */
public class DataFrameStackTest {

    @Test
    public void stack() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                null, null,
                2, "y").stack();

        new DataFrameAsserts(df, "row", "column", "value")
                .expectHeight(4)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 2, "a", 2)
                .expectRow(2, 0, "b", "x")
                .expectRow(3, 2, "b", "y");
    }

    @Test
    public void stack_NoNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                5, "z",
                2, "y").stack();

        new DataFrameAsserts(df, "row", "column", "value")
                .expectHeight(6)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 1, "a", 5)
                .expectRow(2, 2, "a", 2)
                .expectRow(3, 0, "b", "x")
                .expectRow(4, 1, "b", "z")
                .expectRow(5, 2, "b", "y");
    }

    @Test
    public void stackIncludeNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                null, null,
                2, "y").stackIncludeNulls();
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "row", "column", "value")
                .expectHeight(6)
                .expectRow(0, 0, "a", 1)
                .expectRow(1, 1, "a", null)
                .expectRow(2, 2, "a", 2)
                .expectRow(3, 0, "b", "x")
                .expectRow(4, 1, "b", null)
                .expectRow(5, 2, "b", "y");
    }
}
