package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 31 Mar 2024, 21:45
 */
public class AdornSample {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Adornment Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            frame.setSize(300, 100);
            frame.setVisible(true);
        });
    }
}
