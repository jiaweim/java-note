
# 简介
Aalto XML 是一个高性能的 Stax XML 实现，具有如下特征：
- 兼容 StAX (Streaming API for XML)
- 支持 XML 的解析和创建
- 支持 SAX 解析
- 高性能

Aalto 和其它 StAX 的主要差别：
- 解析速度，Aalto 是目前基于 StAX 和 SAX 最快的解析 XML 的Java包，如果追求解析速度，选择 Aalto 没错。
- 非阻塞异步解析

# StAX 解析

## 使用 Aalto 基于 StAX 解析XML
用于 StAX 解析的核心类：`XMLInputFactory2`, `XMLStreamReader2`, `XMLEvent`.



# Reference
- https://github.com/FasterXML/aalto-xml/wiki
- http://www.studytrails.com/java/xml/aalto/java-xml-aalto-introduction/