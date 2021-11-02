# Maven Profile

- [Maven Profile](#maven-profile)
  - [简介](#简介)
  - [定义](#定义)
  - [外部文件中的 profiles](#外部文件中的-profiles)
  - [POM 文件中定义 profiles](#pom-文件中定义-profiles)
  - [激活 Profile](#激活-profile)
  - [CMD 命令](#cmd-命令)
  - [Maven 设置](#maven-设置)
  - [环境变量](#环境变量)
  - [OS 设置](#os-设置)
  - [文件存在与否](#文件存在与否)
  - [默认 profile](#默认-profile)
  - [禁用 profile](#禁用-profile)

***

## 简介

配置（Profile）功能用于在不同环境下 (如不同JVM，不同databse，不同OS等) 使用不同的配置。

一个 profile 包含一系列的配置，用于覆盖 Maven 构建的默认值，从而实现自定义构建。

## 定义

profile 可以在三个地方定义：

| 作用域 | 位置                                                     |
| ------ | -------------------------------------------------------- |
| 项目   | 在 POM 文件定义 `pom.xml`                                |
| 用户   | 在 Maven-settings 定义(`%USER_HOME%/.m2/settings.xml`)   |
| 全局   | global maven-settings (%{maven.home}%/conf/settings.xml) |

## 外部文件中的 profiles

在外部文件中定义的 profiles（包括 `settings.xml` 或 `profiles.xml`）严格来说不是 protable。

## POM 文件中定义 profiles

推荐在pom.xml中定义profiles，这样pom.xml会被发布到 repository 中，从而被其他人使用。并且settings.xml中可以定义的项很少：

- repositories
- pluginRepositories
- properties

pom.xml 中可以修改如下的值：

- repositories
- pluginRepositories
- dependencies
- plugins
- properties
- modules
- reporting
- dependencyManagement
- distributionManagement
- a subset of the `<build>` element, which consists of:
  - defaultGoal
  - resources
  - testResources
  - finalName

## 激活 Profile

激活 profile 的方式有多种：

- CMD 命令显式激活
- Through Maven settings
- 环境变量
- OS settings
- 文件的存在与否

## CMD 命令

Profile 可以使用 `-P` CLI 命令选项显式指定。

该选项的参数为逗号分隔的 profile-ids。指定该选项后，除了 `settings.xml` 中 `activeProfiles` 指定的 profile，这些额外的 profiles 也被激活。

```cmd
mvn groupId:artifactId:goal -P profile-1,profile-2
```

## Maven 设置

Profile 也可以通过 Maven settings 中的 `<activeProfiles>` 设置激活。该部分包含一系列 `activeProfile`元素，每个包含一个 profile-id，如下：

```xml
<settings>
  ...
  <activeProfiles>
    <activeProfile>profile-1</activeProfile>
  </activeProfiles>
  ...
</settings>
```

`activeProfiles` 标签内的 profiles 默认激活。

## 环境变量

Profiles 可以根据构建的环境变量自动激活。在 profile 中的 `<activation>` 指定，目前只支持 JDK 版本匹配，例如，如果JDK版本以 "1.4" 开头（如 "1.4.0_08, "1.4"）激活 profile:

```xml
<profiles>
  <profile>
    <activation>
      <jdk>1.4</jdk>
    </activation>
    ...
  </profile>
</profiles>
```

还可以指定版本范围：

```xml
<profiles>
  <profile>
    <activation>
      <jdk>[1.3,1.6)</jdk>
    </activation>
    ...
  </profile>
</profiles>
```

## OS 设置

根据OS settings 激活：

```xml
<profiles>
  <profile>
    <activation>
      <os>
        <name>Windows XP</name>
        <family>Windows</family>
        <arch>x86</arch>
        <version>5.1.2600</version>
      </os>
    </activation>
    ...
  </profile>
</profiles>
```

下面的 profile 在系统属性 "debug" 指定为任意值时激活：

```xml
<profiles>
  <profile>
    <activation>
      <property>
        <name>debug</name>
      </property>
    </activation>
    ...
  </profile>
</profiles>
```

下面的 profile 在未定义 "debug" 系统属性时激活：

```xml
<profiles>
  <profile>
    <activation>
      <property>
        <name>!debug</name>
      </property>
    </activation>
    ...
  </profile>
</profiles>
```

下面在未定义 "debug" 系统属性或其值不是 "true" 时激活：

```xml
<profiles>
  <profile>
    <activation>
      <property>
        <name>debug</name>
        <value>!true</value>
      </property>
    </activation>
    ...
  </profile>
</profiles>
```

此时通过如下两种方式都可以激活该 profile:

```cmd
mvn groupId:artifactId:goal
mvn groupId:artifactId:goal -Ddebug=false
```

下面则在系统属性 "environment" 的值为 "test"时激活：

```xml
<profiles>
  <profile>
    <activation>
      <property>
        <name>environment</name>
        <value>test</value>
      </property>
    </activation>
    ...
  </profile>
</profiles>
```

> **NOTE**： 系统变量如 `FOO` 可以通过 `env.FOO` 引用。在 Windows 中所有的系统变量都被转换为大写。

## 文件存在与否

例如，如果文件 `target/generated-sources/axistools/wsdl2java/org/apache/maven` 不存在激活 profile:

```xml
<profiles>
  <profile>
    <activation>
      <file>
        <missing>target/generated-sources/axistools/wsdl2java/org/apache/maven</missing>
      </file>
    </activation>
    ...
  </profile>
</profiles>
```

从 Maven 2.0.9 开始，可以插入 `<exists>` 和 `<missing>` 标签。支持的变量包括系统属性（如 `${user.home}$`）和环境变量（如 `${env.HOME}$`），而POM文件中自定义的变量则不支持。

## 默认 profile

Profile 可以设置为默认激活：

```xml
<profiles>
  <profile>
    <id>profile-1</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    ...
  </profile>
</profiles>
```

该 profile 默认激活，除非在相同 POM 文件中使用上面其它的方式指定了激活的 profile。当一个 profile 通过命令行或其配置激活，其它 profile 自动禁用。

## 禁用 profile

从 Maven 2.0.10 开始，在命令行中可以通过前缀 '!' 或 '-' 禁用 profile，例如：

```cmd
mvn groupId:artifactId:goal -P !profile-1,!profile-2
```

通过该方式可以禁用 activeByDefault 定义的 profile和通过 activation 配置激活的 profiles。
