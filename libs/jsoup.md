# Jsoup

## 从 URL 载入

```java
Document doc = Jsoup.connect("http://example.com/").get();
String title = doc.title();
```

`connect(String url)` 方法创建一个 `Connection`，`get()` 方法解析并获取 HTML 文件。在获取 HTML 文件时出错抛出 `IOException`。

`Connection` 接口支持链式方法操作：

```java
Document doc = Jsoup.connect("http://example.com")
  .data("query", "Java")
  .userAgent("Mozilla")
  .cookie("auth", "token")
  .timeout(3000)
  .post();
```

## 从文件载入

从文件载入 HTML 内容。

```java
File input = new File("/tmp/input.html");
Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
```

## 提取数据

### 使用 DOM 方法浏览文档

在获得 `Document` 后，可以使用类似 DOM 的方法获取文档信息：

```java
File input = new File("/tmp/input.html");
Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

Element content = doc.getElementById("content");
Elements links = content.getElementsByTag("a");
for (Element link : links) {
  String linkHref = link.attr("href");
  String linkText = link.text();
}
```

