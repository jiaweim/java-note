package mjw.swing.event;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Apr 2024, 9:51 AM
 */
public class ActionFocusMover implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.focusNextComponent();
    }
}
