package mjw.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.util.Date;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 02 Feb 2024, 12:03 PM
 */
public class PersonSerializer extends Serializer<Person>
{
    @Override
    public void write(Kryo kryo, Output output, Person object) {
        output.writeString(object.getName());
        output.writeLong(object.getBirthDate().getTime());
    }

    @Override
    public Person read(Kryo kryo, Input input, Class<? extends Person> type) {
        Person person = new Person();
        person.setName(input.readString());
        long birthDate = input.readLong();
        person.setBirthDate(new Date(birthDate));
        person.setAge(calculateAge(birthDate));
        return person;
    }

    private int calculateAge(long birthDate) {
        // Some custom logic
        return 18;
    }
}
