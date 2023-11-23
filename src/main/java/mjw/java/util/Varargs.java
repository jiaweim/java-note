package mjw.java.util;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 03 8月 2023, 10:08
 */
public class Varargs {

    static boolean testNull(String... string) {
        if (string == null) {
            return true;
        } else {
            System.out.println(string.length);
            System.out.println(string.getClass());
            return false;
        }
    }

    static boolean callTestNull(String s) {
        return testNull(s);
    }

    public static void main(String[] args) {
        boolean a = testNull();
        System.out.println(a);
    }
}
