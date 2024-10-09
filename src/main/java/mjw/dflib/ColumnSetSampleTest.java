package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 6:35 PM
 */
public class ColumnSetSampleTest {
    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d").of(
                        1, "x", "m", "z",
                        2, "y", "x", "E")
                // using fixed seed to get reproducible result
                .colsSample(2, new Random(11)).select();

        System.out.println(Printers.tabular.toString(df));
        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2);
    }
}
