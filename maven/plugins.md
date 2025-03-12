# Maven Plugin

|插件|功能|
|---|---|
|maven-jar-plugin|JAR的构建和签名，不过只编译打包 `src/main/java` 和 `src/main/resources/`目录下的文件，不包含JAR依赖项|
|maven-assembly-plugin|提取包括依赖 JARs 在内的所有类，可用于构建可执行 JAR。只适用于依赖项较少的项目，对包含许多依赖项的大型项目，无法处理类名称冲突的问题|
|maven-shade-plugin|将所有依赖项打包为一个 uber-JAR。可用于构建可执行 JAR。能解决命名冲突问题|



