package mjw.swing;

import javax.swing.*;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 21 Dec 2023, 22:49
 */
public class HelloWorld {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        JPanel pane = new JPanel();
        frame.setContentPane(pane);
        JButton button = new JButton("This is a button");
        pane.add(button);
        frame.setVisible(true);
    }
}
