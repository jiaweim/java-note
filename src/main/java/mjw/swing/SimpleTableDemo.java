package mjw.swing;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Dec 2023, 9:41 AM
 */
public class SimpleTableDemo extends JPanel {

    private boolean DEBUG = false;

    public SimpleTableDemo() {
        super(new GridLayout(1, 0));
        String[] columnNames = {"First Name", "Last Name", "Sport",
                "# of Years", "Vegetarian"};

        Object[][] data = {
                {"Kathy", "Smith", "Snowboarding", 5, false},
                {"John", "Doe", "Rowing", 3, true},
                {"Sue", "Black", "Knitting", 2, false},
                {"Jane", "White", "Speed reading", 20, true},
                {"Joe", "Brown", "Pool", 10, false}
        };
        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SimpleTableDemo newContentPane = new SimpleTableDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(SimpleTableDemo::createAndShowGUI);
    }
}
