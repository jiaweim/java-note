package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Apr 2024, 11:50 AM
 */
public class NextComponentSample {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Reverse Sample");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.setLayout(new GridLayout(3, 3));
                // for (int i=1; i<10; i++) {
                for (int i = 9; i > 0; i--) {
                    JButton button = new JButton(Integer.toString(i));
                    frame.add(button, 0);
                }

                final Container contentPane = frame.getContentPane();
                Comparator<Component> comp = (c1, c2) -> {
                    Component[] comps = contentPane.getComponents();
                    java.util.List<Component> list = Arrays.asList(comps);
                    int first = list.indexOf(c1);
                    int second = list.indexOf(c2);
                    return second - first;
                };

                FocusTraversalPolicy policy = new SortingFocusTraversalPolicy(comp);
                frame.setFocusTraversalPolicy(policy);

                frame.setSize(300, 200);
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
