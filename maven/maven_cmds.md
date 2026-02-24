# Maven 命令

2026-02-24
@author Jiawei Mao
***

## 简介

Maven 包含许多命令，其命令由 build life cycles, build phases and build goals 等组成。Maven 命令包含两部分：

- `mvn`
- 一个或多个 build life cycles, build phases or build goals

Maven 包含三个主要的 build life cycles:
- clean
- default
- site

每个 build life cycles 包含多个 build phases, 每个 build phase 包含多个 build goals.

**install**

```sh
mvn install
```

该命令构建项目，并将生成的JAR文件复制到 local Maven repository。实际上，该命令会执行 install 前面的所有 phases。

**执行多个 life cycle 或 phases**

```sh
mvn clean install
```

该命令首先执行 clean-life-cycle, 即删除 Maven 输出目录所有的编译类，然后执行 install-build-phase。

**指定单个 goal**

```sh
mvn dependency:copy-dependencies	
```

该命令执行dependency-build-phase下的 copy-dependencies goal。

## 示例

### 安装本地 jar 到本地仓库

命令：

```sh
mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> \
  -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>
```

如果有 pom 文件，可以使用如下命令：

```
mvn install:install-file -Dfile=<path-to-file> -DpomFile=<path-to-pomfile>
```

安装 JAR：
```
mvn deploy:deploy-file \
    -DgroupId=com.yourname.jgoodies \
    -DartifactId=jgoodies-forms \
    -Dversion=1.50 \
    -Dfile=/path/to/jgoodies-1.50.jar \
    -Dpackaging=jar \
    -Durl=file://path/to/your/local/repository
```

安装源码：
```
mvn deploy:deploy-file \
    -DgroupId=com.yourname.jgoodies \
    -DartifactId=jgoodies-forms \
    -Dversion=1.50 \
    -Dfile=/path/to/jgoodies-sources.jar \
    -Dpackaging=jar \
    -Durl=file://path/to/your/local/repository \
    -Dclassifier=sources
```

安装 javadoc:
```
mvn deploy:deploy-file \
    -DgroupId=com.yourname.jgoodies \
    -DartifactId=jgoodies-forms \
    -Dversion=1.50 \
    -Dfile=/path/to/jgoodies-javadoc.jar \
    -Dpackaging=jar \
    -Durl=file://path/to/your/local/repository \
    -Dclassifier=javadoc
```