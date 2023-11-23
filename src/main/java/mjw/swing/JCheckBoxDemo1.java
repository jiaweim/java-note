package mjw.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 8:55 PM
 */
public class JCheckBoxDemo1
{
    static final int WIDTH = 300;
    static final int HEIGHT = 200;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("测试");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);
        JCheckBox box1 = new JCheckBox("羽毛球");
        JCheckBox box2 = new JCheckBox("足球");
        JCheckBox box3 = new JCheckBox("电脑书");
        JCheckBox box4 = new JCheckBox("数学书");
        JCheckBox box5 = new JCheckBox("电影");
        JCheckBox box6 = new JCheckBox("录像");

        contentPane.add(box1);
        contentPane.add(box2);
        contentPane.add(box3);
        contentPane.add(box4);
        contentPane.add(box5);
        contentPane.add(box6);
    }
}
