package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 5:27 PM
 */
public class SeriesValueCountTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void valueCounts(SeriesType type) {
        DataFrame counts = type.createSeries("a", "b", "a", "a", "c").valueCounts();

        new DataFrameAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, "a", 3)
                .expectRow(1, "b", 1)
                .expectRow(2, "c", 1);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void valueCounts_Nulls(SeriesType type) {
        DataFrame counts = type.createSeries("a", "b", "a", "a", null, "c").valueCounts();
        System.out.println(Printers.tabular.toString(counts));

        new DataFrameAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, "a", 3)
                .expectRow(1, "b", 1)
                .expectRow(2, "c", 1);

    }
}
