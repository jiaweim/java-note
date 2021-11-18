package mjw.study.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 5:12 PM
 */
public class JFormDesignerDemoTest
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Demo");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new JFormDesignerDemo());
        frame.pack();
    }
}
