# Modern Docking

## 依赖项

大多数应用只需要引入 `modern-docking-api` 和 `modern-docking-single-app`。

另有配套 UI 包（Modern Docking UI），专门为 FlatLaf 外观样式库提供适配支持。

```xml
<dependency>
    <groupId>io.github.andrewauclair</groupId>
    <artifactId>modern-docking-api</artifactId>
    <version>1.4.9</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.github.andrewauclair</groupId>
    <artifactId>modern-docking-single-app</artifactId>
    <version>1.4.9</version>
    <scope>compile</scope>
</dependency>
```

## HelloWorld

下面通过一个示例演示：

1. 创建并注册 dpckable panels
2. 创建并注册 root panels
3. 通过代码手动实现组件 dock

创建类：

```java
public class HelloWorld {
    public static class MainFrame extends JFrame {
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
```

这是极简基础程序，作为后续开发起点。

接下来实现以下功能：

1. 设置 size
2. 初始化 Modern Docking
3. 添加 `RootDockingPanel`
4. 创建、注册若干可停靠组件

首先，设置 `JFrame` 尺寸：

```java
setSize(400, 300);
```

初始化 docking 框架：
```java
Docking.initialize(this);
```

该操作会以 `MainFrame` 类作为主窗口来初始化停靠框架，同时初始化后续所需的内部组件。

接下来，创建并添加一个根停靠面板（`RootDockingPanel`）。如果需要更换布局，或是在可停靠区域外围添加其他组件，可以对它做进一步定制。此处直接使用 `BorderLayout` ，让该根面板占满窗口全部空间。

```java
RootDockingPanel root = new RootDockingPanel(this);
add(root, BorderLayout.CENTER);
```

这段代码会新建一个根停靠面板并将其添加到主窗口 `JFrame` 中。传入 this 参数是为了设置 `RootDockingPanel` 所属的窗口。框架会依靠该参数完成面板注册，同时生成用于固定面板所需的各类工具栏。

接下来创建一个可停靠组件。需要新建一个实现 `Dockable` 接口的 `JPanel`。`Dockable` 接口中的大部分方法都提供了默认实现，无需手动重写。

以下方法必须实现：

| Method            | Description                                                  |
| :---------------- | :----------------------------------------------------------- |
| `getPersistentID` | 可停靠组件在停靠框架内的唯一标识                             |
| `getTabText`      | 若该组件停靠至标签组，该文本会显示在标题栏以及` JTabbedPane` 标签上 |

下面创建一个简易面板，该面板仅接收文本作为构造函数参数，并将该文本同时用作持久化ID与标签文字。

```java
static class DockingPanel extends JPanel implements Dockable {

    private final String text;

    public DockingPanel(String text) {
        this.text = text;

        Docking.registerDockable(this);
    }

    @Override
    public String getPersistentID() {
        return text;
    }

    @Override
    public String getTabText() {
        return text;
    }
}
```

创建好实现 `Dockable` 接口的面板，接下来就可以在构造方法中实例化各类可停靠组件。

```java
DockingPanel helloWorld = new DockingPanel("Hello World");
```

该操作会向停靠框架注册此可停靠组件。持久化ID用于在整个框架范围内标识该组件。在更复杂的应用程序中，我们会像下方示例那样，在自定义面板的构造函数内部调用 `Docking.registerDockable` 方法完成注册。

现在可以对可停靠组件执行停靠操作。第一种方式是直接通过面板引用完成停靠。

```java
Docking.dock(helloWorld, this);
```

也可以使用持久化ID的值来实现：

```java
Docking.dock("Hello World", this);
```

两种方式中传入的 `this` 均指代主窗口 `MainFrame`，用于告知框架将面板停靠至当前窗口。

至此就完成了完整示例程序，程序会创建一个主窗口 ` JFrame`，并内置标题文字为“Hello World”的可停靠组件。完整示例代码如下：

```java
public class HelloWorld {
    static class DockingPanel extends JPanel implements Dockable {
        private final String text;

        public DockingPanel(String text) {
            this.text = text;

            Docking.registerDockable(this);
        }

        @Override
        public String getPersistentID() {
            return text;
        }

        @Override
        public String getTabText() {
            return text;
        }
    }

    public static class MainFrame extends JFrame {
        public MainFrame() {
            setSize(400, 300);

            Docking.initialize(this);

            RootDockingPanel root = new RootDockingPanel(this);
            add(root, BorderLayout.CENTER);

            DockingPanel helloWorld = new DockingPanel("Hello World");

            Docking.dock(helloWorld, this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
```

## Modern Docking UI

Modern Docking UI 是 Modern Docking 的扩展组件，它借助 FlatLaf 样式库，将设置图标和关闭图标从 PNG 图片替换为 SVG 矢量图。

基于 Hello World 示例，可以按如下方式修改代码以初始化 Modern Docking UI，完成这一步即可启用Modern Docking UI。

```java
public static class MainFrame extends JFrame {
    public MainFrame() {
        setSize(400, 300);

        Docking.initialize(this);
        DockingUI.initialize();

        RootDockingPanel root = new RootDockingPanel(this);
        add(root, BorderLayout.CENTER);

        DockingPanel helloWorld = new DockingPanel("Hello World");

        Docking.dock(helloWorld, this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
```



## 参考

- https://moderndocking.readthedocs.io/en/latest/