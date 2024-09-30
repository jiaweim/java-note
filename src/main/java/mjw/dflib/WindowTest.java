package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 30 Sep 2024, 10:39 AM
 */
public class WindowTest {
    @Test
    public void partitioned() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                2, "y",
                1, "x");

        System.out.println(Printers.tabular.toString(df));

        Series<String> sa = df.over().partitioned("a").shift("b", 1);
        new SeriesAsserts(sa).expectData(null, null, "x", null, "y", "z");

        Series<String> sb = df.over().partitioned("b").shift("b", -1);
        new SeriesAsserts(sb).expectData("x", "y", null, null, null, null);
    }
}
