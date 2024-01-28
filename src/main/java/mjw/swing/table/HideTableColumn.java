package mjw.swing.table;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Jan 2024, 3:49 PM
 */
public class HideTableColumn extends JFrame
{
    JTable table = new JTable(
            new Object[][]{
                    {"Mouse", "Mighty", "M."},
                    {"Mouse", "Polly", "A."},
                    {"Doright", "Dudley", "L."}},
            new Object[]{"Last Name", "First Name", "Middle Initial"}
    );

    public HideTableColumn() throws HeadlessException {
        Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    class ControlPanel extends JPanel
    {
        private JCheckBox checkBox = new JCheckBox("First Name Column Showing");

        public ControlPanel() {
            final TableColumnModel tcm = table.getColumnModel();
            final TableColumn firstNameColumn = table.getColumn("First Name");
            checkBox.setSelected(true);

            add(checkBox);

        }
    }
}
