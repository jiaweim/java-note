package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class FileSamplePanel
{

    public static void main(String[] args) {
        Runnable runner = () -> {
            JFrame frame = new JFrame("JFileChooser Popup");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            final JLabel directoryLabel = new JLabel(" ");
            directoryLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
            frame.add(directoryLabel, BorderLayout.NORTH);

            final JLabel filenameLabel = new JLabel(" ");
            filenameLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
            frame.add(filenameLabel, BorderLayout.SOUTH);

            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setControlButtonsAreShown(false);
            frame.add(fileChooser, BorderLayout.CENTER);

            // Create ActionListener
            ActionListener actionListener = actionEvent -> {
                JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
                String command = actionEvent.getActionCommand();
                if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                    File selectedFile = theFileChooser.getSelectedFile();
                    directoryLabel.setText(selectedFile.getParent());
                    filenameLabel.setText(selectedFile.getName());
                } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
                    directoryLabel.setText(" ");
                    filenameLabel.setText(" ");
                }
            };
            fileChooser.addActionListener(actionListener);

            frame.pack();
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
