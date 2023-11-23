package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

/**
 * 创建两个按钮：
 * - 选择任意一个，选择的按钮颜色随机变化；
 * - 第二个按钮监听第一个按钮的颜色变化，背景颜色更换为相同。
 *
 * @author JiaweiMao
 * @version 1.0.0
 * @since 19 Nov 2021, 1:45 PM
 */
public class BoundSample
{
    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Bound");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            final JButton b1 = new JButton("Select Me");
            final JButton b2 = new JButton("No Select Me");
            final Random random = new Random();

            ActionListener actionListener = e -> {
                JButton button = (JButton) e.getSource();
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);
                button.setBackground(new Color(red, green, blue));
            };

            PropertyChangeListener propertyChangeListener = evt -> {
                String propertyName = evt.getPropertyName();
                if (propertyName.equals("background")) {
                    b2.setBackground((Color) evt.getNewValue());
                }
            };

            b1.addActionListener(actionListener);
            b1.addPropertyChangeListener(propertyChangeListener);
            b2.addActionListener(actionListener);

            frame.add(b1, BorderLayout.NORTH);
            frame.add(b2, BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
