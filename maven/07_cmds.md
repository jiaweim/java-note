- [简介](#%e7%ae%80%e4%bb%8b)
  - [Maven 命令结构](#maven-%e5%91%bd%e4%bb%a4%e7%bb%93%e6%9e%84)
- [实例](#%e5%ae%9e%e4%be%8b)
  - [安装本地 jar 到本地仓库](#%e5%ae%89%e8%a3%85%e6%9c%ac%e5%9c%b0-jar-%e5%88%b0%e6%9c%ac%e5%9c%b0%e4%bb%93%e5%ba%93)
# 简介
Maven 包含许多命令，其命令由 build life cycles, build phases and build goals 等组成。

## Maven 命令结构
Maven 命令包含两部分：
- mvn
- 一个或多个 build life cycles, build phases or build goals

Maven 包含三个主要的 build life cycles:
- clean
- default
- site

每个 build life cycles 里面都包含多个 build phases, 每个 build phase 包含多个 build goals.

# 实例
## 安装本地 jar 到本地仓库
命令：
```
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