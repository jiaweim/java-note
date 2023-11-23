package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 10:58 PM
 */
public class Event1
{

    static final int WIDTH = 300;
    static final int HEIGHT = 200;
    static JTextField l = new JTextField(20);

    public static void main(String[] args)
    {
        JFrame jf = new JFrame("测试程序");
        jf.setSize(WIDTH, HEIGHT);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        jf.setContentPane(contentPane);

        JButton b = new JButton("清空文本框中的信息");
        contentPane.add(l, "North");
        contentPane.add(b, "South");
        b.addActionListener(e -> Event1.l.setText("")); //向事件源注册
    }
}
