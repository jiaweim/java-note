# static Logger

## 简介

static logger 对类的所有实例，只有一个变量引用，而 instance logger 则对类的每个实例，都有一个变量引用。如果一个简单的类实例化上千次，则其资源消耗会很显著。

但是，日志系统框架（如 log4j, logback）为应用服务器中的每个运行程序提供不同的 logger context。因此，即使服务器中只部署了单个 log4j.jar 或 logback-classic.jar，日志系统会区分不同应用，为不同应用提供不同的 logger context。

具体来说，每次通过调用 LoggerFactory.getLogger() 获得 logger，底层的日志系统会返回一个适合当前应用的 logger 实例。即，在相同程序的，相同日志名获得相同的 logger，而在不同应用，相同日志名或得到不同的 logger。

如果 logger 是 static 的，那么当托管类被加载到内存中，它只会被创建一次。如果托管类只在一个应用中使用，那么没有任何问题。如果托管类在不同应用中共享，则共享类的所有实例都将输出日志到最先载入共享类的 logger context，这种不可预测的行为往往不为人所喜。

SL4J API的 non-native 实现，即 slf4j-log4j12，log4j 的仓库选择器无法根据环境选择合适的 logger。 SLF4J的原生实现，如 logback-classic，则可以按照预期的选择合适的仓库。


我们曾经建议将 logger 声明为实例变量而不是静态变量。但是经过进一步分析，我们不再推荐哪一种方法。

## 声明为 static

**优点：**

1. 普遍的使用模式
2. CPU 开销更少：logger 对每个类只初始化一次
3. 内存开销更少：logger 对每个类只有一个引用

**缺点：**

1. 对应用程序之间的共享库，无法利用储存库选择器。如果每个应于都有一个 SLF4J provider 和底层日志框架，则每个应用的日志环境依然是独立的。
2. 在IOC中不好使用。

## 声明为实例变量

**优点：**

1. 不同应用共享的库，也可以利用储存库选择器。但是只对 logback-classic 框架有效。存储库选择器在 SLFJ+log4j 中不能使用。
2. IOC友好

**缺点：**

1. 相对声明为 static 来说，使用较少
2. CPU开销较大：对类的每个实例，需要一个 logger
3. 内存开销较大