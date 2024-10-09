package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 7:10 PM
 */
public class DataFrameByColumnTest {
    @Test
    public void array() {

        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "c"),
                        Series.ofInt(1, 2, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)

                .expectIntColumns("b")

                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void iterable() {

        DataFrame df = DataFrame
                .byColumn("a", "b")
                .ofIterable(List.of(Series.of("a", "b", "c"),
                        Series.ofInt(1, 2, 3)));
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)

                .expectIntColumns("b")

                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }
}
