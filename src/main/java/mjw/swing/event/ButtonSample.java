package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 07 Apr 2024, 5:29 PM
 */
public class ButtonSample {

    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JButton button = new JButton("Select Me");

            ActionListener actionListener = e -> System.out.println("I was selected.");
            MouseListener mouseListener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int modifiers = e.getModifiers();
                    if ((modifiers & InputEvent.BUTTON1_MASK)
                            == InputEvent.BUTTON1_MASK) {
                        System.out.println("Left button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON2_MASK)
                            == InputEvent.BUTTON2_MASK) {
                        System.out.println("Middle button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON3_MASK)
                            == InputEvent.BUTTON3_MASK) {
                        System.out.println("Right button pressed.");
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Left button released.");
                    }
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        System.out.println("Middle button released.");
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        System.out.println("Right button released.");
                    }
                    System.out.println();
                }
            };
            button.addActionListener(actionListener);
            button.addMouseListener(mouseListener);

            frame.add(button, BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
