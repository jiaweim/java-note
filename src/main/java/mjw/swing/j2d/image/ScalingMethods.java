package mjw.swing.j2d.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ScalingMethods extends JComponent
{

    private static BufferedImage picture = null;
    private static final int PADDING = 10;
    private static final double SCALE_FACTOR = .05;
    private int scaleW, scaleH;

    /**
     * Creates a new instance of ScalingMethods
     */
    public ScalingMethods() {
        try {
            URL url = getClass().getResource("images/BB.jpg");
            picture = ImageIO.read(url);
            scaleW = (int) (SCALE_FACTOR * picture.getWidth());
            scaleH = (int) (SCALE_FACTOR * picture.getHeight());
            setPreferredSize(new Dimension(PADDING + (5 * (scaleW + PADDING)),
                    scaleH + (2 * PADDING)));
        } catch (Exception e) {
            System.out.println("Problem reading image file: " + e);
            System.exit(0);
        }
    }

    /**
     * Draws the picture five times, using the five different scaling approaches described in the book. All five look
     * the same, since all are using default (nearest neighbor) filtering during the scale operation.
     */
    public void paintComponent(Graphics g) {
        int x = PADDING;
        int y = PADDING;

        // Simplest approach
        g.drawImage(picture, x, y, scaleW, scaleH, null);

        // Subregion approach
        x += scaleW + PADDING;
        g.drawImage(picture, x, y, x + scaleW, y + scaleH,
                0, 0, picture.getWidth(), picture.getHeight(), null);

        // Graphics2D.scale approach
        x += scaleW + PADDING;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(x, y);
        g2d.scale(SCALE_FACTOR, SCALE_FACTOR);
        g2d.drawImage(picture, 0, 0, null);
        g2d.dispose();

        // AffineTransform.scale approach
        x += scaleW + PADDING;
        g2d = (Graphics2D) g.create();
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.scale(SCALE_FACTOR, SCALE_FACTOR);
        g2d.drawImage(picture, at, null);
        g2d.dispose();

        // getScaledInstance() approach
        x += scaleW + PADDING;
        Image scaledImg = picture.getScaledInstance(scaleW, scaleH,
                Image.SCALE_DEFAULT);
        g.drawImage(scaledImg, x, y, null);
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("ScalingMethods");
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScalingMethods test = new ScalingMethods();
        f.add(test);
        f.validate();
        f.pack();
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