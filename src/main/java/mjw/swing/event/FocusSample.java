package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Apr 2024, 10:49 AM
 */
public class FocusSample {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Focus Sample");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                ActionListener actionListener = new ActionFocusMover();
                MouseListener mouseListener = new MouseEnterFocusMover();

                frame.setLayout(new GridLayout(3, 3));
                for (int i = 1; i < 10; i++) {
                    JButton button = new JButton(Integer.toString(i));
                    button.addActionListener(actionListener);
                    button.addMouseListener(mouseListener);

                    if ((i % 2) != 0) {
                        button.setFocusable(false);
                    }
                    frame.add(button);
                }
                frame.setSize(300, 300);
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
