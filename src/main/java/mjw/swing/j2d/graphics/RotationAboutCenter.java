package mjw.swing.j2d.graphics;

import javax.swing.*;
import java.awt.*;

public class RotationAboutCenter extends JComponent
{

    /**
     * Creates a new instance of RotationAboutCenter
     */
    public RotationAboutCenter() {
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d;
        g2d = (Graphics2D) g.create();

        // Erase background to white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // base rectangle
        g2d.setColor(Color.GRAY.brighter());
        g2d.fillRect(50, 50, 50, 50);

        // rotated 45 degrees around origin
        g2d.rotate(Math.toRadians(45));
        g2d.setColor(Color.GRAY.darker());
        g2d.fillRect(50, 50, 50, 50);

        // rotated 45 degrees about center of rect
        g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(45), 75, 75);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(50, 50, 50, 50);

        // done with g2d, dispose it
        g2d.dispose();
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Rotation");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(200, 200);
        f.add(new RotationAboutCenter());
        f.setVisible(true);
    }

    public static void main(String args[]) {
        Runnable doCreateAndShowGUI = new Runnable()
        {
            public void run() {
                createAndShowGUI();
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}
