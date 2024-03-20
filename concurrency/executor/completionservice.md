# CompletionService

## 简介

如果向 Executor 提交了一组计算任务，并且希望在计算完成后获得结果，那么可以保留与每个任务关联的 Future，然后反复使用 `get` 方法，同时将 timeout 设置为 0。这种方法虽然可行，但却有些繁琐，还有一种更好的方法：`CompletionService`。

CompletionService 将 Executor 和 BlockingQueue 的功能融合在一起。可以将 Callable 任何提交给它执行，然后使用类似于队列操作的 take 和 poll 等方法来获取已完成的结果，而这些结果在完成时被封装在 Future。ExecutorCompletionService 实现了 CompletionService，并将计算部分委托给一个 Executor。

ExecutorCompletionService 实现非常简单，在构造函数中创建一个 BlockingQueue 来保存计算完成的结果。计算完成时，调用 FutureTask 的 done 方法。当提交任务时，首先将任务包装为一个 QueueingFuture，这是 FutureTask 的一个子类，然后再改写子类的 done 方法，并将结果放入 BlockingQueue 中。

## 示例

使用 CompletionService 实现页面渲染器。

可以通过 CompletionService 从两个方面来提高页面渲染器的性能：缩短总运行时间以及提高响应性。

- 为每张图像的下载都创建一个独立任务，并在线程池中执行它们，从而将串行下载转换为并行：减少所有图像下载的总时间。
- 此外，通过从 CompletionService 获取结果以及使每张图像在下载后立刻显示出来，能使用户获得一个更加动态和更高响应的用户界面。

```java
import java.util.List;
import java.util.concurrent.*;

public abstract class Renderer {

    private final ExecutorService executor;

    Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        final List<ImageInfo> info = scanForImageInfo(source);
        // 线程池
        CompletionService<ImageData> completionService =
                new ExecutorCompletionService<ImageData>(executor);
        // 每张图片创建一个线程
        for (final ImageInfo imageInfo : info)
            completionService.submit(new Callable<ImageData>() {
                public ImageData call() {
                    return imageInfo.downloadImage();
                }
            });

        renderText(source);

        try {
            for (int t = 0, n = info.size(); t < n; t++) {
                // 下载完成后，立刻显示
                Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    interface ImageData {
    }

    interface ImageInfo {

        ImageData downloadImage();
    }

    abstract void renderText(CharSequence s);

    abstract List<ImageInfo> scanForImageInfo(CharSequence s);

    abstract void renderImage(ImageData i);

    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }
}
```

多个 `ExecutorCompletionService` 可以**共享一个 Executor**，因此可以创建一个对于特定计算私有，又能共享一个公共 `Executor` 的 `ExecutorCompletionService`。因此，`CompletionService` 的作用就相当于一组计算的句柄，这与 `Future` 作为单个计算的句柄非常类似。

