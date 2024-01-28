package mjw.swing.table;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Jan 2024, 3:30 PM
 */
public class TableModelDemo1 extends JFrame
{
    JTable table = new JTable(new AbstractTableModel()
    {
        int rows = 100;
        int cols = 10;

        @Override
        public int getRowCount() {
            return rows;
        }

        @Override
        public int getColumnCount() {
            return cols;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return "(" + rowIndex + "," + columnIndex + ")";
        }
    });

}
