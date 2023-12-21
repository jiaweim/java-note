package mjw.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 8:49 PM
 */
public class JRadioDemo1 {

    public static void main(String[] args) {
        JFrame frame = new JFrame("测试");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);
        JRadioButton b1 = new JRadioButton("忽略");
        JRadioButton b2 = new JRadioButton("继续");
        JRadioButton b3 = new JRadioButton("跳过");
        contentPane.add(b1);
        contentPane.add(b2);
        contentPane.add(b3);
    }
}
