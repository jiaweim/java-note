package mjw.study.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 24 Nov 2021, 10:47 PM
 */
public class SimpleList extends JPanel
{
    String[] label = {"Zero", "One", "Two", "Three", "Four", "Five", "Six",
            "Seven", "Eight", "Nine", "Ten", "Eleven"};

    JList list;

    public SimpleList()
    {
        setLayout(new BorderLayout());
        list = new JList(label);
        JScrollPane pane = new JScrollPane(list);
        JButton button = new JButton("Print");
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int[] selectedIndices = list.getSelectedIndices();
                System.out.println("Selected Elements:");
                for (int index : selectedIndices) {
                    String element = (String) list.getModel().getElementAt(index);
                    System.out.println(" " + element);
                }
            }
        });

        add(pane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Simple List Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new SimpleList());
            frame.setSize(250, 200);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
