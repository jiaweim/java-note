package mjw.swing.j2d;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Jan 2024, 9:29 PM
 */
public class FontHints extends JComponent
{

    Map desktopHints = null;

    /**
     * Creates a new instance of FontHints
     */
    public FontHints() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints"));
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.BLACK);

        g2d.drawString("Unhinted string", 10, 20);
        if (desktopHints != null) {
            g2d.addRenderingHints(desktopHints);
        }
        g2d.drawString("Desktop-hinted string", 10, 40);
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("FontHints");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(200, 90);
        FontHints component = new FontHints();
        f.add(component);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable doCreateAndShowGUI = FontHints::createAndShowGUI;
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}