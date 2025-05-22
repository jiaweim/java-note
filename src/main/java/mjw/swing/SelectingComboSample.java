package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SelectingComboSample {

    private static String selectedString(ItemSelectable is) {
        Object[] selected = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String) selected[0]);
    }

    public static void main(String[] args) {
        Runnable runner = () -> {
            String[] labels = {"Chardonnay", "Sauvignon", "Riesling", "Cabernet",
                    "Zinfandel", "Merlot", "Pinot Noir", "Sauvignon Blanc",
                    "Syrah", "Gewürztraminer"};
            JFrame frame = new JFrame("Selecting JComboBox");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JComboBox comboBox = new JComboBox(labels);
            frame.add(comboBox, BorderLayout.SOUTH);

            final JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane sp = new JScrollPane(textArea);
            frame.add(sp, BorderLayout.CENTER);

            ItemListener itemListener = itemEvent -> {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                int state = itemEvent.getStateChange();
                String stateString =
                        ((state == ItemEvent.SELECTED) ? "Selected" : "Deselected");
                pw.print("Item: " + itemEvent.getItem());
                pw.print(", State: " + stateString);
                ItemSelectable is = itemEvent.getItemSelectable();
                pw.print(", Selected: " + selectedString(is));
                pw.println();
                textArea.append(sw.toString());
            };
            comboBox.addItemListener(itemListener);

            ActionListener actionListener = actionEvent -> {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.print("Command: " + actionEvent.getActionCommand());
                ItemSelectable is = (ItemSelectable) actionEvent.getSource();
                pw.print(", Selected: " + selectedString(is));
                pw.println();
                textArea.append(sw.toString());
            };
            comboBox.addActionListener(actionListener);

            frame.setSize(400, 200);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
