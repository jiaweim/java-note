package mjw.swing.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 07 Apr 2024, 5:03 PM
 */
public class AnActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("I was selected.");
    }

    public static void main(String[] args) {

    }

    public void mousePressed(MouseEvent mouseEvent) {
        int modifiers = mouseEvent.getModifiers();
        if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
            System.out.println("Middle button pressed.");
        }
    }
}
