# OWLAPI

## 简介

OWL ontology 是对概念的规范化。

OWL API 用于创建、操作和序列化 OWL Ontologies。

- 最新版 API 支持 OWL 2
- OWLAPI 5.5.0 需要 Java 11
- Open Source licenses (LGPL 和 Apache)

包含如下组件：

- OWL 2 的 API 和内存引用实现
- 支持 RDF/XML, OWL/XML, Functional 语法，Manchester 语法，Turtle 和 OBO 读写
- 支持 KRSS, DL 语法和 LaTeX 写入
- 通过 RIO 集成的其它格式（NTriples, JSON 等）
- Reasoner 接口用于与 Reasoner 合作，如 FaCT++, HermiT, Pellet, Racer, JFact 和 Chainsaw

## 使用 API

- `OWLEntity`: 可以使用 `IRI` 识别的任何事物，如类名、数据、对象属性等
- `OWLAnonymousIndividual`, `OWLClassExpression`, `OWLPropertyExpression`: 未命名实体，类别表达式，性质表达式
- `OWLAnnotation`: entity, ongology, expression 或 axiom 的注释，

WOL API 只需使用 `OWLManager` 创建 `OWLOntologyManager` 实例；每个 `OWLOntologyManager` 处理一组  `OWLOntology` 对象。

**示例**：从远程解析 Pizza ontology，并以函数语法打印：

```java
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
OWLOntology ontology = manager.loadOntology(IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl"));
ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
```



## 参考

- https://github.com/owlcs/owlapi/
- https://github.com/owlcs/owlapi/wiki
- https://owlapi.sourceforge.net/owled2011_tutorial.pdf