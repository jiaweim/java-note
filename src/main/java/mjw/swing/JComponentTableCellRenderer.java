package mjw.swing;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class JComponentTableCellRenderer implements TableCellRenderer
{
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return (JComponent) value;
    }
}