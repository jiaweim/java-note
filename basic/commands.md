# Java Commands

| 命令 | 功能               |
| ---- | ------------------ |
| -Xms | 设置最小虚拟机内存 |
| -Xmx | 设置最大虚拟机内存 |
| -Xss | 设置线程堆栈大小   |


运行 jar 方法：

```cmd
java -jar xxx.jar
```

- `--class-path classpath`, `-classpath classpath` 或 `-cp classpath`

指定搜索类文件位置，`classpath` 是由分号（;）分隔的目录，JAR文件和ZIP文件列表。

例如指定主类运行：

```cmd
java -cp xxx.jar xxx.com.mainClass
```

这里 `-cp xxx.jar` 表示把 xxx.jar 加入到 classpath，这样 class loader 就会在其中查找匹配的类。

## Reference

- [Oracle docs](https://docs.oracle.com/javase/10/tools/java.htm#JSWOR624)
