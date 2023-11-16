package mjw.study.java.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 14 Nov 2023, 5:37 PM
 */
public class MatcherTest {

    @Test
    void testFind() {
        String text = "Hello Regex!";
        final Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println("Start index: " + matcher.start());
            System.out.println("End index: " + matcher.end());
            System.out.println(matcher.group());
            System.out.println();
        }
    }
}
