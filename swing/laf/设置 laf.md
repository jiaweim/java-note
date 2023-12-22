# 设置 laf

- [设置 laf](#设置-laf)
  - [简介](#简介)
  - [可用 LaF](#可用-laf)
  - [设置 LaF - 代码](#设置-laf---代码)
  - [设置 LaF - 命令](#设置-laf---命令)
  - [设置 LaF - swing.properties 文件](#设置-laf---swingproperties-文件)
  - [UIManager 如何选择 LaF](#uimanager-如何选择-laf)
  - [启动后设置 LaF](#启动后设置-laf)
  - [示例](#示例)
  - [主题](#主题)
  - [参考](#参考)

2023-12-22, 15:23⭐
****

## 简介

Swing 支持改变 GUI 的 look and feel (LaF)：

- "Look" 指的是 GUI 组件（即 `JComponent`）的外观
- "Feel" 指的是 GUI 组件的行为

Swing 通过将每个组件拆分为两部分来支持 LaF：一个 `JComponent` 的子类，以及对应的 `ComponentUI` 子类。例如，JList 包含一个ListUI 类（继承自 ComponentUI）。ComponentUI 在 Swing 中有很多名字，如 "the UI," "component UI", "UI delegate", "look and feel delegate"，都指的 ComponentUI 。

大部分开发人员不需要直接和 `ComponentUI`  交互，而是由 `JComponent` 子类在内部使用。所有 `JComponent` 的子类将渲染都代理给了 `ComponentUI` 。通过这种渲染代理，使得 "look" 可以随着 L&F 变化。

`ComponentUI` 的实现由 LaF 提供。例如，Java LaF 为 `JTabbedPane` 提供了 `MetalTabbedPaneUI` 实现。Swing 会自动创建 `ComponentUI` 创建，所以我们基本不需要直接和 `ComponentUI` 交互。

## 可用 LaF

Sun 的 JRE 提供了如下 LaF：

1. `CrossPlatformLookAndFeel`, 即 "Java L&F"，也称为 "Metal"，是 Java 的默认 LaF，也是 Java API `javax.swing.plaf.metal` 的一部分，在所有平台具有统一外观。
2. `SystemLookAndFeel`, 使用当前系统的 LaF。系统 LaF 在运行时确定。
3. `Synth`, 使用 XML 文自定义 LaF 的基础。
4. `Multiplexing`, 同时使用多种 LaF 的方法。

对 Linux 和 Solaris，如果安装了 GTK+ 2.2 或更高版本，则系统 LaF 为 "GTK+"，否则为 "Motif"。

对 Windows，系统 LaF 是 "Windows"，它模仿了正在运行的 Windows 操作系统的 LaF，包括经典的 Windows, Xp, Vista 等。GTK+, Motif 和 Windows Laf 由 Sun 提供，打包在 Java SDK和 JRE 中（不过没有提供 Java API）。

Apple 使用自己的 JVM，其中包括它们专有的 LaF。

总而言之，如果使用 `SystemLookAndFeel`，实际的 LaF 为：

| Platform                              | Look and Feel |
| ------------------------------------- | ------------- |
| Solaris, Linux with GTK+ 2.2 or later | GTK+          |
| Other Solaris, Linux                  | Motif         |
| IBM UNIX                              | IBM*          |
| HP UX                                 | HP*           |
| Classic Windows                       | Windows       |
| Windows XP                            | Windows XP    |
| Windows Vista                         | Windows Vista |
| Macintosh                             | Macintosh*    |

\* Supplied by the system vendor.

在 API 中看不到 `SystemLookAndFeel`。GTK+, Motif 和 Windows 的 LaF 在：

```java
com.sun.java.swing.plaf.gtk.GTKLookAndFeel
com.sun.java.swing.plaf.motif.MotifLookAndFeel
com.sun.java.swing.plaf.windows.WindowsLookAndFeel
```

以上，只有 Java (Metal) LaF 和 Motif LaF 支持所有平台。

所有 Sun 的 LaF 都有共同点，这些共性定义在 `javax.swing.plaf.basic` 包中的 Basic LaF 中。Motif 和 Windows LaF 都是通过扩展 `javax.swing.plaf.basic` 包中的 UI delegate 类实现，"Basic" LaF 本身不能直接使用，必须进行扩展，自定义 LaF 也是通过扩展 Basic 得到。

在 API 中可以看到 4 个 LaF 包：

- `javax.swing.plaf.basic`, 基本 UI delegates，自定义 LaF 时进行扩展
- `javax.swing.plaf.metal`, java LaF (Metal)，也称为跨平台 L&F，其默认主题为 "Ocean"
- `javax.swing.plaf.multi`, 可同时使用多个 LaF 实现，可用于增强特定 LaF 的功能。
- `javax.swing.plaf.synth`, 使用 XML 文件配置的 LaF

## 设置 LaF - 代码

> 设置 LaF，应该在程序的第一步执行。否则，不管你设置的是什么 LaF，多有初始化 Java LaF 的风险。当有静态字段引用 Swing 类时，可能无意中会发生这种情况。如果不指定 LaF，则加载 JRE 默认的 LaF，对 Sun JRE 为 Java LaF，对 Apple JRE 为 Apple LaF。

Swing 组件使用的 LaF 通过 `javax.swing` 包中的 `UIManager` 类指定。在创建 Swing 组件时，该组件会向 `UIManager` 请求实现组件 LaF 的 `ComponentUI`。例如，`JLabel` 构造函数会向 `UIManager` 请求实现组件 LaF 的 `ComponentUI`。然后使用 `ComponentUI` 实现它所有的绘图和事件处理。

使用 `UIManager.setLookAndFeel()` 设置 LaF，参数为 `LookAndFeel` 子类的完全限定名。例如：

```java
public static void main(String[] args) {
    try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
        
    new SwingApplication(); //Create and show the GUI.
}
```

使用 System LaF:

```java
public static void main(String[] args) {
    try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }

    new SwingApplication(); //Create and show the GUI.
}
```

也可以使用实际类型作为参数：

```java
// Set cross-platform Java L&F (also called "Metal")
UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
```

或者：

```java
// Set Motif L&F on any platform
UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
```

## 设置 LaF - 命令

可以在命令行使用 `-D` flag 设置 `swing.defaultlaf` 属性。例如：

```sh
java -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel MyApp

java -Dswing.defaultlaf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel MyApp
```

## 设置 LaF - swing.properties 文件

在 `swing.properties` 文件中设置 `swing.defaultlaf` 属性。该文件可能需要手动创建，位于 Sun Java 的 lib 目录（其它 Java 供应商可能使用不同位置）。例如，如果 Java 解释器位于 `javaHomeDirectory\bin`，那么 `swing.properties` 文件在 `javaHomeDirectory\lib`。下面是一个 `swing.properties` 文件内容示例：

```
# Swing properties

swing.defaultlaf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
```

## UIManager 如何选择 LaF

`UIManager` 设置 L&F的步骤：

1) 如果程序在需要 LaF 前设置了 LaF，则 `UIManager` 尝试实例化指定的 LaF 类。如果实例化成功，所有组件使用该 LaF；
2) 如果实例化失败，`UIManager` 使用 `swing.defaultlaf` 属性指定的 LaF。如果同时在命令行和 `swing.properties` 文件指定了该属性，命令行优先；
3) 如果上面的步骤都失败，使用默认 LaF。

## 启动后设置 LaF

在程序启动后，GUI 组件已经可见，也可以使用 `setLookAndFeel` 设置 LaF。要使所有组件更新到新的LaF，需对每个顶层容器调用一次 `SwingUtilities.updateComponentTreeUI` 方法，具体操作为：

```java
UIManager.setLookAndFeel(lnfName);
SwingUtilities.updateComponentTreeUI(frame);
frame.pack();
```

## 示例

下面创建一个带按钮和标签的简单 GUI。每次单击按钮，标签的数值 +1.

通过修改 `LOOKANDFEEL` 字段可以更改 LaF：

- 这里已经设置为 "Motif"，即默认的跨平台 LaF "Metal"
- "GTK+" 不能在 Windows 上运行
- "Windows" 则只能在 Windows 上运行
- 如果选择了不能运行的 LaF，户自动切换回 Java LaF，即 Metal

```java
import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class LookAndFeelDemo implements ActionListener {

    private static String labelPrefix = "Number of button clicks: ";
    private int numClicks = 0;
    final JLabel label = new JLabel(labelPrefix + "0    ");

    // 有效值: null (use the default), "Metal", "System", "Motif", "GTK"
    final static String LOOKANDFEEL = "Metal";

    // If you choose the Metal L&F, you can also choose a theme.
    // Specify the theme to use by defining the THEME constant
    // Valid values are: "DefaultMetal", "Ocean",  and "Test"
    final static String THEME = "Test";

    public Component createComponents() {
        JButton button = new JButton("I'm a Swing button!");
        button.setMnemonic(KeyEvent.VK_I);
        button.addActionListener(this);
        label.setLabelFor(button);

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        JPanel pane = new JPanel(new GridLayout(0, 1));
        pane.add(button);
        pane.add(label);
        pane.setBorder(BorderFactory.createEmptyBorder(
                30, //top
                30, //left
                10, //bottom
                30) //right
        );

        return pane;
    }

    public void actionPerformed(ActionEvent e) {
        numClicks++;
        label.setText(labelPrefix + numClicks);
    }

    private static void initLookAndFeel() {
        String lookAndFeel = null;

        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
                //  an alternative way to set the Metal L&F is to replace the
                // previous line with:
                // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";

            } else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK")) {
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                        + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);

                // If L&F = "Metal", set the theme

                if (LOOKANDFEEL.equals("Metal")) {
                    if (THEME.equals("DefaultMetal"))
                        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                    else if (THEME.equals("Ocean"))
                        MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                    else
                        MetalLookAndFeel.setCurrentTheme(new TestTheme());

                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                }


            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                        + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                        + lookAndFeel
                        + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                        + lookAndFeel
                        + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建 GUI 并显示，需从 EDT 调用
     */
    private static void createAndShowGUI() {
        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("SwingApplication");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LookAndFeelDemo app = new LookAndFeelDemo();
        Component contents = app.createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

/**
 * This class describes a theme using "primary" colors.
 * You can change the colors to anything else you want.
 * <p>
 */
public class TestTheme extends DefaultMetalTheme {

    public String getName() {return "Toms";}

    private final ColorUIResource primary1 = new ColorUIResource(255, 255, 0);
    private final ColorUIResource primary2 = new ColorUIResource(0, 255, 255);
    private final ColorUIResource primary3 = new ColorUIResource(255, 0, 255);

    protected ColorUIResource getPrimary1() {return primary1;}

    protected ColorUIResource getPrimary2() {return primary2;}

    protected ColorUIResource getPrimary3() {return primary3;}

}
```

## 主题

主题（theme）是为了方便修改 Metal LaF 的字体和颜色。在上例中，可以通过修改 `THEME` 常量设置 Metal LaF 的主题：

- `DefaultMetal`, Java SE 5 之前的默认主题
- `Ocean`, 比 Metal 风格要柔和一些，从 Java SE 5 开始为默认主题
- `Test` 上例中自定义的主题

设置主题的代码：

```java
 if (LOOKANDFEEL.equals("Metal")) {
    if (THEME.equals("DefaultMetal"))
       MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
    else if (THEME.equals("Ocean"))
       MetalLookAndFeel.setCurrentTheme(new OceanTheme());
    else
       MetalLookAndFeel.setCurrentTheme(new TestTheme());
                     
    UIManager.setLookAndFeel(new MetalLookAndFeel()); 
 }     
```

## 参考

- https://docs.oracle.com/javase%2Ftutorial%2F/uiswing/lookandfeel/plaf.html