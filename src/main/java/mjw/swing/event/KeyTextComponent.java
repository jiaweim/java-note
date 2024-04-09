package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Apr 2024, 10:23 AM
 */
public class KeyTextComponent extends JComponent {

    private ActionListener actionListenerList = null;

    public KeyTextComponent() {
        setBackground(Color.CYAN);
        KeyListener internalKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (actionListenerList != null) {
                    int keyCode = e.getKeyCode();
                    String keyText = KeyEvent.getKeyText(keyCode);
                    ActionEvent actionEvent = new ActionEvent(
                            this,
                            ActionEvent.ACTION_PERFORMED,
                            keyText);
                    actionListenerList.actionPerformed(actionEvent);
                }
            }
        };
        MouseListener internalMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        };

        addKeyListener(internalKeyListener);
        addMouseListener(internalMouseListener);
    }

    public void addActionListener(ActionListener actionListener) {
        actionListenerList = AWTEventMulticaster.add(actionListenerList, actionListener);
    }

    public void removeActionListener(ActionListener actionListener) {
        actionListenerList = AWTEventMulticaster.remove(actionListenerList, actionListener);
    }

    public boolean isFocusable() {
        return true;
    }
}
