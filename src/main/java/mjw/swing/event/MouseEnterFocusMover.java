package mjw.swing.event;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Apr 2024, 9:36 AM
 */
public class MouseEnterFocusMover extends MouseAdapter {

    @Override
    public void mouseEntered(MouseEvent e) {
        Component component = e.getComponent();
        if (!component.hasFocus()) {
            component.requestFocusInWindow();
        }
    }
}
