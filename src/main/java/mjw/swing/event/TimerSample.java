package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Apr 2024, 11:41 AM
 */
public class TimerSample {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Hello World Timer");
                    }
                };
                Timer timer = new Timer(500, actionListener);
                timer.start();
            }
        };
        EventQueue.invokeLater(runner);
    }
}
