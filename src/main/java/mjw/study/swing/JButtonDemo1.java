package mjw.study.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 8:46 PM
 */
public class JButtonDemo1
{
    static final int WIDTH = 300;
    static final int HEIGHT = 200;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("测试程序");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);

        JButton b1 = new JButton("确定");
        JButton b2 = new JButton("取消");

        contentPane.add(b1);
        contentPane.add(b2);
    }
}
