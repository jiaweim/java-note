# 监听目录

- [监听目录](#监听目录)
  - [简介](#简介)
  - [实例](#实例)
  - [创建监听服务](#创建监听服务)
  - [处理事件](#处理事件)
  - [查询文件名](#查询文件名)

2020-07-14, 09:16
***

## 简介

在文件发生更改时，能够获得通知。为实现该功能，程序必须能够检测相关目录正在发生的变化。一种方式是轮询文件系统查找更改，不过效率不高，无法同时监视上百个文件或目录。

`java.nio.file` 包提供了文件更改通知 API，称为监视服务API（Watch Service API）。通过该 API，可以通过检测服务注册一个或多个目录。注册时，告诉服务你感兴趣的事件类型：文件创建、文件删除或文件修改。当服务检测到感兴趣的事件时，会将其转发到注册的进程。已注册的进程包含一个线程（或线程池）用于监视其注册的事件，当事件进入，将根据需求进行处理。

`WatchService` API 是一个相当底层的API，可以进行自定义。你可以直接使用，也可以在其基础上创建一个高级 API，以适合你的工作。

实现监听服务的基本步骤：

- 对文件系统创建 `WatchService`
- 注册需要监听的目录。在注册目录时，同时注册感兴趣的事件类型。对每个监听目录，返回一个 `WatchKey` 实例
- 实现一个无限循环，等待输入事件。当事件发生时，触发 key，并将其放入监听序列。
- 从监听序列中获得 key，可以从 key 中获得文件名称
- 检测 key 的每个事件，根据需求进行处理。
- 重置 key，继续等待事件。
- 关闭服务，关闭线程（`closed()` 方法）后监听服务退出。

`WatchKeys` 线程安全，可以和 `java.nio.concurrent` 包。

## 实例

创建一个 `test` 目录，`WatchDir` 使用单个线程处理所有事件，所以在等待事件时会阻塞键盘输入。

```java
public class WatchDir
{
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event)
    {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException
    {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Path dir, boolean recursive) throws IOException
    {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents()
    {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    static void usage()
    {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException
    {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean recursive = false;
        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2)
                usage();
            recursive = true;
            dirArg++;
        }

        // register directory and process its events
        Path dir = Paths.get(args[dirArg]);
        new WatchDir(dir, recursive).processEvents();
    }
}
```

## 创建监听服务

使用 `FileSystem` 的 `newWatchService` 方法创建 `WatchService`:

```java
WatchService watcher = FileSystems.getDefault().newWatchService();
```

然后注册实现 `Watchable` 接口的对象。`Path` 类实现了 `Watchable` 接口，所以可以使用 `Path` 对象作为目录的注册监听对象。

和 `Watchable` 一样，`Path` 实现了两个 `register` 方法。

在注册时需要指定监听的事件类型。支持的 `StandardWatchEventKinds` 事件类型有：

| 事件类型     | 说明                               |
| ------------ | ---------------------------------- |
| ENTRY_CREATE | 创建目录                           |
| ENTRY_DELETE | 删除目录                           |
| ENTRY_MODIFY | 修改目录                           |
| OVERFLOW     | 事件丢失或者被舍弃。不用注册该事件 |

如下，为 `Path` 注册以上三个事件：

```java
import static java.nio.file.StandardWatchEventKinds.*;

Path dir = Paths.get("testdir");
try {
    WatchKey key = dir.register(watcher,
                           ENTRY_CREATE,
                           ENTRY_DELETE,
                           ENTRY_MODIFY);
} catch (IOException x) {
    System.err.println(x);
}
```

## 处理事件

事件处理的流程如下：

1. 获得 watch key

`WatchService` 有三个方法实现该目的

- `poll`，从队列返回一个 key。如果没有，立刻返回 `null`。
- `poll(long, TimeUnit)` 从队列返回一个 key，指定最长等待时间。
- `take` 从队列返回一个 key，没有就一直等待。

`close()` 关闭服务。

```java
while (true){
    WatchKey watchKey = watchService.poll();
    if(watchKey != null){
        // ...
        watchKey.reset();
    }
}
```

2. 获得 watch key 的事件

使用 `WatchKey.pollEvents()` 获得 `WatchEvent` 列表，然后依次处理事件：

```java
while (true){
    WatchKey watchKey = watchService.poll();
    if(watchKey != null){
        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
        for (WatchEvent<?> watchEvent : watchEvents) {
            // process event
        }

        watchKey.reset();
    }
}
```

|`WatchKey` 方法|功能|
|---|---|
|boolean isValid()|key 是否有效|
|`List<WatchEvent> pollEvents()`|拉取并删除 key 的事件列表，可能为空|
|boolean reset()|重置 key，重置后，新的事件才能加入 key 的事件队列|
|void cancel()|取消 key 的注册|
|Watchable watchable()|返回该 key 关联的 Watchable|

3. 判断事件类型

通过 `kind()` 方法获得事件类型。不管注册了哪些时间类型，都可能收到 `OVERFLOW` 事件。可以直接忽略，也可以对其进行处理。

```java
WatchEvent.Kind<?> kind = watchEvent.kind();
if(kind == OVERFLOW)
    continue;
```

`count()` 获得事件个数，大于 1 表示多个相同事件（重复事件）。

4. 获得事件对应的文件名。

文件名保存在事件的 context 中，所以可以使用 `context` 方法查询文件名。

```java
WatchEvent<Path> event = (WatchEvent<Path>) watchEvent;
Path filename = event.context();
```

5. 重置状态

在处理事件后，需要使用 `reset` 方法将 key 重置为 `ready` 状态。

如果 `reset` 方法返回 `false`，表示 key 无效，可以退出循环。该步骤十分重要，如果 reset 失败，该 key 不再接受任何事件。

watch key 的状态：

- `Ready`, 表示 key 准备好接收事件。key 创建时的默认状态
- `Signaled`, 表示队列中有1到多个事件。除非调用 `reset` 方法，否则 key 不会进入 `Ready` 状态，即不再接收事件。
- `Invalid` 表示 key 无效。当发生下列事件时出现该状态：
  - 调用 `cancel` 方法取消 key
  - 目录无法访问
  - 监听服务关闭

下面是一个事件处理循环的实例。监听一个目录，等到新文件出现。当新文件可用时，使用 `probeContentType(Path)` 方法检测文件类型是否为 `text/plain`。

如下：

```java
for (;;) { // infinite loop

    // wait for key to be signaled
    WatchKey key;
    try {
        key = watcher.take();
    } catch (InterruptedException x) {
        return;
    }

    for (WatchEvent<?> event: key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();

        // 就算只注册了 ENTRY_CREATE 事件，OVERFLOW 事件也可能发生
        if (kind == OVERFLOW) {
            continue;
        }

        // 文件名是事件对应的 context
        WatchEvent<Path> ev = (WatchEvent<Path>)event;
        Path filename = ev.context();

        // 验证新文件是否为文本文件
        try {
            // Resolve the filename against the directory.
            // If the filename is "test" and the directory is "foo",
            // the resolved name is "test/foo".
            Path child = dir.resolve(filename);
            if (!Files.probeContentType(child).equals("text/plain")) {
                System.err.format("New file '%s'" +
                    " is not a plain text file.%n", filename);
                continue;
            }
        } catch (IOException x) {
            System.err.println(x);
            continue;
        }

        // Email the file to the
        //  specified email alias.
        System.out.format("Emailing file %s%n", filename);
        //Details left to reader....
    }

    // Reset the key -- this step is critical if you want to
    // receive further watch events.  If the key is no longer valid,
    // the directory is inaccessible so exit the loop.
    boolean valid = key.reset();
    if (!valid) {
        break;
    }
}
```

## 查询文件名

从 context 获得文件名。

```java
WatchEvent<Path> ev = (WatchEvent<Path>)event;
Path filename = ev.context();
```
