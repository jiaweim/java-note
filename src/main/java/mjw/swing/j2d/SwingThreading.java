package mjw.swing.j2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Jan 2024, 7:04 PM
 */
public class SwingThreading extends JFrame implements ActionListener
{
    private JLabel counter;
    private int tickCounter = 0;
    private static SwingThreading edt;

    public SwingThreading() {
        super("Swing Threading");

        JButton freezer = new JButton("Increment");
        freezer.addActionListener(this);

        counter = new JLabel("0");

        add(freezer, BorderLayout.CENTER);
        add(counter, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        incrementLabel();
    }

    private void incrementLabel() {
        tickCounter++;
        Runnable code = () -> counter.setText(String.valueOf(tickCounter));

        if (SwingUtilities.isEventDispatchThread()) {
            code.run();
        } else {
            SwingUtilities.invokeLater(code);
        }
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            edt = new SwingThreading();
            edt.setVisible(true);

            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    edt.incrementLabel();
                }
            }).start();
        });
    }
}
