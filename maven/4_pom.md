# POM

- [POM](#pom)
  - [简介](#%e7%ae%80%e4%bb%8b)
  - [POM 文件内容](#pom-%e6%96%87%e4%bb%b6%e5%86%85%e5%ae%b9)
  - [Properties](#properties)
  - [配置 encoding](#%e9%85%8d%e7%bd%ae-encoding)
  - [Developers](#developers)

## 简介

POM (Project Object Model) 是 XML 文件，一般在项目的根目录，命名为 `pom.xml`.

POM 文件包含 Maven 用于构建goals所需的资源，包括源码目录，测试代码目录等。当执行特定的 task 或 goal，Maven 会搜索当前目录下的 POM 文件，读取POM获取配置信息，然后执行 goal，POM中部分配置信息如下：

- project dependencies
- plugins
- goals
- build profiles
- project version
- developers
- mailing list

所以，PO M描述的是构建什么，而不是如何去构建。如何构建取决于Maven build phases 和 goals。

每个 Maven 项目都有一个 `pom.xml`，如果有子模块，单个模块也各有一个 pom.xml,从而方便整个项目和各个模块的构建。

## POM 文件内容

下面是一个最小的POM文件：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.hello</groupId>
    <artifactId>java-web-crawler</artifactId>
    <version>1.0.0</version>
</project>
```

内容说明：
| 元素                       | 说明                                                                                                                                                                                                                                                                                                                                                      | 必需 |
| -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---- |
| project                    | POM 文件的顶层元素                                                                                                                                                                                                                                                                                                                                        | 是   |
| groupId:artifactId:version | 包在仓库中的位置                                                                                                                                                                                                                                                                                                                                          | 是   |
| modelVersion               | 当前使用的POM版本，POM版本要和Maven匹配，Version 4.0.0和Maven 2 和 3匹配                                                                                                                                                                                                                                                                                  | 是   |
| groupId                    | 项目或组织的识别号                                                                                                                                                                                                                                                                                                                                        | 是   |
| artifactId                 | 正在构建的项目名。构建的输出在Maven中被称为 artifact，一般是JAR,WAR或EAR文件                                                                                                                                                                                                                                                                              | 是   |
| packaging                  | This element indicates the package type to be used by this artifact (e.g. JAR, WAR, EAR, etc.). This not only means if the artifact produced is JAR, WAR, or EAR but can also indicate a specific lifecycle to use as part of the build process. The default value for the packaging element is JAR so you do not have to specify this for most projects. |
| version                    | 当前项目的版本号                                                                                                                                                                                                                                                                                                                                          |
| url                        | This element indicates where the project's site can be found. This is often used in Maven's generated dodumentation                                                                                                                                                                                                                                       |
| description                | This element provides a basic description of your project. This is often used in Maven's generated documentation                                                                                                                                                                                                                                          |

如果你的项目满足Maven 目录结构，并且没有额外的依赖包，上面的POM文件已足够用于构建。

在创建POM时，首先需要设置必须项：groupId, artifactId, version, packageing, classifier
these attributes help in uniquely identifying the proejct in repository.

## Properties

在 Maven 中，属性（Property）用于定义常量，在POM文件里的其他地方，可以通过 `${X}` 引用该属性，X为对应的属性。例如：

```xml
<project>
  ...
  <properties>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  </properties>
  ...
</project>
```

属性值可以分为5类：

1. `env.X` 

在变量前添加 `env.` 前缀，用于获得 shell 的环境变量。例如 `${evn.PATH}` 包含 PATH 环境变量。

> 在 Windows 中环境变量不区分大小写，但是 maven 查找属性值时区分大小写。例如，对 Window shell `%PATH%` 和 `%Path%` 返回相同的值，而Maven 则认为 `${evn.PATH}$` 和 `${evn.Path}$`不同。Maven 2.1.0 会将所有的系统变量转换为大写，以统一结果。

2. `project.x`

POM文件中的 `.` 用于引用特定路径下的元素。例如 `<project><version>1.0</version></project>` 可以通过 `${project.version}` 引用。

3. `settings.x`

在 `settings.xml` 文件中可以通过该方式引用特定元素。例如 `<settings><offline>false</offline></settings>` 可以通过 `${settings.offline}$` 引用。

4. Java 系统属性

所有可以通过 `java.lang.System.getProperties()` 获得的属性都可以作为 POM 属性引用，例如 `${java.home}$`。

5. `x`

POM 文件中通过 `<properties>` 元素定义的值。例如 `<properties><someVar>value</someVar></properties>` 值可以通过 `${someVar}` 引用。

## 配置 encoding

```xml
<project>
...
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
...
</project>
```

## Developers

```xml
<developers>
  <developer>
    <id>jdoe</id>
    <name>John Doe</name>
    <email>jdoe@example.com<email>
    <url>http://www.example.com/jdoe</url>
    <organization>ACME</organization>
    <organizationUrl>http://www.example.com</organizationUrl>
    <roles>
      <role>architect</role>
      <role>developer</role>
    </roles>
    <timezone>America/New_York</timezone>
    <properties>
      <picUrl>http://www.example.com/jdoe/pic</picUrl>
    </properties>
  </developer>
</developers>
```

|信息|说明|
|---|---|
|id|开发人员ID，如在公司中的ID|
|name|开发人员名称|
|email|开发人员邮箱|

说明：

- organization, organizationUrl: As you probably guessed, these are the developer's organization name and it's URL, respectively.
- roles: A role should specify the standard actions that the person is responsible for. Like a single person can wear many hats, a single person can take on multiple roles.
- timezone: A valid time zone ID like America/New_York or Europe/Berlin, or a numerical offset in hours (and fraction) from UTC where the developer lives, e.g., -5 or +1. Time zone IDs are highly preferred because they are not affected by DST and time zone shifts. Refer to the IANA for the official time zone database and a listing in Wikipedia.
- properties: This element is where any other properties about the person goes. For example, a link to a personal image or an instant messenger handle. Different plugins may use these properties, or they may simply be for other developers who read the POM.
