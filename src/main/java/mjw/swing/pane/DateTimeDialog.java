package mjw.swing.pane;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeDialog extends JDialog {

    JLabel dateTimeLabel = new JLabel("Datetime placeholder");
    JButton okButton = new JButton("OK");

    public DateTimeDialog() {
        initFrame(); /*  w  w w   . d   em   o  2 s  .   c o  m  */
    }

    private void initFrame() {
        // Release all resources when JDialog is closed 
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.setTitle("Current Date and Time");
        this.setModal(true);

        String currentDateTimeString = getCurrentDateTimeString();
        dateTimeLabel.setText(currentDateTimeString);

        // There is no need to add components to the content pane. 
        // You can directly add them to the JDialog. 
        this.add(dateTimeLabel, BorderLayout.NORTH);
        this.add(okButton, BorderLayout.SOUTH);

        // Add an action listener to the OK button 
        okButton.addActionListener(e -> DateTimeDialog.this.dispose());
    }

    private String getCurrentDateTimeString() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("EEEE MMMM dd, yyyy hh:mm:ss a");
        String dateString = ldt.format(formatter);
        return dateString;
    }

    public static void main(String[] args) {
        DateTimeDialog dateTimeDialog = new DateTimeDialog();
        dateTimeDialog.pack();
        dateTimeDialog.setVisible(true);
    }
} 