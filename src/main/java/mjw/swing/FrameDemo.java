package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Dec 2023, 1:06 PM
 */
public class FrameDemo {

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(175, 100));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(FrameDemo::createAndShowGUI);
    }
}
