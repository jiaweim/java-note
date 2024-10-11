package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Oct 2024, 7:09 PM
 */
public class DataFrameNullifyTest {

    @Test
    public void nullify() {
        DataFrame cond = DataFrame.byColumn("a", "b").of(
                Series.ofBool(true, false),
                Series.ofBool(true, false));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y")
                .nullify(cond);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, null, null)
                .expectRow(1, 2, "y");
    }

    @Test
    public void nullifyNoMatch() {
        DataFrame cond = DataFrame.byColumn("a", "b").of(
                Series.ofBool(true, false),
                Series.ofBool(true, false));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y").nullifyNoMatch(cond);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, null, null);
    }

    @Test
    public void nullifyByColumn() {

        DataFrame cond = DataFrame.byColumn("c", "b").of(
                Series.ofBool(true, false),
                Series.ofBool(true, false));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y").nullify(cond);
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, null)
                .expectRow(1, 2, "y");
    }
}
