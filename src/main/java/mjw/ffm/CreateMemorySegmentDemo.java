package mjw.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 29 Dec 2025, 11:03 AM
 */
public class CreateMemorySegmentDemo {
    static long invokeStrlen(String s) throws Throwable {

        try (Arena arena = Arena.ofConfined()) {

            // 分配堆外内存，并将 Java String 参数复制到堆外内存
            MemorySegment nativeString = arena.allocateFrom(s);

            // 连接并调用 C 函数 strlen

            // 获取 native linker 实例
            Linker linker = Linker.nativeLinker();

            // 查找 C 函数签名的地址
            SymbolLookup stdLib = linker.defaultLookup();
            MemorySegment strlen_addr = stdLib.find("strlen").get();

            // 创建 C 函数的描述
            FunctionDescriptor strlen_sig =
                    FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);

            // 为 C 函数创建一个 downcall handle
            MethodHandle strlen = linker.downcallHandle(strlen_addr, strlen_sig);

            // 直接从 Java 调用 C 和俺叔
            return (long) strlen.invokeExact(nativeString);
        }
    }



    static void main() {
        String s = "My string";
        try (Arena arena = Arena.ofConfined()) {

            // Allocate off-heap memory
            MemorySegment nativeText = arena.allocateFrom(s);

            // Access off-heap memory
            for (int i = 0; i < s.length(); i++) {
                System.out.print((char) nativeText.get(ValueLayout.JAVA_BYTE, i));
            }
        } // Off-heap memory is deallocated
    }
}
