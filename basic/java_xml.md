# Content
- [Content](#content)
- [简介](#%e7%ae%80%e4%bb%8b)
- [StAX](#stax)
- [JAXB](#jaxb)
- [Reference](#reference)

# 简介
Sun 目前提供的 XML 处理工具：

|工具|特征|
|---|---|
|SAX|
|DOM|
|StAX|内存占用最少|
|XPath Evaluator|
|XSL Processor|
|JAXB|解析速度最快|

# StAX
StAX 是类似于SAX的XML解析器，不过两者的API有两个主要差别：
- StAX 是 pull 类型解析器，在需要时从XML中获取信息，SAX是推式解析器，使用SAX需要做更多的工作；
	> StAX 可以读写XML，SAX只能读。
SAX 支持 Schema 验证

# JAXB
JAXB (Java Architecture for XML Binding) 将 XML Schema 和 Java Object 相结合，提供 XML 文档和 Java 类相互转换的功能(根据 Schema 生成 Java 类, 或将 Java 对象类树写到XML文档)，从而使 XML 文件的读(unmarshalling)、写(marshalling) 变得十分方便。

![](images/2019-10-15-11-50-29.png)

# Reference
- [Java XML Tutorial](http://tutorials.jenkov.com/java-xml/index.html)
- [tutorialspoint tutorial](https://www.tutorialspoint.com/java_xml/index.htm)
- https://www.vogella.com/tutorials/JavaXML/article.html
- http://www.vogella.com/tutorials/JAXB/article.html
- https://www.javatpoint.com/jaxb-tutorial
- https://docs.oracle.com/javase/tutorial/jaxb/intro/index.html
- https://javaee.github.io/jaxb-v2/doc/user-guide/index.html