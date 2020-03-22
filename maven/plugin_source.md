# maven-source-plugin

- [maven-source-plugin](#maven-source-plugin)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [使用方法](#%e4%bd%bf%e7%94%a8%e6%96%b9%e6%b3%95)
    - [和 build phase 绑定使用](#%e5%92%8c-build-phase-%e7%bb%91%e5%ae%9a%e4%bd%bf%e7%94%a8)
    - [使用 profile](#%e4%bd%bf%e7%94%a8-profile)
  - [自定义配置](#%e8%87%aa%e5%ae%9a%e4%b9%89%e9%85%8d%e7%bd%ae)

***

## 简介

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

```cmd
mvn source:jar
```

打包测试源码命令：

```cmd
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
