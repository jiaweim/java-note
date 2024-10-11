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
 * @since 10 Oct 2024, 4:44 PM
 */
public class DataFrameHeadTest {

    @Test
    public void withinBounds() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .head(2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void zero() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .head(0);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(0);
    }

    @Test
    public void outOfBounds() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z")
                .head(4);

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
                .head(-2);
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 3, "z");
    }
}
