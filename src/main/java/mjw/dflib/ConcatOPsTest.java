package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.JoinType;
import org.dflib.Printers;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Sep 2024, 6:55 PM
 */
public class ConcatOPsTest {
    @Test
    void testLeft() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4,
                5, 6);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                7, 8,
                9, 10);

        DataFrame dfv = df1.hConcat(JoinType.full, df2);
        System.out.println(Printers.tabular.toString(dfv));
    }

    @Test
    void join() {
        DataFrame left = DataFrame
                .foldByRow("id", "name")
                .of(1, "Jerry", 2, "Juliana", 3, "Joan");

        DataFrame right = DataFrame
                .foldByRow("id", "age")
                .of(2, 25, 3, 59, 4, 40);

        DataFrame joined = left
                .join(right)
                .on("id")
                .select();
        System.out.println(Printers.tabular.toString(joined));
    }
}
