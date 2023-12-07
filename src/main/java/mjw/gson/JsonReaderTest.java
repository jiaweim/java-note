package mjw.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 05 Dec 2023, 4:50 PM
 */
public class JsonReaderTest {

    @Test
    void demo() throws IOException {
//        JsonReader reader = new JsonReader(Files.newBufferedReader(Path.of("D:\\config.json")));


        String json = "{'id': 1001,'firstName': 'Lokesh','lastName': 'Gupta','email': null}";

        JsonReader jsonReader = new JsonReader(new StringReader(json));
        jsonReader.setLenient(true);
        while (jsonReader.hasNext()) {
            JsonToken nextToken = jsonReader.peek();

            if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {

                jsonReader.beginObject();

            } else if (JsonToken.NAME.equals(nextToken)) {

                String name = jsonReader.nextName();
                System.out.println("Token KEY >>>> " + name);

            } else if (JsonToken.STRING.equals(nextToken)) {

                String value = jsonReader.nextString();
                System.out.println("Token Value >>>> " + value);

            } else if (JsonToken.NUMBER.equals(nextToken)) {

                long value = jsonReader.nextLong();
                System.out.println("Token Value >>>> " + value);

            } else if (JsonToken.NULL.equals(nextToken)) {

                jsonReader.nextNull();
                System.out.println("Token Value >>>> null");

            } else if (JsonToken.END_OBJECT.equals(nextToken)) {

                jsonReader.endObject();

            }
        }
    }
}