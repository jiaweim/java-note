package mjw.java.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 27 Nov 2023, 9:20 AM
 */
public class Main {

    public static void main(String[] args) {
        List<Person2> persons = Person2Generator.generatePersonList(10000);
        Stream<Person2> person2Stream = persons.parallelStream();
        System.out.println(person2Stream.count());
    }

    @Test
    void createWithGenerator() {
        Supplier<String> supplier = new MySupplier();
        Stream<String> generate = Stream.generate(supplier);
        generate.parallel().limit(10).forEach(System.out::println);
    }

    @Test
    void createAndCount() {
        List<Character> list = Arrays.asList('a', 'b', 'c');
        long count = list.stream().count();
        System.out.println(count);
    }

    @Test
    void FilesList() throws IOException {
        Stream<Path> dirContent = Files.list(Paths.get(System.getProperty("user.home")));
        long count = dirContent.parallel().count();
        System.out.println(count);
        dirContent.close();
    }

    @Test
    void randomNumbersWithDoubleStream() {
        Random random = new Random();
        DoubleStream doubleStream = random.doubles(10);
        double average = doubleStream
                .parallel()
                .peek(System.out::println)
                .average().getAsDouble();
    }

    @Test
    void concat() {
        Stream<String> s1 = Stream.of("1", "2", "3", "4");
        Stream<String> s2 = Stream.of("5", "6", "7", "8");

        Stream<String> s = Stream.concat(s1, s2);
        s.parallel().forEach(s3 -> System.out.printf("%s : ", s3));
    }

    @Test
    void pointStream() {
        List<Double> numbers = DoubleGenerator.generateDoubleList(10000, 1000);
        DoubleStream doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        long count = doubleStream.parallel().count();
        System.out.printf("The list of numbers has %d elements.\n", count);

        doubleStream = DoubleGenerator.generateStreamFromList(numbers);
        double sum = doubleStream.parallel().sum();
        System.out.printf("Its numbers sum %f.\n", sum);
    }

    @Test
    void reduceSum() {
        List<Point> points = PointGenerator.generatePointList(10000);
        Optional<Point> point = points.parallelStream().reduce((p1, p2) -> {
            Point p = new Point();
            p.setX(p1.getX() + p2.getX());
            p.setY(p1.getY() + p2.getY());
            return p;
        });
    }

    @Test
    void reduce2() {
        List<Person2> person2s = Person2Generator.generatePersonList(10000);
        Integer salary = person2s
                .parallelStream()
                .map(Person2::getSalary)
                .reduce(0, Integer::sum);

        Integer value = 0;
        Integer reduce = person2s.parallelStream()
                .reduce(value,
                        (n, p) -> {
                            if (p.getSalary() > 5000) {
                                return n + 1;
                            } else {
                                return n;
                            }
                        }, (n1, n2) -> n1 + n2);

    }

    @Test
    void collect1() {
        List<Person2> person2s = Person2Generator.generatePersonList(100);
        ConcurrentMap<String, List<Person2>> personsByName = person2s.parallelStream()
                .collect(Collectors.groupingByConcurrent(Person2::getFirstName));
        personsByName.keySet().forEach(s -> {
            List<Person2> listOfPersons = personsByName.get(s);
            System.out.printf("%s: There are %d persons with that  name\n", s, listOfPersons.size());
        });
    }

    @Test
    void collectJoin() {
        List<Person2> persons = Person2Generator.generatePersonList(100);
        String msg = persons.parallelStream()
                .map(Person2::toString)
                .collect(Collectors.joining(","));
        System.out.println(msg);

    }

    @Test
    void partitioningBy1() {
        List<Person2> persons = Person2Generator.generatePersonList(100);
        Map<Boolean, List<Person2>> personsBySalary = persons.parallelStream()
                .collect(Collectors.partitioningBy(p -> p.getSalary() > 50000));

    }

    @Test
    void toConcurrentMap1() {
        List<Person2> persons = Person2Generator.generatePersonList(100);
        ConcurrentMap<String, String> nameMap = persons.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        Person2::getFirstName,
                        Person2::getLastName,
                        (s1, s2) -> s1 + ", " + s2));
        nameMap.forEach((s, s2) -> System.out.printf("%s: %s \n", s, s2));
    }

    @Test
    void toList1() {
        List<Person2> persons = Person2Generator.generatePersonList(100);
        List<Person2> highSalaryPeople = persons
                .parallelStream().collect(
                        ArrayList::new,
                        (list, person) -> {
                            if (person.getSalary() > 50000) {
                                list.add(person);
                            }
                        },
                        ArrayList::addAll
                );
        System.out.printf("High Salary People: %d\n", highSalaryPeople.size());

        Stream<String> stringStream = Stream.of("a", "b");

        List<String> asList = stringStream.collect(ArrayList::new,
                ArrayList::add,
                ArrayList::addAll);
    }

    public static void imperative() {
        int[] iArr = {1, 3, 4, 5, 6, 9, 8, 7, 4, 2};
        for (int i = 0; i < iArr.length; i++) {
            System.out.println(iArr[i]);
        }
        Arrays.stream(iArr).forEach(System.out::println);
    }

    @Test
    void smallCode() {
        int[] arr = {1, 3, 4, 5, 6, 7, 8, 9, 10};

        Arrays.stream(arr)
                .map(x -> x % 2 == 0 ? x : x + 1)
                .forEach(System.out::println);

    }


    @Test
    void methodReferenceSameName() {
        List<Double> numbers = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            numbers.add((double) i);

        numbers.stream().map(Object::toString).forEach(System.out::println);
    }

    @Test
    void andThenConsumer() {
        int[] arr = {1, 3, 4, 5, 6, 7, 8, 9, 10};

        Arrays.stream(arr)
                .forEach(((IntConsumer) System.out::println)
                        .andThen(System.err::println));

    }

}
