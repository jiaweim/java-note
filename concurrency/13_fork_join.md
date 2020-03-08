# Fork/Join Framework

fork/join 框架实现 `ExecutorService` 接口，为充分利用多处理器，为那些可以拆分为小任务的工作而设计。

和其它实现 `ExecutorService` 的框架一样，fork/join 也是在线程池中执行任务，fork/join 不同点在于，它使用了工作窃取算法（work-stealing）。执行完任务的工作线程可以从其它很忙的线程中窃取任务执行。

fork/join 框架的核心类是 `ForkJoinPool`，该类扩展 `AbstractExecutorService` 类，实现工作窃取算法，可以执行 `ForkJoinTask`。

## 基本使用

使用 fork/join 框架的第一步是编写执行部分工作的代码，例如：

```pseudo
if (my portion of the work is small enough)
  do the work directly
else
  split my work into two pieces
  invoke the two pieces and wait for the results
```

将代码放在 `ForkJoinTask` 子类，如 `RecursiveTask`（返回结果）或 `RecursiveAction`。

## 模糊照片

为了更好的理解 fork/join 框架的工作原理，下面来看个例子。假设你想将照片模糊化处理，原照片由整数数组表示，每个整数表示单个像素的颜色值，模糊化处理的目标照片是与原照片大小相同的整数数组表示。

执行模糊化处理，首先遍历原照片数组，将每个像素与其周围的像素进行平均（分别对红、绿和蓝分量进行平均），然后将结果放在目标数组。由于数组很大，次过程可能需要很长时间，使用 fork/join 框架能充分发挥多处理器的优势。下面是可能的一种实现方式：

```java
public class ForkBlur extends RecursiveAction {

    private int[] mSource;
    private int mStart;
    private int mLength;
    private int[] mDestination;
    private int mBlurWidth = 15; // Processing window size, should be odd.

    public ForkBlur(int[] src, int start, int length, int[] dst) {
        mSource = src;
        mStart = start;
        mLength = length;
        mDestination = dst;
    }

    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        int sidePixels = (mBlurWidth - 1) / 2;
        for (int index = mStart; index < mStart + mLength; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0), mSource.length - 1);
                int pixel = mSource[mindex];
                rt += (float) ((pixel & 0x00ff0000) >> 16) / mBlurWidth;
                gt += (float) ((pixel & 0x0000ff00) >> 8) / mBlurWidth;
                bt += (float) ((pixel & 0x000000ff) >> 0) / mBlurWidth;
            }

            // Re-assemble destination pixel.
            int dpixel = (0xff000000)
                    | (((int) rt) << 16)
                    | (((int) gt) << 8)
                    | (((int) bt) << 0);
            mDestination[index] = dpixel;
        }
    }
    protected static int sThreshold = 10000;

    @Override
    protected void compute() {
        if (mLength < sThreshold) {
            computeDirectly();
            return;
        }

        int split = mLength / 2;

        invokeAll(new ForkBlur(mSource, mStart, split, mDestination),
                new ForkBlur(mSource, mStart + split, mLength - split,
                mDestination));
    }

    // Plumbing follows.
    public static void main(String[] args) throws Exception {
        String srcName = "red-tulips.jpg";
        File srcFile = new File(srcName);
        BufferedImage image = ImageIO.read(srcFile);

        System.out.println("Source image: " + srcName);

        BufferedImage blurredImage = blur(image);

        String dstName = "blurred-tulips.jpg";
        File dstFile = new File(dstName);
        ImageIO.write(blurredImage, "jpg", dstFile);

        System.out.println("Output image: " + dstName);

    }

    public static BufferedImage blur(BufferedImage srcImage) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();

        int[] src = srcImage.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        System.out.println("Array size is " + src.length);
        System.out.println("Threshold is " + sThreshold);

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(Integer.toString(processors) + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");

        ForkBlur fb = new ForkBlur(src, 0, src.length, dst);

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(fb);
        long endTime = System.currentTimeMillis();

        System.out.println("Image blur took " + (endTime - startTime) +
                " milliseconds.");

        BufferedImage dstImage =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);

        return dstImage;
    }
}
```

## 标准实现

除了使用 fork/join 框架自定义算法外，Java SE 中有些常用功能已用 fork/join 框架。例如 `java.util.Arrays` 的 `parallelSort()` 排序算法，`java.util.streams` 包中的方法。