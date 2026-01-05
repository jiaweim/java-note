# Apache Commons Pool

2026-01-05⭐
@author Jiawei Mao
***

## 简介

Apache Commons Pool 是一个开源库，提供对象池 API 和多种实现。

`org.apache.commons.pool2` package 定义了几种池化接口和一些基类。

依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.13.1</version>
</dependency>
```

### PooledObjectFactory

`PooledObjectFactory` 提供管理池化对象声明周期的通用接口：

```java
public interface PooledObjectFactory<T> {

  void activateObject(PooledObject<T> p) throws Exception;

  void destroyObject(PooledObject<T> p) throws Exception;

  default void destroyObject(final PooledObject<T> p, final DestroyMode destroyMode) throws Exception {
      destroyObject(p);
  }

  PooledObject<T> makeObject() throws Exception;

  void passivateObject(PooledObject<T> p) throws Exception;

  boolean validateObject(PooledObject<T> p);
}
```

1.x 版本的 commons-pool 的 `PoolableObjectFactory` 直接管理和创建池化对象，v2 的 `PooledObjectFactory` 则创建和管理 `PooledObject`。该包装类型提供池化状态，使 `PooledObjectFactory` 能够访问实例创建时间、最后访问时间等数据。`DefaultPooledObject` 类提供了池化状态相关方法。

实现 `PoolableObjectFactory` 最简单的方法是扩展 `BasePooledObjectFactory`，它提供了的 `makeObject()` 返回 `wrap(create()`，其中 `create` 和 `wrap` 都是 `abstract`。我们需要提供 `create` 实现来创建要在池中管理的底层对象，然后用 `wrap` 将创建的实例包装在 `PooledObject` 中。`DefaultPooledObject` 的使用：

```java
@Override
 public PooledObject<Foo> wrap(Foo foo) {
    return new DefaultPooledObject<Foo>(foo);
 }
```

其中，`Foo` 是要池化对象的类型（`create()` 的返回类型）。

`KeyedPooledObjectFactory` 提供 `KeyedPooledObjectFactory` 的抽象实现。

`org.apache.commons.pool2.impl` 提供一些 Pool 实现。

## GenericObjectPool

`GenericObjectPool` 提供了许多配置选项，包括限制闲置或 active 实例数量、驱逐闲置实例等。从 v2 开始，`GenericObjectPool` 还提供跟踪和删除废弃实例的功能。

`GenericKeyedObjectPool` 对 keyed 池提供了相同功能。

## SoftReferenceObjectPool

`SoftReferenceObjectPool` 可以根据需要增长，同时允许 GC 根据需要从池中驱逐空闲实例。

## 示例

假设你正在编写一套 `java.io.Reader` 工具，提供将 `Reader` 内容转存到 `String` 的方法。下面是不使用 `ObjectPool` 的 `ReaderUtil` 的代码：

```java
import java.io.Reader; 
import java.io.IOException; 
 
public class ReaderUtil { 
    public ReaderUtil() {} 
 
    /** 
     * Dumps the contents of the {@link Reader} to a 
     * String, closing the {@link Reader} when done. 
     */ 
    public String readToString(Reader in) throws IOException { 
        StringBuffer buf = new StringBuffer(); 
        try { 
            for (int c = in.read(); c != -1; c = in.read()) { 
                buf.append((char)c); 
            } 
            return buf.toString(); 
        } catch (IOException e) { 
            throw e; 
        } finally { 
            try { 
                in.close(); 
            } catch (Exception e) { 
                // ignored 
            } 
        } 
    } 
}
```

对该示例，假设我们想要池化用于缓冲 `Reader` 内容的 `StringBuffer`。（在实践中，池化 `StringBuffer` 可能没用，这里只是为了展示池化用法）

现在假设通过构造函数提供一个完成的 pool 实现。在使用 pool 时，只需调用 `borrowObject` 就可以获得 buffer，使用完成后再调用 `returnObject`。使用 `StringBuffer` 的 pool 实现大致如下：

```java
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.pool2.ObjectPool;

public class ReaderUtil {

    private ObjectPool<StringBuffer> pool;

    public ReaderUtil(ObjectPool<StringBuffer> pool) {
        this.pool = pool;
    }

    /**
     * Dumps the contents of the {@link Reader} to a String, closing the {@link Reader} when done.
     */
    public String readToString(Reader in) throws IOException {
        StringBuffer buf = null;
        try {
            buf = pool.borrowObject();
            for (int c = in.read(); c != -1; c = in.read()) {
                buf.append((char) c);
            }
            return buf.toString();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to borrow buffer from pool" + e.toString());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // ignored
            }
            try {
                if (null != buf) {
                    pool.returnObject(buf);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }
}
```

由于这里限制在 `ObjectPool` 接口，因此可以使用任何 pool 实现。

**PooledObjectFactory**

pool2 提供的实现将对象包装在 `PooledObject` 中，供 pool 和对象 factory 使用。

`PooledObjectFactory` 接口定义池化对象的生命周期。实现 `PooledObjectFactory` 的最简单方法是扩展 `BasePooledObjectFactory`，下面是 `StringBuffer` 的 `PooledObjectFactory` 实现：

```java
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class StringBufferFactory
        extends BasePooledObjectFactory<StringBuffer> {

    @Override
    public StringBuffer create() {
        return new StringBuffer();
    }

    /**
     * Use the default PooledObject implementation.
     */
    @Override
    public PooledObject<StringBuffer> wrap(StringBuffer buffer) {
        return new DefaultPooledObject<StringBuffer>(buffer);
    }

    /**
     * When an object is returned to the pool, clear the buffer.
     */
    @Override
    public void passivateObject(PooledObject<StringBuffer> pooledObject) {
        pooledObject.getObject().setLength(0);
    }

    // for all other methods, the no-op implementation
    // in BasePooledObjectFactory will suffice
}
```

现在就可以将该 factory 配合 `GenericObjectPool` 实例化 `ReaderUtil`：

```java
ReaderUtil readerUtil = new ReaderUtil(
    new GenericObjectPool<StringBuffer>(new StringBufferFactory())
);
```





## 参考

- https://commons.apache.org/proper/commons-pool/index.html
- https://github.com/apache/commons-pool
