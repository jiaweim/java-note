# JFrame

- [JFrame](#jframe)
  - [简介](#简介)
  - [创建 JFrame](#创建-jframe)
  - [窗口装饰](#窗口装饰)
  - [关闭窗口事件](#关闭窗口事件)
  - [JFrame API](#jframe-api)
  - [参考](#参考)

2023-12-27, 20:17⭐

***

## 简介

`JFrame` 是带边框和标题的顶层窗口，`getInsets` 返回边框大小。

frame 区域除掉边框，余下才是渲染或显示子组件的矩形区域：

- 左上角：(`insets.left`, `insetes.top`)
- 宽度：`width - (insets.left+insets.right)`
- 高度：`height - (insets.top+insets.bottom)`

除了边框和标题，`JFrame` 还包括关闭和最小化等功能按钮。

一个应用至少包含一个 `JFrame`，其它需求：

- 让一个窗口依赖于另一个窗口，例如窗口最小化它随之最小化，使用 `JDialog`
- 让一个窗口在另一个窗口内部显示，用 `JInternalFrame`

## 创建 JFrame

创建并设置 `JFrame` 的步骤：

1. 创建 frame

```java
JFrame frame = new JFrame("FrameDemo");
```

2. 设置关闭 frame 的默认行为

```java
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
```

3. 添加组件

```java
//...create emptyLabel...
frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
```

4. 调整尺寸

```java
frame.pack();
```

5. 显示

```java
frame.setVisible(true);
```

<img src="images/2023-12-27-13-08-10.png" width="250"/>

```java
public class FrameDemo {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("FrameDemo"); // 创建 frame，设置标题
        // 指定关闭 frame 时的行为，`EXIT_ON_CLOSE` 关闭 frame 时推出程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        // 向 frame 添加一个空白标签
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(175, 100));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);

        // 调整 frame 尺寸，使其不小于所有组件的 prefSize 加和
        frame.pack();
        // 在屏幕显示
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(FrameDemo::createAndShowGUI);
    }
}
```

使用 `setLocationRelativeTo` 或 `setLocation` 设置 frame 位置。例如，将 frame 屏幕居中：

```java
frame.setLocationRelativeTo(null);
```

## 窗口装饰

窗口装饰默认由 native 窗口系统提供，其它来源有：

- Laf 提供装饰
- 不要装饰
- 自定义装饰
- 使用全屏独占模式

除了窗口装饰的来源，还可以指定 icon。如何使用 icon 取决于窗口系统或提供装饰的 Laf：

- 如果窗口系统支持最小化，那么 icon 用于表示最小化的窗口
- 大多数窗口系统或 Laf 还会在装饰中显示 icon
- icon 典型大小为 16x16 像素，少数窗口系统使用其它尺寸



下面显示三个 frames，除了窗口装饰它们完全一样。从 frame 的按钮可以看出，它们都使用的 Java Laf：

- 第一个 frame 是 Windows 系统的默认装饰
- 第二个和第三个使用的是 Java Laf 的装饰
- 第三个 frame 使用 Java Laf 装饰，且自定义 icon

| <img src="images/2023-12-27-17-30-33.png" /> | <img src="images/2023-12-27-17-30-39.png" /> | <img src="images/2023-12-27-17-30-43.png" /> |
| :--: | :--: | :--: |

**示例：** 使用 Laf 提供的窗口装饰创建 frame，自定义 icon

```java
// 使用 Laf 的窗口装饰
JFrame.setDefaultLookAndFeelDecorated(true);

// 创建 frame
JFrame frame = new JFrame("A window");

// 设置 frame 的 icon
frame.setIconImage(new ImageIcon(imgURL).getImage());
```

`setDefaultLookAndFeelDecorated`必须在创建 `JFrame` 之前调用，否则无效。设置值用于随后创建的所有 `JFrame`。调用 `JFrame.setDefaultLookAndFeelDecorated(false)` 切换回 native 窗口系统装饰。

有些 Laf 不支持窗口装饰，此时设置为 Laf 装饰无效，会自动切换回窗口系统的装饰。

完整代码：

1. 展示如何选择窗口装饰
2. 如何禁用窗口装饰
3. 设置窗口位置

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;

public class FrameDemo2 extends WindowAdapter implements ActionListener {

    private Point lastLocation = null;
    private int maxX;
    private int maxY;

    // the main frame's default button
    private static JButton defaultButton = null;

    // constants for action commands
    protected final static String NO_DECORATIONS = "no_dec";
    protected final static String LF_DECORATIONS = "laf_dec";
    protected final static String WS_DECORATIONS = "ws_dec";
    protected final static String CREATE_WINDOW = "new_win";
    protected final static String DEFAULT_ICON = "def_icon";
    protected final static String FILE_ICON = "file_icon";
    protected final static String PAINT_ICON = "paint_icon";

    // true: 新窗口无装饰
    protected boolean noDecorations = false;

    // true: 新窗口设置 icon
    protected boolean specifyIcon = false;

    // true: 新窗口自定义 icon
    protected boolean createIcon = false;

    public FrameDemo2() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        maxX = screenSize.width - 50;
        maxY = screenSize.height - 50;
    }

    // 创建一个新的 MyFrame
    public void showNewWindow() {
        JFrame frame = new MyFrame();

        // 禁用窗口装饰：
        // 对无装饰窗口，除非需要 JFrame 的功能，否则建议使用 Window 或 JWindow
        // 而不是无装饰 JFrame
        if (noDecorations) {
            frame.setUndecorated(true);
        }

        // 设置 window 位置
        if (lastLocation != null) {
            // 向右、下平移 40 pixels.
            lastLocation.translate(40, 40);
            if ((lastLocation.x > maxX) || (lastLocation.y > maxY)) {
                lastLocation.setLocation(0, 0);
            }
            frame.setLocation(lastLocation);
        } else {
            lastLocation = frame.getLocation();
        }

        //Calling setIconImage sets the icon displayed when the window
        //is minimized.  Most window systems (or look and feels, if
        //decorations are provided by the look and feel) also use this
        //icon in the window decorations.
        if (specifyIcon) {
            if (createIcon) {
                frame.setIconImage(createFDImage()); // 直接绘制 icon
            } else {
                frame.setIconImage(getFDImage());    // 从文件加载 icon
            }
        }

        frame.setSize(new Dimension(170, 100));
        frame.setVisible(true);
    }

    // Create the window-creation controls that go in the main window.
    protected JComponent createOptionControls() {
        JLabel label1 = new JLabel("Decoration options for subsequently created frames:");
        ButtonGroup bg1 = new ButtonGroup();
        JLabel label2 = new JLabel("Icon options:");
        ButtonGroup bg2 = new ButtonGroup();

        //Create the buttons
        JRadioButton rb1 = new JRadioButton();
        rb1.setText("Look and feel decorated");
        rb1.setActionCommand(LF_DECORATIONS);
        rb1.addActionListener(this);
        rb1.setSelected(true);
        bg1.add(rb1);
        //
        JRadioButton rb2 = new JRadioButton();
        rb2.setText("Window system decorated");
        rb2.setActionCommand(WS_DECORATIONS);
        rb2.addActionListener(this);
        bg1.add(rb2);
        //
        JRadioButton rb3 = new JRadioButton();
        rb3.setText("No decorations");
        rb3.setActionCommand(NO_DECORATIONS);
        rb3.addActionListener(this);
        bg1.add(rb3);
        
        JRadioButton rb4 = new JRadioButton();
        rb4.setText("Default icon");
        rb4.setActionCommand(DEFAULT_ICON);
        rb4.addActionListener(this);
        rb4.setSelected(true);
        bg2.add(rb4);
        //
        JRadioButton rb5 = new JRadioButton();
        rb5.setText("Icon from a JPEG file");
        rb5.setActionCommand(FILE_ICON);
        rb5.addActionListener(this);
        bg2.add(rb5);
        //
        JRadioButton rb6 = new JRadioButton();
        rb6.setText("Painted icon");
        rb6.setActionCommand(PAINT_ICON);
        rb6.addActionListener(this);
        bg2.add(rb6);

        //Add everything to a container.
        Box box = Box.createVerticalBox();
        box.add(label1);
        box.add(Box.createVerticalStrut(5)); //spacer
        box.add(rb1);
        box.add(rb2);
        box.add(rb3);
        //
        box.add(Box.createVerticalStrut(15)); //spacer
        box.add(label2);
        box.add(Box.createVerticalStrut(5)); //spacer
        box.add(rb4);
        box.add(rb5);
        box.add(rb6);

        //Add some breathing room.
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return box;
    }

    //Create the button that goes in the main window.
    protected JComponent createButtonPane() {
        JButton button = new JButton("New window");
        button.setActionCommand(CREATE_WINDOW);
        button.addActionListener(this);
        defaultButton = button; //Used later to make this the frame's default button.

        //Center the button in a panel with some space around it.
        JPanel pane = new JPanel(); //use default FlowLayout
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.add(button);

        return pane;
    }

    // 处理点击 button 的事件
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // New window button 事件
        if (CREATE_WINDOW.equals(command)) {
            showNewWindow();
            //Handle the first group of radio buttons.
        } else if (NO_DECORATIONS.equals(command)) {
            noDecorations = true;
            JFrame.setDefaultLookAndFeelDecorated(false);
        } else if (WS_DECORATIONS.equals(command)) {
            noDecorations = false;
            JFrame.setDefaultLookAndFeelDecorated(false);
        } else if (LF_DECORATIONS.equals(command)) {
            noDecorations = false;
            JFrame.setDefaultLookAndFeelDecorated(true);

            //Handle the second group of radio buttons.
        } else if (DEFAULT_ICON.equals(command)) {
            specifyIcon = false;
        } else if (FILE_ICON.equals(command)) {
            specifyIcon = true;
            createIcon = false;
        } else if (PAINT_ICON.equals(command)) {
            specifyIcon = true;
            createIcon = true;
        }
    }

    //Creates an icon-worthy Image from scratch.
    protected static Image createFDImage() {
        // Create a 16x16 pixel image.
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);

        //Draw into it.
        Graphics g = bi.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 15, 15);
        g.setColor(Color.RED);
        g.fillOval(5, 3, 6, 6);

        //Clean up.
        g.dispose();

        return bi;
    }

    // Returns an Image or null.
    protected static Image getFDImage() {
        java.net.URL imgURL = FrameDemo2.class.getResource("images/FD.jpg");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return null;
        }
    }

    private static void createAndShowGUI() {
        // 使用 Java Laf
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        // 使用 Laf 窗口装饰
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("FrameDemo2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        FrameDemo2 demo = new FrameDemo2();

        //Add components to it.
        Container contentPane = frame.getContentPane();
        contentPane.add(demo.createOptionControls(),
                BorderLayout.CENTER);
        contentPane.add(demo.createButtonPane(),
                BorderLayout.PAGE_END);
        frame.getRootPane().setDefaultButton(defaultButton);

        frame.pack();
        frame.setLocationRelativeTo(null); // center it
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FrameDemo2::createAndShowGUI);
    }

    class MyFrame extends JFrame implements ActionListener {

        // 创建 frame：只有一个 button
        public MyFrame() {
            super("A window");
            // 指定默认关闭窗口行为
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            // 用于关闭窗口的 button：处理无装饰的窗口
            JButton button = new JButton("Close window");
            button.addActionListener(this);

            // Place the button near the bottom of the window.
            Container contentPane = getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
            contentPane.add(Box.createVerticalGlue()); //takes all extra space
            contentPane.add(button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT); //horizontally centered
            contentPane.add(Box.createVerticalStrut(5)); //spacer
        }

        // 点击 button 的操作，与默认关闭窗口行为一致 (DISPOSE_ON_CLOSE).
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dispose();
        }
    }
}
```

## 关闭窗口事件

关闭屏幕显示的 frame 默认隐藏该 frame。虽然 frame 不可见，但它依然在，可以使它再次可见。如果想要不同的行为，有两种方式：

- 注册一个处理 window-closing 事件的 window-listener
- 使用 `setDefaultCloseOperation` 指定默认的关闭操作

以下是 `setDefaultCloseOperation` 的参数值，前三个定义在 `WindowConstants` 接口中。

|参数值|说明|
|---|---|
|`DO_NOTHING_ON_CLOSE`|当用户关闭窗口，不做任何操作。此时，程序应该在 window-listener 的 `windowClosing` 方法中执行某些操作|
|`HIDE_ON_CLOSE`|`JDialog` 和 `JFrame` 的默认值，当用户关闭窗口时隐藏窗口，当可以再次显示|
|`DISPOSE_ON_CLOSE` |`JInternalFrame` 的默认值，用户关闭窗口时隐藏并销毁窗口。即从屏幕删除窗口，并释放它使用的资源|
|`EXIT_ON_CLOSE` |使用 `System.exit(0)` 退出程序|

> 如果屏幕上只有一个窗口，`DISPOSE_ON_CLOSE` 和 `EXIT_ON_CLOSE` 结果类似。换句话说，当 JVM 中最后一个可显示的窗口被 dispose 掉，JVM 终止。

默认的关闭操作在处理 window-closing 事件的 window-listener 之后执行。即优先执行 window-listener。

因此，假设你指定 frame 的默认关闭操是 dispose；但你同时实现了一个 window-listener，检测该 frame 是否是最后一个可见的 frame，如果是，则保存数据并退出程序。此时，当用户关闭一个 frame 时，将首先调用 window-listener，如果它没有退出程序，再执行默认的关闭操作（dispose frame）。

## JFrame API

下面列出 `JFrame` 的常用方法。

**创建和设置 Frame**

- 创建一个初始不可见的 frame。`String` 参数指定 frame 标题

```java
JFrame()
JFrame(String)
```

- 设置或获取用户按下 frame 关闭按钮发生的操作，可选项

`DO_NOTHING_ON_CLOSE`, `HIDE_ON_CLOSE`, `DISPOSE_ON_CLOSE`, `EXIT_ON_CLOSE`

```java
void setDefaultCloseOperation(int)
int getDefaultCloseOperation()
```

- 设置或获取 frame 的 icon。参数是 `java.awt.Image` 类型

```java
void setIconImage(Image)
Image getIconImage()
```

- 设置或获取 frame 的标题

```java
void setTitle(String)
String getTitle()
```

- 设置或获取该 frame 是否需要装饰。仅当该 frame 还没显示时设置才有效，一般用于全屏独占模式或启用自定义装饰

```java
void setUndecorated(boolean)
boolean isUndecorated()
```

- 确定随后创建的 frame 是否应该具有当前 Laf 提供的窗口装饰。

```java
static void setDefaultLookAndFeelDecorated(boolean)
static boolean isDefaultLookAndFeelDecorated()
```

**设置窗口大小和位置**

- 调整窗口大小，使其所有内容不小于 prefSize

```java
void pack()
```

- 设置或获取窗口总大小，setSize 的参数分别为宽度和高度

```java
void setSize(int, int)
void setSize(Dimension)
Dimension getSize()
```

- 设置或获取窗口的大小和位置。

```java
void setBounds(int, int, int, int)
void setBounds(Rectangle)
Rectangle getBounds()
```

- 设置或获取窗口左上角的位置

```java
void setLocation(int, int)
Point getLocation()
```

- 设置窗口位置，使其位于指定组件的中心。如果参数为 null，则窗口位于屏幕中心。要正确将窗口居中，应该再设置窗口大小后再调用该方法。

```java
void setLocationRelativeTo(Component)
```

**与 root-pane 相关的方法**

- 设置或获取 frame 的 content-pane。content-pane 包含 frame 内可见的 GUI 组件

```java
void setContentPane(Container)
Container getContentPane()
```

- 创建、设置或获取 frame 的 root-pane。root-pane 管理 frame 的内部，包括 content-pane，glass-pane 等。

```java
JRootPane createRootPane()
void setRootPane(JRootPane)
JRootPane getRootPane()
```

- 设置或获取 frame 的菜单栏

```java
void setJMenuBar(JMenuBar)
JMenuBar getJMenuBar()
```

- 设置或获取 frame 的 glass-pane。可以用 glass-pane 拦截鼠标事件，或者再 GUI 上面绘制

```java
void setGlassPane(Component)
Component getGlassPane()
```

- 设置或获取 layered-pane。使用 layered-pane 可以将组件放在其它组件的上面或下面

```java
void setLayeredPane(JLayeredPane)
JLayeredPane getLayeredPane()
```


## 参考

- https://docs.oracle.com/javase%2Ftutorial%2F/uiswing/components/frame.html