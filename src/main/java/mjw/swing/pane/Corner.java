package mjw.swing.pane;

import javax.swing.*;
import java.awt.*;

public class Corner extends JComponent {

    protected void paintComponent(Graphics g) {
        // Fill me with dirty brown/orange.
        g.setColor(new Color(230, 163, 4));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
