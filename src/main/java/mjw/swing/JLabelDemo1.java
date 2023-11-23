package mjw.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 8:37 PM
 */
public class JLabelDemo1
{
    static final int WIDTH = 300;
    static final int HEIGHT = 200;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("标签测试");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);

        JLabel label1 = new JLabel("这是一个标签测试程序");
        JLabel label2 = new JLabel("标签不可编辑");
        contentPane.add(label1);
        contentPane.add(label2);

        frame.pack();
    }
}
