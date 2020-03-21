|命令|缩写命令|功能|
|---|---|---|
|`--module-path`|`-p`|指定模块路径|
|`--module modulename/classname`|`-m`|指定主类|

# Require
`java.lang` 包放在 `java.base` module 中，默认 required.

# Export

# open
`open` 关键字可以让 runtime 访问对应包中所有的类型和成员，也可以通过 reflection 访问私有成员，例如：
```java
module test{
    opens cn.ac.hello;
}
```
这样，那些依赖于 reflection 的包，如JAXB就可以正常工作。

还可以之间将module声明为open:
```java
open module test{
    requires java.xml.bind;
}
```
这样 `exports` 的所有包都是 open 的。

# service
service 在 java 9 之前通过在 `META-INF/services` 目录添加包含实现类的文本文件实现，module 系统提供了一个更好的方式，不需要文本文件，直接在 module 中添加声明。

模块通过 `provides` 语句列出service接口（可以在任意模块中定义）的实现（必须在该模块中指定）。下面是 `jdk.security.auth` 模块的例子：
```java
module jdk.security.auth {
    ...
    provides javax.security.auth.spi.LoginModule with
        com.sun.security.auth.module.Krb5LoginModule,
        com.sun.security.auth.module.UnixLoginModule,
        com.sun.security.auth.module.JndiLoginModule,
        com.sun.security.auth.module.KeyStoreLoginModule,
        com.sun.security.auth.module.LdapLoginModule,
        com.sun.security.auth.module.NTLoginModule;
}
```
和使用 `META-INF/services` 文件是等价的。

使用服务的模块通过 `uses` 语句：
```java
module java.base {
    ...
    uses javax.security.auth.spi.LoginModule;
}
```
