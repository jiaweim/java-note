# Gradle 命令


|gradle 指令|功能|
|---|---|
|gradle clean|清空 build 目录|
|gradle classes|编译业务代码和配置文件|
|gradle test|编译测试代码，生成测试报告|
|gradle build|构建项目|
|gradle build -x test|跳过测试构建|

!!! note
    gradle 指令需要在包含 build.gradle 文件的目录中执行。


- 帮助命令
 
```gradle
gradle --help
```

- 查看版本

```gradle
gradle -v
```

- 执行特定任务

```gradle
gradle [taskname]
```

- 构建

```gradle
gradle build
```

- 跳过测试构建

```gradle
gradle build -x test
```

- 忽略前面失败的任务继续执行

```powershell
gradle build --continue
```

- 生成 build 运行报告

```powershell
gradle build --profile
```

- 显示任务间的依赖关系

```powershell
gradle tasks --all
```

