package mjw.swing.j2d;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 12:29 PM
 */
public class SafeRepaint extends JFrame
{
    private SafeComponent safeComponent;

    public SafeRepaint() {
        super("Safe Repaint");

        safeComponent = new SafeComponent();
        add(safeComponent);

        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        new Thread(() -> {
            while (true) {
                safeComponent.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SafeRepaint repaint = new SafeRepaint();
            repaint.setVisible(true);
        });

    }
}