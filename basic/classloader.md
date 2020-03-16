# ClassLoader

类加载器（ClassLoader）是负责加载类的对象。

从java虚拟机的角度讲，只有两种类加载器：

- 一种是启动类加载器（Bootstrap ClassLoader），这个类加载器使用c++实现的，是虚拟机的一部分；
- 另一类是所有其他类加载器，这些类加载器由java语言实现，独立于虚拟机外部，并且全都继承自抽象类。

从开发人员的角度看，类加载器还可以划分为3种系统类加载器：

- 启动类加载器（Bootstrap ClassLoader），负责加载存放在<JAVA_HOME>/lib目录中的，或者被 `-Xbootclasspath` 参数所指定的路径中的，并且是虚拟机识别的（仅按照文件名识别，如rt.jar，名字不符的类库即使放在lib目录中也不会被加载）类库加载到虚拟机中内存中。启动类加载器无法被java程序直接引用，用户在编写自定义类加载器是，如果需要把加载请求委派给引导类加载器，那直接使用null代替即可。
- 扩展类加载器（Extension ClassLoader）：这个类加载器有sun.misc.Launcher$ExtClassLoader实现，负责加载<JAVA_HOME>/lib/ext目录中的，或者被java.ext.dirs系统变量所指定的路径中的所有类库，开发者可以直接使用扩展类加载器。
- 应用类加载器（Application ClassLoader）：这个类加载器由sun.misc.Launcher$AppClassLoader实现。由于这个类加载器是ClassLoader中的getSystemClassLoader（）方法的返回值，所以也称它为系统类加载器（System ClassLoader）。他负责加载用户类路径（ClassPath）上所指定的类库，开发者可以直接使用这个类加载器，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。对此，如果有必要开发者可以加入自己定义的类加载器。

一般对于我们java程序员来说，类的加载使用的是双亲委派模型，即当一个类需要加载时，会将类传给Application ClassLoader，但是Application ClassLoader并不会加载，不管它是否能加载，而是传给它的"父类" Extension ClassLoader，Extension ClassLoader同样不会加载，同样传给 Bootstrap ClassLoader（注意不是我们常说的那种父类，但是可以这样理解）,这时Bootstrap ClassLoader会判断它是否能加载，能加载就直接加载了，不能加载就传给Extension ClassLoader，Extension ClassLoader同样的判断是否能加载，能加载就直接加载，不能加载就传给Application ClassLoader，然后Application ClassLoader也判断能否加载，如果还是不能加载应该就是报ClassNotFoundException了。这就是双亲委托模型的简单理解了。
    对于上面的"父类"为什么要打引号，因为它们并不是真的像java中继承的关系，而是组合的关系，即在"子类"中存在一个成员变量指向"父类"的引用。
    所以AE对DF错了。
    对于C，很容易理解，因为我们知道一个类只需要加载一次就够了，所以要保证线程安全。
    难点就在B了，其实也好理解，就是体现双亲委托模型的优势的时候了，之所以使用双亲委托机制是为了保证java程序的稳定运作，比如当你使用的不是双亲委托模型的时候，然后刚好开发者又定义了一个类，一个java.lang.String这样一个类，如果不使用双亲委托模型，当类加载的时候就有可能会加载开发者定义的String类，这导致了java代码的一片混乱，可读性极差。（PS：但这并不意味着类加载器只要双亲委托模型就行了，没有完美的模型，只有最合适的模型，有不同的需求使用不同的模型。比如破坏双亲委派模型，有兴趣的牛友可以自行了解），所以可以这么说，不同的类加载器加载出来的类是不一样的，不同的类加载器加载同一个类会在方法区产生两个不同的类，彼此不可见，并且在堆中生成不同的Class实例。对于接口，其实就是一个特殊的类，和类一样，在堆中产生不同的class对象。
    纯手打，个人理解，欢迎大佬指出错误。