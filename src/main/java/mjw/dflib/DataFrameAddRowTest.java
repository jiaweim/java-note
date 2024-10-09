package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.LongSeries;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.dflib.series.ObjectSeries;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 6:55 PM
 */
public class DataFrameAddRowTest {

    @Test
    public void addRow() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df1 = df.addRow(Map.of("a", 3, "b", "z"));
        System.out.println(Printers.tabular.toString(df1));
        new DataFrameAsserts(df1, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z");

        DataFrame df2 = df1.addRow(Map.of("a", 55, "b", "A"));

        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "z")
                .expectRow(3, 55, "A");
    }

    @Test
    public void addRowMissingOrExtraValues() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df1 = df.addRow(Map.of("c", 3, "b", "z"));

        new DataFrameAsserts(df1, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, null, "z");
    }

    @Test
    public void addRow_PrimitiveColumns() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.ofLong(5L, 6L),
                        Series.ofInt(1, 2))
                .addRow(Map.of("a", 3L, "b", "str"));
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 5L, 1)
                .expectRow(1, 6L, 2)
                .expectRow(2, 3L, "str");

        assertInstanceOf(LongSeries.class, df.getColumn("a").unsafeCastAs(Long.class));
        assertInstanceOf(ObjectSeries.class, df.getColumn("b"));
    }
}
