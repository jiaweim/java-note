package mjw.study.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 13 Nov 2021, 9:05 AM
 */
public class OvalComponent extends JComponent
{
    @Override
    protected void paintComponent(Graphics g)
    {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GRAY);
        g.fillOval(0, 0, getWidth(), getHeight());
    }

    private static void createAndShowGUI()
    {
        JFrame f = new JFrame("Oval");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(200, 200);
        f.add(new OvalComponent());
        f.setVisible(true);
    }

    public static void main(String[] args)
    {
        Runnable doCreateAndShowGUI = OvalComponent::createAndShowGUI;
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}
