package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 28 Nov 2024, 2:18 PM
 */
public class ToolBarSample {

    private static final int COLOR_POSITION = 0;
    private static final int STRING_POSITION = 1;

    static Object[][] buttonColors = {
            {Color.RED, "RED"},
            {Color.BLUE, "BLUE"},
            {Color.GREEN, "GREEN"},
            {Color.BLACK, "BLACK"},
            null, // separator
            {Color.CYAN, "CYAN"}
    };

    public static class TheActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getActionCommand());
        }
    }

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("JToolBar Example");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ActionListener actionListener = new TheActionListener();

                JToolBar toolBar = new JToolBar();
                toolBar.setRollover(true);

                for (Object[] color : buttonColors) {
                    if (color == null) {
                        toolBar.addSeparator();
                    } else {
                        Icon icon = new DiamondIcon((Color) color[COLOR_POSITION], true, 20, 20);
                        JButton button = new JButton(icon);
                        button.setActionCommand(color[STRING_POSITION].toString());
                        button.addActionListener(actionListener);
                        toolBar.add(button);
                    }
                }

            }
        };
    }

}
