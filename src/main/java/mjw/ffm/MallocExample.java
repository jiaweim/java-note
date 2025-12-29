package mjw.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

public class MallocExample {

    // 获取 native-linkder 实例
    static final Linker linker = Linker.nativeLinker();

    // 为 malloc() 创建下调句柄
    static final MethodHandle malloc = linker.downcallHandle(
            linker.defaultLookup().findOrThrow("malloc"),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG)
    );

    // 为 free() 创建下调句柄
    static final MethodHandle free = linker.downcallHandle(
            linker.defaultLookup().findOrThrow("free"),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
    );

    static MemorySegment allocateMemory(long byteSize, Arena arena) throws Throwable {

        // 调用 malloc(), 返回一个指针
        MemorySegment segment = (MemorySegment) malloc.invokeExact(byteSize);

        // malloc() 创建的内存段大小为 0 bytes
        System.out.println("Size, in bytes, of memory segment created by calling malloc.invokeExact("
                + byteSize + "): " + segment.byteSize());

        Consumer<MemorySegment> cleanup = s -> {
            try {
                free.invokeExact(s);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        // reintepret 方法:
        // 1. 调整内存段大小，使其与 byteSize 相等
        // 2. 将其与已有 arena 关联
        // 3. 当 arena 关闭，调用 free() 释放 malloc() 分配的内存

        return segment.reinterpret(byteSize, arena, cleanup);
    }

    static void main() {
        String s = "My string!";
        try (Arena arena = Arena.ofConfined()) {

            // Allocate off-heap memory with malloc()
            var nativeText = allocateMemory(
                    ValueLayout.JAVA_CHAR.byteSize() * (s.length() + 1), arena);

            // Access off-heap memory
            for (int i = 0; i < s.length(); i++) {
                nativeText.setAtIndex(ValueLayout.JAVA_CHAR, i, s.charAt(i));
            }

            // Add the string terminator at the end
            nativeText.setAtIndex(
                    ValueLayout.JAVA_CHAR, s.length(), Character.MIN_VALUE);

            // Print the string
            for (int i = 0; i < s.length(); i++) {
                System.out.print((char) nativeText.getAtIndex(ValueLayout.JAVA_CHAR, i));
            }
            System.out.println();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}