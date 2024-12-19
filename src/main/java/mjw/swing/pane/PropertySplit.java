package mjw.swing.pane;

import com.beust.jcommander.JCommander;
import mjw.swing.j2d.OvalComponent;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 17 Dec 2024, 4:46 PM
 */
public class PropertySplit {
    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Property Split");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setContinuousLayout(true);
            splitPane.setOneTouchExpandable(true);

            JComponent topComponent = new OvalComponent();
            splitPane.setTopComponent(topComponent);

            JComponent bottomComponent = new OvalComponent();
            splitPane.setBottomComponent(bottomComponent);

            PropertyChangeListener propertyChangeListener = changeEvent -> {
                JSplitPane sourceSplitPane = (JSplitPane) changeEvent.getSource();
                String propertyName = changeEvent.getPropertyName();
                if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
                    int current = sourceSplitPane.getDividerLocation();
                    System.out.println("Current: " + current);
                    Integer last = (Integer) changeEvent.getNewValue();
                    System.out.println("Last: " + last);
                    Integer priorLast = (Integer) changeEvent.getOldValue();
                    System.out.println("Prior last: " + priorLast);
                }
            };

            splitPane.addPropertyChangeListener(propertyChangeListener);

            frame.add(splitPane, BorderLayout.CENTER);
            frame.setSize(300, 150);
            frame.setVisible(true);
        };

        EventQueue.invokeLater(runner);
    }
}
