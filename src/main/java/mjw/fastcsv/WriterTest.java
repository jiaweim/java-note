package mjw.fastcsv;

import de.siegmar.fastcsv.writer.CsvWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 24 Nov 2025, 1:08 PM
 */
public class WriterTest {

    @Test
    void write(){
        try(CsvWriter csv = CsvWriter.builder().build(Paths.get("output.csv"))){
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
