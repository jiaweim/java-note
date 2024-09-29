package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.RowPredicate;
import org.dflib.csv.CsvLoader;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 29 Sep 2024, 2:17 PM
 */
public class CsvRowFilterTest {
    private String csv() {
        return "A,B" + System.lineSeparator()
                + "1,7" + System.lineSeparator()
                + "2,8" + System.lineSeparator()
                + "3,9" + System.lineSeparator()
                + "4,10" + System.lineSeparator()
                + "5,11" + System.lineSeparator()
                + "6,12" + System.lineSeparator();
    }

    @Test
    public void pos() {

        DataFrame df = new CsvLoader()
                .intCol(0)
                .rows(RowPredicate.of(0, (Integer i) -> i % 2 == 0))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void name() {
        DataFrame df = new CsvLoader()
                .intCol(0)
                .rows(RowPredicate.of("A", (Integer i) -> i % 2 == 0))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, 2, "8")
                .expectRow(1, 4, "10")
                .expectRow(2, 6, "12");
    }

    @Test
    public void multipleConditions_LastWins() {

        DataFrame df = new CsvLoader()
                .intCol(0)
                .intCol(1)
                .rows(RowPredicate.of("B", (Integer i) -> i % 2 == 0))
                .rows(RowPredicate.of("B", (Integer i) -> i == 12))

                // this is the only one that will have effect
                .rows(RowPredicate.of("B", (Integer i) -> i > 10))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 5, 11)
                .expectRow(1, 6, 12);
    }

    @Test
    public void cantFilterOnExcludedColumns() {

        CsvLoader loader = new CsvLoader()
                .intCol("A")
                .intCol("B")

                // column "A" is not present in the result, so it should cause an exception on load
                .rows(RowPredicate.of("A", (Integer i) -> i % 2 == 0))
                .cols("B");

        assertThrows(IllegalArgumentException.class, () -> loader.load(new StringReader(csv())));
    }

    @Test
    public void selectColumns_Condition() {
        DataFrame df = new CsvLoader()
                .intCol("A")
                .intCol("B")
                .cols("B", "A")

                // using positional indices of the resulting DataFrame, not the CSV
                .rows(RowPredicate.of(1, (Integer i) -> i == 4))
                .load(new StringReader(csv()));

        new DataFrameAsserts(df, "B", "A")
                .expectHeight(1)
                .expectRow(0, 10, 4);
    }
}
