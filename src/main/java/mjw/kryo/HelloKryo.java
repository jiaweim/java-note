package mjw.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 31 Jan 2024, 5:13 PM
 */
public class HelloKryo {
    public static void main(String[] args) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.register(SomeClass.class);

        SomeClass object = new SomeClass();
        object.value = "Hello Kryo!";

        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeObject(output, object);
        output.close();

        Input input = new Input(new FileInputStream("file.bin"));
        SomeClass object2 = kryo.readObject(input, SomeClass.class);
        input.close();

        System.out.println(object2.value);
    }

    public static class SomeClass {
        String value;
    }
}
