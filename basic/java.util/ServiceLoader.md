# ServiceLoader

2023-06-07
****
## 简介

用于加载 service 实现的工具类。

service 是一个接口或类，对应 0 到多个 service provider。provider 扩展或实现 service。`ServiceLoader` 在运行时定位并加载部署在运行环境中的 providers。应用代码只需要引用 service，而无需关心 provider。

## 获取 ServiceLoader

通过调用 `ServiceLoader`  的静态 `load` 方法获得 `ServiceLoader`。如果应用是 module，则该 module 声明中必须使用 `uses` 指令指定该 `service`；这有助于定位 provider，确定运行的可靠性。另外，如果 service 不在应用 module 中，在还需要在 module 声明中使用 `requires` 指令指定导出该 service 的 module。

通过 `ServiceLoader` 的 `iterator` 可以迭代、实例化 service 的 providers。`ServiceLoader` 还定义了 `stream` 来获取 provider 流，可以在不实例化 provider 的情况下进行检查和过滤。

例如，假设 service 为 `com.example.CodecFactory`，一个定义 encoder 和 decoder 的接口：

```java
 package com.example;
 public interface CodecFactory {
	 Encoder getEncoder(String encodingName);
	 Decoder getDecoder(String encodingName);
 }
```

下面的代码获取 `CodecFactory` service 的 `ServiceLoader`，然后通过它迭代并实例化定位到的 provider：

```java
ServiceLoader<CodecFactory> loader = ServiceLoader.load(CodecFactory.class);
for (CodecFactory factory : loader) {
	 Encoder enc = factory.getEncoder("PNG");
	 if (enc != null)
		 ... use enc to encode a PNG file
		 break;
}
```

如果上面的代码在一个 module 中，那么为了引用 `com.example.CodecFactory` 接口，module 声明中需要 require 导出 service 接口的 module，同时指定 use `com.example.CodecFactory`：

```java
requires com.example.codec.core;
uses com.example.CodecFactory;
```

有时候，应用代码可能希望在实例化 provider 之前进行检查，确定该 provider 是否有用。例如，`CodecFactory` 的 provider 对能够给生成 "PNG" encoder 可能添加 @PNG 注释。下面使用 `ServiceLoader` 的 `stream` 方法生成 `Provider<CodecFactory>` 实例，而非迭代器中的 `CodecFactory` 实例：

```java
 ServiceLoader<CodecFactory> loader = ServiceLoader.load(CodecFactory.class);
 Set<CodecFactory> pngFactories = loader
		.stream()                                              // Note a below
		.filter(p -> p.type().isAnnotationPresent(PNG.class))  // Note b
		.map(Provider::get)                                    // Note c
		.collect(Collectors.toSet());
```

a. `Provider<CodecFactory>` stream
b. `p.type()` 返回 `Class<CodecFactory>`
c. `get()` 返回 `CodecFactory` 实例

## Service 设计

service 是单一类型，通常是接口或抽象类。也可以使用具体类（concrete class），但不推荐。service 类型可以具有任何可访问性。service 的方法特定于领域，因此 Service 的 API 规范没有指定形式或功能。有两个指导意见：

1. service 应该声明足够多的方法，使 provider 能够提供特定于领域的知识，这样应用代码就能通过这些方法选择而合适的 provider。
2. service 应该说清楚它是打算作为 service 的直接实现，还是作为 "proxy" 或 "factory" 的间接机制。当 provider 的实例化成本相对较高时，provider 一般是间接机制，此时 service 应该设计成按需实例化 provider。例如，`CodecFactory` 通常其名称知道其 provider 为 codec 的 factory 类，而不是 codec 本身，因为直接生成 codec 可能成本高或过于复杂。

## Provider 开发

provider 是单一类型，通常是一个具体类。也允许使用接口或抽象类，因为它可以声明 static 发方法。其类型必须为 public，不能是内部类。

provider 及其支撑代码可能位于一个 module，然后部署在应用的 module 路径。或者打包为 一个 JAR 使用。使用 module 的优点是可以完全封装 provider，从而隐藏其实现的所有细节。

不管以哪种形式部署，获取 ServiceLoader 的方法没有差别。

## 将 provider 部署为 module

