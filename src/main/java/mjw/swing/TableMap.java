package mjw.swing;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Jan 2024, 8:48 AM
 */
public class TableMap extends AbstractTableModel implements TableModelListener
{
    TableModel model;

    public TableModel getModel() {
        return model;
    }

    public void setModel(TableModel model) {
        if (this.model != null) {
            this.model.removeTableModelListener(this);
        }
        this.model = model;
        if (this.model != null) {
            this.model.addTableModelListener(this);
        }
    }

    public Class getColumnClass(int column) {
        return model.getColumnClass(column);
    }

    @Override
    public int getColumnCount() {
        return ((model == null) ? 0 : model.getColumnCount());
    }

    public String getColumnName(int column) {
        return model.getColumnName(column);
    }

    public int getRowCount() {
        return ((model == null) ? 0 : model.getRowCount());
    }

    public Object getValueAt(int row, int column) {
        return model.getValueAt(row, column);
    }

    public void setValueAt(Object value, int row, int column) {
        model.setValueAt(value, row, column);
    }

    public boolean isCellEditable(int row, int column) {
        return model.isCellEditable(row, column);
    }

    public void tableChanged(TableModelEvent tableModelEvent) {
        fireTableChanged(tableModelEvent);
    }
}
