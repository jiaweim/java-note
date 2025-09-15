# central-publishing-maven-plugin

## 简介

Central Publisher 门户支持通过 Maven 分布。

> [!NOTE]
>
> 该插件不会生成发布所需的所有内容，因此需要提前准备 javadoc, sources, jar 和 GPG 签名文件等。

pom 设置：

```xml
<build>
    <plugins>
        <plugin>
          <groupId>org.sonatype.central</groupId>
          <artifactId>central-publishing-maven-plugin</artifactId>
          <version>0.8.0</version>
          <extensions>true</extensions>
          <configuration>
            <publishingServerId>central</publishingServerId>
          </configuration>
        </plugin>
    </plugins>
</build>
```

## 证书

需要使用用户 token 令牌配置 Maven 的 `settings.xml` 文件。通过 [generating a user token](https://central.sonatype.org/publish/generate-portal-token/) 可以获得个人里令牌。

settings.xml 设置：

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username><!-- your token username --></username>
      <password><!-- your token password --></password>
    </server>
  </servers>
</settings>
```

