# 传入 JavaFX 应用参数

2023-07-05, 10:20
****
## 1. 简介

和 Java 程序一样，JavaFX 支持通过命令行或 IDE 的配置传入参数。

JavaFX 程序将参数保存在 `Application` 类的 static 内部类 `Parameters` 中。`Parameter` 将参数分为三类：

- 命名参数（Named parameters）：由 name 和 value 两部分组成
- 非命名参数（Unnamed paramters）：单个值
- 原始参数（包含命名参数和非命名参数）

`Paramters` 类下面三种方法访问这三类参数：

```java
Map<String, String> getNamed();

List<String> getUnnamed();

List<String> getRaw();
```

命名参数包含 (name, value) 两部分，

`Application.getParameters()` 返回 `Application.Paramters` 类的引用。

`Parameters` 的引用在 `Application` 的 `init()` 中可用，在 `Application` 构造函数中不可用。即 `Paramters` 是在 `Application` 构造之后，`init()` 之前创建。

## 2. 示例

**示例：**  访问传入 JavaFX 的所有参数

传入的参数在 `TextArea` 中显示。

```java
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class FXParamApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Get application parameters
        Parameters p = this.getParameters();
        Map<String, String> namedParams = p.getNamed();
        List<String> unnamedParams = p.getUnnamed();
        List<String> rawParams = p.getRaw();

        String paramStr = "Named Parameters: " + namedParams + "\n" +
                "Unnamed Parameters: " + unnamedParams + "\n" +
                "Raw Parameters: " + rawParams;

        TextArea ta = new TextArea(paramStr);
        Group root = new Group(ta);
        stage.setScene(new Scene(root));
        stage.setTitle("Application Parameters");
        stage.show();
    }
}
```

- 以如下参数运行上面的示例

```sh
java [options] javafx.example.FXParamApp Anna Lola
```

上面传入了两个非命名参数：Anna 和 Lola。输出如下：

```
Named Parameters: {}
Unnamed Parameters: [Anna, Lola]
Raw Parameters: [Anna, Lola]
```

- 传入命名参数

命名参数的 key 前面有两个 `--`。即格式为：

```
--key=value
```

命令示例：

```sh
java [options] javafx.example.FXParamApp Anna Lola --width=200 --height=100
```

输出：

```
Named Parameters: {height=100, width=200}
Unnamed Parameters: [Anna, Lola]
Raw Parameters: [Anna, Lola, --width=200, --height=100]
```

