package mjw.swing.event;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Apr 2024, 3:00 PM
 */
public class ShareModel {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Sharing Sample");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                Container content = frame.getContentPane();
                JTextArea textarea1 = new JTextArea();
                Document document = textarea1.getDocument();
                JTextArea textarea2 = new JTextArea(document);
                JTextArea textarea3 = new JTextArea(document);

                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                content.add(new JScrollPane(textarea1));
                content.add(new JScrollPane(textarea2));
                content.add(new JScrollPane(textarea3));
                frame.setSize(300, 400);
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
