package mjw.swing.j2d.graphics;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Jan 2024, 8:00 PM
 */
public class Demo extends JComponent
{
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        g2d.drawLine(0, 0, 10, 10);
        g2d.dispose();
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Oval");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(200, 200);
        f.add(new Demo());
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable doCreateAndShowGUI = Demo::createAndShowGUI;
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}
