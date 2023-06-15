# maven-shade-plugin

2023-06-13
***
## 简介

maven-shade-plugin 用于将项目打包成一个 jar，包括可执行 jar。对命名重叠的依赖项，会进行 shade 操作（如重命名对应的包）。

Shade 插件只有一个 `shade:shade` goal，和 package phase 绑定。

基本配置：

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-shade-plugin</artifactId>
	<configuration>
		<!-- 配置 -->
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


## 可执行 JAR

打包成可执行 JAR，只需要指定 main 类，如下所示：

```xml
<configuration>
	<transformers>
		<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
			<mainClass>org.HelloWorld</mainClass>
		</transformer>
	</transformers>
</configuration>
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

- 使用 `<excludes>` 排除依赖项

```xml
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
```

- 使用 `<includes>` 指定要包含的 artifact

artifact 格式为 `groupId:artifactId[[:type]:classifier]`。可以使用 ? 和 * 通配符。为了更细粒度地控制包含依赖项中的哪些类，可以使用 artifact filter：

```xml
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
```

> 这里是显示了 `<configuration>` 部分

上面，对依赖项 `junit:junit` 只有指定的类和资源包含在 uber JAR 中。第二个过滤器在指定 artifact 处演示了通配符的使用，它移除了所有 JAR 的签名相关的文件。

- 除了手动指定过滤器，还可以自动删除所有项目不使用的依赖类，从而最小化最终的 uber JAR

```xml
<configuration>
	<minimizeJar>true</minimizeJar>
</configuration>
```

- 从版本 1.6 开始，`minimizeJar` 会考虑那些被标记为要包含的类

指定 include filter 会隐式排除该 artifact 所有没有指定的类。`<excludeDefaults>false<\excludeDefaults>` 会覆盖该行为，从而包含那些未指定的类：

```xml
<configuration>
	<minimizeJar>true</minimizeJar>
	<filters>
		<filter>
			<artifact>log4j:log4j</artifact>
			<includes>
				<include>**</include>
			</includes>
		</filter>
		<filter>
			<artifact>commons-logging:commons-logging</artifact>
			<includes>
				<include>**</include>
			</includes>
		</filter>
		<filter>
			<artifact>foo:bar</artifact>
			<excludeDefaults>false</excludeDefaults>
			<includes>
				<include>foo/Bar.class</include>
			</includes>
		</filter>
	</filters>
</configuration>
```

## 参考

- https://maven.apache.org/plugins/maven-shade-plugin/