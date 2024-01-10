package mjw.swing.j2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageLoader extends JFrame
{
    private JTextArea log;
    private JPanel viewer;

    public ImageLoader() {
        super("Image Loader");

        this.log = new JTextArea(4, 4);
        this.viewer = new JPanel();

        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                String[] files = new String[]{
                        "Bodie_small.png", "Carmela_small.png",
                        "Unknown.png", "Denied.png",
                        "Death Valley_small.png", "Lake_small.png"
                };
                new ImageLoadingWorker(log, viewer, files).execute();
            }
        });

        add(new JScrollPane(log), BorderLayout.NORTH);
        add(new JScrollPane(viewer), BorderLayout.CENTER);
        add(start, BorderLayout.SOUTH);

        setSize(360, 280);
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() {
                new ImageLoader().setVisible(true);
            }
        });
    }
}
