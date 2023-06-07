# Entity 和 Component

## 简介

下面通过开发一个乒乓球街机游戏（Pong）来学习  FXGL 的游戏开发过程。主要包括：

- 表示游戏对象的 Entity 类
- 为 Entity 添加数据和行为的 Component
- 游戏世界以及如何查询游戏世界中的 Entity

游戏对象是任何游戏的基础概念。游戏越大，游戏对象的实现就必须越 robust。面向对象编程背景的开发者容易陷入一个问题，即在游戏对象中广泛使用继承，导致软件架构的可扩展性降低。继承有利于垂直层次结构，在中小型游戏中非常适用。而在大型游戏中，Entity-Component 水平层次结构的模型更合适。

## Entity

任何游戏对象（如玩家、敌人、硬币、子弹、升级道具、墙壁、泥土、武器、宝箱等）都是 entity。entity 本身是一个通用对象，没有添加任何 component。component 是定义数据和行为的关键对象，如果没有 component，entity 之间就没有差别，即 component 定义了 entity。

entity 包含许多方法：

- `addComponent(Component c)` / `removeComponent(Component c)` – 用于添加或删除 components
- `setProperty(String key, Object value)` – 为 entity 添加任意类型的变量。如果不想用新的component 来存储数据，用变量也是个选择。变量值可以通过 `getInt(String key)`, `getString(String key)`, `getObject(String key)` 等类似方法查询
- `getComponents()` – 返回 entity 包含的所有 components
- `getPosition()`/`setPosition(Point2D p)` – entity 在 2D 空间的位置。该方法通过 `TransformComponent` 对象获取或修改位置（因为 entity 不保存任何数据）
- `translate(Point2D vector)` – 移动 entity。即要求 `TransformComponent` 修改位置数据
- `isVisible()`/`setVisible(boolean b)` – 检查 entity 是否被绘制
- `getType()`/`setType(Object type)` – entity 类型设置。一般用 enum 作为类型

## Component 添加数据

下面介绍如何使用 component 为 entity 添加数据。在 FXGL 的 Entity-Component 模型中，component 是 entity 的一部分。component 可以携带任何数据，类似于为 Java 类添加字段：

```java
class Car {
	int moveSpeed;
	Color color;
}
```

如果要给 car 添加新的属性，就需要修改上面的类。然而在游戏中，entity 通常会丢失旧属性，获得新属性，这意味着我们需要能够在运行时修改类。

另外，上面的 `Car` 类包含一个 `moveSpeed` 字段，意味着它可以移动。游戏中有很多可以移动的游戏对象，因此，我们可能需要一个抽象类 `MovableGameObject` 或接口 `Movable` 来定义可移动对象。但是，如果我们需要一个有时能移动，有时不能移动的物体呢？例如，一辆由玩家控制的骑车可以移动，但是它自己不能动。使用简单的继承或接口无法很好解决。

不过在计算机科学中，每个问题都不止一种解决方案。你可能听过 “组合优于继承” 这种说法。component 可以看作该思想的实现。它允许在运行时为 entity 附加字段和方法。下面是 `Car` 的 component 实现：

```java
Entity entity = new Entity();
entity.addComponent(new MoveSpeedComponent());
entity.addComponent(new ColorComponent());

// 在需要时令其无法移动
entity.removeComponent(MoveSpeedComponent.class);
// 修改 component 的值和修改字段一样
entity.getComponentOptional(ColorComponent.class).ifPresent(color -> {
	color.setValue(Color.RED);
});
```

component 解决了继承的问题。component 隔离了每个组件中的逻辑，遵循单一职责原则。

在创建 entity 时，最好先添加好 entity 所需的 component。然后，可以根据需要启动或禁用 component。例如，`CollidableComponent` 表示 entity 可以与某些东西碰撞。假设在创建时我们希望它不可碰撞，则可以在添加该 component 将其值设为 `false`。

```java
var c = new CollidableComponent();
c.setValue(false);
entity.addComponent(c);
```

