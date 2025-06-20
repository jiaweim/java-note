# maven-gpg-plugin

## 简介

该插件使用 GnuPG 签名项目的所有 artifacts。

## 使用

使用 GnuPG 为项目的所有 artifacts 签名。

需要先配置好 GnuPG 的默认密钥。并将 `gpg` 添加到环境变量。

首先将 gpg 插件添加到 pom.xml 文件：

```xml
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-gpg-plugin</artifactId>
        <version>3.2.7</version>
        <executions>
          <execution>
            <id>sign-artifacts</id>
            <phase>verify</phase>
            <goals>
              <goal>sign</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  ...
</project>
```

理想情况下，如果在工作站上调用，应该依赖 gpg-agent 收集密码，这样就不会有任何机密信息进入终端历史记录或磁盘上任何文件。对五

## 参考

- https://maven.apache.org/plugins/maven-gpg-plugin/