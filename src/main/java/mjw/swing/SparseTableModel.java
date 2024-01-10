package mjw.swing;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Jan 2024, 9:20 PM
 */
public class SparseTableModel extends AbstractTableModel {

    private Map<Point, Object> lookup;
    private final int rows;
    private final int columns;
    private final String headers[];

    public SparseTableModel(int rows, String columnHeaders[]) {
        if ((rows < 0) || (columnHeaders == null)) {
            throw new IllegalArgumentException("Invalid row count/columnHeaders");
        }
        this.rows = rows;
        this.columns = columnHeaders.length;
        headers = columnHeaders;
        lookup = new HashMap<>();
    }

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return lookup.get(new Point(rowIndex, columnIndex));
    }

    public void setValueAt(Object value, int row, int column) {
        if ((rows < 0) || (columns < 0)) {
            throw new IllegalArgumentException("Invalid row/column setting");
        }
        if ((row < rows) && (column < columns)) {
            lookup.put(new Point(row, column), value);
        }
    }
}
