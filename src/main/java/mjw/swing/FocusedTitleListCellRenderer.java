package mjw.swing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class FocusedTitleListCellRenderer implements ListCellRenderer {

    protected static Border noFocusBorder =
            new EmptyBorder(15, 1, 1, 1);
    protected static TitledBorder focusBorder =
            new TitledBorder(LineBorder.createGrayLineBorder(), "Focused");
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public String getTitle() {
        return focusBorder.getTitle();
    }

    public void setTitle(String newValue) {
        focusBorder.setTitle(newValue);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        renderer.setBorder(cellHasFocus ? focusBorder : noFocusBorder);
        return renderer;
    }
}

