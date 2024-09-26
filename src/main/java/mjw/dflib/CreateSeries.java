package mjw.dflib;

import org.dflib.IntSeries;
import org.dflib.Printers;
import org.dflib.Series;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Sep 2024, 6:57 PM
 */
public class CreateSeries {

    @Test
    void testOf() {
        Series<String> s = Series.of("a", "bcd", "ef", "g");
        System.out.println(Printers.tabular.toString(s));
    }

    @Test
    void testOfInt(){
        IntSeries is = Series.ofInt(0, 1, -300, Integer.MAX_VALUE);
    }
}
