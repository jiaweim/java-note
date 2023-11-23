package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 18 Nov 2021, 12:27 PM
 */
public class ButtonSample
{
    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JButton button = new JButton("Select Me");
            // Attach listeners
            button.addActionListener(e -> System.out.println("I was selected."));
            MouseListener mouseListener = new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    int modifiers = e.getModifiersEx();
                    if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) {
                        System.out.println("Left button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK) {
                        System.out.println("Middle button pressed.");
                    }
                    if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
                        System.out.println("Right button pressed.");
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Left button released.");
                    }
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        System.out.println("Middle button released.");
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        System.out.println("Right button released.");
                    }
                }
            };
            button.addMouseListener(mouseListener);
            frame.add(button, BorderLayout.SOUTH);
            frame.setSize(300, 100);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
