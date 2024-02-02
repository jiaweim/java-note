package mjw.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <a href="https://www.baeldung.com/kryo">Kryo tutorial in Baeldung</a>
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 01 Feb 2024, 7:47 PM
 */
public class KryoTest
{
    private Kryo kryo;
    private Output output;
    private Input input;

    @BeforeEach
    public void init() throws FileNotFoundException {
        kryo = new Kryo();
        output = new Output(new FileOutputStream("file.dat"));
        input = new Input(new FileInputStream("file.dat"));
    }

    @Test
    public void writeAndRead() {
        Object someObj = "Some string";
        kryo.writeClassAndObject(output, someObj);
        output.close();

        Object theObj = kryo.readClassAndObject(input);
        input.close();
        assertEquals(theObj, someObj);
    }

    @Test
    void writeAndReadMultiple() {
        String someString = "Multiple objects";
        Date someDate = new Date(915170400000L);

        kryo.register(Date.class);

        kryo.writeObject(output, someString);
        kryo.writeObject(output, someDate);
        output.close();

        String readString = kryo.readObject(input, String.class);
        Date readDate = kryo.readObject(input, Date.class);
        input.close();

        assertEquals(readString, someString);
        assertEquals(readDate, someDate);
    }

    @Test
    void serializePerson() {
        kryo.register(Person.class);
        kryo.register(Date.class);

        Person person = new Person();
        kryo.writeObject(output, person);
        output.close();

        Person readPerson = kryo.readObject(input, Person.class);
        input.close();
        assertEquals(readPerson.getName(), "John Doe");
    }

    @Test
    void customSerializer() {
        Person person = new Person();
        person.setAge(0);

        kryo.register(Person.class, new PersonSerializer());
        kryo.writeObject(output, person);
        output.close();

        Person readPerson = kryo.readObject(input, Person.class);
        input.close();
        assertEquals(readPerson.getName(), "John Doe");
        assertEquals(readPerson.getAge(), 18);
    }

    @Test
    void customSerializerAnnotation() {
        Person person = new Person();
        person.setAge(0);

        kryo.writeObject(output, person);
        output.close();

        Person readPerson = kryo.readObject(input, Person.class);
        input.close();
        assertEquals(readPerson.getName(), "John Doe");
        assertEquals(readPerson.getAge(), 18);
    }


}
