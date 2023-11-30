package mjw.java.util;

import java.util.*;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 27 Nov 2023, 9:15 AM
 */
public class Person2Generator {

    public static List<Person2> generatePersonList(int size) {
        List<Person2> ret = new ArrayList<>();

        String firstNames[] = {"Mary", "Patricia", "Linda",
                "Barbara", "Elizabeth", "James",
                "John", "Robert", "Michael",
                "William"};
        String lastNames[] = {"Smith", "Jones", "Taylor",
                "Williams", "Brown", "Davies",
                "Evans", "Wilson", "Thomas",
                "Roberts"};

        Random randomGenerator = new Random();
        for (int i = 0; i < size; i++) {
            Person2 person = new Person2();
            person.setId(i);
            person.setFirstName(firstNames[randomGenerator
                    .nextInt(10)]);
            person.setLastName(lastNames[randomGenerator
                    .nextInt(10)]);
            person.setSalary(randomGenerator.nextInt(100000));
            person.setCoefficient(randomGenerator.nextDouble() * 10);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -randomGenerator
                    .nextInt(30));
            Date birthDate = calendar.getTime();
            person.setBirthDate(birthDate);
            ret.add(person);
        }
        return ret;
    }

}
