package mjw.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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

    @BeforeAll
    public void init() throws FileNotFoundException {
        kryo = new Kryo();
        output = new Output(new FileOutputStream("file.dat"));
        input = new Input(new FileInputStream("file.dat"));
    }

    @Test
    public void writeAndRead() {
        Object someObj = "Some string";
        kryo.writeClassAndObject(output, someObj);
    }

}