## Component 添加行为

component 可以携带行为，以支持 entity 获取新方法执行新行为。添加行为 component 与添加方法一样。在自定义 component 前，先看一下 `Component`  的内置方法：

- `onAdded()` – 当添加 component 到 entity，执行该 callback
- `onRemoved()` – 当从 entity 移除 component，执行该 callback
- `onUpdate()` – 该 callback 每一帧都被调用，使 component 能更新其逻辑
- `pause()` – 使 component 暂停处理 `onUpdate()` 的更新
- `resume()` – 使 component 恢复处理 `onUpdate()` 的更新

### 自定义 Component

假设 entity 是一个电梯，能够把 player 带到山顶。将 component 添加到 entity 实现该功能：

```java
entity.addComponent(new LiftComponent());
```

那么，如何实现 `LiftComponent` 类。每个 component  都有一个 `onUpdate()` 方法，覆盖后提供所需功能。如下，将 entity 转换为电梯：

```java
public class LiftComponent extends Component {

	LocalTimer timer = ...;
	boolean isGoingUp = true;
	double speed = ...;
	
	@Override
	public void onUpdate(double tpf) {
		if (timer.elapsed(duration)) {
			isGoingUp = ! isGoingUp;
			timer.capture();
		}
		entity.translateY(isGoingUp? -speed * tpf : speed * tpf);
	}
}
```

可以看到，不需要初始化对 entity 对象的引用。当我们将它添加到 entity 时，会自动将 entity 的引用注入 component。这样极大简化了代码。另外，component 注入也可以减少样板代码。

### Component 注入

component 自动注入的常见用例，如你有两个 component，如 Component1 和 Component2，Component2 需要引用 Component1：

```java
class Component1 extends Component {}
class Component2 extends Component {
	private Component1 comp;
}
```

此时，不需要在 `Component2` 中手动获取对 `Component1` 的引用。在添加 component 时，FXGL 会自动将对象注入 `comp` 字段。需要注意，`Component2` 需要 `Component1` 也在 entity，这是一个依赖项。在添加 component 时会执行依赖项检查，所以一定要保证所需 component 已添加到 entity。对上例，`Component1` 应该在 `Component2` 之前添加，否则 FXGL 会抛出异常。

下面是一个简单的 player component，包含 up, down, left, right 方法，以支持添加该 component 的 entity 的移动。

```java
public class PlayerComponent extends Component {  
  
    // 该 component 被自动注入  
    private TransformComponent position;  
    private double speed = 0;  
  
    @Override  
    public void onUpdate(double tpf) {  
  
        speed = tpf * 60;  
    }  
  
    public void up() {  
  
        position.translateY(-5 * speed);  
    }  
  
    public void down() {  
  
        position.translateY(5 * speed);  
    }  
  
    public void left() {  
  
        position.translateX(-5 * speed);  
    }  
  
    public void right() {  
  
        position.translateX(5 * speed);  
    }  
}
```

可以看到，这里不需要开发人员初始化 `position` 字段。FXGL 会自动注入，从而减少代码量。

## Game World

Game World 本质上是一个包含 entity 的数据结构，负责为 game 添加、更新和删除 entity。另外，Game World 还提供各种查询 entity 的方法。例如，可以收集所有表示敌人的 entities。

添加 entity 到 world，等价于添加到 game。类似地，要从 game 删除 entity，将其从 world 删除即可。当前在 game world 中的 entities 处于 active 状态，调用 `isActive()` 返回 true。下面创建一个简单 entity，并将其添加到 game world。首先，获取 game world:

```java
var world = FXGL.getGameWorld();
```

然后添加 entity:

```java
world.addEntity(e);
```

以及，删除 entity:

```java
world.removeEntity(e);
```

### 查询

查询的方法有多种，这些方法一般以 "get" 开头，如 `getEntitiesByType()` 。

- 通过类型查询，需要 `TypeComponent`

## Pong

定义 entity：

- 