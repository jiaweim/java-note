package mjw.fastcsv;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.writer.CsvWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_16LE;

/**
 * Example for reading CSV files with a BOM header.
 */
public class ExampleCsvReaderWithBomHeader {

    public static void main(final String[] args) throws IOException {
        final Path testFile = prepareTestFile();

        final CsvReader.CsvReaderBuilder builder = CsvReader.builder()
                .detectBomHeader(true);

        try (Stream<CsvRecord> csv = builder.ofCsvRecord(testFile).stream()) {
            csv.forEach(System.out::println);
        }
    }

    // 创建一个 CSV 文件，用 BOM header 指定编码为 UTF-16 little-endian
    static Path prepareTestFile() throws IOException {
        final Path tmpFile = Files.createTempFile("fastcsv", ".csv");
        tmpFile.toFile().deleteOnExit();

        try (var out = Files.newOutputStream(tmpFile);
             var csv = CsvWriter.builder()
                     .build(new OutputStreamWriter(out, UTF_16LE))) {

            // 手动输出 UTF-16LE BOM header
            out.write(new byte[]{(byte) 0xff, (byte) 0xfe});

            csv.writeRecord("a", "o", "u");
            csv.writeRecord("ä", "ö", "ü");
        }

        return tmpFile;
    }

}