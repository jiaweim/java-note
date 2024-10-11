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
 * @since 10 Oct 2024, 7:04 PM
 */
public class DataFrameMapTest {
    @Test
    public void map_UnaryOp() {
        DataFrame df = DataFrame
                .foldByRow("a", "b").of(1, "x", 2, "y")
                .map(f -> f.cols("b").drop());

        System.out.println(Printers.tabular.toString(df));
        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }
}
