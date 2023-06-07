# FXSampler

## 简介

FXSampler 是 ControlsFX 的子项目，用于 JavaFX 可视化项目的演示。

首先，需要一个实现 `fxsampler.FXSamplerProject` 接口的类：

```java
import fxsampler.FXSamplerProject;
import fxsampler.model.WelcomePage;
 
public class CalendarFXSamplerProject implements FXSamplerProject {
 
   @Override
   public String getProjectName() {
      return "CalendarFX";
   }
 
   @Override
   public String getSampleBasePackage() {
      return "com.calendarfx.demo";
   }
 
   @Override
   public WelcomePage getWelcomePage() {
      return new CalendarFXSamplerWelcome();
   }
}
```

说明：

- `getProjectName()` 指定项目名称
- `getSampleBasePackage()` 指定示例的基础包
- `getWelcomePage()` 指定欢迎页面

## 欢迎页面

欢迎页面是可选的，它允许你为 sampler 指定一个标题和一个 UI，在 sampler 应用启动时呈现给用户。例如，CalendarFX sampler 显示一个简单的 “about message”：

![[Pasted image 20230606222100.png]]

## Base Package

FXSampler 使用 "base package" 来查找 sample 类。FXSampler 将 base package 中找到 sample 类放在左侧 tree 结构的顶层，在 sub packages 找到的 sample 类则放在子节点下。下面是 CalendarFX sampler 项目的结构：

![[Pasted image 20230606222434.png]]

CalendarFX 的 base package 为 `com.calendarfx.demo`，其中 `HelloDayEntryView` 在 `entries` 包中，因此在 Entries 子节点下显示。

![[Pasted image 20230606222613.png]]

可以看到，所有的 sample 都以 "Hello" 开头，这是 ControlsFX 采取的约定，并非强制。

## Sample

FXSampler 将实现 `fxsampler.Sample` 接口的类识别 sample。但是扩展 `fxsampler.SampleBase` 更好，该类扩展了 `Application` 接口，因此每个 sample 可以单独运行。

一个 sample 包含如下信息：

- sample 名称，如 "Day Entry View"
- 对 sample 或 control 的简短描述
- 所属 project
- project 版本，如 "8.4.0"
- 一个包含自定义控件的 `Node`，会在 pane 中间显示
- 与 sample 交互的控制面板（右侧，通常是 `PropertySheet`）
- 控制面板 divider handle 的位置（基于控制面板所需宽度进行设置）
- JavaDocs 的 URL
- sample 相关样式表的 URL
- sample 源码的 URL
- 一个 flag，用于设置是否隐藏该 sample，用于隐藏还在进行中的 sample

## Launcher

要运行 sampler 项目，还需要一个包含 `main()` 方法并扩展 `Application` 类，即 `fxsampler.FXSampler`。`CalendarFX` 中的实现：

```java
package com.calendarfx.demo;
 
import fxsampler.FXSampler;
import javafx.stage.Stage;
 
public class CalendarFXSampler extends FXSampler {
 
   @Override
   public void start(Stage primaryStage) throws Exception {
      super.start(primaryStage);
   }
 
   public static void main(String[] args) {
      FXSampler.main(args);
   }
}
```

上面的 launcher 没有实例化任何 sampler 项目。那么它如何找到要运行的 sample 项目呢？

## Service Provider

`FXSampler` 通过 service provider 查找 sampler 项目。在 META-INF/services 目录创建 `fxsampler.FXSamplerProject` 文件，在文件中 sampler 项目的完整类名。

## 可执行 jar

将示例打包成可执行 jar，Maven 配置如下：

```xml
<build>
   <plugins>
      <plugin>
         <artifactId>maven-assembly-plugin</artifactId>
         <version>2.4</version>
         <configuration>
            <finalName>sampler-demo</finalName>
            <appendAssemblyId>false</appendAssemblyId>
            <descriptorRefs>
               <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
            <archive>
               <manifest>
                  <mainClass>com.calendarfx.demo.CalendarFXSampler</mainClass>
               </manifest>
            </archive>
         </configuration>
         <executions>
            <execution>
               <id>make-samples</id>
               <phase>package</phase>
               <goals>
                  <goal>single</goal>
               </goals>
            </execution>
         </executions>
      </plugin>
   </plugins>
</build>
```


## 参考

- https://dlsc.com/2017/03/02/fxsampler/