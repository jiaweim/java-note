# 内置扩展

2023-08-26⭐
@author Jiawei Mao
****
## 简介

虽然 JUnit 团队鼓励在单独的库中打包和维护可重用扩展，但 JUnit Jupiter API 依然内置了一些常用的扩展。

## TempDir 扩展

内置 `TempDirectory` 扩展用于为单个测试或测试类的所有测试创建和清理临时目录。默认注册。

**使用**：

- 用 `@TempDir` 注释 non-final、未赋值的 `java.nio.file.Path` 或 `java.io.File` 类型的字段
- 或用 `@TempDir` 注释测试类构造函数、生命周期方法或测试方法的 `java.nio.file.Path` 或 `java.io.File` 类型参数

### 单个临时目录

**示例：** 下面为一个测试方法声明了一个带 `@TempDir` 注释的参数，用于在临时目录中创建并写入文件，检查文件内容：

```java
@Test
void writeItemsToFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("test.txt");

    new ListWriter(file).write("a", "b", "c");

    assertEquals(singletonList("a,b,c"), Files.readAllLines(file));
}
```

### 多个临时目录

指定多个注释参数，可以指定多个临时目录。

**示例：** 需要多个临时目录的测试方法

```java
@Test
void copyFileFromSourceToTarget(@TempDir Path source, @TempDir Path target) throws IOException {
    Path sourceFile = source.resolve("test.txt");
    new ListWriter(sourceFile).write("a", "b", "c");

    Path targetFile = Files.copy(sourceFile, target.resolve("test.txt"));

    assertNotEquals(sourceFile, targetFile);
    assertEquals(singletonList("a,b,c"), Files.readAllLines(targetFile));
}
```

### 共享临时目录

构造函数参数不支持 @TempDir。如果希望跨生命周期方法和当前测试方法保留对临时目录的引用，可使用 @TempDir 注释实例字段。

**示例：** 将共享临时目录存储在 static 字段中。从而在所有生命周期方法和测试方法中使用相同的 `sharedTempDir`。为了更好隔离，应该使用实例字段，使得每个测试方法使用单独的目录。

```java
class SharedTempDirectoryDemo {

    @TempDir
    static Path sharedTempDir;

    @Test
    void writeItemsToFile() throws IOException {
        Path file = sharedTempDir.resolve("test.txt");

        new ListWriter(file).write("a", "b", "c");

        assertEquals(singletonList("a,b,c"), Files.readAllLines(file));
    }

    @Test
    void anotherTestThatUsesTheSameTempDir() {
        // use sharedTempDir
    }
}
```

### 清理临时目录

`@TempDir `注释有一个可选的 `cleanup` 属性，可以设置为 `NEVER`, `ON_SUCCESS`, `ALWAYS`：

- `NEVER` - 测试完成后不删除临时目录
- `ON_SUCCESS` - 测试成功后删除临时目录
- `ALWAYS` - 测试后不管成功还是失败，都删除临时目录

默认为 `ALWAYS`。可以使用 `junit.jupiter.tempdir.cleanup.mode.default` 配置参数覆盖默认值。

**示例：** 一个带临时目录的测试类，测试失败不删除临时目录

```java
class CleanupModeDemo {
    @Test
    void fileTest(@TempDir(cleanup = ON_SUCCESS) Path tempDir) {
        // perform test
    }
}
```

### 自定义临时目录

通过 `@TempDir` 可选的 `factory` 属性，支持以编程方式创建临时目录，从而获得对临时目录创建的完全控制。

通过实现 `TempDirFactory` 创建 factory。实现必须提供一个无参构造函数，并且不能对实例化时间和次数做任何假设，不过可以假设 `createTempDirectory(…​)` 和 `close()` 方法都会在同一个线程按顺序为每个实例调用一次。

Jupiter 中的默认实现使用 `java.nio.file.Files::createTempDirectory` 创建临时目录，并将 `junit` 作为生成目录名称的前缀。

**示例：** 使用测试名称作为临时目录前缀，而非 `junit` 常量

```java
class TempDirFactoryDemo {

    @Test
    void factoryTest(@TempDir(factory = Factory.class) Path tempDir) {
        assertTrue(tempDir.getFileName().toString().startsWith("factoryTest"));
    }

    static class Factory implements TempDirFactory {
        @Override
        public Path createTempDirectory(AnnotatedElementContext elementContext, ExtensionContext extensionContext)
                throws IOException {
            return Files.createTempDirectory(extensionContext.getRequiredTestMethod().getName());
        }
    }
}
```

也可以使用内存文件系统，如 `Jimfs` 创建临时目录。

**示例：** 使用 Jimfs 内存文件系统创建临时目录

```java
class InMemoryTempDirDemo {

    @Test
    void test(@TempDir(factory = JimfsTempDirFactory.class) Path tempDir) {
        // perform test
    }

    static class JimfsTempDirFactory implements TempDirFactory {

        private final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        @Override
        public Path createTempDirectory(AnnotatedElementContext elementContext, ExtensionContext extensionContext)
                throws IOException {
            return Files.createTempDirectory(fileSystem.getPath("/"), "junit");
        }

        @Override
        public void close() throws IOException {
            fileSystem.close();
        }
    }
}
```

### 元注释

`@TempDir` 也可以作为元注释使用来减少重复。

**示例：** 创建 `@JimfsTempDir` 注释替代 `@TempDir(factory = JimfsTempDirFactory.class)`

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@TempDir(factory = JimfsTempDirFactory.class)
@interface JimfsTempDir {
}
```

使用自定义的 `@JimfsTempDir `注释：

```java
class JimfsTempDirAnnotationDemo {
    @Test
    void test(@JimfsTempDir Path tempDir) {
        // perform test
    }
}
```

总之，临时目录的 factory 按下列顺序确定：

1. `@TempDir` 注释的 factory 属性
2. 通过配置文件设置的默认 `TempDirFactory` 
3. 否则使用 `org.junit.jupiter.api.io.TempDirFactory$Standard`

## AutoClose 扩展 

内置 `AutoCloseExtension` 会自动关闭与字段关联的资源。它默认注释，在测试类中为为字段添加 `@AutoClose` 注释来使用。

`@AutoClose` 字段可以是 `static` 或 non-static。



## 参考

- https://docs.junit.org/current/writing-tests/built-in-extensions
