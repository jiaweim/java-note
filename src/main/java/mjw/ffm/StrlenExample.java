package mjw.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class StrlenExample {

    // 为 C 函数 strlen 创建一个下调 handle
    static final MethodHandle strlen = strlenMH();

    static MethodHandle strlenMH() {

        // 获取 native linker 实例
        Linker linker = Linker.nativeLinker();

        // 找到 C 函数签名地址
        SymbolLookup stdLib = linker.defaultLookup();
        MemorySegment strlen_addr = stdLib.findOrThrow("strlen");

        // 创建一个 C 函数描述
        FunctionDescriptor strlen_sig =
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);

        // 返回 C 函数下调 handle
        return linker.downcallHandle(strlen_addr, strlen_sig);
    }

    static long invokeStrlen(String s) throws Throwable {
        try (Arena arena = Arena.ofConfined()) {
            // 分配堆外内存，复制一个 Java String 参数到堆外内存
            MemorySegment nativeString = arena.allocateFrom(s);
            // 直接从 Java 调用 C 函数
            return (long) strlen.invokeExact(nativeString);
        }
    }


    static void main(String[] args) {
        StrlenExample myApp = new StrlenExample();
        try {
            System.out.println(myApp.invokeStrlen(args[0]));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}