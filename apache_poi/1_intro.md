# Introduction

- [Introduction](#introduction)
  - [简介](#简介)

2020-05-11, 13:58
***

## 简介

apache poi 用于读写Office文件，包括两部分：

- OLE2 文件格式（office2007 之前的格式，如.xls, .doc等）
- OOXML (Office Open XML)格式(.xlsx)等。

|应用类型|组件|jar包|说明|
|---|---|---|---|
|OLE2文件系统|POIFS|poi|OLE2文件是一种类似zip格式的压缩文件，同时支持读写；包含 xls, doc, ppt以及基于 MFC serialization API的文件格式|
|OLE2属性集|HPSF|poi||
|xls|HSSF|poi|MS-Excel 97-2003 (.xls)，基于BIFF8格式的java接口|
|doc|HWPF|poi-scratchpad|MS-Word 97-2003，基于BIFF8格式的java接口。支持.doc文件的简单读写功能|
|ppt|HSLF|poi-scratchpad|PowerPoint PPT|
|vsd|HDGF|poi-scratchpad|Visio|
|publisher|HPBF|poi-scratchpad|Publisher pub|
|outlook|HSMF|poi-scratchpad|Outlook msg|
|xlsx|XSSF|poi-ooxml|MS-Excel 2007+|
|docx|XWPF|poi-ooxml|MS-Word 2007+|
|pptx|XSLF|poi-ooxml|MS-PowerPoint|

依赖项：

|Maven artifactId|Prerequisites|JAR|
|---|---|---|
|poi|commons-logging, commons-codec, commons-collections, commons-math, log4j|poi-version-yyyymmdd.jar|
|poi-scratchpad|poi|poi-scratchpad-version-yyyymmdd.jar|
|poi-ooxml|poi, poi-ooxml-schemas, commons-compress, SparseBitSet<br>For SVG support: batik-all, xml-apis-ext, xmlgraphics-commons|poi-ooxml-version-yyyymmdd.jar|
|poi-ooxml-schemas|xmlbeans|poi-ooxml-schemas-version-yyyymmdd.jar|
|poi-examples|poi, poi-scratchpad, poi-ooxml|poi-examples-version-yyyymmdd.jar|
|ooxml-schemas|xmlbeans|ooxml-schemas-1.4.jar|
|ooxml-security|xmlbeans <br> For signing: bcpkix-jdk15on, bcprov-jdk15on, xmlsec, slf4j-api|ooxml-security-1.1.jar|
