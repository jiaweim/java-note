package mjw.java.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 22 Nov 2023, 7:20 PM
 */
public class StreamTest {

    @Test
    void filter() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("demo");
        list.add("morning");
        list.add("one");
        list.add("died");

        list.stream().filter(s -> s.startsWith("d")).forEach(System.out::println);
    }

    @Test
    void map() {
        List<String> words = List.of("Aa", "BB", "cc");
        words.stream().map(String::toLowerCase).forEach(System.out::println);
    }

    @Test
    void skip() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        list.stream().skip(3).forEach(System.out::println);
    }

    @Test
    void takeWhile() {
        Stream.of('A', 'B', 'c', 'D')
                .takeWhile(Character::isUpperCase)
                .forEach(System.out::println);
    }

    @Test
    void concat() {
        Stream.concat(Stream.of(1, 2, 3), Stream.of(5, 6))
                .forEach(System.out::println);
    }

    @Test
    void unique() {
        Stream.of("merrily", "merrily", "merrily", "gently")
                .distinct()
                .forEach(System.out::println);
    }

    @Test
    void sort() {
        Stream.of(1, 2, 4, 3, 5)
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void sort2() {
        Stream.of("a", "aa", "aaa", "ccc", "bbb")
                .sorted(Comparator.comparing(String::length).reversed())
                .forEach(System.out::println);
    }

    @Test
    void peek() {
        List<String> list = Stream.of("one", "two", "three", "four")
                .filter(e -> e.length() > 3)
                .peek(e -> System.out.println("Filtered value: " + e))
                .map(String::toUpperCase)
                .peek(e -> System.out.println("Mapped value: " + e))
                .toList();
        System.out.println(list);
    }

    @Test
    void count() {
        long count = Stream.of("one", "two", "three", "four").count();
        System.out.println(count);
    }

    @Test
    void max() {
        Optional<Integer> min = Stream.of(1, 2, 3, 10, 3)
                .min(Comparator.naturalOrder());
        System.out.println("min=" + min.orElse(-1));

        Optional<Integer> max = Stream.of(1, 2, 3, 10, 3)
                .max(Comparator.naturalOrder());
        System.out.println("max=" + max.orElse(100));
    }

    @Test
    void findFirst() {
        Optional<String> first = Stream.of("a", "b", "c", "Qb", "Qc", "d")
                .filter(s -> s.startsWith("Q"))
                .findFirst();
        System.out.println("first Q=" + first.orElse(""));
    }

    @Test
    void findAny() {
        Optional<String> first = Stream.of("a", "b", "c", "Qb", "Qc", "d")
                .parallel()
                .filter(s -> s.startsWith("Q"))
                .findAny();
        System.out.println("first Q=" + first.orElse(""));
    }

    @Test
    void anyMatch() {
        boolean anyMatch = Stream.of("a", "b", "c", "Qb", "Qc", "d")
                .parallel()
                .anyMatch(s -> s.startsWith("Q"));

    }

    @Test
    void iterator() {
        Iterator<String> it = Stream.of("a", "B", "c")
                .map(String::toUpperCase)
                .iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    @Test
    void forEach() {
        Stream.of("a", "B", "c")
                .forEach(System.out::println);
    }

    @Test
    void toListTest() {
        List<String> list = Stream.of("a", "b", "c").toList();
    }

    @Test
    void collect() {
        List<String> list = Stream.of("a", "b", "c", "a").collect(toList());
        System.out.println(list);
    }

    @Test
    void toSetTest() {
        Set<String> collect = Stream.of("a", "b", "c", "a").collect(toSet());
        System.out.println(collect);

    }

    @Test
    void summarizing() {
        IntSummaryStatistics summary = Stream.of("a", "b", "bb", "c", "cc", "ccc")
                .collect(summarizingInt(String::length));
        System.out.println(summary.getCount());
        System.out.println(summary.getAverage());
        System.out.println(summary.getMin());
        System.out.println(summary.getMax());
        System.out.println(summary.getSum());
    }

    class Book {

        private String name;
        private int year;
        private String isbn;

        public Book(String name, int year, String isbn) {
            this.name = name;
            this.year = year;
            this.isbn = isbn;
        }

        public String getName() {
            return name;
        }

        public int getYear() {
            return year;
        }

        public String getIsbn() {
            return isbn;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "name='" + name + '\'' +
                    ", year=" + year +
                    ", isbn='" + isbn + '\'' +
                    '}';
        }
    }

    @Test
    void toMapDemo() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("The Fellowship of the Ring", 1954, "0395489318"));
        bookList.add(new Book("The Two Towers", 1954, "0345339711"));
        bookList.add(new Book("The Return of the King", 1955, "0618129111"));

        Map<Integer, Book> map = bookList.stream()
                .collect(toMap(Book::getYear,
                        Function.identity(),
                        (exiting, newValue) -> exiting));
        System.out.println(map);

        bookList.stream().collect(toMap(
                Book::getYear,
                Collections::singleton,
                (a, b) -> {
                    Set<Book> union = new HashSet<>(a);
                    union.addAll(b);
                    return union;
                }
        ));
    }

    record Person(int id, String name, double salary, Department department) {

    }

    record Department(int id, String name) {

    }

    @Test
    void data() {
        List<Person> persons = List.of(
                new Person(1, "Alex", 100, new Department(1, "HR")),
                new Person(2, "Brian", 200, new Department(1, "HR")),
                new Person(3, "Charles", 900, new Department(2, "Finance")),
                new Person(4, "David", 200, new Department(2, "Finance")),
                new Person(5, "Edward", 200, new Department(2, "Finance")),
                new Person(6, "Frank", 800, new Department(3, "ADMIN")),
                new Person(7, "George", 900, new Department(3, "ADMIN")));
    }


    @Test
    void toMap2() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("The Fellowship of the Ring", 1954, "0395489318"));
        bookList.add(new Book("The Two Towers", 1954, "0345339711"));
        bookList.add(new Book("The Return of the King", 1955, "0618129111"));

        Map<String, String> map = bookList.stream()
                .collect(toMap(Book::getIsbn, Book::getName));
    }


    @Test
    void test() throws URISyntaxException, IOException {
        URL resource = StreamTest.class.getResource("alice30.txt");
        String contents = Files.readString(Path.of(resource.toURI()));
        List<String> words = List.of(contents.split("\\PL+"));// \\PL+ 匹配所有非字符

        long count = 0;
        for (String w : words) {
            if (w.length() > 12) count++;
        }
        System.out.println(count);

        count = words.stream().filter(w -> w.length() > 12).count();
        System.out.println(count);

        count = words.parallelStream().filter(w -> w.length() > 12).count();
        System.out.println(count);
    }
}

