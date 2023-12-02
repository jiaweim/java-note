package mjw.beam;

import mjw.beam.util.PrintElements;
import mjw.beam.util.Tokenize;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 01 Dec 2023, 8:35 PM
 */
public class FirstPipeline {

    public static void main(String[] args) throws IOException {
        ClassLoader classLoader = FirstPipeline.class.getClassLoader();
        String file = classLoader.getResource("lorem.txt").getFile();
        List<String> lines = Files.readAllLines(Path.of(file));

        Pipeline pipeline = Pipeline.create();
        PCollection<String> input = pipeline.apply(Create.of(lines));
        PCollection<String> words = input.apply(Tokenize.of());

        PCollection<KV<String, Long>> result = words.apply(Count.perElement());
        result.apply(PrintElements.of());

        pipeline.run().waitUntilFinish();
    }

}
