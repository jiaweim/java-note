package mjw.study.parquet;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.io.IOException;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 07 Dec 2022, 17:05
 */
public class ReadTest
{
    public static void main(String[] args) throws IOException
    {
        Schema schema = new Schema.Parser()
                .parse(new File("D:\\repositories\\java-note\\src\\main\\java\\mjw\\study\\parquet\\user.avsc"));

        GenericRecord user1 = new GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);

        GenericRecord user2 = new GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "ref");

    }
}
