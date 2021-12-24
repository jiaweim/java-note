package mjw.study.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 24 Nov 2021, 5:14 PM
 */
public class WindowDecorationDemo
{
    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Window decoration demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);

            // 表示 JRootPane 提供 Frame 类型的装饰
            frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
