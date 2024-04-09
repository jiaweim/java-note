package mjw.swing.event;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Apr 2024, 11:17 AM
 */
public class KeyTextComponent2 extends JComponent {

    private EventListenerList actionListenerList = new EventListenerList();

    public KeyTextComponent2() {
        setBackground(Color.CYAN);
        KeyListener internalKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                String keyText = KeyEvent.getKeyText(keyCode);
                ActionEvent actionEvent = new ActionEvent(
                        this,
                        ActionEvent.ACTION_PERFORMED,
                        keyText
                );
                fireActionPerformed(actionEvent);
            }
        };
        MouseListener internalMouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                requestFocusInWindow();
            }
        };

        addKeyListener(internalKeyListener);
        addMouseListener(internalMouseListener);
    }

    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(ActionListener.class, actionListener);
    }

    public void removeActionListener(ActionListener actionListener) {
        actionListenerList.remove(ActionListener.class, actionListener);
    }

    protected void fireActionPerformed(ActionEvent actionEvent) {
        EventListener[] listenerList =
                actionListenerList.getListeners(ActionListener.class);
        for (EventListener eventListener : listenerList) {
            ((ActionListener) eventListener).actionPerformed(actionEvent);
        }
    }

    public boolean isFocusable() {
        return true;
    }
}
