package mjw.swing.event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Apr 2024, 2:14 PM
 */
public class ActionTester {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Action Sample");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                final PrintHelloAction printAction = new PrintHelloAction();

                JMenuBar menuBar = new JMenuBar();

                JMenu menu = new JMenu("File");
                menuBar.add(menu);
                menu.add(new JMenuItem(printAction));

                JToolBar toolBar = new JToolBar();
                toolBar.add(new JButton(printAction));

                JButton enableButton = new JButton("Enable");
                ActionListener enableActionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        printAction.setEnabled(true);
                    }
                };
                enableButton.addActionListener(enableActionListener);

                JButton disableButton = new JButton("Disable");
                ActionListener disableActionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        printAction.setEnabled(false);
                    }
                };
                disableButton.addActionListener(disableActionListener);

                JButton relabelButton = new JButton("Relabel");
                ActionListener relabelActionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        printAction.putValue(Action.NAME, "Hello, World");
                    }
                };
                relabelButton.addActionListener(relabelActionListener);

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(enableButton);
                buttonPanel.add(disableButton);
                buttonPanel.add(relabelButton);

                frame.setJMenuBar(menuBar);

                frame.add(toolBar, BorderLayout.SOUTH);
                frame.add(buttonPanel, BorderLayout.NORTH);
                frame.setSize(300, 200);
                frame.setVisible(true);

                for (Object key : printAction.getKeys()) {
                    System.out.println(key);
                }

            }
        };
        EventQueue.invokeLater(runner);
    }
}

