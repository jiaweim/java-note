package mjw.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 28 Dec 2023, 10:24 PM
 */
public class CustomDialog extends JDialog implements ActionListener, PropertyChangeListener {

    private String typedText = null;

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
