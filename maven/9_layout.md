# 标准目录布局

采用统一的目录布局方便熟悉 Maven 项目的用户马上熟悉项目内容。

| 目录                 | 内容                     |
| -------------------- | ------------------------ |
| `src/main/java`      | 源码                     |
| `src/main/resources` | 资源                     |
| `src/main/filters`   | 资源过滤器文件           |
| `src/main/webapp`    | Web 应用源码             |
| `src/test/java`      | 测试源码                 |
| `src/test/resources` | 测试资源                 |
| `src/test/filters`   | 测试资源过滤器文件       |
| `src/it`             | 集成测试（主要用于插件） |
| `src/assembly`       | 程序集描述符             |
| `src/site`           | Site                     |
| `LICENSE.txt`        | 项目 license             |
| `NOTICE.txt`         | 注意项                   |
| `README.txt`         | 项目 readme              |

顶层目录包含描述项目的 `pom.xml` 文件和方面哦用户可以立刻阅读的文本文件 `README.txt` 和 `LICENSE.txt` 等。

该结构只有两个子目录：`src` 和 `target`。还可能包含其它一些元数据，如 `CSV`, `.git` 或 `.svn`，以及多模块项目的子模块目录。

`target` 目录包含所有构建输出。

`src` 目录包含用于构建项目的所有源文件、site 等。对每种类型包含子目录: `main` 对应 main 构建，`test` 对应单元测试，`site` 对应文档等。

在源码目录包含对应语言的目录，如 `java`，如果有其它语言源码参与构建，也创建对应目录，如 `src/main/antlr` 包含 Antlr 定义文件。
