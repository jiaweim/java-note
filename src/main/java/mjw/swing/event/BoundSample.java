package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Apr 2024, 9:09 AM
 */
public class BoundSample {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Button Sample");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                final JButton button1 = new JButton("Select Me");
                final JButton button2 = new JButton("No Select Me");
                final Random random = new Random();

                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        int red = random.nextInt(255);
                        int green = random.nextInt(255);
                        int blue = random.nextInt(255);
                        button.setBackground(new Color(red, green, blue));
                    }
                };

                PropertyChangeListener propertyChangeListener =
                        new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                String propertyName = evt.getPropertyName();
                                if ("background".equals(propertyName)) {
                                    button2.setBackground((Color) evt.getNewValue());
                                }
                            }
                        };
                button1.addActionListener(actionListener);
                button1.addPropertyChangeListener(propertyChangeListener);
                button2.addActionListener(actionListener);

                frame.add(button1, BorderLayout.NORTH);
                frame.add(button2, BorderLayout.SOUTH);
                frame.setSize(300, 100);
                frame.setVisible(true);

            }
        };
        EventQueue.invokeLater(runner);
    }
}
