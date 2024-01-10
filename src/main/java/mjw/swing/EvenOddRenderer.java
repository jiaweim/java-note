package mjw.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Jan 2024, 9:47 AM
 */
public class EvenOddRenderer implements TableCellRenderer {

    public static final DefaultTableCellRenderer TABLE_CELL_RENDERER = new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component renderer = TABLE_CELL_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color foreground, background;
        if (isSelected) {
            foreground = Color.YELLOW;
            background = Color.GREEN;
        } else {
            if (row % 2 == 0) {
                foreground = Color.BLUE;
                background = Color.WHITE;
            } else {
                foreground = Color.WHITE;
                background = Color.BLUE;
            }
        }
        renderer.setForeground(foreground);
        renderer.setBackground(background);
        return renderer;
    }
}
