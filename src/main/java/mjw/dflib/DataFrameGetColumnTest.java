package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.junit5.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Oct 2024, 3:59 PM
 */
public class DataFrameGetColumnTest {

    @Test
    public void byLabel() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn("b");

        new SeriesAsserts(cb).expectData("x", "y");
    }

    @Test
    public void byLabelException() {
        DataFrame df = DataFrame.foldByRow("a").of(1, 2);
        assertThrows(IllegalArgumentException.class, () -> df.getColumn("x"));
    }

    @Test
    public void byPosition() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        Series<String> cb = df.getColumn(0);

        new SeriesAsserts(cb).expectData(1, 2);
    }

    @Test
    public void byPositionException() {
        DataFrame df = DataFrame.foldByRow("a").of(1, 2);
        assertThrows(IllegalArgumentException.class, () -> df.getColumn(1));
        assertThrows(IllegalArgumentException.class, () -> df.getColumn(-1));
    }
}
