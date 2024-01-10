package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 05 Jan 2024, 12:35 PM
 */
public class EDTDemo {

    static Process p;

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    p = new Process();
                    try {
                        p.sleep(30000);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
        }
        JFrame frame = new JFrame("Swing多线程测试程序");
        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        JButton button1 = new JButton("按钮一");
        JButton button2 = new JButton("按钮二");
        JButton button3 = new JButton("按钮三");
        JButton button4 = new JButton("按钮四");
        panel.setLayout(new GridLayout(2, 2));
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        int x = (width - Process.WIDTH) / 2;
        int y = (height - Process.HEIGHT) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}

class Process extends Thread {

    static final int WIDTH = 700;
    static final int HEIGHT = 400;

    public Process() {
        JFrame frame = new JFrame("Swing多线程测试程序");
        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        JButton button1 = new JButton("按钮一");
        JButton button2 = new JButton("按钮二");
        JButton button3 = new JButton("按钮三");
        JButton button4 = new JButton("按钮四");
        panel.setLayout(new GridLayout(2, 2));
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        int x = (width - 700) / 2;
        int y = (height - 400) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
