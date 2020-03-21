- [总结](#%e6%80%bb%e7%bb%93)
- [maven-source-plugin](#maven-source-plugin)
  - [使用方法](#%e4%bd%bf%e7%94%a8%e6%96%b9%e6%b3%95)
    - [和 build phase 绑定使用](#%e5%92%8c-build-phase-%e7%bb%91%e5%ae%9a%e4%bd%bf%e7%94%a8)
    - [使用 profile](#%e4%bd%bf%e7%94%a8-profile)
  - [自定义配置](#%e8%87%aa%e5%ae%9a%e4%b9%89%e9%85%8d%e7%bd%ae)
- [maven-javadoc-plugin](#maven-javadoc-plugin)
- [maven-shade-plugin](#maven-shade-plugin)
  - [Goal](#goal)
  - [打包成可执行 JAR](#%e6%89%93%e5%8c%85%e6%88%90%e5%8f%af%e6%89%a7%e8%a1%8c-jar)
  - [Resource Transformaters](#resource-transformaters)
    - [ManifestResourceTransformer](#manifestresourcetransformer)
  - [选择打包内容](#%e9%80%89%e6%8b%a9%e6%89%93%e5%8c%85%e5%86%85%e5%ae%b9)

# 总结

|插件|功能|
|---|---|
|maven-jar-plugin|JAR的构建和签名，不过只编译打包 `src/main/java` 和 `src/main/resources/`目录下的文件，不包含JAR依赖项|
|maven-assembly-plugin|提取包括依赖 JARs 在内的所有类，可用于构建可执行 JAR。只适用于依赖项较少的项目，对包含许多依赖项的大型项目，无法处理类名称冲突的问题|
|maven-shade-plugin|将所有依赖项打包为一个 uber-JAR。可用于构建可执行 JAR。能解决命名冲突问题|

# maven-source-plugin
`maven-source-plugin` 插件将源码打包成 jar 文件，生成的源码文件默认在项目的 *target* 目录。

该插件包括如下 5 个 goals:
|goal|功能|
|---|----|
|source:aggregate|聚合项目中的所有源码|
|source:jar|将项目main源码打包为 jar 文件|
|source:test-jar|将项目test源码打包为 jar 文件|
|source:jar-no-fork|类似于 jar，但是绑定在构建生命周期上，不分叉|
|source:test-jar-no-fork|同上|

## 使用方法
通过该插件实现打包源码有两种实现方式：和特定的 phase 绑定，或者以 profile 实现。

**source:jar-no-fork** 和 **source:test-jar-no-fork** 更适合绑定到构建生命周期。

打包源码命令：
```
mvn source:jar
```

打包测试源码命令：
```
mvn source:test-jar
```

### 和 build phase 绑定使用
```xml
<build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-source-plugin</artifactId>
        <version>3.0.1</version>
        <executions>
          <execution>
            <id>attach-sources</id>
            <phase>verify</phase>
            <goals>
              <goal>jar-no-fork</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
 </build>
```
这里和 `verify` phase 绑定，因为 `verify` phase 在 `install` phase 之前执行，这样可以保证在 install 之前源码已经打包好。

### 使用 profile
```xml
<profiles>
    <profile>
      <id>release</id>
      <build>
        <plugins>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
            <version>3.0.1</version>
            <executions>
              <execution>
                <id>attach-sources</id>
                <goals>
                  <goal>jar-no-fork</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    </profile>
</profiles>
```

## 自定义配置
```xml
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-source-plugin</artifactId>
        <version>3.0.1</version>
        <configuration>
          <outputDirectory>/absolute/path/to/the/output/directory</outputDirectory>
          <finalName>filename-of-generated-jar-file</finalName>
          <attach>false</attach>
        </configuration>
      </plugin>
    </plugins>
  </build>
  ...
</project>
```
生成的 jar 文件名，对 main sources 为 fileName + "-sources"，对 test sources 为 fileName+"-test-sources"。

生成的 jar 文件在 outputDirectory 目录。

attach  parameter specifies whether the java sources will be attached to the artifact list of the project.

# maven-javadoc-plugin
Apache Maven Javadoc Plugin 使用 Javadoc 工具生成指定项目的 javadocs。

# maven-shade-plugin
该插件用于将项目打包成一个 jar，可用于打包可执行 jar。并且对命名重叠的依赖项，会进行 shade 操作（如重命名对应的包）。

Shade 插件只有一个 shade:shade goal，和 package phase 绑定，用于创建 shaded jar。

## Goal
```xml
<project>
...
<build>
<!-- To define the plugin version in your parent POM -->
<pluginManagement>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-shade-plugin</artifactId>
			<version>3.2.1</version>
		</plugin>
...
	</plugins>
</pluginManagement>
<!-- To use the plugin goals in your POM or parent POM -->
<plugins>
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-shade-plugin</artifactId>
		<version>3.2.1</version>
</plugin>
...
</plugins>
</build>
...
</project>
```

## 打包成可执行 JAR
打包成可执行 JAR
只需要指定 main 类即可，如下所示：
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.2.0</version>
  <configuration>
	<transformers>
		<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
			<mainClass>org.HelloWorld</mainClass>
		</transformer>
	</transformers>
  </configuration>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## Resource Transformaters
如果没有重叠（如不同版本的相同包），将多个包合并为一个 JAR 很容易。否则，就需要以特定的方式将这些 JARs 合并，Resource transformers 就是干这事。

org.apache.maven.plugins.shade.resource 中的 transformers:
|Transformer|功能|
|---|----|
|ApacheLicenseResourceTransformer|Prevents license duplication|
|ApacheNoticeResourceTransformer|Prepares merged NOTICE|
|AppendingTransformer|Adds content to a resource|
|ComponentsXmlResourceTransformer|Aggregates Plexus components.xml|
|DontIncludeResourceTransformer|Prevents inclusion of matching resources|
|GroovyResourceTransformer|Merges Apache Groovy extends modules|
|IncludeResourceTransformer|Adds files from the project|
|ManifestResourceTransformer|Sets entries in the MANIFEST|
|PluginXmlResourceTransformer|Aggregates Mavens plugin.xml|
|ResourceBundleAppendingTransformer|Merges ResourceBundles|
|ServicesResourceTransformer|Relocated class names in META-INF/services resources and merges them|
|XmlAppendingTransformer|Adds XML content to an XML resource|

### ManifestResourceTransformer
用于设置 MANIFEST 中的内容：
- Main-Class 用于设置 app.main.class 属性
- X-Compile-Source-JDK 用于设置 maven,compile.source 属性值
- X-Compile-Target-JDK 用于设置maven.compile.target 属性值

## 选择打包内容
为了控制 JAR 文件大小，可以通过如下方式指定包含在JAR文件里的内置:
```xml
<project>
    ...
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <artifactSet>
                                <excludes>
                                    <exclude>classworlds:classworlds</exclude>
                                    <exclude>junit:junit</exclude>
                                    <exclude>jmock:*</exclude>
                                    <exclude>*:xml-apis</exclude>
                                    <exclude>org.apache.maven:lib:tests</exclude>
                                    <exclude>log4j:log4j:jar:</exclude>
                                </excludes>
                            </artifactSet>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    ...
</project>
```

也可以通过 `<includes>` 指定想要包含的内容。artifact 指定格式为 `groupId:artifactId[[:type]:classifier]`。可以通过 ? 和 * 通配符。如下所示：

```xml
<execution>
    <phase>package</phase>
    <goals>
        <goal>shade</goal>
    </goals>
    <configuration>
        <filters>
            <filter>
                <artifact>junit:junit</artifact>
                <includes>
                    <include>junit/framework/**</include>
                    <include>org/junit/**</include>
                </includes>
                <excludes>
                    <exclude>org/junit/experimental/**</exclude>
                    <exclude>org/junit/runners/**</exclude>
                </excludes>
            </filter>
            <filter>
                <artifact>*:*</artifact>
                <excludes>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                </excludes>
            </filter>
        </filters>
    </configuration>
</execution>
```

上面，对依赖项 `junit:junit` 只有指定的类和资源包含在 uber JAR 中。第二个过滤器在指定 artifact 处演示了通配符的使用，它移除了所有JAR的签名相关的文件。

另外，还支持自动移除所有项目不依赖的类，从而最小化 uber JAR：
```xml
<execution>
    <phase>package</phase>
    <goals>
        <goal>shade</goal>
    </goals>
    <configuration>
        <minimizeJar>true</minimizeJar>
    </configuration>
</execution>
```
