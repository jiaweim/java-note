package mjw.beam.util;

import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 01 Dec 2023, 8:55 PM
 */
public class Tokenize extends PTransform<PCollection<String>, PCollection<String>> {

    public static Tokenize of() {
        return new Tokenize();
    }

    @Override
    public PCollection<String> expand(PCollection<String> input) {
        PCollection<String> result = input.apply(FlatMapElements.into(TypeDescriptors.strings()).via(Utils::toWords));
        if (input.hasSchema()) {
            result.setSchema(
                    input.getSchema(),
                    input.getTypeDescriptor(),
                    input.getToRowFunction(),
                    input.getFromRowFunction()
            );
        }
        return result;
    }
}
