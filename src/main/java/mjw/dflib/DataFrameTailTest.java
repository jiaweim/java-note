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
 * @since 11 Oct 2024, 11:06 AM
 */
public class DataFrameTailTest {

    @Test
    public void tail() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .tail(2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 2, "y")
                .expectRow(1, 3, "z");
    }

    @Test
    public void tail_Zero() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .tail(0);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void tail_OutOfBounds() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .tail(4);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");
    }

    @Test
    public void negative() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .tail(-2);
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, "x");
    }
}
