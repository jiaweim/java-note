package mjw.swing.j2d.graphics;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Jan 2024, 9:22 AM
 */
public class DiagonalLineDemo extends JComponent
{
    @Override
    protected void paintComponent(Graphics g) {
        g.drawLine(0, 0, getWidth() - 1, getHeight() - 1);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Diagonal Line Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.add(new DiagonalLineDemo());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable runnable = DiagonalLineDemo::createAndShowGUI;
        SwingUtilities.invokeLater(runnable);
    }
}
