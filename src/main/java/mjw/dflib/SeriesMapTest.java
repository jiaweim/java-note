package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 4:27 PM
 */
public class SeriesMapTest {
    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void map_Value(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").map(String::toUpperCase);
        new SeriesAsserts(s).expectData("A", "B", "C");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void map_DataFrame(SeriesType type) {
        DataFrame df = type.createSeries("a", "b", "c")
                .map(Index.of("upper", "is_c"), (v, r) -> r
                        .set(0, v.toUpperCase())
                        .set(1, v.equals("c")));

        System.out.println(Printers.tabular.toString(df));
        new DataFrameAsserts(df, "upper", "is_c")
                .expectHeight(3)
                .expectRow(0, "A", false)
                .expectRow(1, "B", false)
                .expectRow(2, "C", true);
    }
}
