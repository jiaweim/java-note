# Process

- [Process](#process)
  - [简介](#简介)
  - [启动程序](#启动程序)
  - [查看输出](#查看输出)
  - [重定向输出](#重定向输出)
  - [重定向输入和输出](#重定向输入和输出)
  - [inheritIO](#inheritio)
  - [environment](#environment)
  - [directory](#directory)
  - [non-blocking 操作](#non-blocking-操作)
  - [配置 Process](#配置-process)
  - [调用外部程序](#调用外部程序)
  - [参考](#参考)

2021-11-01, 17:02
***

## 简介

使用 `ProcessBuilder` 和 `Process` 类可以运行外部程序。

`Process` 在一个单独的操作系统进程中执行命令，并可以与该程序的标准输入、输出和错误流交互。

`ProcessBuilder` 用于配置构建 `Process` 对象，支持如下配置：

- 命令
- 环境
- 工作目录
- 输入源
- 标准输出和错误输出位置
- 重定向

## 启动程序

```java
import java.io.IOException;

public class ExecuteProgram {

    public static void main(String[] args) throws IOException, InterruptedException {

        var processBuilder = new ProcessBuilder();

        processBuilder.command("notepad.exe");

        var process = processBuilder.start();

        var ret = process.waitFor();

        System.out.printf("Program exited with code: %d", ret);
    }
}
```

会等待 Notepad 程序执行，返回 Notepad 的 exit 代码。

## 查看输出

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessBuilderEx {

    public static void main(String[] args) throws IOException {

        var processBuilder = new ProcessBuilder();

        processBuilder.command("cal", "2019", "-m 2");

        var process = processBuilder.start();

        try (var reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        }
    }
}
```

上例执行 Linux 的 `cal` 命令：

```java
processBuilder.command("cal", "2019", "-m 2");
```

`command()` 执行 `cal` 程序。其它参数是该程序的参数。要在 Windows 中执行该程序，可以使用如下命令 `processBuilder.command("cmd.exe", "/c", "ping -n 3 google.com")`。然后启动进程：

```java
var process = processBuilder.start();
```

接着使用 `getInputStream()` 获得进程的输出：

```java
try (var reader = new BufferedReader(
    new InputStreamReader(process.getInputStream()))) {
```

## 重定向输出

使用 `redirectOutput()` 可以重定向 Process 的输出。

例如，重定向输出到 output.txt 文件：

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RedirectOutputEx {

    public static void main(String[] args) throws IOException {

        var homeDir = System.getProperty("user.home");

        var processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "date /t");

        var fileName = new File(String.format("%s/Documents/tmp/output.txt", homeDir));

        processBuilder.redirectOutput(fileName);

        var process = processBuilder.start();

        try (var reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
```

## 重定向输入和输出

```java
import java.io.File;
import java.io.IOException;

public class ProcessBuilderRedirectIOEx {

    public static void main(String[] args) throws IOException {

        var processBuilder = new ProcessBuilder();

        processBuilder.command("cat")
                .redirectInput(new File("src/resources", "input.txt"))
                .redirectOutput(new File("src/resources/", "output.txt"))
                .start();
    }
}
```

## inheritIO

`inheritIO()` 表示使子进程的 I/O 和当前进程的一致：

```java
import java.io.IOException;

public class ProcessBuilderInheritIOEx {

    public static void main(String[] args) throws IOException, InterruptedException {

        var processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "dir");

        var process = processBuilder.inheritIO().start();

        int exitCode = process.waitFor();
        System.out.printf("Program ended with exitCode %d", exitCode);
    }
}
```

## environment

`environment()` 返回当前环境的 map：

```java
public class ProcessBuilderEnvEx {

    public static void main(String[] args) {

        var pb = new ProcessBuilder();
        var env = pb.environment();

        env.forEach((s, s2) -> {
            System.out.printf("%s %s %n", s, s2);
        });

        System.out.printf("%s %n", env.get("PATH"));
    }
}
```

## directory

`directory()` 设置工作目录：

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessBuilderDirectoryEx {

    public static void main(String[] args) throws IOException {

        var homeDir = System.getProperty("user.home");

        var pb = new ProcessBuilder();

        pb.command("cmd.exe", "/c", "dir");
        pb.directory(new File(homeDir));

        var process = pb.start();

        try (var reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
```

## non-blocking 操作

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ProcessBuilderNonBlockingEx {

    public static void main(String[] args) throws InterruptedException,
            ExecutionException, TimeoutException, IOException {

        var executor = Executors.newSingleThreadExecutor();

        var processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "ping -n 3 google.com");

        try {

            var process = processBuilder.start();

            System.out.println("processing ping command ...");
            var task = new ProcessTask(process.getInputStream());
            Future<List<String>> future = executor.submit(task);

            // non-blocking, doing other tasks
            System.out.println("doing task1 ...");
            System.out.println("doing task2 ...");

            var results = future.get(5, TimeUnit.SECONDS);

            for (String res : results) {
                System.out.println(res);
            }

        } finally {
            executor.shutdown();
        }
    }

    private static class ProcessTask implements Callable<List<String>> {

        private InputStream inputStream;

        public ProcessTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public List<String> call() {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.toList());
        }
    }
}
```

## 配置 Process

首先要指定要执行的命令，可以通过 `List<String>`，也可以使用包含完整命令的字符串。如下所示：

```java
var builder = new ProcessBuilder("gcc", "myapp.c");
```

需要注意的是，命令的第一个字符串必须是可执行程序。

每个进程都有一个工作目录，用于解析相对目录。该目录默认与虚拟机相同，可以通过 `directory` 方法修改：

```java
builder = builder.directory(path.toFile());
```

还可以指定对Process 的标准输入、出错和错误流的操作。默认都可以访问：

```java
OutputStream processIn = p.getOutputStream();
InputStream processOut = p.getInputStream();
InputStream processErr = p.getErrorStream();
```

需要注意的是，



## 调用外部程序

```java
Process process = new ProcessBuilder("C:\\PathToExe\\MyExe.exe","param1","param2").start();
```

## 参考

- https://zetcode.com/java/processbuilder/
