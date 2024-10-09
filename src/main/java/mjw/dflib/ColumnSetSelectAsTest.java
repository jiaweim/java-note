package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Printers;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Oct 2024, 1:37 PM
 */
public class ColumnSetSelectAsTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .selectAs("c", "d");
        new DataFrameAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void byName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c")
                .selectAs("X", "Y");

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void newColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .selectAs("X", "Y", "NEW");

        new DataFrameAsserts(df, "X", "Y", "NEW")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }

    @Test
    public void Mapall() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .selectAs(Map.of("a", "c", "b", "d"));

        new DataFrameAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void MapbyName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c")
                .selectAs(Map.of("a", "X", "c", "Y"));

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void MapnewColumnsAndPartialRename() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .selectAs(Map.of("a", "X", "new", "NEW"));

        new DataFrameAsserts(df, "X", "c", "NEW")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }


    @Test
    public void operatorall() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .selectAs(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[b]")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void operatorbyName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a",
                        2, "y", "b")
                .cols("a", "c")
                .selectAs(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[c]")
                .expectHeight(2)
                .expectRow(0, 1, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void operatornewColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a",
                        2, "y", "b")
                .cols("a", "c", "new")
                .selectAs(c -> "[" + c + "]");
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "[a]", "[c]", "[new]")
                .expectHeight(2)
                .expectRow(0, 1, "a", null)
                .expectRow(1, 2, "b", null);
    }
}
