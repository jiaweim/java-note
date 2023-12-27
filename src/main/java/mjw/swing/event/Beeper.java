package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Dec 2023, 9:39 AM
 */
public class Beeper extends JPanel implements ActionListener {

    JButton button;

    public Beeper() {
        super(new BorderLayout());
        button = new JButton("Click Me");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
