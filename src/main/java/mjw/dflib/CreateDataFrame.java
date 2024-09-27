package mjw.dflib;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Printers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

/**
 *
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Sep 2024, 6:43 PM
 */
public class CreateDataFrame {

    @Test
    void createRow() {
        DataFrame df = DataFrame.byArrayRow("name", "age")
                .appender()
                .append("Joe", 18)
                .append("Andrus", 49)
                .append("Joan", 32)
                .toDataFrame();
        System.out.println(Printers.tabular().toString(df));
    }

    @Test
    void fromList() {
        record Person(String name, int age) {
        }

        List<Person> people = List.of(
                new Person("Joe", 18),
                new Person("Andrus", 49),
                new Person("Joan", 32));

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$col(Person::name),
                        Extractor.$int(Person::age))
                .columnNames("name", "age")
                .appender()
                .append(people)
                .toDataFrame();
        System.out.println(Printers.tabular().toString(df));
    }

    @Test
    void fold() {
        DataFrame df = DataFrame
                .foldByRow("name", "age")
                .of("Joe", 18, "Andrus", 49, "Joan", 32);
    }

    public static void main(String[] args) {
        DataFrame df1 = DataFrame.foldByRow("a", "b", "c")
                .ofStream(IntStream.range(1, 10000));
        DataFrame df2 = df1.rows(r -> r.getInt(0) % 2 == 0).select();
        System.out.println(Printers.tabular.toString(df2));
    }
}
