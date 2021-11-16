package mjw.study.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 12:29 PM
 */
public class SafeComponent extends JLabel
{
    public SafeComponent()
    {
        super("Safe Repaint");
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        System.out.println(SwingUtilities.isEventDispatchThread());
    }
}
