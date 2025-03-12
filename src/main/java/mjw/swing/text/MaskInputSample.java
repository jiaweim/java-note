package mjw.swing.text;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 11 Mar 2025, 11:46 AM
 */
public class MaskInputSample {
    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Mask Input");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JLabel label;
                JFormattedTextField input;
                JPanel panel;
                MaskFormatter formatter;

                BoxLayout layout =
                        new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
                frame.setLayout(layout);

                try {
                    label = new JLabel("SSN");
                    formatter = new MaskFormatter("###'-##'-####");
                    input = new JFormattedTextField(formatter);
                    input.setValue("123-45-6789");
                    input.setColumns(20);
                    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    panel.add(label);
                    panel.add(input);
                    frame.add(panel);
                } catch (ParseException e) {
                    System.err.println("Unable to add SSN");
                }

                try {
                    label = new JLabel("US Phone");
                    formatter = new MaskFormatter("'(###')' ###'-####");
                    formatter.setPlaceholderCharacter('*');
                    input = new JFormattedTextField(formatter);
                    input.setColumns(20);
                    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    panel.add(label);
                    panel.add(input);
                    frame.add(panel);
                } catch (ParseException e) {
                    System.err.println("Unable to add Phone");
                }

                frame.pack();
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
