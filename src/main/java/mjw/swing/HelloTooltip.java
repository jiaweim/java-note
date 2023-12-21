package mjw.swing;

import javax.swing.*;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 21 Dec 2023, 23:22
 */
public class HelloTooltip {

    static final int WIDTH = 300;
    static final int HEIGHT = 200;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tooltip test");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);

        JButton b1 = new JButton("确定");
        JButton b2 = new JButton("取消");
        b1.setToolTipText("这是确定按钮");
        b2.setToolTipText("这是取消按钮");

        contentPane.add(b1);
        contentPane.add(b2);
        frame.setVisible(true);
    }
}
