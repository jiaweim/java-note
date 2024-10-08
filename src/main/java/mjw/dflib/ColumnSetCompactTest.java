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
 * @since 08 Oct 2024, 6:34 PM
 */
public class ColumnSetCompactTest {

    @Test
    public void all_compactBool() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofBool(true, false),
                        Series.of(null, "true"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactBool();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectBooleanColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, true, false, true)
                .expectRow(1, false, true, false);
    }

    @Test
    public void all_compactBoolConverter() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of(5, 6),
                        Series.ofInt(8, 9)
                )
                .cols()
                .compactBool((Integer o) -> o % 2 == 0);

        new DataFrameAsserts(df, "a", "b")
                .expectBooleanColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, false, true)
                .expectRow(1, true, false);
    }

    @Test
    public void compactBool() {
        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofBool(true, false),
                        Series.of(null, "true"),
                        Series.of(Boolean.TRUE, Boolean.FALSE),
                        Series.of("one", "two")
                )
                .cols("a", "b", "c")
                .compactBool();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectBooleanColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, true, false, true, "one")
                .expectRow(1, false, true, false, "two");
    }

    @Test
    public void compactBoolConverter() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of(5, 6),
                        Series.of("one", "two"),
                        Series.ofInt(8, 9)
                )
                .cols("a", "c")
                .compactBool((Integer o) -> o % 2 == 0);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectBooleanColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, false, "one", true)
                .expectRow(1, true, "two", false);
    }


    @Test
    public void all_compactLong() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofDouble(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactDouble(-1);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectDoubleColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1., -1., 1.)
                .expectRow(1, 2., 5., 0.);
    }

    @Test
    public void all_compactDoubleConverter() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of("a", "ab"),
                        Series.of("abc", "abcd")
                )
                .cols()
                .compactDouble((String o) -> o.length() + 0.1);

        new DataFrameAsserts(df, "a", "b")
                .expectDoubleColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, 1.1, 3.1)
                .expectRow(1, 2.1, 4.1);
    }

    @Test
    public void compactDouble() {
        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofDouble(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE),
                        Series.of("one", "two")
                )
                .cols("a", "b", "c")
                .compactDouble(-1);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectDoubleColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1., -1., 1., "one")
                .expectRow(1, 2., 5., 0., "two");
    }

    @Test
    public void compactDoubleConverter() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of("a", "ab"),
                        Series.of("one", "two"),
                        Series.of("abc", "abcd")
                )
                .cols("a", "c")
                .compactDouble((String o) -> o.length() + 0.1);
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectDoubleColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, 1.1, "one", 3.1)
                .expectRow(1, 2.1, "two", 4.1);
    }
}
