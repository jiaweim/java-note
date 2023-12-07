package mjw.java.text;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 07 Dec 2023, 3:23 PM
 */
public record Range(int from, int to) {

    public Range {
        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }
    }
}
