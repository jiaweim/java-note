package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 11:28 AM
 */
public class SwingThreadingWait extends JFrame implements ActionListener
{
    private JLabel counter;
    private long start = 0;

    public SwingThreadingWait()
    {
        super("Invoke & Wait");

        JButton freezer = new JButton("Open File");
        freezer.addActionListener(this);

        counter = new JLabel("Time elapsed: 0s");

        add(freezer, BorderLayout.CENTER);
        add(counter, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e)
    {
        start = System.currentTimeMillis();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e12) {
                }

                final int elapsed = (int) ((System.currentTimeMillis() - start) / 1000);
                SwingUtilities.invokeLater(() -> counter.setText("Time elapsed: " + elapsed + "s"));

                if (elapsed == 4) {
                    try {
                        final int[] answer = new int[1];
                        SwingUtilities.invokeAndWait(() -> answer[0] =
                                JOptionPane.showConfirmDialog(SwingThreadingWait.this,
                                        "Abort long operation?",
                                        "Abort?",
                                        JOptionPane.YES_NO_OPTION));
                        if (answer[0] == JOptionPane.YES_OPTION) {
                            return;
                        }
                    } catch (InterruptedException e1) {
                    } catch (InvocationTargetException e1) {
                    }
                }
            }
        }).start();
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(() -> {
            SwingThreadingWait edt = new SwingThreadingWait();
            edt.setVisible(true);
        });
    }
}
