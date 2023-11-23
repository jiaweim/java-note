package mjw.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 13 Nov 2021, 7:14 PM
 */
public class Containers2
{
    static final int WIDTH = 300;
    static final int HEIGHT = 200;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("测试内容面板");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        JButton b1 = new JButton("确定");
        JButton b2 = new JButton("取消");
        panel.add(b1);
        panel.add(b2);
    }
}
