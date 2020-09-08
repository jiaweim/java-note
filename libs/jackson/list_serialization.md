# List 序列化

## 简介

jackson 对 List进行序列化和反序列时，默认没有保存类型信息。下面，我们通过两个例子来说明 List的序列化问题。第一个，序列化包含List的类；第二个，则直接序列化List.

## 例一，序列化包含List的对象

下面将 Zoo 类转换为 json 对象。

```java
class Zoo {
	public String name;
	public String city;

	public Zoo(String name, String city) {
		this.name = name;
		this.city = city;
	}

	public List&ltAnimal&gt animals = new ArrayList&ltAnimal&gt();

	public List&ltAnimal&gt addAnimal(Animal animal) {
		animals.add(animal);
		return animals;
	}

}
```
