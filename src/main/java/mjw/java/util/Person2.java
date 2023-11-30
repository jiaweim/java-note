package mjw.java.util;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 27 Nov 2023, 9:07 AM
 */
public class Person2 implements Comparable<Person2> {

    private int id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private int salary;
    private double coefficient;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    private static final Comparator<Person2> COMPARATOR = Comparator.comparing(Person2::getLastName)
            .thenComparing(Person2::getFirstName);

    @Override
    public int compareTo(Person2 o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
