package mjw.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class InitializeNativeArrayExample {

    static void main() {
        InitializeNativeArrayExample myApp = new InitializeNativeArrayExample();
        try {
            myApp.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initialize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(2 * 4 * 10, 1);

            for (int i = 0; i < 10; i++) {
                int xValue = i;
                int yValue = i * 10;
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2), xValue);
                segment.setAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1, yValue);
            }

            for (int i = 0; i < 10; i++) {
                int xVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2));
                int yVal = segment.getAtIndex(ValueLayout.JAVA_INT, (i * 2) + 1);
                System.out.println("(" + xVal + ", " + yVal + ")");
            }
        }
    }
}