package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 17 Nov 2021, 11:02 AM
 */
public class ArrayListComboBoxModel extends AbstractListModel implements ComboBoxModel
{
    private Object selectedItem;
    private ArrayList anArrayList;

    public ArrayListComboBoxModel(ArrayList arrayList)
    {
        anArrayList = arrayList;
    }

    @Override
    public Object getSelectedItem()
    {
        return selectedItem;
    }

    @Override
    public void setSelectedItem(Object newValue)
    {
        selectedItem = newValue;
    }

    @Override
    public int getSize()
    {
        return anArrayList.size();
    }

    @Override
    public Object getElementAt(int i)
    {
        return anArrayList.get(i);
    }

    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("ArrayListComboBoxModel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Collection<Object> col = System.getProperties().values();
            ArrayList<Object> arrayList = new ArrayList<>(col);
            ArrayListComboBoxModel model = new ArrayListComboBoxModel(arrayList);
            JComboBox comboBox = new JComboBox(model);
            frame.add(comboBox, BorderLayout.NORTH);
            frame.setSize(300, 225);
            frame.setVisible(true);
        };
        SwingUtilities.invokeLater(runner);
    }
}
