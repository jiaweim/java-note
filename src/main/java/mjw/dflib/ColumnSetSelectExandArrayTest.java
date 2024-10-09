package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.$val;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 6:39 PM
 */
public class ColumnSetSelectExandArrayTest {

    @Test
    public void array() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("c", "b")
                .selectExpandArray($val(new String[]{"one", "two"}));

        new DataFrameAsserts(df, "c", "b")
                .expectHeight(2)
                .expectRow(0, "one", "two")
                .expectRow(1, "one", "two");
    }

    @Test
    public void array_VaryingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c")
                .selectExpandArray($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 2:
                            return new String[]{"one", "two"};
                        case 3:
                            return new String[]{"one", "two", "three"};
                        default:
                            return null;
                    }
                }));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(3)
                .expectRow(0, "one", null)
                .expectRow(1, "one", "two")
                .expectRow(2, "one", "two");
    }

    @Test
    public void array_WithNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c")
                .selectExpandArray($int("a").mapVal(i -> {

                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 3:
                            return new String[]{"one", "two", "three"};
                        default:
                            return null;
                    }
                }));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(3)
                .expectRow(0, "one", null)
                .expectRow(1, null, null)
                .expectRow(2, "one", "two");
    }

    @Test
    public void array_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols()
                .selectExpandArray($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 2:
                            return new String[]{"one", "two"};
                        case 3:
                            return new String[]{"one", "two", "three"};
                        default:
                            return null;
                    }
                }));
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "0", "1", "2")
                .expectHeight(3)
                .expectRow(0, "one", null, null)
                .expectRow(1, "one", "two", null)
                .expectRow(2, "one", "two", "three");
    }
}
