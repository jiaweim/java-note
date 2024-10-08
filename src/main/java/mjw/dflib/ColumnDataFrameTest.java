package mjw.dflib;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Oct 2024, 12:52 PM
 */
public class ColumnDataFrameTest {
    @Test
    public void constructor() {
        ColumnDataFrame df = new ColumnDataFrame(
                "n1",
                Index.of("a", "b"),
                Series.ofInt(1, 2),
                Series.ofInt(3, 4));

        new DataFrameAsserts(df, "a", "b").expectHeight(2);
        assertEquals("n1", df.getName());
    }

    @Test
    public void constructor_NoData() {
        ColumnDataFrame df = new ColumnDataFrame(null, Index.of("a", "b"));
        new DataFrameAsserts(df, "a", "b").expectHeight(0);
    }

    @Test
    public void as() {
        DataFrame df = new ColumnDataFrame(
                "n1",
                Index.of("a", "b"),
                Series.ofInt(1, 2),
                Series.ofInt(3, 4)).as("n2");

        new DataFrameAsserts(df, "a", "b").expectHeight(2);
        assertEquals("n2", df.getName());
    }
}
