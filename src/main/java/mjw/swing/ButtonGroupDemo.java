package mjw.swing;

import javax.swing.*;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 22 Dec 2023, 00:22
 */
public class ButtonGroupDemo {

    public static void main(String[] args) {
        JFrame frame = new JFrame("ButtonGroup 测试");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);
        JRadioButton jr1 = new JRadioButton("忽略");
        JRadioButton jr2 = new JRadioButton("继续");
        JRadioButton jr3 = new JRadioButton("跳过");
        ButtonGroup bg = new ButtonGroup();
        bg.add(jr1);
        bg.add(jr2);
        bg.add(jr3);
        contentPane.add(jr1);
        contentPane.add(jr2);
        contentPane.add(jr3);
    }
}
