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
 * @since 09 Oct 2024, 10:19 AM
 */
public class ColumnSetFillTest {
    @Test
    public void fill() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fill("B", "C", "*");

        System.out.println(Printers.tabular.toString(df));
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "B", "C", "*")
                .expectRow(1, "B", "C", "*")
                .expectRow(2, "B", "C", "*");
    }


    @Test
    public void fill2() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fill("B", "C", "*");

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "B", "C", "*")
                .expectRow(1, null, "B", "C", "*")
                .expectRow(2, 3, "B", "C", "*");
    }

    @Test
    public void fillNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNulls("*");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "*")
                .expectRow(1, "*", "*", "a")
                .expectRow(2, 3, "*", "*");
    }

    @Test
    public void fillNullsBackwards() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNullsBackwards();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 3, null, "a")
                .expectRow(2, 3, null, null);
    }

    @Test
    public void fillNullsForward() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNullsForward();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", null)
                .expectRow(1, 1, "x", "a")
                .expectRow(2, 3, "x", "a");
    }

    @Test
    public void fillNullsFromSeries() {

        Series<String> filler = Series.of("row1", "row2", "row3");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols().fillNullsFromSeries(filler);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "x", "row1")
                .expectRow(1, "row2", "row2", "a")
                .expectRow(2, 3, "row3", "row3");
    }


    @Test
    public void targetfillNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNulls("*");

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "*", "*")
                .expectRow(1, null, "*", "a", "*")
                .expectRow(2, 3, "*", "*", "*");
    }

    @Test
    public void targetfillNullsBackwards() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNullsBackwards();

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, null, null, "a", null)
                .expectRow(2, 3, null, null, null);
    }

    @Test
    public void targetfillNullsForward() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNullsForward();

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, null, "x", "a", null)
                .expectRow(2, 3, "x", "a", null);
    }

    @Test
    public void targetfillNullsFromSeries() {

        Series<String> filler = Series.of("row1", "row2", "row3");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, "x", null,
                        null, null, "a",
                        3, null, null
                )
                .cols("b", "c", "new").fillNullsFromSeries(filler);

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "row1", "row1")
                .expectRow(1, null, "row2", "a", "row2")
                .expectRow(2, 3, "row3", "row3", "row3");
    }
}
