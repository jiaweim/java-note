package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 10:31 AM
 */
public class HighlightedButton extends JButton
{
    static final int HIGHLIGHT_SIZE = 18;
    BufferedImage highlight = new BufferedImage(HIGHLIGHT_SIZE, HIGHLIGHT_SIZE, BufferedImage.TYPE_INT_ARGB);

    /**
     * Creates a new instance of HighlightedButton
     */
    public HighlightedButton(String label)
    {
        super(label);

        // Get the Graphics for the image
        Graphics2D g2d = highlight.createGraphics();

        // Erase the image with a transparent background
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
        g2d.setComposite(AlphaComposite.SrcOver);

        // Draw the highlight
        Point2D center = new Point2D.Float((float) HIGHLIGHT_SIZE / 2.0f,
                (float) HIGHLIGHT_SIZE / 2.0f);
        float radius = (float) HIGHLIGHT_SIZE / 2.0f;
        float[] dist = {0.0f, .85f};
        Color[] colors = {Color.white, new Color(255, 255, 255, 0)};
        RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(paint);
        g2d.fillOval(0, 0, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(highlight, getWidth() / 4, getHeight() / 4, null);
    }

    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new FlowLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(100, 100);
        f.add(new JButton("Standard"));
        f.add(new HighlightedButton("Highlighted"));
        f.setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(HighlightedButton::createAndShowGUI);
    }
}
