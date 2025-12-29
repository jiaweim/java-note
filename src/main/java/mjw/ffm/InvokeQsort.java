package mjw.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class InvokeQsort {

    class Qsort {
        static int qsortCompare(MemorySegment elem1, MemorySegment elem2) {
            return Integer.compare(elem1.get(ValueLayout.JAVA_INT, 0),
                    elem2.get(ValueLayout.JAVA_INT, 0));
        }
    }

    // Obtain instance of native linker
    final static Linker linker = Linker.nativeLinker();

    // Create downcall handle for qsort
    static final MethodHandle qsort = linker.downcallHandle(
            linker.defaultLookup().findOrThrow("qsort"),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG,
                    ValueLayout.JAVA_LONG,
                    ValueLayout.ADDRESS));

    // Create method handle for qsortCompare
    static final MethodHandle compareHandle = initCompareHandle();

    static MethodHandle initCompareHandle() {
        MethodHandle ch = null;
        try {
            ch = MethodHandles.lookup()
                    .findStatic(Qsort.class,
                            "qsortCompare",
                            MethodType.methodType(int.class,
                                    MemorySegment.class,
                                    MemorySegment.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ch;
    }

    static int[] qsortTest(int[] unsortedArray) throws Throwable {

        int[] sorted = null;

        // Create a Java description of a C function implemented by a Java method
        FunctionDescriptor qsortCompareDesc = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
                ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT));

        // Create function pointer for qsortCompare
        MemorySegment compareFunc = linker.upcallStub(compareHandle,
                qsortCompareDesc,
                Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {

            // Allocate off-heap memory and store unsortedArray in it                
            MemorySegment array = arena.allocateFrom(ValueLayout.JAVA_INT,
                    unsortedArray);

            // Call qsort        
            qsort.invoke(array,
                    (long) unsortedArray.length,
                    ValueLayout.JAVA_INT.byteSize(),
                    compareFunc);

            // Access off-heap memory
            sorted = array.toArray(ValueLayout.JAVA_INT);
        }
        return sorted;
    }

    static void main() {
        try {
            int[] sortedArray = InvokeQsort.qsortTest(new int[]{0, 9, 3, 4, 6, 5, 1, 8, 2, 7});
            for (int num : sortedArray) {
                System.out.print(num + " ");
            }
            System.out.println();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}