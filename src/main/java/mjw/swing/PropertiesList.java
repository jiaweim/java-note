package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesList extends JList {

    SortedListModel model;
    Properties tipProps;

    public PropertiesList(Properties props) {
        model = new SortedListModel();
        setModel(model);
        ToolTipManager.sharedInstance().registerComponent(this);

        tipProps = props;
        addProperties(props);
    }

    private void addProperties(Properties props) {
        // Load
        Enumeration names = props.propertyNames();
        while (names.hasMoreElements()) {
            model.add(names.nextElement());
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        Point p = event.getPoint();
        int location = locationToIndex(p);
        String key = (String) model.getElementAt(location);
        String tip = tipProps.getProperty(key);
        return tip;
    }

    public static void main(String args[]) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Custom Tip Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Properties props = System.getProperties();
            PropertiesList list = new PropertiesList(props);
            JScrollPane scrollPane = new JScrollPane(list);
            frame.add(scrollPane);
            frame.setSize(300, 300);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
