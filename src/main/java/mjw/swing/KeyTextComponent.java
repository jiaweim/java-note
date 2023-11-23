package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 任何时候按下
 *
 * @author JiaweiMao
 * @version 1.0.0
 * @since 19 Nov 2021, 2:36 PM
 */
public class KeyTextComponent extends JComponent
{
    private ActionListener actionListenerList = null;

    public KeyTextComponent()
    {
        setBackground(Color.CYAN);
        KeyListener internalKeyListener = new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (actionListenerList != null) {
                    int keyCode = e.getKeyCode();
                    String keyText = KeyEvent.getKeyText(keyCode);
                    ActionEvent actionEvent = new ActionEvent(this,
                            ActionEvent.ACTION_PERFORMED,
                            keyText);
                    actionListenerList.actionPerformed(actionEvent);
                }
            }
        };

        MouseListener internalMouseListener = new MouseAdapter()
        {
            public void mousePressed(MouseEvent mouseEvent)
            {
                requestFocusInWindow();
            }
        };

        addKeyListener(internalKeyListener);
        addMouseListener(internalMouseListener);
    }

    public void addActionListener(ActionListener actionListener)
    {
        actionListenerList = AWTEventMulticaster.add(actionListenerList, actionListener);
    }

    public void removeActionListener(ActionListener actionListener)
    {
        actionListenerList = AWTEventMulticaster.remove(
                actionListenerList, actionListener);
    }

    public boolean isFocusable()
    {
        return true;
    }
}
