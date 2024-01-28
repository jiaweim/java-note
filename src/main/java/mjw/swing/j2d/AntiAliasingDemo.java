package mjw.swing.j2d;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 19 Nov 2021, 9:26 PM
 */
public class AntiAliasingDemo extends JComponent
{
    public void paintComponent(Graphics g) {
        // we will need a Graphics2D Object to set the RenderingHint
        Graphics2D g2d = (Graphics2D) g;

        // Erase to white
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        //Draw line with default setting.
        g2d.drawLine(0, 0, 50, 50);

        //Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draw line with new setting
        g2d.drawLine(50, 0, 100, 50);

    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(150, 100);
        JComponent component = new AntiAliasingDemo();

        frame.add(component);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable doCreateAndShowGui = () -> createAndShowGui();
        EventQueue.invokeLater(doCreateAndShowGui);
    }
}
