package mjw.swing;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 08 Jan 2024, 10:00 PM
 */
public class CreateTable {

    public static void main(String[] args) {
        TableModel model = new AbstractTableModel() {
            Object rowData[][] = {
                    {"one", "ichi"},
                    {"two", "ni"},
                    {"three", "san"},
                    {"four", "shi"},
                    {"five", "go"},
                    {"six", "roku"},
                    {"seven", "shichi"}, {"eight", "hachi"},
                    {"nine", "kyu"},
                    {"ten", "ju"}
            };
            Object columnNames[] = {"English", "Japanese"};

            public String getColumnName(int column) {
                return columnNames[column].toString();
            }

            public int getRowCount() {
                return rowData.length;
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                return rowData[row][col];
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
    }
}
