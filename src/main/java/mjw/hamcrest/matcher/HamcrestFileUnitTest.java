package mjw.hamcrest.matcher;


import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class HamcrestFileUnitTest {

    @Test
    public final void testFileName() {
        File file = new File("src/test/resources/test1.in");

        assertThat(file, aFileNamed(equalToIgnoringCase("test1.in")));
    }

    @Test
    public final void whenVerifyingFileOrDirExist_thenCorrect() {
        File file = new File("src/test/resources/test1.in");
        File dir = new File("src/test/resources");

        assertThat(file, anExistingFile());
        assertThat(dir, anExistingDirectory());
        assertThat(file, anExistingFileOrDirectory());
        assertThat(dir, anExistingFileOrDirectory());
    }

    @Test
    public final void testFileIsReadableAndWritable() {
        File file = new File("src/test/resources/test1.in");

        assertThat(file, aReadableFile());
        assertThat(file, aWritableFile());
    }

    @Test
    public final void testFileSize() {
        File file = new File("src/test/resources/test1.in");

        assertThat(file, aFileWithSize(11));
        assertThat(file, aFileWithSize(greaterThan(1L)));
    }

    @Test
    public final void whenVerifyingFilePath_thenCorrect() {
        File file = new File("src/test/resources/test1.in");

//        assertThat(file, aFileWithCanonicalPath(containsString("src/test/resources")));
//        assertThat(file, aFileWithAbsolutePath(containsString("src\test\resources")));
    }
}