在 module 中开发的 provider 必须在 module 声明中使用 `providers` 指令。`providers` 指令同时指定 service 和 provider。强烈建议 module 不要导出包含 provider 的包。另外，不支持使用 `providers` 指定在另一个 module 中定义的 provider。

在 module 中开发的 provider 无法控制它何时被实例化，因为这是由应用代码控制的，但它可以控制如何被实例化：

- 如果 provider 声明了一个 `provider` 方法，那么 `ServiceLoader` 将调用该方法来获取 provider 实例。provider 方法是一个名为 "provider" 的 public static 方法。此时，不需要将 provider 分配给 service。
- 如果 provider 没有声明 "provider" 方法，那么该 provider 通过其提供的构造函数直接实例化。此时，provider 必须能够分配给 service。

例如，假设一个 module 包含如下指令：

```java
provides com.example.CodecFactory with com.example.impl.StandardCodecs;
provides com.example.CodecFactory with com.example.impl.ExtendedCodecsFactory;
```

其中：

- `com.example.impl.StandardCodecs` 是实现 `CodecFactory` 的 public 类，包含一个 public 午餐构造函数。
- `com.example.impl.ExtendedCodecsFactory` 是一个 public 类，但没有实现 `CodecFactory`，但是它声明了要一个名为 `provider` 的 public static 无参方法，返回类型为 `CodecFactory`。

`ServiceLoader` 将通过其构造函数实例化 `StandardCodecs`，通过 `provider` 方法实例化 `ExtendedCodecsFactory`。

## 将 provider 部署到 class path

打包到 JAR 的 provider，通过 META-INF/services 目录下的 provider 配置文件识别。provider 配置文件位 service 的完整名称，其中包含 provider 的完整名称，一行一个。

例如，假设 `com.example.impl.StandardCodecs` 是打包在 JAR 中的一个 provider。该 JAR 文件需要提供一个provider 配置文件，名称为：

```
META-INF/services/com.example.CodecFactory
```

其中包含：

```
com.example.impl.StandardCodecs # Standard codecs
```

provider 配置文件必须使用 UTF-8 编码。provider 名称周围的空格、制表符以及空行都被忽略。`#` 为注释字符，每一行，第一个 `#` 字符后面的内容被忽略。如果某个 provider 类名出现多次，忽略重复 provider。如果一个 provider 类出现在多个配置文件中，同样忽略重复配置。

provider 配置文件中提供的 provider 可能与配置文件处于同一个 JAR，也可能位于不同的 JAR，只需要对初始定位 provider 配置文件的 ClassLoader 可见即可。

## provider 加载顺序

provider 是按需 lazily 加载和实例化。`ServiceLoader` 维护了一个已加载的 provider 缓存。每次调用 `iterator` 都返回一个 `Iterator`，该 Iterator 首先按实例化顺序生成从上一次迭代中缓存的所有元素，然后 lazily 定位和实例化余下的 provider，并依次添加到缓存。

类似地，每次调用 `stream` 返回一个 `Stream`。该 `Stream` 首先按照加载顺序处理上一次 stream 操作缓存的 provider，然后 lazily 定位余下的 providers。通过 `reload` 方法清空缓存。

## Errors

在使用 ServiceLoader 的 iterator 方法时，如果 provider 的定位、加载或实例化出错，hasNext 和 next 方法会失败，抛出 `ServiceConfigurationError`。当处理 ServiceLoader stream 时，任何需要定位或加载 provider 的方法都可能抛出 `ServiceConfigurationError`。

在 module 中加载或实例化 ServiceLoader，抛出 `ServiceConfigurationError` 的可能原因有：

- 无法加载 provider
- provider 没有提供 `provider` 方法，或不是 service 类型，或者没有 provider 构造函数
- 提供了 `provider` 方法，但是返回的类型与 service 不兼容
- provider 类包含多个名为 "provider" 的public static 无参方法
- provider 提供了 `provider` 方法，但是该方法返回 null 或抛出异常
- provider 没有提供 provider 方法，其构造函数抛出异常

当读取 provider 配置文件，或者加载、实例化 provider 配置文件中的 provider 类，抛出 `ServiceConfigurationError` 可能原因有：

- provider 配置文件格式不对
- 读取 provider 文件抛出 `IOException`
- 无法加载 provider
- provider 与 service 类型不兼容，或者没有定义 provider 构造函数，或者无法实例化
