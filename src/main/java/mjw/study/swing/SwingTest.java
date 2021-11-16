package mjw.study.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 13 Nov 2021, 4:02 PM
 */
public class SwingTest
{
    static final int WIDTH = 300;
    static final int HEIGHT = 200;

    public static void main(String[] args)
    {
        JFrame jf = new JFrame("helloswing");
        jf.setSize(WIDTH, HEIGHT);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
