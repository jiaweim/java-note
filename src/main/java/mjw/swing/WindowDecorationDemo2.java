package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 24 Nov 2021, 8:00 PM
 */
public class WindowDecorationDemo2
{
    public static void main(String[] args)
    {
        Runnable runner = () -> {
            // 设置为 true，表示从 laf 获取装饰
            JFrame.setDefaultLookAndFeelDecorated(true);
            JFrame frame = new JFrame("Adornment example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
