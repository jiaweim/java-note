package mjw.study.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 10:38 AM
 */
public class TranslucentButton extends JButton
{
    BufferedImage buttonImage = null;

    public TranslucentButton(String text)
    {
        super(text);
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g)
    {
        if (buttonImage == null || buttonImage.getWidth() != getWidth()
                || buttonImage.getHeight() != getHeight()) {
            buttonImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
        }
        Graphics gButton = buttonImage.getGraphics();
        gButton.setClip(g.getClip());

        super.paint(gButton);

        // Make the graphics object sent to this paint() method translucent
        Graphics2D g2d = (Graphics2D) g;
        AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
        g2d.setComposite(newComposite);

        // Copy the button's image to the destination graphics, translucently
        g2d.drawImage(buttonImage, 0, 0, null);
    }

    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        JPanel checkerboard = new Checkerboard();
        checkerboard.add(new TranslucentButton("Translucent Button"));
        f.add(checkerboard);
        f.setVisible(true);
    }

    public static void main(String[] args)
    {
        Runnable doCreateAndShowGUI = () -> createAndShowGUI();
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }

    private static class Checkerboard extends JPanel
    {
        private static final int DIVISIONS = 10;
        static final int CHECKER_SIZE = 60;

        public void paintComponent(Graphics g)
        {
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            for (int stripeX = 0; stripeX < getWidth(); stripeX += CHECKER_SIZE) {
                for (int y = 0, row = 0; y < getHeight(); y += CHECKER_SIZE / 2, ++row) {
                    int x = (row % 2 == 0) ? stripeX : (stripeX + CHECKER_SIZE / 2);
                    g.fillRect(x, y, CHECKER_SIZE / 2, CHECKER_SIZE / 2);
                }
            }
        }
    }
}
