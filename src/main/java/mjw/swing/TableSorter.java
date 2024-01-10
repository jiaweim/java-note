package mjw.swing;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Date;
import java.util.Vector;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Jan 2024, 8:47 AM
 */
public class TableSorter extends TableMap implements TableModelListener
{
    int[] indexes = new int[0];
    Vector sortingColumns = new Vector();
    boolean ascending = true;

    public TableSorter() {
    }

    public TableSorter(TableModel model) {
        setModel(model);
    }

    public void setModel(TableModel model) {
        super.setModel(model);
        reallocateIndexes();
        sortByColumn(0);
        fireTableDataChanged();
    }

    public int compareRowsByColumn(int row1, int row2, int column) {
        Class type = model.getColumnClass(column);
        TableModel data = model;
        // Check for nulls
        Object o1 = data.getValueAt(row1, column);
        Object o2 = data.getValueAt(row2, column);
        // If both values are null return 0
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) { // Define null less than everything.
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        if (type.getSuperclass() == Number.class) {
            Number n1 = (Number) data.getValueAt(row1, column);
            double d1 = n1.doubleValue();
            Number n2 = (Number) data.getValueAt(row2, column);
            double d2 = n2.doubleValue();
            return Double.compare(d1, d2);
        } else if (type == String.class) {
            String s1 = (String) data.getValueAt(row1, column);
            String s2 = (String) data.getValueAt(row2, column);
            int result = s1.compareTo(s2);
            return Integer.compare(result, 0);
        } else if (type == java.util.Date.class) {
            Date d1 = (Date) data.getValueAt(row1, column);
            long n1 = d1.getTime();
            Date d2 = (Date) data.getValueAt(row2, column);
            long n2 = d2.getTime();
            return Long.compare(n1, n2);
        } else if (type == Boolean.class) {
            Boolean bool1 = (Boolean) data.getValueAt(row1, column);
            boolean b1 = bool1.booleanValue();
            Boolean bool2 = (Boolean) data.getValueAt(row2, column);
            boolean b2 = bool2.booleanValue();
            if (b1 == b2)
                return 0;
            else if (b1) // Define false < true
                return 1;
            else
                return -1;
        } else {
            Object v1 = data.getValueAt(row1, column);
            String s1 = v1.toString();
            Object v2 = data.getValueAt(row2, column);
            String s2 = v2.toString();
            int result = s1.compareTo(s2);
            if (result < 0)
                return -1;
            else if (result > 0)
                return 1;
            else
                return 0;
        }
    }

    public int compare(int row1, int row2) {
        for (int level = 0, n = sortingColumns.size(); level < n; level++) {
            Integer column = (Integer) sortingColumns.elementAt(level);
            int result = compareRowsByColumn(row1, row2, column);
            if (result != 0) {
                return (ascending ? result : -result);
            }
        }
        return 0;
    }

    public void reallocateIndexes() {
        int rowCount = model.getRowCount();
        indexes = new int[rowCount];
        for (int row = 0; row < rowCount; row++) {
            indexes[row] = row;
        }
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
        super.tableChanged(tableModelEvent);
        reallocateIndexes();
        sortByColumn(0);
        fireTableStructureChanged();
    }

    public void checkModel() {
        if (indexes.length != model.getRowCount()) {
            System.err.println("Sorter not informed of a change in model.");
        }
    }

    public void sort() {
        checkModel();
        shuttlesort(indexes.clone(), indexes, 0, indexes.length);
        fireTableDataChanged();
    }

    public void shuttlesort(int[] from, int[] to, int low, int high) {
        if (high - low < 2) {
            return;
        }
        int middle = (low + high) / 2;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);
        int p = low;
        int q = middle;
        for (int i = low; i < high; i++) {
            if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
                to[i] = from[p++];
            } else {
                to[i] = from[q++];
            }
        }
    }

    private void swap(int first, int second) {
        int temp = indexes[first];
        indexes[first] = indexes[second];
        indexes[second] = temp;
    }

    @Override
    public Object getValueAt(int row, int column) {
        checkModel();
        return model.getValueAt(indexes[row], column);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        checkModel();
        model.setValueAt(aValue, indexes[row], column);
    }

    public void sortByColumn(int column) {
        sortByColumn(column, true);
    }

    public void sortByColumn(int column, boolean ascending) {
        this.ascending = ascending;
        sortingColumns.removeAllElements();
        sortingColumns.addElement(column);
        sort();
        super.tableChanged(new TableModelEvent(this));
    }
}
