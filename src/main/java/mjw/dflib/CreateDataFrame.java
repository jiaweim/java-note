package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;

import java.util.stream.IntStream;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Sep 2024, 6:43 PM
 */
public class CreateDataFrame {
    public static void main(String[] args) {
        DataFrame df1 = DataFrame.foldByRow("a", "b", "c")
                .ofStream(IntStream.range(1, 10000));
        DataFrame df2 = df1.rows(r -> r.getInt(0) % 2 == 0).select();
        System.out.println(Printers.tabular.toString(df2));
    }
}
