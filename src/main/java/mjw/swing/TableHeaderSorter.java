package mjw.swing;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Jan 2024, 9:01 AM
 */
public class TableHeaderSorter extends MouseAdapter
{
    private TableSorter sorter;
    private JTable table;

    private TableHeaderSorter() {
    }

    public static void install(TableSorter sorter, JTable table) {
        TableHeaderSorter tableHeaderSorter = new TableHeaderSorter();
        tableHeaderSorter.sorter = sorter;
        tableHeaderSorter.table = table;
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.addMouseListener(tableHeaderSorter);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        TableColumnModel columnModel = table.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(mouseEvent.getX());
        int column = table.convertColumnIndexToModel(viewColumn);
        if (mouseEvent.getClickCount() == 1 && column != -1) {
            System.out.println("Sorting ...");
            boolean ascending = (mouseEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK;
            sorter.sortByColumn(column, ascending);
        }
    }
}
