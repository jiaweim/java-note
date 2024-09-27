package mjw.dflib;

import org.dflib.Extractor;
import org.dflib.IntSeries;
import org.dflib.Printers;
import org.dflib.Series;
import org.dflib.builder.SeriesAppender;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

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
    void testOfInt() {
        IntSeries is = Series.ofInt(0, 1, -300, Integer.MAX_VALUE);
    }

    @Test
    void testByElement() {
        SeriesAppender<String, String> appender = Series
                .byElement(Extractor.<String>$col())
                .appender();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            appender.append(scanner.next());
        }
        Series<String> s = appender.toSeries();
    }

}
