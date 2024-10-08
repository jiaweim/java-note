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
 * @since 08 Oct 2024, 5:42 PM
 */
public class ColumnSetAsTest {
    @Test
    public void ascols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .as("c", "d");

        new DataFrameAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void asbyName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c")
                .as("X", "Y");


        new DataFrameAsserts(df, "X", "b", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void asnewColumns() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .as("X", "Y", "NEW");

        new DataFrameAsserts(df, "X", "b", "Y", "NEW")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, 2, "y", "b", null);
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .as(Map.of("a", "c", "b", "d"));

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
                .as(Map.of("a", "X", "c", "Y"));


        new DataFrameAsserts(df, "X", "b", "Y")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void newColumnsAndPartialRename() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .as(Map.of("a", "X", "new", "NEW"));


        new DataFrameAsserts(df, "X", "b", "c", "NEW")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, 2, "y", "b", null);
    }

    @Test
    public void opcols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols()
                .as(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "[b]")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void opbyName() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c")
                .as(c -> "[" + c + "]");

        new DataFrameAsserts(df, "[a]", "b", "[c]")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a")
                .expectRow(1, 2, "y", "b");
    }

    @Test
    public void opnewColumn() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c")
                .of(1, "x", "a", 2, "y", "b")
                .cols("a", "c", "new")
                .as(c -> "[" + c + "]");
        System.out.println(Printers.tabular.toString(df));

        new DataFrameAsserts(df, "[a]", "b", "[c]", "[new]")
                .expectHeight(2)
                .expectRow(0, 1, "x", "a", null)
                .expectRow(1, 2, "y", "b", null);
    }
}
