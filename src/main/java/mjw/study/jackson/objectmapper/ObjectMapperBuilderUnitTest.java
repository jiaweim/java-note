package mjw.study.jackson.objectmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectMapperBuilderUnitTest {

    ObjectMapper mapper = new ObjectMapperBuilder()
            .enableIndentation()
            .dateFormat()
            .preserveOrder(true)
            .build();

    Car givenCar = new Car("White", "Sedan");
    String givenCarJsonStr = "{ \"color\" : \"White\", \"type\" : \"Sedan\" }";

    @Test
    public void whenReadCarJsonStr_thenReturnCarObjectCorrectly() throws JsonProcessingException {
        Car actual = mapper.readValue(givenCarJsonStr, Car.class);

        assertThat(actual.getColor(), is("White"));
        assertThat(actual.getType(), is("Sedan"));
    }

    @Test
    public void testSerializeRequest() throws JsonProcessingException {
        Request request = new Request();
        request.setCar(givenCar);
        Date date = new Date(1684909857000L);
        request.setDatePurchased(date);

        String actual = mapper.writeValueAsString(request);
        String expected = "{\n" + "  \"car\" : {\n" + "    \"color\" : \"White\",\n" +
                "    \"type\" : \"Sedan\"\n" + "  },\n" + "  \"datePurchased\" : \"2023-05-24 12:00 PM IST\"\n" +
                "}";
        assertEquals(expected, actual);
    }
}
