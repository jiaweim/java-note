package mjw.study.swing;

import javax.swing.*;
import java.awt.*;

public class FileSamplePanel
{
    public static void main(String args[])
    {
        Runnable runner = new Runnable()
        {
            public void run()
            {
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
                frame.pack();
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }


}